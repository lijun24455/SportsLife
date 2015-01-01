package sysu.project.lee.sportslife.Excercise;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.location.LocationProviderProxy;

import java.util.Timer;
import java.util.TimerTask;

import sysu.project.lee.sportslife.UI.MainActivity;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.Utils.mConvertTool;
import sysu.project.lee.sportslife.Utils.mHelper;

/**
 * 跳绳运动记录界面类
 *
 */
@SuppressLint("HandlerLeak")
public class SkipCount extends Activity implements  AMapLocationListener{

	private TextView timeView, calView;
	private mHelper appHelper;
	private int step;
	private Button button_stop;
	private int time;
	private int cal;
    private TextView countView;
    private boolean flag = false;

    private String mLocationCity = "未知地点";

    private LocationManagerProxy mlocationManagerProxy;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_skip_count);

        mlocationManagerProxy = LocationManagerProxy.getInstance(this);
        mlocationManagerProxy.requestLocationData(LocationProviderProxy.AMapNetwork,60*1000, -1, this);
        mlocationManagerProxy.setGpsEnable(true);

        appHelper = (mHelper) getApplicationContext();
        reSetHelper();
        timeView = (TextView) findViewById(R.id.tv_time_second);
        calView = (TextView) findViewById(R.id.tv_cal_count);
        button_stop = (Button) findViewById(R.id.stopRunButton);
        countView = (TextView) findViewById(R.id.tv_skip_count);
        time = cal = 0;

		bindStepService();

        flag = true;
		new Thread(mRunnable).start();

		handler.postDelayed(runnable, 1000);

		button_stop.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
                flag = false;
				unbindService(mConnection);
				if (appHelper != null) {
					if (appHelper.getEtype() == 3) {
						Exercise exercise = appHelper.getCurrExercise();
						exercise.setTotalCount(step);
						exercise.setTotalTime(time);
//						exercise.setDistance((int) distance);
						exercise.setTotalCal(cal);
                        appHelper.getExerManager().addOneExercise(
								exercise);
						exercise.setFinish(true);
					}
				}

				TimerTask task = new TimerTask() {
					public void run() {
						Intent intent = new Intent();
                        intent.putExtra("Location", mLocationCity);
						intent.setClass(SkipCount.this, SkipResultShow.class);
						startActivity(intent);
						SkipCount.this.finish();
					}
				};
				Timer timer = new Timer();
				timer.schedule(task, 1000);
				// pause();

			}
		});
	}


	@Override
	protected void onResume() {
		super.onResume();

	}

	@Override
	protected void onPause() {

		super.onPause();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onDestroy() {
//		getApplicationContext().unregisterReceiver(phonelistener);
        super.onDestroy();
//		unbindService(mConnection);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.step_count, menu);---------->
		return true;
	}

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			((StepService.StepBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
		}
	};

    /**
     * 绑定计数服务
     */
	private void bindStepService() {
		startService(new Intent(SkipCount.this, StepService.class));
		bindService(new Intent(SkipCount.this, StepService.class), mConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

    /**
     * 界面刷新消息发送线程
     */
	private Runnable mRunnable = new Runnable() {
		public void run() {
			while (flag) {
				try {
					Thread.sleep(1000);
					mHandler.sendMessage(mHandler.obtainMessage());
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

    /**
     * 接受刷新线程发来的信息，执行界面刷新任务
     */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			refreshUI();

            Log.i("STEP","---->step still");

		}
	};

    /**
     * 重置全局Helper
     */
    private void reSetHelper() {
        appHelper.setStep(0);
        appHelper.setNum(0);
    }

    /**
     * 刷新界面，主要刷新计数和热量数据
     */
	private void refreshUI() {
        step = appHelper.getStep();
        cal = time / 60 * 8;
        calView.setText( cal + "" );
        countView.setText(String.valueOf(step));
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dialog();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

    protected void dialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                SkipCount.this);

        alertDialog.setMessage("停止运动记录?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        // pause();
                        Intent intent = new Intent();
                        intent.setClass(SkipCount.this, MainActivity.class);
                        startActivity(intent);
                        SkipCount.this.finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }


    Handler handler = new Handler();
    private String timeShowView;
    Runnable runnable = new Runnable() {
		@SuppressLint("HandlerLeak")
		@Override
		public void run() {
			time++;
            timeShowView = mConvertTool.parseSecondToTimeFormat(time + "");
			timeView.setText(timeShowView);
			handler.postDelayed(this, 1000);
		}
	};

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		// System.out.println("ThirdActivity"+tag+"--->onNewIntent");
	}


    /**
     * 定位变化响应方法
     *
     * @param aMapLocation AMapLocation实例
     */
    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {

        if(aMapLocation!=null){
            mLocationCity = aMapLocation.getCity();
        }else{
            appHelper.DisplayToast("定位失败");
        }
    }

    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

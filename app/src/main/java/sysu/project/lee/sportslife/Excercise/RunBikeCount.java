package sysu.project.lee.sportslife.Excercise;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
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
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMap.CancelableCallback;
import com.amap.api.maps.AMap.OnMapScreenShotListener;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.GeocodeSearch.OnGeocodeSearchListener;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import sysu.project.lee.sportslife.UI.MainActivity;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.User.Utils;
import sysu.project.lee.sportslife.Utils.ToastUtils;
import sysu.project.lee.sportslife.Utils.mConvertTool;
import sysu.project.lee.sportslife.Utils.mHelper;

/**
 * 跑步和骑行运动记录界面类
 *
 */
@SuppressLint("HandlerLeak")
public class RunBikeCount extends Activity implements LocationSource,
        AMapLocationListener, OnMapScreenShotListener, OnGeocodeSearchListener {
	private TextView timeView, calView;
	private TextView distance_count, speed_count;
    private TextView tvDistanceLabel = null;
	private mHelper appHelper;
	private int step;
	private Button button_stop;
	private int time;
	private int cal;
	private int location_change_count = 0;
	private float distance = 0;
	private float distance_for_two_second;

	private AMap aMap;
	private MapView mapView;
	private OnLocationChangedListener mListener;
	private LocationManagerProxy mAMapLocationManager;
	private LatLng newpoint;
	private LatLng oldpoint;
	private LatLonPoint firstpoint;
	private String path;
	private String screen_shot_image_path = "/res/drawable-hdpi/showbg.jpg";

	private String province = "未知";
	private String addressName = "";
	private GeocodeSearch geocoderSearch;
    private int mDistance = 0;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_run_bike_count);

		mapView = (MapView) findViewById(R.id.map);
		mapView.onCreate(savedInstanceState);
		initMap();

        appHelper = (mHelper) getApplicationContext();
		timeView = (TextView) findViewById(R.id.timeTextView);
		calView = (TextView) findViewById(R.id.calTextView);
		button_stop = (Button) findViewById(R.id.stopRunButton);
		distance_count = (TextView) findViewById(R.id.distanceCountView);
		speed_count = (TextView) findViewById(R.id.speedViewText);
        tvDistanceLabel = (TextView) findViewById(R.id.tv_distanceLabelView);
		time = cal = 0;

        reSetHelper();

		new Thread(mRunnable).start();

		handler.postDelayed(runnable, 1000);

		button_stop.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
//				unbindService(mConnection);
				if (appHelper != null) {
					if (appHelper.getEtype() == 0 || appHelper.getEtype() == 1) {
                        Exercise exercise = appHelper.getCurrExercise();
                        exercise.setTotalCount(step);
                        exercise.setTotalTime(time);
                        exercise.setTotalDistance((int) distance);
                        exercise.setTotalCal(cal);
                        appHelper.getExerManager().addOneExercise(
                                exercise);
                        exercise.setFinish(true);
					}
				}

				getMapScreenShot(mapView);

				TimerTask task = new TimerTask() {
					public void run() {
						Intent intent = new Intent();
						intent.setClass(RunBikeCount.this, RunBikeResultShow.class);
						intent.putExtra("screen_shot_name",
                                screen_shot_image_path);
						intent.putExtra("Province", province);
						startActivity(intent);
						RunBikeCount.this.finish();
					}
				};
				Timer timer = new Timer();
				timer.schedule(task, 1000);
				// pause();
				deactivate();//地图释放

			}
		});
	}

    /**
     * 根据当前运动类型重置全局Helper的运动参数
     */
    private void reSetHelper() {
        appHelper.setStep(0);
        appHelper.setNum(0);
    }

    private void changeCamera(CameraUpdate update, CancelableCallback callback) {
		aMap.animateCamera(update, 1000, callback);
	}

    /**
     * 初始化地图
     */
	private void initMap() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

    /**
     * 地图配置方法
     */
	private void setUpMap() {
		MyLocationStyle myLocationStyle = new MyLocationStyle();
		myLocationStyle.myLocationIcon(BitmapDescriptorFactory
				.fromResource(R.drawable.location_marker));
		myLocationStyle.strokeColor(Color.BLACK);
		myLocationStyle.radiusFillColor(Color.argb(10, 0, 0, 20));
		myLocationStyle.strokeWidth(1.0f);
		aMap.setMyLocationStyle(myLocationStyle);
		aMap.setLocationSource(this);
		aMap.getUiSettings().setMyLocationButtonEnabled(false);
		aMap.getUiSettings().setLogoPosition(
				AMapOptions.LOGO_POSITION_BOTTOM_LEFT);
		aMap.setMyLocationEnabled(true);
		aMap.getUiSettings().setScaleControlsEnabled(true);
		aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
		aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.0515,
                113.3928), 14));
		// changeCamera(CameraUpdateFactory.zoomTo(17),null);
		//
	}

	@Override
	protected void onResume() {
		super.onResume();
        mapView.onResume();
        // resume();

	}

	@Override
	protected void onPause() {

		super.onPause();
		mapView.onPause();
		// deactivate();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);

	}

	@Override
	protected void onDestroy() {
		// getApplicationContext().unregisterReceiver(phonelistener);
        super.onDestroy();
        mapView.onDestroy();

		// unbindService(mConnection);
	}

    /**
     * 对地图进行截屏
     */
	public void getMapScreenShot(View v) {
		aMap.getMapScreenShot(this);
	}

    /**
     * 保存地图截图
     * @param bitmap Bitmap型的截图
     */
	@SuppressLint("SimpleDateFormat")
	public void onMapScreenShot(Bitmap bitmap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			path = Utils.SCREENSHOOT_PATH_BASE_DIR;
			File path_temp = new File(path);
			if (!path_temp.exists()) {
				path_temp.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(path_temp
					+ File.separator + "SCREENSHOOT_" + sdf.format(new Date()) + ".png");
			screen_shot_image_path = path_temp + File.separator + "SCREENSHOOT_"
					+ sdf.format(new Date()) + ".png";
			boolean b = bitmap.compress(CompressFormat.PNG, 100, fos);
			try {
				fos.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}


		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void onLocationChanged(Location location) {
	}

	public void onProviderDisabled(String provider) {
	}

	public void onProviderEnabled(String provider) {
	}

	public void onStatusChanged(String provider, int status, Bundle extras) {
	}

    /**
     * 定位变动回调方法，在此绘制轨迹
     *
     * @param aLocation AMapLocation类型 地位信息
     */
	public void onLocationChanged(AMapLocation aLocation) {
		if (mListener != null && aLocation != null) {
			mListener.onLocationChanged(aLocation);
			if (distance >= 0 && distance <= 500) {
				changeCamera(CameraUpdateFactory.zoomTo(18), null);
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        aLocation.getLatitude(), aLocation.getLongitude()), 18));
			} else if (distance > 500 && distance <= 1000) {
				changeCamera(CameraUpdateFactory.zoomTo(17), null);
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        aLocation.getLatitude(), aLocation.getLongitude()), 17));
			} else if (distance > 1000 && distance <= 2000) {
				changeCamera(CameraUpdateFactory.zoomTo(16), null);
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        aLocation.getLatitude(), aLocation.getLongitude()), 16));
			} else if (distance > 2000 && distance <= 4000) {
				changeCamera(CameraUpdateFactory.zoomTo(15), null);
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        aLocation.getLatitude(), aLocation.getLongitude()), 15));
			} else if (distance > 4000 && distance <= 10000) {
				changeCamera(CameraUpdateFactory.zoomTo(14), null);
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        aLocation.getLatitude(), aLocation.getLongitude()), 14));
			} else if (distance > 10000 && distance <= 20000) {
				changeCamera(CameraUpdateFactory.zoomTo(13), null);
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        aLocation.getLatitude(), aLocation.getLongitude()), 13));
			} else if (distance > 20000 && distance <= 40000) {
				changeCamera(CameraUpdateFactory.zoomTo(12), null);
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        aLocation.getLatitude(), aLocation.getLongitude()), 12));
			} else if (distance > 40000 && distance <= 100000) {
				changeCamera(CameraUpdateFactory.zoomTo(11), null);
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        aLocation.getLatitude(), aLocation.getLongitude()), 11));
			} else {
				changeCamera(CameraUpdateFactory.zoomTo(10), null);
				aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
                        aLocation.getLatitude(), aLocation.getLongitude()), 10));
			}
			if (location_change_count < 2) {
				firstpoint = new LatLonPoint(aLocation.getLatitude(),
						aLocation.getLongitude());

				geocoderSearch = new GeocodeSearch(this);
				geocoderSearch.setOnGeocodeSearchListener(this);
                // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
				RegeocodeQuery query = new RegeocodeQuery(firstpoint, 200,
						GeocodeSearch.AMAP);
				geocoderSearch.getFromLocationAsyn(query);

				oldpoint = new LatLng(aLocation.getLatitude(),
						aLocation.getLongitude());
				location_change_count++;
			} else {
				newpoint = new LatLng(aLocation.getLatitude(),
						aLocation.getLongitude());
				distance_for_two_second = AMapUtils.calculateLineDistance(
                        oldpoint, newpoint);
				if (distance_for_two_second < 50) {
					aMap.addPolyline((new PolylineOptions())
							.add(oldpoint, newpoint).width(4).color(Color.RED));
					distance += distance_for_two_second;

                    mDistance = Math.round(distance);

                    if(mDistance < 1000){
                        tvDistanceLabel.setText("米");
                        distance_count.setText(mDistance+"");
                    }else{
                        tvDistanceLabel.setText("公里");
                        distance_count.setText(mConvertTool.parseMeterToFormat(mDistance));
                    }

					speed_count.setText(""
							+ (float) (Math
									.round(distance_for_two_second * 10 * 1.8))
							/ 10);
					oldpoint = newpoint;
				} else {
					location_change_count = 0;
				}
			}
		}
//		aMap.invalidate();
	}

    /**
     * 获得AMapManager实例
     *
     * @param listener 定位变化接口
     */
	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;

		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);

			mAMapLocationManager.requestLocationData(
					LocationManagerProxy.GPS_PROVIDER, 2000, 1, this);
		}
	}

    /**
     * 销毁定位管理
     */
	@Override
	public void deactivate() {
		mListener = null;
		if (mAMapLocationManager != null) {
			// mAMapLocationManager.removeUpdates(this);
			mAMapLocationManager.destroy();
		}
		mAMapLocationManager = null;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
//		getMenuInflater().inflate(R.menu.step_count, menu);---------->
		return true;
	}


    /**
     * 发送更新界面请求线程
     */
	private Runnable mRunnable = new Runnable() {
		public void run() {
			while (true) {
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
     * 处理更新界面线程发来的信息
     */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			refreshUI();
		}
	};

    /**
     * 更新界面，主要更新卡路里数据
     */
	private void refreshUI() {
		cal = time / 60 * 7;
		calView.setText( cal + "" );
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

    /**
     * 停止运动时弹出的提示框
     */
    protected void dialog() {

        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                RunBikeCount.this);

        alertDialog.setMessage("停止运动记录?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();

                        // pause();
                        deactivate();
//                        unbindService(mConnection);
                        Intent intent = new Intent();
                        intent.setClass(RunBikeCount.this, MainActivity.class);
                        startActivity(intent);
                        RunBikeCount.this.finish();
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
    /**
     * 计时线程
     */
    Runnable runnable = new Runnable() {
		@SuppressLint("HandlerLeak")
		@Override
		public void run() {
			time++;
            timeShowView = mConvertTool.parseSecondToTimeFormat(time+"");
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

	@Override
	public void onGeocodeSearched(GeocodeResult arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRegeocodeSearched(RegeocodeResult result, int rCode) {
		// TODO Auto-generated method stub
		if (rCode == 0) {
			if (result != null && result.getRegeocodeAddress() != null
					&& result.getRegeocodeAddress().getFormatAddress() != null) {
				addressName = result.getRegeocodeAddress().getCity();
				province = addressName;
			} else {
			}
		} else {
			ToastUtils.show(RunBikeCount.this, "网络错误,获取当前位置失败");
		}
	}

}

package sysu.project.lee.sportslife.Excercise;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
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

import sysu.project.lee.sportslife.Utils.ToastUtils;
import sysu.project.lee.sportslife.Utils.mConvertTool;
import sysu.project.lee.sportslife.Utils.mHelper;
import sysu.project.lee.sportslife.UI.MainActivity;
import sysu.project.lee.sportslife.R;

@SuppressLint("HandlerLeak")
public class StepCount extends Activity implements LocationSource,
        AMapLocationListener, OnMapScreenShotListener, OnGeocodeSearchListener {
	private TextView m_TextView, timeView, calView;
	private TextView distance_count, speed_count;
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

	// public static final int LOCATION_TYPE_MAP_FOLLOW = 2;
	// private TextView nameTextView;
	// private SeekBar seekBar;
	// private ListView listView;
	// private List<Map<String, String>> data;
	// private int current;
	// private MediaPlayer player;
	// private Handler handlerM = new Handler();
	// private Button ppButton;
	// private boolean isPause = false;
	// private boolean isStartTrackingTouch;
	// private PhoneListener phonelistener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		setContentView(R.layout.activity_step_count);


        mapView = (MapView) findViewById(R.id.map);
        mapView.onCreate(savedInstanceState);
        initMap();


        appHelper = (mHelper) getApplicationContext();
        m_TextView = (TextView) findViewById(R.id.stepTextView);
        timeView = (TextView) findViewById(R.id.timeTextView);
        calView = (TextView) findViewById(R.id.calTextView);
        button_stop = (Button) findViewById(R.id.stopRunButton);
        distance_count = (TextView) findViewById(R.id.distanceCountView);
        speed_count = (TextView) findViewById(R.id.speedViewText);
        time = cal = 0;

        // nameTextView = (TextView) findViewById(R.id.name);
        // seekBar = (SeekBar) findViewById(R.id.seekBar);
        // listView = (ListView) findViewById(R.id.list);
        // ppButton = (Button) findViewById(R.id.pp);
        // //创建一个音乐播放器
        // player = new MediaPlayer();
        // //显示音乐播放列表
        // generateListView();
        // //进度条监听器
        // seekBar.setOnSeekBarChangeListener(new MySeekBarListener());
        // //播放器监听器
        // player.setOnCompletionListener(new MyPlayerListener());
        // //意图过滤器
        // IntentFilter filter = new IntentFilter();
        // //播出电话暂停音乐播放
        // filter.addAction("android.intent.action.NEW_OUTGOING_CALL");
        // phonelistener = new PhoneListener();
        // //创建一个电话服务
        // getApplicationContext().registerReceiver(phonelistener, filter);
        // TelephonyManager manager = (TelephonyManager)
        // getSystemService(TELEPHONY_SERVICE);
        // //监听电话状态，接电话时停止播放
        // manager.listen(new MyPhoneStateListener(),
        // PhoneStateListener.LISTEN_CALL_STATE);


		bindStepService();

		new Thread(mRunnable).start();

		handler.postDelayed(runnable, 1000);

		button_stop.setOnClickListener(new Button.OnClickListener() {
			public void onClick(View v) {
				unbindService(mConnection);
				if (appHelper != null) {
					if (appHelper.getEtype() == 2) {
						Exercise exercise = appHelper.getCurrExercise();
						exercise.setTotalCount(appHelper.getStep());
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
						intent.setClass(StepCount.this, StepResultShow.class);
						intent.putExtra("screen_shot_name",
								screen_shot_image_path);
						intent.putExtra("Province", province);
						startActivity(intent);
						StepCount.this.finish();
					}
				};
				Timer timer = new Timer();
				timer.schedule(task, 1000);
				// pause();
				deactivate();//地图释放

			}
		});
	}

	private void changeCamera(CameraUpdate update, CancelableCallback callback) {
		aMap.animateCamera(update, 1000, callback);
	}

	private void initMap() {
		if (aMap == null) {
			aMap = mapView.getMap();
			setUpMap();
		}
	}

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
		// aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
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
		// aMap.invalidate();// ˢ�µ�ͼ
	}

	@SuppressLint("SimpleDateFormat")
	public void onMapScreenShot(Bitmap bitmap) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		try {
			File sdcardDir = Environment.getExternalStorageDirectory();
			path = sdcardDir.getPath() + "/screenShot";
			File path_temp = new File(path);
			if (!path_temp.exists()) {
				path_temp.mkdirs();
			}
			FileOutputStream fos = new FileOutputStream(path_temp
					+ "/IRunning_" + sdf.format(new Date()) + ".png");
			screen_shot_image_path = path_temp + "/IRunning_"
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

			/*
			 * if (b) ToastUtil.show(StepCount.this, "截屏成功"); else {
			 * ToastUtil.show(StepCount.this, "截屏失败"); }
			 */

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
					distance_count.setText(""
							+ (float) (Math.round(distance * 10)) / 10);
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

	@Override
	public void activate(OnLocationChangedListener listener) {
		mListener = listener;

		if (mAMapLocationManager == null) {
			mAMapLocationManager = LocationManagerProxy.getInstance(this);

			mAMapLocationManager.requestLocationData(
					LocationManagerProxy.GPS_PROVIDER, 2000, 1, this);
		}
	}

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

	private ServiceConnection mConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			((StepService.StepBinder) service).getService();
		}

		public void onServiceDisconnected(ComponentName className) {
		}
	};

	private void bindStepService() {
		startService(new Intent(StepCount.this, StepService.class));
		bindService(new Intent(StepCount.this, StepService.class), mConnection,
				Context.BIND_AUTO_CREATE + Context.BIND_DEBUG_UNBIND);
	}

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

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			refreshUI();
		}
	};

	private void refreshUI() {
		step = appHelper.getStep();
		m_TextView.setText("" + step);
		cal = step / 10;
		calView.setText("" + cal);
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
                StepCount.this);

        alertDialog.setMessage("停止运动记录?")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        // pause();
                        deactivate();
                        unbindService(mConnection);
                        Intent intent = new Intent();
                        intent.setClass(StepCount.this, MainActivity.class);
                        startActivity(intent);
                        StepCount.this.finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).show();
    }

    private String timeShowView;

    Handler handler = new Handler();
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

	/*
	 * 监听电话状态
	 */
    // private final class MyPhoneStateListener extends PhoneStateListener {
    // public void onCallStateChanged(int state, String incomingNumber) {
    // pause();
    // }
    // }
	/*
	 * 播放器监听器
	 */
    // private final class MyPlayerListener implements OnCompletionListener {
    // public void onCompletion(MediaPlayer mp) {
    // next();
    // }
    // }

    // 下一首按钮
    // public void next(View view) {
    // next();
    // }

    // 上一首按钮
    // public void previous(View view) {
    // previous();
    // }

    // 播放上一首
    // private void previous() {
    // current = current - 1 < 0 ? data.size() - 1 : current - 1;
    // play();
    // }

    // 播放下一首
    // private void next() {
    // current = (current + 1) % data.size();
    // play();
    // }

	/*
	 * 进度条监听器
	 */
    // private final class MySeekBarListener implements OnSeekBarChangeListener
    // {
    // //移动触发
    // public void onProgressChanged(SeekBar seekBar, int progress,
    // boolean fromUser) {
    // }
    // //起始触发
    // public void onStartTrackingTouch(SeekBar seekBar) {
    // isStartTrackingTouch = true;
    // }
    // //结束触发
    // public void onStopTrackingTouch(SeekBar seekBar) {
    // player.seekTo(seekBar.getProgress());
    // isStartTrackingTouch = false;
    // }
    // }

	/*
	 * 显示音乐播放列表
	 */
    // private void generateListView() {
    // List<File> list = new ArrayList<File>();
    // //获取sdcard中的所有歌曲
    // findAll(Environment.getExternalStorageDirectory(), list);
    // //播放列表进行排序，字符顺序
    // Collections.sort(list);
    // data = new ArrayList<Map<String, String>>();
    // for (File file : list) {
    // Map<String, String> map = new HashMap<String, String>();
    // map.put("name", file.getName());
    // map.put("path", file.getAbsolutePath());
    // data.add(map);
    // }
    // SimpleAdapter adapter = new SimpleAdapter(this, data, R.layout.item,
    // new String[] { "name" }, new int[] { R.id.mName });
    // listView.setAdapter(adapter);
    // listView.setOnItemClickListener(new MyItemListener());
    // }
    //
    //
    // private final class MyItemListener implements OnItemClickListener {
    // public void onItemClick(AdapterView<?> parent, View view, int position,
    // long id) {
    // current = position;
    // play();
    // }
    // }
    //
    // //播放
    // private void play() {
    // try {
    // //重播
    // player.reset();
    // //获取歌曲路径
    // player.setDataSource(data.get(current).get("path"));
    // //缓冲
    // player.prepare();
    // //开始播放
    // player.start();
    // //显示歌名
    // nameTextView.setText(data.get(current).get("name"));
    // //设置进度条长度
    // seekBar.setMax(player.getDuration());
    // ppButton.setBackgroundResource(R.drawable.stopmusic);
    // //发送一个Runnable, handler收到之后就会执行run()方法
    // handlerM.post(new Runnable() {
    // public void run() {
    // // 更新进度条状态
    // if (!isStartTrackingTouch)
    // seekBar.setProgress(player.getCurrentPosition());
    // // 1秒之后再次发送
    // handlerM.postDelayed(this, 1000);
    // }
    // });
    // } catch (Exception e) {
    // e.printStackTrace();
    // }
    // }
    //
    // /**
    // * 查找文件路径中所有mp3文件
    // * @param file 要找的目录
    // * @param list 用来装文件的List
    // */
    // private void findAll(File file, List<File> list) {
    // File[] subFiles = file.listFiles();
    // if (subFiles != null)
    // for (File subFile : subFiles) {
    // if (subFile.isFile() && subFile.getName().endsWith(".mp3"))
    // list.add(subFile);
    // else if (subFile.isDirectory())
    // findAll(subFile, list);
    // }
    // }
    //
    // /*
    // * 暂停/播放按钮
    // */
    // public void pp(View view) {
    // //默认从第一首歌曲开始播放
    // if (!player.isPlaying() && !isPause) {
    // play();
    // return;
    // }
    // Button button = (Button) view;
    // //暂停/播放按钮
    // if (!isPause) {
    // pause();
    // button.setBackgroundResource(R.drawable.playmusic);
    // } else {
    // resume();
    // button.setBackgroundResource(R.drawable.stopmusic);
    // }
    // }
    //
    // /*
    // * 开始操作
    // */
    // private void resume() {
    //
    // if (isPause) {
    // player.start();
    // isPause = false;
    // }
    // }
    //
    // //暂停
    // private void pause() {
    // if (player != null && player.isPlaying()) {
    // player.pause();
    // isPause = true;
    // }
    // }
    // /*
    // * 收到广播时暂停
    // */
    // private final class PhoneListener extends BroadcastReceiver {
    // public void onReceive(Context context, Intent intent) {
    // pause();
    // }
    // }

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
			ToastUtils.show(StepCount.this, "网络错误,获取当前位置失败");
		}
	}

}

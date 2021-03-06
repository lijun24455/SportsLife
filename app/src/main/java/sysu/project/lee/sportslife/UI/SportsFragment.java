package sysu.project.lee.sportslife.UI;


import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.amap.api.location.AMapLocalWeatherForecast;
import com.amap.api.location.AMapLocalWeatherListener;
import com.amap.api.location.AMapLocalWeatherLive;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.LocationManagerProxy;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.LocationSource;
import com.amap.api.maps.MapView;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import sysu.project.lee.sportslife.Database.HistoryDBHelper;
import sysu.project.lee.sportslife.Database.HistoryRealize;
import sysu.project.lee.sportslife.Database.HistoryService;
import sysu.project.lee.sportslife.Excercise.Exercise;
import sysu.project.lee.sportslife.Excercise.RunBikeCount;
import sysu.project.lee.sportslife.Excercise.SkipCount;
import sysu.project.lee.sportslife.Excercise.StepCount;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.User.UserEntity;
import sysu.project.lee.sportslife.Utils.ToastUtils;
import sysu.project.lee.sportslife.Utils.mConvertTool;
import sysu.project.lee.sportslife.Utils.mHelper;

public class SportsFragment extends Fragment implements LocationSource, AMapLocalWeatherListener, AMapLocationListener{
    private MapView mapView;
    private AMap aMap;
    private LocationManagerProxy mAMapLocationManager;
    private OnLocationChangedListener mListener;
    private TextView mWeather;
    private TextView mAirHumidity;
    private TextView mTotalUseTimesTody;
    private TextView mTotalTimeSpendTody;
    private TextView mExerciseType;
    private ImageView mExerciseTypeImg;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private int mHour = 0;
    private int mMinute = 0;
    private StringBuilder mdate = null;
    private StringBuilder mtime = null;
    private int totalUseCount = 0;
    private int totalTimesSpendCount = 0;
    private Button mStartButton;
    private String eDest, eTime;
    private int eType = 0;

    private mHelper mSportsLifeHelper = null;
    private UserEntity mCurrentUser = null;

    private PopupMenu mExerciseTypePopupMenu;
    private RelativeLayout btnChooseExerciseType;
    private int mCurrentUserId = 0;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("fragmentLife", "onActivityCreated()");
        mCurrentUser = (UserEntity) getActivity().getIntent().getSerializableExtra("USER_INFO");
        mCurrentUserId = mCurrentUser.getId();
        mWeather = (TextView) getView().findViewById(R.id.weatherViewText);
        mAirHumidity = (TextView) getView().findViewById(R.id.airHumidityViewText);
        mapView = (MapView) getView().findViewById(R.id.mapAtStart);
        mTotalTimeSpendTody = (TextView) getView().findViewById(R.id.todayTotalTime);
        mTotalUseTimesTody = (TextView) getView().findViewById(R.id.todayTotalDis);
        mStartButton = (Button) getView().findViewById(R.id.btn_start);
        mSportsLifeHelper = (mHelper) getActivity().getApplicationContext();
        mSportsLifeHelper.setCurrentUser(mCurrentUser);
        mExerciseType = (TextView) getView().findViewById(R.id.tv_exercise_type);
        btnChooseExerciseType = (RelativeLayout) getView().findViewById(R.id.btn_choose_exercise_type);
        mExerciseTypeImg = (ImageView) getView().findViewById(R.id.iv_exercise_type_img);


        reFreshUI();

        if (!isGPSOpen(this.getActivity())) {

            ToastUtils.show(getActivity(),"请开启GPS定位");
            Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(intent, 0);
        }

        if (!isNetworkConnected(this.getActivity())) {

            ToastUtils.show(getActivity(),"请开启网络连接");
            Intent intent = new Intent(Settings.ACTION_DATA_ROAMING_SETTINGS);
            startActivityForResult(intent, 0);
        }

        mapView.onCreate(savedInstanceState);
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        setUpMap();
        
        final Calendar calendar = Calendar.getInstance();
        
        mYear = calendar.get(Calendar.YEAR);
        mMonth = calendar.get(Calendar.MONTH);
        mDay = calendar.get(Calendar.DAY_OF_MONTH);
        mHour = calendar.get(Calendar.HOUR_OF_DAY);
        mMinute = calendar.get(Calendar.MINUTE);
        mdate = new StringBuilder().append(mYear).append("-")
                .append((mMonth + 1) < 10 ? "0" + (mMonth + 1) : (mMonth + 1))
                .append("-").append((mDay < 10) ? "0" + mDay : mDay);

        mtime = new StringBuilder().append(mHour).append(":")
                .append((mMinute < 10) ? "0" + mMinute : mMinute);


        HistoryDBHelper helper = new HistoryDBHelper(getActivity()); // 建立数据库
        helper.getWritableDatabase();

        reFreshTimeCountCard();

        eDest = "test";

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTime = mdate.toString() + "  " + mtime.toString();
//                eType = mSportsLifeHelper.getEtype();

//                Log.i("btn","eType-->"+eType+"-->eDest"+eDest);
                Log.i("Type","type--click->"+eType);

                Exercise currentExercise = new Exercise(eType, eTime, eDest);
                try {
                    mSportsLifeHelper.setCurrentExercise(currentExercise);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent();
                switch(eType){
                    case 0:
                    case 1:
                        intent.setClass(getActivity(), RunBikeCount.class);
                        break;
                    case 2:
                        intent.setClass(getActivity(), StepCount.class);
                        break;
                    case 3:
                        intent.setClass(getActivity(), SkipCount.class);
                        break;
                }
                startActivity(intent);
            }
        });

        btnChooseExerciseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExerciseTypePopupMenu = new PopupMenu(getActivity(), v);
                mExerciseTypePopupMenu.getMenuInflater().inflate(R.menu.exercise_type, mExerciseTypePopupMenu.getMenu());
                mExerciseTypePopupMenu.show();
                mExerciseTypePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.type_run:
                                eType = 0;
                                break;
                            case R.id.type_bike:
                                eType = 1;
                                break;
                            case R.id.type_step:
                                eType = 2;
                                break;
                            case R.id.type_skip:
                                eType = 3;
                                break;
                        }
                        mSportsLifeHelper.setEtype(eType);
                        reFreshUI();
                        Log.i("Type","type--->"+mSportsLifeHelper.getEtype());
                        return true;
                    }
                });

            }
        });

    }

    private void reFreshTimeCountCard() {
        HistoryService service = new HistoryRealize(getActivity());
        String mArgs[] = {mCurrentUserId+""};
        List<Map<String, String>> list = service.listHistoryByUserID(mArgs);

        totalTimesSpendCount = 0;
        totalUseCount = 0;
        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                String[] datePartOne;
                datePartOne = list.get(i).get("date").split(" ");
                if (mdate.toString().equals(datePartOne[0])) {
                    totalUseCount ++;
                    totalTimesSpendCount += Integer.parseInt(list.get(i).get("time"));
                }
            }
        }
        mTotalUseTimesTody.setText(String.valueOf(totalUseCount));
        mTotalTimeSpendTody.setText(mConvertTool.parseSecondToTimeFormat(String.valueOf(totalTimesSpendCount)));
    }

    private void reFreshUI() {
        switch (eType){
            case 0:
                mExerciseType.setText("跑步");
                mExerciseTypeImg.setImageResource(R.drawable.ic_run_label);
                break;
            case 1:
                mExerciseType.setText("骑行");
                mExerciseTypeImg.setImageResource(R.drawable.ic_bike_label);
                break;
            case 2:
                mExerciseType.setText("步行");
                mExerciseTypeImg.setImageResource(R.drawable.ic_step_label);
                break;
            case 3:
                mExerciseType.setText("跳绳");
                mExerciseTypeImg.setImageResource(R.drawable.ic_skip_label);
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.i("fragmentLife","onResume()");
        mapView.onResume();
        reFreshTimeCountCard();
    }

    @Override
    public void onPause() {
        mapView.onPause();
        super.onPause();
        Log.i("fragmentLife", "onPause()");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.i("fragmentLife","onSaveInstanceState()");
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.i("fragmentLife","onDestroy()");
        mapView.onDestroy();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("fragmentLife", "onCreate()");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i("fragmentLife","onStart()");
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
        aMap.setMyLocationType(AMap.LOCATION_TYPE_MAP_ROTATE);
        aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.0515,
                113.3928), 14));
    }

    public boolean isNetworkConnected(Context context) {
        if (context != null) {
            ConnectivityManager mConnectivityManager = (ConnectivityManager) context
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo mNetworkInfo = mConnectivityManager
                    .getActiveNetworkInfo();
            if (mNetworkInfo != null) {
                return mNetworkInfo.isAvailable();
            }
        }
        return false;
    }

    public static final boolean isGPSOpen(final Context context) {
        LocationManager locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);

        boolean gps = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);
        if (gps) {
            return true;
        }
        return false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_sports, container, false);

    }

    @Override
    public void activate(OnLocationChangedListener onLocationChangedListener) {
        mListener = onLocationChangedListener;

        if (mAMapLocationManager == null) {
            mAMapLocationManager = LocationManagerProxy.getInstance(getActivity());

            mAMapLocationManager.requestLocationData(
                    LocationManagerProxy.GPS_PROVIDER, 2000, 1,
                    this);
        }
        mAMapLocationManager.requestWeatherUpdates(
                LocationManagerProxy.WEATHER_TYPE_LIVE, this);
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
    public void onWeatherLiveSearched(AMapLocalWeatherLive aMapLocalWeatherLive) {
        if (aMapLocalWeatherLive != null
                && aMapLocalWeatherLive.getAMapException().getErrorCode() == 0) {
            String weather = aMapLocalWeatherLive.getWeather();
            String humidity = aMapLocalWeatherLive.getHumidity();
            Log.i("weather","------------>"+weather);
            Log.i("weather","------------>"+humidity);

            mWeather.setText(weather);
            mAirHumidity.setText(humidity);
        } else {
            ToastUtils.show(getActivity(), "天气获取失败原因:"
                    + aMapLocalWeatherLive.getAMapException()
                    .getErrorMessage());
        }
    }

    @Override
    public void onWeatherForecaseSearched(AMapLocalWeatherForecast aMapLocalWeatherForecast) {

    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            mListener.onLocationChanged(aMapLocation);
//            changeCamera(CameraUpdateFactory.zoomTo(18), null);
//            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(
//            aLocation.getLatitude(), aLocation.getLongitude()), 18));
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

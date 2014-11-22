package sysu.project.lee.sportslife;


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
import android.widget.ImageButton;
import android.widget.PopupMenu;
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
import sysu.project.lee.sportslife.Excercise.StepCount;
import sysu.project.lee.sportslife.Utils.ToastUtils;
import sysu.project.lee.sportslife.Utils.mHelper;

public class SportsFragment extends Fragment implements LocationSource, AMapLocalWeatherListener, AMapLocationListener{
    private MapView mapView;
    private AMap aMap;
    private LocationManagerProxy mAMapLocationManager;
    private OnLocationChangedListener mListener;
    private TextView mWeather;
    private TextView mAirHumidity;
    private TextView mTotalDistanceTody;
    private TextView mTotalTimeTody;
    private int mYear = 0;
    private int mMonth = 0;
    private int mDay = 0;
    private int mHour = 0;
    private int mMinute = 0;
    private StringBuilder mdate = null;
    private StringBuilder mtime = null;
    private int Total_distance = 0;
    private int Total_time = 0;
    private Button mStartButton;
    private String eType, eDest, eTime;

    private mHelper mSportsLifeHelper = null;

    private ImageButton btnChooseExerciseType = null;
    private PopupMenu mExerciseTypePopupMenu = null;
    private String chosenType;

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i("fragmentLife","onActivityCreated()");
        mWeather = (TextView) getView().findViewById(R.id.weatherViewText);
        mAirHumidity = (TextView) getView().findViewById(R.id.airHumidityViewText);
        mapView = (MapView) getView().findViewById(R.id.mapAtStart);
        mTotalTimeTody = (TextView) getView().findViewById(R.id.todayTotalTime);
        mTotalDistanceTody = (TextView) getView().findViewById(R.id.todayTotalDis);
        mStartButton = (Button) getView().findViewById(R.id.btn_start);
        mSportsLifeHelper = (mHelper) getActivity().getApplicationContext();
        btnChooseExerciseType = (ImageButton) getView().findViewById(R.id.btn_choose_exercise_type);


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

        HistoryService service = new HistoryRealize(getActivity());
        List<Map<String, String>> list = service.listHistoryMaps(null);

        if (!list.isEmpty()) {
            for (int i = 0; i < list.size(); ++i) {
                String[] datePartOne;
                datePartOne = list.get(i).get("date").split(" ");
                if (mdate.toString().equals(datePartOne[0])) {
                    Total_distance += Integer.parseInt(list.get(i).get(
                            "distance"));
                    Total_time += Integer.parseInt(list.get(i).get("time"));
                }
            }
        }
        mTotalDistanceTody.setText(String.valueOf(Total_distance));
        mTotalTimeTody.setText(String.valueOf(Total_time));

        eType = "Run";
        eDest = "ZSDX";

        mStartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                eTime = mdate.toString() + "  " + mtime.toString();

                Log.i("btn","eType-->"+eType+"-->eDest"+eDest);

                Exercise oneExercise = new Exercise(eType, eTime, eDest);
                try {
                    mSportsLifeHelper.setCurrentExercise(oneExercise);
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                }

                mSportsLifeHelper.setEtype(0);

                Intent intent = new Intent();
                intent.setClass(getActivity(), StepCount.class);
                startActivity(intent);
            }
        });

        btnChooseExerciseType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mExerciseTypePopupMenu = new PopupMenu(getActivity(),v);
                mExerciseTypePopupMenu.getMenuInflater().inflate(R.menu.exercise_type, mExerciseTypePopupMenu.getMenu());
                mExerciseTypePopupMenu.show();
                mExerciseTypePopupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {

                        int tmp = item.getItemId();
                        Log.i("chose","----->"+tmp);
                        return true;
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i("fragmentLife","onResume()");
        mapView.onResume();
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

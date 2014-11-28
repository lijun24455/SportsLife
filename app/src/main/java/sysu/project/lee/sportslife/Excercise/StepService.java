package sysu.project.lee.sportslife.Excercise;

import android.app.Service;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.IBinder;
import android.util.Log;

import sysu.project.lee.sportslife.Utils.mHelper;

public class StepService extends Service {
	private SensorManager mSensorManager;
	private Sensor mSensor;
	private StepDetector mStepDetector;
	private StepDisplayer mStepDisplayer;
	private int mSteps;
	private mHelper appHealthHelper;

	public class StepBinder {
		StepService getService() {
			return StepService.this;
		}
	}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();

        Log.i("StepService","------>onCreate()");

		appHealthHelper = (mHelper) getApplicationContext();

		mStepDetector = new StepDetector();
		mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		registerDetector();

		mStepDisplayer = new StepDisplayer();
		mStepDisplayer.setSteps(0);
		mStepDisplayer.addListener(mStepListener);

		mStepDetector.addStepListener(mStepDisplayer);

	}

	@SuppressWarnings("deprecation")
	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
        appHealthHelper.setNum(0);
        appHealthHelper.setStep(0);
        Log.i("StepService","------>onStart()");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("StepService","------>oStartCommand()");
        return super.onStartCommand(intent, flags, startId);
	}

    @Override
    public boolean onUnbind(Intent intent) {
        Log.i("StepService", "------>onUnbind()");
        this.stopSelf();
        return super.onUnbind(intent);
    }

    @Override
	public void onDestroy() {
		unregisterDetector();
        Log.i("StepService", "------>onDestroy()--->unregisterDetector");
        super.onDestroy();
	}

	private void registerDetector() {
		mSensor = mSensorManager
				.getDefaultSensor(Sensor.TYPE_ACCELEROMETER /*
															 * | Sensor.
															 * TYPE_MAGNETIC_FIELD
															 * | Sensor.
															 * TYPE_ORIENTATION
															 */);
		mSensorManager.registerListener(mStepDetector, mSensor,
				SensorManager.SENSOR_DELAY_FASTEST);
	}

	private void unregisterDetector() {
		mSensorManager.unregisterListener(mStepDetector);
	}

	public interface ICallback {
		public void stepsChanged(int value);

		public void paceChanged(int value);

		public void distanceChanged(float value);

		public void speedChanged(float value);

		public void caloriesChanged(float value);
	}

	private ICallback mCallback;

	public void registerCallback(ICallback cb) {
		mCallback = cb;
	}

	private StepDisplayer.Listener mStepListener = new StepDisplayer.Listener() {
		public void stepsChanged(int value) {
			mSteps = value;
			appHealthHelper.setStep(mSteps);
			passValue();
		}

		public void passValue() {
			if (mCallback != null) {
				mCallback.stepsChanged(mSteps);
			}
		}
	};
}

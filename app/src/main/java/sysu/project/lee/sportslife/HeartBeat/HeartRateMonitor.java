package sysu.project.lee.sportslife.HeartBeat;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import sysu.project.lee.sportslife.R;


/**
 * This class extends Activity to handle a picture preview, process the preview
 * for a red values and determine a heart beat.
 * 
 * @author Justin Wetherell <phishman3579@gmail.com>
 */
public class HeartRateMonitor extends Activity {

    private static final String TAG = "HeartRateMonitor";
    private static final int STOP_MEASURE = 1;
    private static final AtomicBoolean processing = new AtomicBoolean(false);

    private static SurfaceView preview = null;
    private static SurfaceHolder previewHolder = null;
    private static Camera camera = null;
    private static View image = null;
    private static TextView text = null;
    private static ImageView btnNaviBack = null;

    private Button btnStartMeasure = null;
    private Button btnRestartMeasure = null;

    private static int averageIndex = 0;
    private static final int averageArraySize = 4;
    private static final int[] averageArray = new int[averageArraySize];

    public static enum TYPE {
        GREEN, RED
    };

    private static TYPE currentType = TYPE.GREEN;

    public static TYPE getCurrent() {
        return currentType;
    }

    private static int beatsIndex = 0;
    private static final int beatsArraySize = 3;
    private static int[] beatsArray = null;
    private static double beats = 0;
    private static long startTime = 0;

    Runnable mRun = new Runnable() {
        @Override
        public void run() {
            try {
                Log.i("HeartBeat","---Thread name-->"+Thread.currentThread().getName());
                Thread.sleep(20*1000);
                Message msg = new Message();
                msg.what = STOP_MEASURE;
                handler.sendMessage(msg);

            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };

//    private Thread mThread = new Thread(new Runnable() {
//        @Override
//        public void run() {
//            while(isRunning){
//                try {
//                    Log.i("HeartBeat","---Thread name-->"+Thread.currentThread().getName());
//                    Thread.sleep(20*1000);
//                    Message msg = new Message();
//                    msg.what = STOP_MEASURE;
//                    handler.sendMessage(msg);
//                    isRunning = false;
//
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//            }
//
//        }
//    });

    private String mHeartRate = null;

    private Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case STOP_MEASURE:
                    stopMeasure();
                    mHeartRate = (String) text.getText();
                    btnStartMeasure.setTextColor(getResources().getColor(R.color.white));
                    btnStartMeasure.setClickable(true);
                    btnRestartMeasure.setTextColor(getResources().getColor(R.color.white));
                    btnRestartMeasure.setClickable(true);
                    btnNaviBack.setClickable(true);
                    Log.i("HeartBeat", "------rate--->" + text.getText());
                    break;
            }
            return true;
        }
    });


    /**
     * {@inheritDoc}
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Heart","-------------->onCreate()");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_heartbeat_monitor);


        preview = (SurfaceView) findViewById(R.id.preview);
        previewHolder = preview.getHolder();
        previewHolder.addCallback(surfaceCallback);
        previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        image = findViewById(R.id.image);
        text = (TextView) findViewById(R.id.tv_heartbeat_count_show);
        btnStartMeasure = (Button) findViewById(R.id.btn_start_monitor);
        btnRestartMeasure = (Button) findViewById(R.id.btn_restart_monitor);
        btnNaviBack = (ImageView) findViewById(R.id.navi_back);

        camera = Camera.open();
        camera.setPreviewCallback(previewCallback);

        btnStartMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String btnLable = (String) btnStartMeasure.getText();
                if(btnLable.equals("开始")){
                    btnStartMeasure.setText("重测");
                    btnRestartMeasure.setVisibility(View.VISIBLE);
                    onMeasure();

                }
                if(btnLable.equals("重测")){

                    text.setText("00");

                    if (camera == null){
                        camera = Camera.open();
                        camera.setPreviewCallback(previewCallback);
                    }

                    onMeasure();
                }

            }
        });

        btnRestartMeasure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToResultShowPage();
            }
        });
        btnNaviBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHeartRate != null){
                    backToResultShowPage();
                }else{
                    new AlertDialog.Builder(HeartRateMonitor.this)
                            .setTitle("提示")
                            .setMessage("还未测试心率，确定要返回吗？")
                            .setPositiveButton("直接返回",new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    HeartRateMonitor.this.finish();
                                }
                            })
                            .setNegativeButton("取消", null)
                            .show();
                }
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("Heart","-------------->onStart()");


    }


    @Override
    protected void onRestart() {

        super.onRestart();
        Log.i("Heart", "-------------->onRestart()");

        new AlertDialog.Builder(this)
                .setTitle("重新测试")
                .setMessage("请用指腹遮住摄像头和闪光灯，确定后点击重测")
                .setPositiveButton("确定",new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onResume() {
        super.onResume();
        Log.i("Heart","-------------->onResume()");

    }

    private void onMeasure() {

        btnRestartMeasure.setClickable(false);
        btnRestartMeasure.setTextColor(getResources().getColor(R.color.gray));
        btnStartMeasure.setClickable(false);
        btnStartMeasure.setTextColor(getResources().getColor(R.color.gray));
        btnNaviBack.setClickable(false);


        beatsArray = new int[beatsArraySize];
        beatsIndex = 0;
        beats = 0;
        startTime = 0;

//        camera = Camera.open();

        startTime = System.currentTimeMillis();

        Camera.Parameters parameters = camera.getParameters();
        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
        Camera.Size size = getSmallestPreviewSize(9999, 9999, parameters);
        if (size != null) {
            parameters.setPreviewSize(size.width, size.height);
            Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
        }
        camera.setParameters(parameters);

        try {
            camera.setPreviewDisplay(previewHolder);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        camera.startPreview();

        new Thread(mRun).start();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onPause() {
        super.onPause();
//        stopMeasure();
        Log.i("Heart","-------------->onPause()");
        if(camera!=null){
            stopMeasure();
        }
    }

    private void stopMeasure() {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    private static PreviewCallback previewCallback = new PreviewCallback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void onPreviewFrame(byte[] data, Camera cam) {
            if (data == null) throw new NullPointerException();
            Camera.Size size = cam.getParameters().getPreviewSize();
            if (size == null) throw new NullPointerException();

            if (!processing.compareAndSet(false, true)) return;

            int width = size.width;
            int height = size.height;

            int imgAvg = ImageProcessing.decodeYUV420SPtoRedAvg(data.clone(), height, width);
            // Log.i(TAG, "imgAvg="+imgAvg);
            if (imgAvg == 0 || imgAvg == 255) {
                processing.set(false);
                return;
            }

            int averageArrayAvg = 0;
            int averageArrayCnt = 0;
            for (int i = 0; i < averageArray.length; i++) {
                if (averageArray[i] > 0) {
                    averageArrayAvg += averageArray[i];
                    averageArrayCnt++;
                }
            }

            int rollingAverage = (averageArrayCnt > 0) ? (averageArrayAvg / averageArrayCnt) : 0;
            TYPE newType = currentType;
            if (imgAvg < rollingAverage) {
                newType = TYPE.RED;
                if (newType != currentType) {
                    beats++;
                    // Log.d(TAG, "BEAT!! beats="+beats);
                }
            } else if (imgAvg > rollingAverage) {
                newType = TYPE.GREEN;
            }

            if (averageIndex == averageArraySize) averageIndex = 0;
            averageArray[averageIndex] = imgAvg;
            averageIndex++;

            // Transitioned from one state to another to the same
            if (newType != currentType) {
                currentType = newType;
                image.postInvalidate();
            }

            long endTime = System.currentTimeMillis();
            double totalTimeInSecs = (endTime - startTime) / 1000d;
            if (totalTimeInSecs >= 10) {
                double bps = (beats / totalTimeInSecs);
                int dpm = (int) (bps * 60d);
                if (dpm < 30 || dpm > 180) {
                    startTime = System.currentTimeMillis();
                    beats = 0;
                    processing.set(false);
                    return;
                }

                // Log.d(TAG,
                // "totalTimeInSecs="+totalTimeInSecs+" beats="+beats);

                if (beatsIndex == beatsArraySize) beatsIndex = 0;
                beatsArray[beatsIndex] = dpm;
                beatsIndex++;

                int beatsArrayAvg = 0;
                int beatsArrayCnt = 0;
                for (int i = 0; i < beatsArray.length; i++) {
                    if (beatsArray[i] > 0) {
                        beatsArrayAvg += beatsArray[i];
                        beatsArrayCnt++;
                    }
                }
                int beatsAvg = (beatsArrayAvg / beatsArrayCnt);
                text.setText(String.valueOf(beatsAvg));
                startTime = System.currentTimeMillis();
                beats = 0;
            }
            processing.set(false);
        }
    };

    private static SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
//            try {
//                camera.setPreviewDisplay(previewHolder);
//                camera.setPreviewCallback(previewCallback);
//            } catch (Throwable t) {
//                Log.e("PreviewDemo-surfaceCallback", "Exception in setPreviewDisplay()", t);
//            }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
//            Camera.Parameters parameters = camera.getParameters();
//            parameters.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//            Camera.Size size = getSmallestPreviewSize(width, height, parameters);
//            if (size != null) {
//                parameters.setPreviewSize(size.width, size.height);
//                Log.d(TAG, "Using width=" + size.width + " height=" + size.height);
//            }
//            camera.setParameters(parameters);
//            camera.startPreview();
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {

        }

    };


    private static Camera.Size getSmallestPreviewSize(int width, int height, Camera.Parameters parameters) {

        Camera.Size result = null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result = size;
                } else {
                    int resultArea = result.width * result.height;
                    int newArea = size.width * size.height;

                    if (newArea < resultArea) result = size;
                }
            }
        }

        return result;
    }

    private void backToResultShowPage(){
        Intent intent = new Intent();

        intent.putExtra("HEART",mHeartRate);

        HeartRateMonitor.this.setResult(RESULT_OK, intent);

        HeartRateMonitor.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i("Heart","-------------->onDestroy()");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            new AlertDialog.Builder(HeartRateMonitor.this)
                    .setTitle("提示")
                    .setMessage("还未测试心率，确定要返回吗？")
                    .setPositiveButton("直接返回",new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            HeartRateMonitor.this.finish();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        }
        return true;
    }
}

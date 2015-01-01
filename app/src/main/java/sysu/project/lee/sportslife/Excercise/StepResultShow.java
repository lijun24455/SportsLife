package sysu.project.lee.sportslife.Excercise;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import sysu.project.lee.sportslife.Database.HistoryDBHelper;
import sysu.project.lee.sportslife.Database.HistoryRealize;
import sysu.project.lee.sportslife.Database.HistoryService;
import sysu.project.lee.sportslife.HeartBeat.HeartRateMonitor;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.User.UserEntity;
import sysu.project.lee.sportslife.Utils.mConvertTool;
import sysu.project.lee.sportslife.Utils.mHelper;

/**
 * 步行运动结果数据展示类
 * Created by lee on 14年11月23日.
 */
public class StepResultShow extends Activity {

    private ImageView btnNaviBack;
    private TextView mDistanceResult, mCalResult, mHeartRateResult, mTimeResult, mStepResult;
    private TextView mDistanceResultLabel;
    private Button btnDeleteRecord, btnSaveRecord;
    private mHelper appSportsLifeHelper;

    private String mPlaceRecord = null;
    private String mTimeRecord = null;
    private String mTotalTimeRecord = null;
    private String mImageRecord = null;
    private String mTotalDistanceRecord = null;
    private int mCurrentTypeRecord = 2;
    private int mTotalCal = 0;
    private int mTotalStepCount = 0;
    private String mHeartRateRecord = null;
    private UserEntity mCurrentUser = null;
    private int mCurrentUserId = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_step_result_show);

        initView();

        HistoryDBHelper dbHelper = new HistoryDBHelper(this);
        dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        mPlaceRecord = intent.getStringExtra("Province");
        mTimeRecord = appSportsLifeHelper.getCurrExercise().getTime();
        mTotalTimeRecord = appSportsLifeHelper.getCurrExercise().getTotalTime() + "";
        mTotalDistanceRecord = appSportsLifeHelper.getCurrExercise().getTotalDistance() + "";
        mImageRecord = intent.getStringExtra("screen_shot_name");
        mCurrentTypeRecord = appSportsLifeHelper.getEtype();
        mTotalCal = appSportsLifeHelper.getCurrExercise().getTotalCal();
        mTotalStepCount = appSportsLifeHelper.getCurrExercise().getTotalCount();
        mCurrentUser = appSportsLifeHelper.getCurrentUser();
        mCurrentUserId = mCurrentUser.getId();



        final HistoryService dbService = new HistoryRealize(this);

        btnSaveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final Object[] recordItem = {
                        mCurrentUserId,
                        mCurrentTypeRecord,
                        mTimeRecord,
                        mTotalDistanceRecord,
                        mTotalTimeRecord,
                        mImageRecord,
                        mPlaceRecord,
                        mTotalCal,
                        mTotalStepCount,
                        mHeartRateRecord};
                dbInsertItem(dbService, recordItem);
            }
        });
        btnDeleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(StepResultShow.this);
                alertDialog
                        .setMessage("要删除此次运动记录吗?")
                        .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                appSportsLifeHelper.DisplayToast("删除成功");
                                dialog.dismiss();
//                                android.os.Process.killProcess(android.os.Process
//                                        .myPid());
                                backToMainActivity();

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });
        btnNaviBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(StepResultShow.this);
                alertDialog
                        .setMessage("未保存此次运动记录?")
                        .setPositiveButton("保存记录并退出", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                btnSaveRecord.callOnClick();
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("不保存并退出", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                appSportsLifeHelper.DisplayToast("记录未保存");
                                dialog.dismiss();
                                backToMainActivity();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        }).show();
            }
        });


        mHeartRateResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intentToBeat = new Intent();
//                intentToBeat.setClass(RunBikeResultShow.this, HeartRateMonitor.class);
                startActivityForResult(new Intent(StepResultShow.this, HeartRateMonitor.class),1);
            }
        });



    }

    /**
     *获得心跳检测界面返回的心跳数据
     *
     * @param requestCode   请求码，即调用startActivityForResult()传递过去的值
     * @param resultCode    结果码，结果码用于标识返回数据来自哪个新Activity
     * @param data      跳转意图，心跳检测界面跳转回来的意图
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data!=null && resultCode == RESULT_OK){
            mHeartRateRecord = data.getExtras().getString("HEART");

            if (mHeartRateRecord!=null){
                mHeartRateResult.setText(mHeartRateRecord);
            }
        }

    }

    /**
     * 向运动记录数据库中添加新的条目
     *
     * @param dbService     数据库实例
     * @param recordItem    字符串数组，运动记录参数
     */
    private void dbInsertItem(HistoryService dbService, Object[] recordItem) {
        if(dbService.Insert(recordItem)){
            appSportsLifeHelper.DisplayToast("记录保存成功!");
            backToMainActivity();
        }else{
            appSportsLifeHelper.DisplayToast("记录保存失败……");
        }
    }

    /**
     * 初始化界面控件
     */
    private void backToMainActivity() {
        StepResultShow.this.finish();
    }

    /**
     * 初始化界面View
     */
    private void initView() {
        appSportsLifeHelper = (mHelper) getApplicationContext();
        btnNaviBack = (ImageView) findViewById(R.id.navi_back);
        mDistanceResult = (TextView) findViewById(R.id.tv_distance_show);
        mCalResult = (TextView) findViewById(R.id.tv_cal_show);
        mHeartRateResult = (TextView) findViewById(R.id.tv_heartrate_show);
        mTimeResult = (TextView) findViewById(R.id.tv_time_show);
        btnDeleteRecord = (Button) findViewById(R.id.btn_delete_record);
        btnSaveRecord = (Button) findViewById(R.id.btn_save_record);
        mStepResult = (TextView) findViewById(R.id.tv_step_show);
        mDistanceResultLabel = (TextView) findViewById(R.id.tv_distance_show_label);

        String formatedTimeShow = mConvertTool.parseSecondToTimeFormat(appSportsLifeHelper.getCurrExercise().getTotalTime() + "");

        int distance = appSportsLifeHelper.getCurrExercise().getTotalDistance();

        if(distance<1000){
            mDistanceResult.setText(distance+"");
            mDistanceResultLabel.setText("米");
        }else {
            mDistanceResult.setText(mConvertTool.parseMeterToFormat(distance)+"");
            mDistanceResultLabel.setText("公里");
        }

        mCalResult.setText(appSportsLifeHelper.getCurrExercise().getTotalCal()+"大卡");
        mTimeResult.setText(formatedTimeShow);
        mStepResult.setText(appSportsLifeHelper.getCurrExercise().getTotalCount()+"");

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(StepResultShow.this);
            alertDialog
                    .setMessage("未保存此次运动记录?")
                    .setPositiveButton("保存记录并退出", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            btnSaveRecord.callOnClick();
                            dialog.dismiss();
                        }
                    })
                    .setNeutralButton("不保存并退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            appSportsLifeHelper.DisplayToast("记录未保存");
                            dialog.dismiss();
                            backToMainActivity();
                        }
                    })
                    .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    }).show();
        }
        return true;
    }
}

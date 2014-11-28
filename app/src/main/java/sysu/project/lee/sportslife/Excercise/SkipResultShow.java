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
import sysu.project.lee.sportslife.MainActivity;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.Utils.mConvertTool;
import sysu.project.lee.sportslife.Utils.mHelper;

/**
 * Created by lee on 14年11月23日.
 */
public class SkipResultShow extends Activity {

    private ImageView btnNaviBack;
    private TextView mSkipCountResult, mCalResult, mHeartRateResult, mTimeResult;
    private Button btnDeleteRecord, btnSaveRecord;
    private mHelper appSportsLifeHelper;

    private String mPlaceRecord = null;
    private String mTimeRecord = null;
    private String mTotalTimeRecord = null;
    private String mImageRecord = null;
    private String mTotalDistanceRecord = null;
    private int mCurrentTypeRecord = 3;
    private int mTotalStepCount = 0;
    private int mTotalCal = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_skip_result_show);

        initView();

        HistoryDBHelper dbHelper = new HistoryDBHelper(this);
        dbHelper.getWritableDatabase();

        Intent intent = getIntent();
        mPlaceRecord = intent.getStringExtra("Location");
        mTimeRecord = appSportsLifeHelper.getCurrExercise().getTime();
        mTotalTimeRecord = appSportsLifeHelper.getCurrExercise().getTotalTime() + "";
        mTotalDistanceRecord = appSportsLifeHelper.getCurrExercise().getTotalDistance() + "";
        mCurrentTypeRecord = appSportsLifeHelper.getEtype();
        mImageRecord = intent.getStringExtra("screen_shot_name");
        mTotalCal = appSportsLifeHelper.getCurrExercise().getTotalCal();
        mTotalStepCount = appSportsLifeHelper.getCurrExercise().getTotalCount();


        final Object[] recordItem = {
                mCurrentTypeRecord,
                mTimeRecord,
                mTotalDistanceRecord,
                mTotalTimeRecord,
                mImageRecord,
                mPlaceRecord,
                mTotalCal,
                mTotalStepCount};

        final HistoryService dbService = new HistoryRealize(this);

        btnSaveRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbInsertItem(dbService, recordItem);
            }
        });
        btnDeleteRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SkipResultShow.this);
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
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(SkipResultShow.this);
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



    }

    private void dbInsertItem(HistoryService dbService, Object[] recordItem) {
        if(dbService.Insert(recordItem)){
            appSportsLifeHelper.DisplayToast("记录保存成功!");
            backToMainActivity();
        }else{
            appSportsLifeHelper.DisplayToast("记录保存失败……");
        }
    }

    private void backToMainActivity() {
        SkipResultShow.this.finish();
    }

    private void initView() {
        appSportsLifeHelper = (mHelper) getApplicationContext();
        btnNaviBack = (ImageView) findViewById(R.id.navi_back);
        mSkipCountResult = (TextView) findViewById(R.id.tv_skip_count_show);
        mCalResult = (TextView) findViewById(R.id.tv_cal_show);
        mHeartRateResult = (TextView) findViewById(R.id.tv_heartrate_show);
        mTimeResult = (TextView) findViewById(R.id.tv_time_show);
        btnDeleteRecord = (Button) findViewById(R.id.btn_delete_record);
        btnSaveRecord = (Button) findViewById(R.id.btn_save_record);
        String formatedTimeShow = mConvertTool.parseSecondToTimeFormat(appSportsLifeHelper.getCurrExercise().getTotalTime()+"");
        mSkipCountResult.setText(appSportsLifeHelper.getCurrExercise().getTotalCount()+"");
        mCalResult.setText(appSportsLifeHelper.getCurrExercise().getTotalCal()+"");
        mTimeResult.setText(formatedTimeShow);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if(keyCode == KeyEvent.KEYCODE_BACK){
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(SkipResultShow.this);
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

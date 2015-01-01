package sysu.project.lee.sportslife.Excercise;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import sysu.project.lee.sportslife.Database.HistoryRealize;
import sysu.project.lee.sportslife.Database.HistoryService;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.User.UserEntity;
import sysu.project.lee.sportslife.Utils.ToastUtils;
import sysu.project.lee.sportslife.Utils.mConvertTool;
import sysu.project.lee.sportslife.Utils.mHelper;

/**
 * 运动记录界面类
 * Created by lee on 14年11月24日.
 */
public class ExerciseHistoryActivity extends Activity{

    private ListView recordListView;
    private ImageView naviBack;
    private ArrayList<HashMap<String, String>> listItems = null;
    private HistorySimpleAdapter mAdapter = null;
    private mHelper appHelper;
    private UserEntity mCurrentUser = null;
    private int mCurrentUserId = 0;
    private RelativeLayout mNoHistoryRL = null;
    private HistoryService dbService = null;

    private List<Map<String, String>> list = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_history);

        appHelper = (mHelper) getApplicationContext();
        mCurrentUser = appHelper.getCurrentUser();
        mCurrentUserId = mCurrentUser.getId();


        naviBack = (ImageView) findViewById(R.id.navi_back);
        recordListView = (ListView) findViewById(R.id.lv_exercixe_history_record);



        mNoHistoryRL = (RelativeLayout) findViewById(R.id.rl_no_history);

        dbService = new HistoryRealize(ExerciseHistoryActivity.this);
        listItems = new ArrayList<HashMap<String, String>>();
        String mArgs[] = {mCurrentUserId+""};
        list = dbService.listHistoryByUserID(mArgs);

        initListViewData(list);

        mAdapter = new HistorySimpleAdapter(ExerciseHistoryActivity.this, listItems);

        recordListView.setDividerHeight(0);
        recordListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ToastUtils.show(ExerciseHistoryActivity.this, "position:"+position);
            }
        });

        recordListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                String mItemType = list.get(position).get("type");
                String mItemDate = list.get(position).get("date");
                String mItemShotPath = list.get(position).get("screen_shot_path");

                final Object[] args = {mCurrentUserId,
                                        mItemType,
                                        mItemDate,
                                        mItemShotPath};

                Log.i("DBDELETE", "userID:" + mCurrentUserId +
                        "ItemType" + mItemType +
                        "ItemDate" + mItemDate +
                        "ItemShotPath" + mItemShotPath);

                new AlertDialog.Builder(ExerciseHistoryActivity.this)
                        .setTitle("确定要删除本条记录？")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(dbService.Delete(args)){
                                    list = dbService.listHistoryByUserID(new String[]{mCurrentUserId+""});
                                    initListViewData(list);
                                    mAdapter.setmArrayList(listItems);
                                    ToastUtils.show(ExerciseHistoryActivity.this, "删除成功");
                                }else{
                                    ToastUtils.show(ExerciseHistoryActivity.this, "删除失败");
                                }
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                return true;
            }
        });

        recordListView.setAdapter(mAdapter);

        naviBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainActivity();
            }
        });


    }

    /**
     * 初始化界面中的数据
     * @param list  运动记录listview的数据源
     */
    private void initListViewData(List<Map<String, String>> list) {
        if (!list.isEmpty()){
            mNoHistoryRL.setVisibility(View.GONE);
            if(!listItems.isEmpty()){
                listItems.clear();
            }

            for (int i = 0 ; i < list.size() ; i++){
                HashMap<String, String> map = new HashMap<String, String>();

                String  dateString = list.get(i).get("date");
                String[] dateStr = dateString.split("  ");//"2014-11-25  15:58"
                String[] date = dateStr[0].split("-");//date[0]=2014;date[1]=11;date[2]=25;


                String formatedTime = mConvertTool.parseSecondToTimeFormat(list.get(i).get("time"));
              /*

               type string,
               date string,
               distance string,
               time string,
               screen_shot_path string,
               address string
               calorie string
               stepcount string
               heart_rate string
                */

                map.put("screen_shot_path",list.get(i).get("screen_shot_path")+"");

                map.put("type",list.get(i).get("type")+"");
                map.put("data_year", date[0]);
                map.put("data_month", date[1]+"月");
                map.put("data_day", date[2]+"日");
                map.put("time_clock", dateStr[1]);
                map.put("distance", list.get(i).get("distance"));
                map.put("address", list.get(i).get("address"));
                map.put("calorie", list.get(i).get("calorie")+"");
                map.put("time", formatedTime);
                map.put("step_count", list.get(i).get("stepcount")+"");
                map.put("heart_rate", list.get(i).get("heart_rate")+"");
                listItems.add(map);
            }
        }else{
            mNoHistoryRL.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
            backToMainActivity();
            return true;
        }
        return false;
    }

    /**
     * 关闭本界面，返回主界面。
     */
    private void backToMainActivity() {
        ExerciseHistoryActivity.this.finish();
    }

}

package sysu.project.lee.sportslife.Excercise;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sysu.project.lee.sportslife.Database.HistoryRealize;
import sysu.project.lee.sportslife.Database.HistoryService;
import sysu.project.lee.sportslife.MainActivity;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.Utils.mConvertTool;

/**
 * Created by lee on 14年11月24日.
 */
public class ExerciseHistoryActivity extends Activity{

    private ListView recordListView;
    private ImageView naviBack;
    private ArrayList<HashMap<String, String>> listItems = null;
    private HistorySimpleAdapter mAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_history);

        naviBack = (ImageView) findViewById(R.id.navi_back);
        recordListView = (ListView) findViewById(R.id.lv_exercixe_history_record);

        HistoryService dbService = new HistoryRealize(ExerciseHistoryActivity.this);
        listItems = new ArrayList<HashMap<String, String>>();
        List<Map<String, String>> list = dbService.listHistoryMaps(null);
        if (!list.isEmpty()){

            for (int i = 0 ; i < list.size() ; i++){
                HashMap<String, String> map = new HashMap<String, String>();

                String  dateString = list.get(i).get("date");
                String[] dateStr = dateString.split("  ");//"2014-11-25  15:58"
                String[] date = dateStr[0].split("-");//date[0]=2014;date[1]=11;date[2]=25;

                Log.i("stepcount","---type--->"+list.get(i).get("type")+"count---->"+list.get(i).get("stepcount"));

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
        }

        mAdapter = new HistorySimpleAdapter(ExerciseHistoryActivity.this, listItems);

        recordListView.setDividerHeight(0);

        recordListView.setAdapter(mAdapter);

        naviBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToMainActivity();
            }
        });

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

    private void backToMainActivity() {
        ExerciseHistoryActivity.this.finish();
    }

}

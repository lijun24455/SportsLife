package sysu.project.lee.sportslife.Excercise;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import sysu.project.lee.sportslife.R;

/**
 * Created by lee on 14年11月24日.
 */
public class HistorySimpleAdapter extends BaseAdapter {

    private List<? extends Map<String, String>> mArrayList;
    private LayoutInflater mLayoutInflater;

    private final int TYPE_RUN = 0;
    private final int TYPE_BIKE = 1;
    private final int TYPE_STEP = 2;
    private final int TYPE_SKIP = 3;
    private final int TYPE_COUNT = 4;

    private class RunRecordViewHolder{
        TextView dataYear;
        TextView dataMonth;
        TextView dataDay;
        TextView tvTimeStartRecord;
        TextView tvLocationRecord;
        TextView tvDistanceRecord;
        TextView tvCalRecord;
        TextView tvTimeCountRecord;
        TextView tvHeartRateRecord;
    }
    private class BikeRecordViewHolder{
        TextView dataYear;
        TextView dataMonth;
        TextView dataDay;
        TextView tvTimeStartRecord;
        TextView tvLocationRecord;
        TextView tvDistanceRecord;
        TextView tvCalRecord;
        TextView tvTimeCountRecord;
        TextView tvHeartRateRecord;
    }
    private class StepRecordViewHolder{
        TextView dataYear;
        TextView dataMonth;
        TextView dataDay;
        TextView tvTimeStartRecord;
        TextView tvLocationRecord;
        TextView tvStepCountRecord;
        TextView tvCalRecord;
        TextView tvTimeCountRecord;
        TextView tvHeartRateRecord;
    }
    private class SkipRecordViewHolder{
        TextView dataYear;
        TextView dataMonth;
        TextView dataDay;
        TextView tvTimeStartRecord;
        TextView tvLocationRecord;
        TextView tvSkipCountRecord;
        TextView tvCalRecord;
        TextView tvTimeCountRecord;
        TextView tvHeartRateRecord;
    }

    /**
     * Constructor
     *
     * @param context  The context where the View associated with this SimpleAdapter is running
     * @param data     A List of Maps. Each entry in the List corresponds to one row in the list. The
     *                 Maps contain the data for each row, and should include all the entries specified in
     *                 "from"
     */
    public HistorySimpleAdapter(Context context, List<? extends Map<String, ?>> data) {
        this.mArrayList = (List<? extends Map<String, String>>) data;
        mLayoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        Log.i("adapter","size of arraylist:--------->"+mArrayList.size());
    }


    @Override
    public int getCount() {
        if (mArrayList == null){
            return 0;
        }else{
            return (mArrayList.size());
        }
    }

    @Override
    public Object getItem(int position) {
        if(mArrayList == null){
            return null;
        }else{
            return mArrayList.get(position);
        }

    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        RunRecordViewHolder runRecordViewHolder = null;
        BikeRecordViewHolder bikeRecordViewHolder = null;
        StepRecordViewHolder stepRecordViewHolder = null;
        SkipRecordViewHolder skipRecordViewHolder = null;

        int currentType = getItemViewType(position);

        if (convertView == null){
            switch(currentType){
                case TYPE_RUN:
                    convertView = mLayoutInflater.inflate(R.layout.record_run_listview_item, parent, false);
                    runRecordViewHolder = new RunRecordViewHolder();

                    runRecordViewHolder.dataYear = (TextView) convertView.findViewById(R.id.tv_year_record);
                    runRecordViewHolder.dataMonth = (TextView) convertView.findViewById(R.id.tv_month_record);
                    runRecordViewHolder.dataDay = (TextView) convertView.findViewById(R.id.tv_day_record);
                    runRecordViewHolder.tvTimeStartRecord = (TextView) convertView.findViewById(R.id.tv_clock_record);
                    runRecordViewHolder.tvLocationRecord = (TextView) convertView.findViewById(R.id.tv_location_record);
                    runRecordViewHolder.tvDistanceRecord = (TextView) convertView.findViewById(R.id.tv_distance_record);
                    runRecordViewHolder.tvCalRecord = (TextView) convertView.findViewById(R.id.tv_cal_record);
                    runRecordViewHolder.tvTimeCountRecord = (TextView) convertView.findViewById(R.id.tv_time_count_record);
                    runRecordViewHolder.tvHeartRateRecord = (TextView) convertView.findViewById(R.id.tv_heart_rate_record);

                    convertView.setTag(runRecordViewHolder);
                    break;
                case TYPE_BIKE:

                    convertView = mLayoutInflater.inflate(R.layout.record_bike_listview_item, parent, false);
                    bikeRecordViewHolder = new BikeRecordViewHolder();

                    bikeRecordViewHolder.dataYear = (TextView) convertView.findViewById(R.id.tv_year_record);
                    bikeRecordViewHolder.dataMonth = (TextView) convertView.findViewById(R.id.tv_month_record);
                    bikeRecordViewHolder.dataDay = (TextView) convertView.findViewById(R.id.tv_day_record);
                    bikeRecordViewHolder.tvTimeStartRecord = (TextView) convertView.findViewById(R.id.tv_clock_record);
                    bikeRecordViewHolder.tvLocationRecord = (TextView) convertView.findViewById(R.id.tv_location_record);
                    bikeRecordViewHolder.tvDistanceRecord = (TextView) convertView.findViewById(R.id.tv_distance_record);
                    bikeRecordViewHolder.tvCalRecord = (TextView) convertView.findViewById(R.id.tv_cal_record);
                    bikeRecordViewHolder.tvTimeCountRecord = (TextView) convertView.findViewById(R.id.tv_time_count_record);
                    bikeRecordViewHolder.tvHeartRateRecord = (TextView) convertView.findViewById(R.id.tv_heart_rate_record);

                    convertView.setTag(bikeRecordViewHolder);

                    break;
                case TYPE_STEP:

                    convertView = mLayoutInflater.inflate(R.layout.record_step_listview_item, parent, false);
                    stepRecordViewHolder = new StepRecordViewHolder();

                    stepRecordViewHolder.dataYear = (TextView) convertView.findViewById(R.id.tv_year_record);
                    stepRecordViewHolder.dataMonth = (TextView) convertView.findViewById(R.id.tv_month_record);
                    stepRecordViewHolder.dataDay = (TextView) convertView.findViewById(R.id.tv_day_record);
                    stepRecordViewHolder.tvTimeStartRecord = (TextView) convertView.findViewById(R.id.tv_clock_record);
                    stepRecordViewHolder.tvLocationRecord = (TextView) convertView.findViewById(R.id.tv_location_record);
//                    stepRecordViewHolder.tvDistanceRecord = (TextView) convertView.findViewById(R.id.tv_distance_record);
                    stepRecordViewHolder.tvStepCountRecord = (TextView) convertView.findViewById(R.id.tv_step_count_record);
                    stepRecordViewHolder.tvCalRecord = (TextView) convertView.findViewById(R.id.tv_cal_record);
                    stepRecordViewHolder.tvTimeCountRecord = (TextView) convertView.findViewById(R.id.tv_time_count_record);
                    stepRecordViewHolder.tvHeartRateRecord = (TextView) convertView.findViewById(R.id.tv_heart_rate_record);

                    convertView.setTag(stepRecordViewHolder);

                    break;
                case TYPE_SKIP:

                    convertView = mLayoutInflater.inflate(R.layout.record_skip_listview_item, parent, false);
                    skipRecordViewHolder = new SkipRecordViewHolder();

                    skipRecordViewHolder.dataYear = (TextView) convertView.findViewById(R.id.tv_year_record);
                    skipRecordViewHolder.dataMonth = (TextView) convertView.findViewById(R.id.tv_month_record);
                    skipRecordViewHolder.dataDay = (TextView) convertView.findViewById(R.id.tv_day_record);
                    skipRecordViewHolder.tvTimeStartRecord = (TextView) convertView.findViewById(R.id.tv_clock_record);
                    skipRecordViewHolder.tvLocationRecord = (TextView) convertView.findViewById(R.id.tv_location_record);
//                    stepRecordViewHolder.tvDistanceRecord = (TextView) convertView.findViewById(R.id.tv_distance_record);
                    skipRecordViewHolder.tvSkipCountRecord = (TextView) convertView.findViewById(R.id.tv_skip_count_record);
                    skipRecordViewHolder.tvCalRecord = (TextView) convertView.findViewById(R.id.tv_cal_record);
                    skipRecordViewHolder.tvTimeCountRecord = (TextView) convertView.findViewById(R.id.tv_time_count_record);
                    skipRecordViewHolder.tvHeartRateRecord = (TextView) convertView.findViewById(R.id.tv_heart_rate_record);

                    convertView.setTag(skipRecordViewHolder);

                    break;
                default:
                    Log.i("history","---------------------->getView中类型设置错误---currentType----convertView=null-->"+currentType);
            }
        }else{
            switch (currentType){
                case TYPE_RUN:
                    runRecordViewHolder = (RunRecordViewHolder) convertView.getTag();
                    break;
                case TYPE_BIKE:
                    bikeRecordViewHolder = (BikeRecordViewHolder) convertView.getTag();
                    break;
                case TYPE_STEP:
                    stepRecordViewHolder = (StepRecordViewHolder) convertView.getTag();
                    break;
                case TYPE_SKIP:
                    skipRecordViewHolder = (SkipRecordViewHolder) convertView.getTag();
                    break;
                default:
                    Log.i("history","---------------------->getView中类型设置错误---currentType----convertView != null-->"+currentType);
            }
        }
        /*
                map.put("type",list.get(i).get("type")+"");
                map.put("data_year", date[0]);
                map.put("data_month", date[1]);
                map.put("data_day", date[2]);
                map.put("time_clock", dateStr[1]);
                map.put("distance", list.get(i).get("distance"));
                map.put("address", list.get(i).get("address"));
                map.put("calorie", list.get(i).get("calorie")+"");
                map.put("time", list.get(i).get("time"));
                map.put("step_count", list.get(i).get("stepcount")+"");
         */
//        以下设置资源
        switch (currentType){
            case TYPE_RUN:
                runRecordViewHolder.dataYear.setText(mArrayList.get(position).get("data_year")+"");
                runRecordViewHolder.dataMonth.setText(mArrayList.get(position).get("data_month")+"");
                runRecordViewHolder.dataDay.setText(mArrayList.get(position).get("data_day")+"");
                runRecordViewHolder.tvTimeStartRecord.setText(mArrayList.get(position).get("time_clock"+""));
                runRecordViewHolder.tvLocationRecord.setText(mArrayList.get(position).get("address")+"");
                runRecordViewHolder.tvDistanceRecord.setText(mArrayList.get(position).get("distance"+""));
                runRecordViewHolder.tvCalRecord.setText(mArrayList.get(position).get("calorie"+"")+"");
                runRecordViewHolder.tvTimeCountRecord.setText(mArrayList.get(position).get("time")+"");
                runRecordViewHolder.tvHeartRateRecord.setText(mArrayList.get(position).get("heart_put")+"bpm");
                break;
            case TYPE_BIKE:
                bikeRecordViewHolder.dataYear.setText(mArrayList.get(position).get("data_year")+"");
                bikeRecordViewHolder.dataMonth.setText(mArrayList.get(position).get("data_month")+"");
                bikeRecordViewHolder.dataDay.setText(mArrayList.get(position).get("data_day")+"");
                bikeRecordViewHolder.tvTimeStartRecord.setText(mArrayList.get(position).get("time_clock"+""));
                bikeRecordViewHolder.tvLocationRecord.setText(mArrayList.get(position).get("address")+"");
                bikeRecordViewHolder.tvDistanceRecord.setText(mArrayList.get(position).get("distance"+""));
                bikeRecordViewHolder.tvCalRecord.setText(mArrayList.get(position).get("calorie"+"")+"");
                bikeRecordViewHolder.tvTimeCountRecord.setText(mArrayList.get(position).get("time")+"");
                bikeRecordViewHolder.tvHeartRateRecord.setText(mArrayList.get(position).get("heart_put")+"bpm");
                break;
            case TYPE_STEP:
                stepRecordViewHolder.dataYear.setText(mArrayList.get(position).get("data_year")+"");
                stepRecordViewHolder.dataMonth.setText(mArrayList.get(position).get("data_month")+"");
                stepRecordViewHolder.dataDay.setText(mArrayList.get(position).get("data_day")+"");
                stepRecordViewHolder.tvTimeStartRecord.setText(mArrayList.get(position).get("time_clock"+""));
                stepRecordViewHolder.tvLocationRecord.setText(mArrayList.get(position).get("address")+"");
                stepRecordViewHolder.tvStepCountRecord.setText(mArrayList.get(position).get("step_count" + ""));
                stepRecordViewHolder.tvCalRecord.setText(mArrayList.get(position).get("calorie"+"")+"");
                stepRecordViewHolder.tvTimeCountRecord.setText(mArrayList.get(position).get("time")+"");
                stepRecordViewHolder.tvHeartRateRecord.setText(mArrayList.get(position).get("heart_put")+"bpm");
                break;
            case TYPE_SKIP:
                skipRecordViewHolder.dataYear.setText(mArrayList.get(position).get("data_year")+"");
                skipRecordViewHolder.dataMonth.setText(mArrayList.get(position).get("data_month")+"");
                skipRecordViewHolder.dataDay.setText(mArrayList.get(position).get("data_day")+"");
                skipRecordViewHolder.tvTimeStartRecord.setText(mArrayList.get(position).get("time_clock"+""));
                skipRecordViewHolder.tvLocationRecord.setText(mArrayList.get(position).get("address")+"");
                skipRecordViewHolder.tvSkipCountRecord.setText(mArrayList.get(position).get("step_count" + ""));
                skipRecordViewHolder.tvCalRecord.setText(mArrayList.get(position).get("calorie"+"")+"");
                skipRecordViewHolder.tvTimeCountRecord.setText(mArrayList.get(position).get("time")+"");
                skipRecordViewHolder.tvHeartRateRecord.setText(mArrayList.get(position).get("heart_put")+"bpm");
                break;
        }


        /*


        switch(currentType){
            case TYPE_RUN:
                runRecordView = convertView;
                RunRecordViewHolder runRecordViewHolder = null;
                if (runRecordView == null){
                    runRecordView = mLayoutInflater.inflate(R.layout.record_run_listview_item, null);
                    runRecordViewHolder = new RunRecordViewHolder();

                    runRecordViewHolder.dataYear = (TextView) runRecordView.findViewById(R.id.tv_year_record);
                    runRecordViewHolder.dataMonth = (TextView) runRecordView.findViewById(R.id.tv_month_record);
                    runRecordViewHolder.dataDay = (TextView) runRecordView.findViewById(R.id.tv_day_record);
                    runRecordViewHolder.tvTimeStartRecord = (TextView) runRecordView.findViewById(R.id.tv_clock_record);
                    runRecordViewHolder.tvLocationRecord = (TextView) runRecordView.findViewById(R.id.tv_location_record);
                    runRecordViewHolder.tvDistanceRecord = (TextView) runRecordView.findViewById(R.id.tv_distance_record);
                    runRecordViewHolder.tvCalRecord = (TextView) runRecordView.findViewById(R.id.tv_cal_record);
                    runRecordViewHolder.tvTimeCountRecord = (TextView) runRecordView.findViewById(R.id.tv_time_count_record);

                    runRecordView.setTag(runRecordView);
                }else{
                    runRecordViewHolder = (RunRecordViewHolder) runRecordView.getTag();
                }
                convertView = runRecordView;
                break;
            case TYPE_BIKE:
                break;
            case TYPE_STEP:
                break;
            case TYPE_SKIP:
                break;

        }
        */
        return convertView;
    }

    @Override
    public int getViewTypeCount() {
        return this.TYPE_COUNT;
    }

    @Override
    public int getItemViewType(int position) {
        Log.i("adapter","getItemViewType--position--------->"+position);
        Log.i("adapter","getItemViewType--position->type-------->"+mArrayList.get(position).get("type"));
        return Integer.parseInt(mArrayList.get(position).get("type"));
    }
}

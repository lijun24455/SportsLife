package sysu.project.lee.sportslife.News;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import sysu.project.lee.sportslife.R;

/**
 * Created by lee on 14年12月2日.
 */
public class mNewsListAdapter extends BaseAdapter {

    private List<NewsItem> data;
    private LayoutInflater mLayoutInflater;
    private Context context;

    private class NewsListItemViewHolder{
        private ImageView ivFront;
        private TextView tvTitle;
        private TextView tvPubdate;
    }

    public mNewsListAdapter(Context context, ArrayList<NewsItem> data){
        this.data = data;
        this.context = context;
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        if (data == null){
            return 0;
        }else{
            return data.size();
        }
    }

    @Override
    public Object getItem(int position) {
        if (data == null){
            return null;
        }else{
            return data.get(position);
        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        NewsListItemViewHolder viewHolder = null;

        if (convertView == null){

            convertView = mLayoutInflater.inflate(R.layout.news_list_item, parent, false);

            viewHolder = new NewsListItemViewHolder();
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.news_item_title);
            viewHolder.tvPubdate = (TextView) convertView.findViewById(R.id.news_item_time);
            viewHolder.ivFront = (ImageView) convertView.findViewById(R.id.news_item_img);
            convertView.setTag(viewHolder);

        }else {
            viewHolder = (NewsListItemViewHolder) convertView.getTag();
        }

        viewHolder.tvTitle.setText(data.get(position).getTitle());
        viewHolder.tvPubdate.setText(data.get(position).getPubDate());


        return convertView;
    }
}

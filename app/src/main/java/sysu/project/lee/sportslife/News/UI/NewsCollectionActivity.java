package sysu.project.lee.sportslife.News.UI;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import org.litepal.crud.DataSupport;

import java.util.ArrayList;
import java.util.List;

import sysu.project.lee.sportslife.News.Utils.ActionLabelUtils;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.User.UserEntity;

/**
 * 我的收藏界面类
 *
 */
public class NewsCollectionActivity extends Activity {

    private ImageView btnNaviBack = null;
    private ListView lvCollectionItems = null;
    private ItemListAdapter mListAdapter = null;
    private ArrayList<FeedItem> itemsData = null;
    private String sectionUrl = null;
    private BroadcastReceiver mReceiver = null;

    private UserEntity mCurrentUser = null;

    private RelativeLayout rlEmptyCollection = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news_collection);

        mCurrentUser = (UserEntity) getIntent().getSerializableExtra("CURRENT_USER");

        initView();

        initData();

        initBroadCast();
    }

    /**
     * 初始化界面控件
     */
    private void initView() {
        btnNaviBack = (ImageView) findViewById(R.id.navi_back);
        lvCollectionItems = (ListView) findViewById(R.id.lv_news_collection);
        rlEmptyCollection = (RelativeLayout) findViewById(R.id.rl_no_collection);

        lvCollectionItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FeedItem item = itemsData.get(position);
                Intent intent = new Intent();

                String content = item.getContent();
                if(content != null && content.length() != 0)
                {
                    intent.putExtra("item_detail", content);
                }
//                intent.putExtra("title", title);
//                intent.putExtra("pubdate", pubdate);
//                intent.putExtra("is_favorite", true);
//                intent.putExtra("link", link);

                intent.putExtra("section_url", sectionUrl);
                intent.putExtra("CLICKED_ITEM", item);

                intent.setClass(NewsCollectionActivity.this, ItemDetail.class);
                NewsCollectionActivity.this.startActivity(intent);
            }
        });

        btnNaviBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    /**
     * 获得数据与界面控件绑定
     *
     */
    private void initData() {
        itemsData = new ArrayList<FeedItem>();
        List<FeedItem> feedItemList = mCurrentUser.getFavoriteFeedItem();
        Log.i("NewsDB","*************collection items number:"+feedItemList.size()+"CURRENTuSERid----->:"+mCurrentUser.getId());
        if(feedItemList.size()>0)
        {
            rlEmptyCollection.setVisibility(View.GONE);
            for(int i = 0 ; i < feedItemList.size(); i++)
            {
                FeedItem item = feedItemList.get(i);
                /*
                String title = cursor.getString(cursor.getColumnIndex("title"));
                String pubdate = cursor.getString(cursor.getColumnIndex("pubdate"));
                String itemDetail = cursor.getString(cursor.getColumnIndex("item_detail"));
                String link = cursor.getString(cursor.getColumnIndex("link"));

                sectionUrl = cursor.getString(cursor.getColumnIndex("table_url"));

                item.setTitle(title);
                item.setPubdate(pubdate);
                item.setContent(itemDetail);
                item.setFavorite(true);
                item.setLink(link);
                */

                itemsData.add(item);
            }
        }else{
            rlEmptyCollection.setVisibility(View.VISIBLE);
        }

        mListAdapter = new ItemListAdapter(this, itemsData, false);
        lvCollectionItems.setAdapter(mListAdapter);
    }

    /**
     * 初始化广播，接收取消收藏的新闻资讯
     *
     */
    private void initBroadCast()
    {
        mReceiver = new BroadcastReceiver(){
            @Override
            public void onReceive(Context context, Intent intent) {
                String link = intent.getStringExtra("link");
                for(int i = 0; i < itemsData.size(); i++)
                {
                    FeedItem item = itemsData.get(i);
                    if(item.getLink().equals(link))
                    {
                        itemsData.remove(i);
                        Log.i("NewsDB","*****delete********collection item number:"+i);
                        mListAdapter.notifyDataSetChanged();
                        return;
                    }
                }
            }
        };
        IntentFilter filter = new IntentFilter();
        filter.addAction(ActionLabelUtils.FAVORITE_ACTION);
        registerReceiver(mReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mReceiver);
    }
}

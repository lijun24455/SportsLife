package sysu.project.lee.sportslife.News.UI;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;

import sysu.project.lee.sportslife.News.Database.DbManager;
import sysu.project.lee.sportslife.News.Utils.ActionLabelUtils;
import sysu.project.lee.sportslife.R;

public class NewsCollectionActivity extends Activity {

    private ImageView btnNaviBack = null;
    private ListView lvCollectionItems = null;
    private ItemListAdapter mListAdapter = null;
    private ArrayList<FeedItem> itemsData = null;
    private String sectionUrl = null;
    private BroadcastReceiver mReceiver = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_news_collection);

        initView();

        initData();

        initBroadCast();
    }

    private void initView() {
        btnNaviBack = (ImageView) findViewById(R.id.navi_back);
        lvCollectionItems = (ListView) findViewById(R.id.lv_news_collection);

        lvCollectionItems.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                FeedItem item = itemsData.get(position);
                Intent intent = new Intent();
                String title = item.getTitle();
                String content = item.getContent();
                String pubdate = item.getPubdate();
                String link = item.getLink();
                if(content != null && content.length() != 0)
                {
                    intent.putExtra("item_detail", content);
                }
                intent.putExtra("title", title);
                intent.putExtra("pubdate", pubdate);
                intent.putExtra("is_favorite", true);
                intent.putExtra("link", link);
                intent.putExtra("section_url", sectionUrl);

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

    private void initData() {
        itemsData = new ArrayList<FeedItem>();
        DbManager mgr = new DbManager(this, DbManager.DB_NAME, null, 1);
        SQLiteDatabase db = mgr.getWritableDatabase();
        Cursor cursor = db.query(DbManager.FAVORITE_ITEM_TABLE_NAME, null, null, null, null, null, null);
        if(cursor.moveToFirst())
        {
            for(int i = 0, n = cursor.getCount(); i < n; i++)
            {
                FeedItem item = new FeedItem();
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

                itemsData.add(item);
                cursor.moveToNext();
            }
        }
        mListAdapter = new ItemListAdapter(this, itemsData, false);
        lvCollectionItems.setAdapter(mListAdapter);
        cursor.close();
        db.close();
    }

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

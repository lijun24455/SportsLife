package sysu.project.lee.sportslife.News;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.xml.sax.SAXException;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;

import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.Utils.HttpCallbackListener;
import sysu.project.lee.sportslife.Utils.HttpUtil;
import sysu.project.lee.sportslife.Utils.ToastUtils;
import sysu.project.lee.sportslife.Utils.URLs;
import sysu.project.lee.sportslife.Utils.XMLUtility;

/**
 * Created by lee on 14年12月1日.
 */
public class NewsListActivity extends Activity {
    private ListView mListView;
    private ArrayList<NewsItem> dataList = new ArrayList<NewsItem>();
//    private mNewsListAdapter mAdapter = null;

    private RssFeed feed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_list_layout);

            new mAsyncTask().execute(URLs.NEO_URL);
            Log.i("feed","------->"+feed.getAllItems());

        mListView = (ListView) findViewById(R.id.news_list);
        if (feed == null){
            ToastUtils.show(this, "rss获取失败");
        }
//        mAdapter = new mNewsListAdapter(NewsListActivity.this, dataList);
        SimpleAdapter mAdapter = new SimpleAdapter(this, feed.getAllItems(), R.layout.news_list_item,
                new String[]{RssItem.TITLE, RssItem.PUBDATE},
                new int[]{R.id.news_item_title, R.id.news_item_time});
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

    }

    class mAsyncTask extends AsyncTask<String, Integer, RssFeed>{

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Log.i("task", "------------->开始加载数据");
        }

        @Override
        protected RssFeed doInBackground(String... params) {
            RssFeed rssFeed = null;
            try {
                rssFeed = new RssFeed_SAXParser().getFeed(params[0]);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return rssFeed;
        }

        @Override
        protected void onPostExecute(RssFeed rssFeed) {
            super.onPostExecute(rssFeed);
            feed = rssFeed;
        }
    }

//    private void queryFromServer() {
//
//        showProgressDialog();
//        HttpUtil.sendHttpRequest(URLs.NEO_URL, new HttpCallbackListener() {
//            @Override
//            public void onFinish(String response) {
//
//                Log.i("neo","----->size---"+response.length());
//                Log.i("neo", "----->xml:---->"+ response);
//
//                dataList = XMLUtility.getNewsList(NewsListActivity.this, response);
//                if(dataList.size()>0){
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            closeProgressDialog();
//                            mAdapter.notifyDataSetChanged();
//                        }
//                    });
//
//                }
//
//            }
//
//            @Override
//            public void onError(Exception e) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        closeProgressDialog();
//                        ToastUtils.show(NewsListActivity.this, "加载失败");
//                    }
//                });
//
//            }
//        });
//
//
//    }
//
//    /**
//     * 显示进度框
//     */
//    private void showProgressDialog() {
//        if (progressDialog == null){
//            progressDialog = new ProgressDialog(this);
//            progressDialog.setMessage("正在加载……");
//            progressDialog.setCancelable(false);
//        }
//        progressDialog.show();
//
//    }
//
//    /**
//     * 关闭进度框
//     */
//
//    private void closeProgressDialog() {
//        if (progressDialog != null){
//            progressDialog.dismiss();
//        }
//    }

}

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

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

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
    private mNewsListAdapter mAdapter = null;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.news_list_layout);
        mListView = (ListView) findViewById(R.id.news_list);
        mAdapter = new mNewsListAdapter(NewsListActivity.this, dataList);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            }
        });

        queryFromServer();

    }

    private void queryFromServer() {

        showProgressDialog();
        HttpUtil.sendHttpRequest(URLs.NEO_URL, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {

                Log.i("neo","----->size---"+response.length());
                Log.i("neo", "----->xml:---->"+ response);

                dataList = XMLUtility.getNewsList(NewsListActivity.this, response);
                if(dataList.size()>0){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            mAdapter.notifyDataSetChanged();
                        }
                    });

                }

            }

            @Override
            public void onError(Exception e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        ToastUtils.show(NewsListActivity.this, "加载失败");
                    }
                });

            }
        });


    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progressDialog == null){
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("正在加载……");
            progressDialog.setCancelable(false);
        }
        progressDialog.show();

    }

    /**
     * 关闭进度框
     */

    private void closeProgressDialog() {
        if (progressDialog != null){
            progressDialog.dismiss();
        }
    }

}

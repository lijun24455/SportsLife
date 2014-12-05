package sysu.project.lee.sportslife;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import java.io.File;

import sysu.project.lee.sportslife.News.AppContext;
import sysu.project.lee.sportslife.News.NewsListActivity;
import sysu.project.lee.sportslife.Utils.ToastUtils;
import sysu.project.lee.sportslife.Utils.URLs;
import sysu.project.lee.sportslife.Utils.mApplication;


public class NewsFragment extends Fragment {

    private String url = null;
    private Context mContext = null;
    private Button mButton;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mContext = mApplication.getContextObject();

        mButton = (Button) getView().findViewById(R.id.btn_neo);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                url = URLs.NEO_URL;
                Intent intent = new Intent();
                intent.putExtra("url", URLs.NEO_URL);

                intent.setClass(getActivity(), NewsListActivity.class);


                File cache = SectionHelper.getSdCache(url);
                if(cache.exists())
                {
                    startActivity(intent);
                }
                else
                {
                    if(!AppContext.isNetworkAvailable(mContext))
                    {
                        ToastUtils.show(mContext, "网络未打开");
                        return;
                    }
                    //异步加载数据
                    new LoadDataTask().execute(url);
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("news","----->onCreateView");
        return inflater.inflate(R.layout.fragment_news, container, false);

    }

}

package sysu.project.lee.sportslife.UI;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.RelativeLayout;

import org.litepal.crud.DataSupport;

import java.io.File;
import java.util.ArrayList;

import sysu.project.lee.sportslife.News.UI.GridAdapter;
import sysu.project.lee.sportslife.News.UI.ItemListEntity;
import sysu.project.lee.sportslife.News.UI.ItemListEntityParser;
import sysu.project.lee.sportslife.News.UI.NewsListActivity;
import sysu.project.lee.sportslife.News.UI.Section;
import sysu.project.lee.sportslife.News.Utils.AppContext;
import sysu.project.lee.sportslife.News.Utils.SectionHelper;
import sysu.project.lee.sportslife.News.Utils.SeriaHelper;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.User.UserEntity;
import sysu.project.lee.sportslife.Utils.ToastUtils;
import sysu.project.lee.sportslife.Utils.URLs;
import sysu.project.lee.sportslife.Utils.mApplication;
import sysu.project.lee.sportslife.Utils.mHelper;


public class NewsFragment extends Fragment {

    private ArrayList<Section> mSectionList = null;
    private GridAdapter mGridAdapter = null;
    private GridView mGridView = null;
    private Intent intent = null;
    private Context mContext = null;
    private RelativeLayout mNewsLoadingLayout = null;
    private UserEntity mUser = null;

    private mHelper appHelper = null;


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        appHelper = (mHelper) getActivity().getApplication();
        mUser = (UserEntity) getActivity().getIntent().getSerializableExtra("USER_INFO");

        mContext = mApplication.getContextObject();

        initViews();

        initData();


    }

    private void initData() {

        UserEntity user = DataSupport.find(UserEntity.class, mUser.getId(), true);
        ArrayList<Section> sections = (ArrayList<Section>) user.getSectionList();

        Log.i("sectiondb","sections------>"+sections.size());

        Log.i("sectiondb","user------>"+user.toString());
        if(sections.size()==0){
            mSectionList = appHelper.getFeedList();
        }else{
            mSectionList = sections;
        }


        mGridAdapter = new GridAdapter(mContext, mSectionList);

        mGridView.setAdapter(mGridAdapter);

        Log.i("fragment","->--onActivityCreated--->");

        mGridAdapter.setSections(mSectionList);


//        Section mNEOSection = new Section();
//        mNEOSection.setTitle("牛男网");
//        mNEOSection.setUrl(URLs.NEO_URL);
//
//        mFeedCenter = new Section();
//        mFeedCenter.setUrl(null);
//        mFeedCenter.setTitle("订阅中心");
//
//        mGridAdapter.addItem(mFeedCenter);
//        mGridAdapter.addItem(mNEOSection);


        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.i("item", "positon----->"+position);

                if (position == 0) {

                    intent = new Intent();
                    ArrayList<Section> tmpList = mGridAdapter.getCurrentSections();

                    intent.putExtra("FeedSelected", tmpList);
                    intent.setClass(getActivity(), FeedCenter.class);
                    startActivityForResult(intent, 1);

                } else {

                    Section section = (Section) mGridAdapter.getItem(position);
                    String title = section.getTitle();
                    String url = section.getUrl();

                    intent = new Intent();

                    intent.putExtra("url", url);
                    intent.putExtra("section_title", title);
                    intent.putExtra("CURRENT_USER", mUser);

                    intent.setClass(getActivity(), NewsListActivity.class);

                    File cache = SectionHelper.getSdCache(url);
                    if (cache.exists()) {
                        startActivity(intent);
                    } else {
                        if (!AppContext.isNetworkAvailable(mContext)) {
                            ToastUtils.show(mContext, "网络未打开");
                            return;
                        }
                        //异步加载数据
                        new LoadDataTask().execute(url);
                    }
                }

            }
        });

    }

    private void initViews() {
        mGridView = (GridView) getView().findViewById(R.id.gv_section);

        mNewsLoadingLayout = (RelativeLayout) getView().findViewById(R.id.news_loading_layout);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.i("news","----->onCreateView");
        return inflater.inflate(R.layout.fragment_news, container, false);

    }

    private class LoadDataTask extends AsyncTask<String, Integer, ItemListEntity>{

        @Override
        protected void onPreExecute()
        {
            mNewsLoadingLayout.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(Integer... values)
        {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(ItemListEntity result)
        {
            mNewsLoadingLayout.setVisibility(View.GONE);
            //跳转界面
            if(result != null && intent != null && !result.getItemList().isEmpty())
            {
                startActivity(intent);
            }
            else
            {
                ToastUtils.show(mContext, "网络未打开");
            }
        }

        @Override
        protected ItemListEntity doInBackground(String... params)
        {
            ItemListEntityParser parser = new ItemListEntityParser();
            ItemListEntity entity = parser.parse(params[0]);
            if(entity != null)
            {
                SeriaHelper helper = SeriaHelper.newInstance();
                File cache = SectionHelper.newSdCache(params[0]);
                helper.saveObject(entity, cache);
            }
            return entity;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if ( data!=null && resultCode == getActivity().RESULT_OK){

            ArrayList<Section> tmpList = (ArrayList<Section>) data.getExtras().getSerializable("FeedSelectedList");
//            if(!tmpList.isEmpty()){
//                for(Section section: tmpList){
//                    mSectionList.add(section);
//                }
//                mGridAdapter.setSections(mSectionList);
//            }
            UserEntity userEntity = DataSupport.find(UserEntity.class, mUser.getId());

            DataSupport.deleteAll(Section.class,"userentity_id=?", String.valueOf(mUser.getId()));
            for(Section section : tmpList){
                Section newSection = new Section();
                newSection.setUser(userEntity);
                newSection.setSelected(section.isSelected());
                newSection.setTitle(section.getTitle());
                newSection.setBackgroundColor(section.getBackgroundColor());
                newSection.setUrl(section.getUrl());
                if(newSection.isSaved()){
                    newSection.delete();
                }else{
                    newSection.save();
                    userEntity.getSectionList().add(section);
                }
            }
            userEntity.save();
            mGridAdapter.setCurrentSection(tmpList);

        }
    }
}

package sysu.project.lee.sportslife.UI;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.util.ArrayList;
import java.util.List;

import sysu.project.lee.sportslife.R;


public class MainActivity extends FragmentActivity {

    private ViewPager mViewPager;
    private FragmentPagerAdapter mAdapter;
    private List<Fragment> mDatas;
    private TextView mSportsTextView;
    private TextView mNewsTextView;
    private TextView mFindsTextView;
    private TextView mMeTextView;
    private ImageView mTabline;
    private LinearLayout mTabSports;
    private LinearLayout mTabNews;
    private LinearLayout mTabFinds;
    private LinearLayout mTabMe;

    private int mScreen_1_4;

    private int mCurrentPageIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);

        initView();

    }

    private void initView() {

        initTabline();

        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        mSportsTextView = (TextView) findViewById(R.id.tv_sport);
        mNewsTextView = (TextView) findViewById(R.id.tv_news);
        mFindsTextView = (TextView) findViewById(R.id.tv_finds);
        mMeTextView = (TextView) findViewById(R.id.tv_me);
//        mTab = (LinearLayout) findViewById(R.id.tab);

        initTab();

        mDatas = new ArrayList<Fragment>();
        SportsFragment sportsFragment = new SportsFragment();
        NewsFragment newsFragment = new NewsFragment();
        FindsFragment findsFragment = new FindsFragment();
        MeFragment meFragment = new MeFragment();

        mDatas.add(sportsFragment);
        mDatas.add(newsFragment);
        mDatas.add(findsFragment);
        mDatas.add(meFragment);

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int i) {
                return mDatas.get(i);
            }

            @Override
            public int getCount() {
                return mDatas.size();
            }
        };
        mViewPager.setOffscreenPageLimit(4);

        mViewPager.setAdapter(mAdapter);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset,
                                       int positionOffsetPx) {
                LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) mTabline
                        .getLayoutParams();
                if(mCurrentPageIndex == 0 && position == 0){//0->1
                    lp.leftMargin = (int) (positionOffset * mScreen_1_4
                            + mCurrentPageIndex * mScreen_1_4);
                }else if(mCurrentPageIndex == 1 && position == 0){//1->0
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen_1_4
                            + (positionOffset-1) * mScreen_1_4);
                }else if(mCurrentPageIndex == 1 && position == 1){//1->2
                    lp.leftMargin = (int) (positionOffset * mScreen_1_4
                            + mCurrentPageIndex * mScreen_1_4);
                }else if(mCurrentPageIndex == 2 && position == 1){//2->1
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen_1_4
                            + (positionOffset-1) * mScreen_1_4);
                }else if(mCurrentPageIndex == 2 && position == 2){//2->3
                    lp.leftMargin = (int) (positionOffset * mScreen_1_4
                            + mCurrentPageIndex * mScreen_1_4);
                }else if(mCurrentPageIndex == 3 && position == 2){//3->2
                    lp.leftMargin = (int) (mCurrentPageIndex * mScreen_1_4
                            + (positionOffset-1) * mScreen_1_4);
                }
                mTabline.setLayoutParams(lp);
            }

            //滑动结束
            @Override
            public void onPageSelected(int position) {

                resetTextView();
                switch(position)
                {
                    case 0:
                        mSportsTextView.setTextColor(Color.parseColor("#e040ece0"));
                        break;
                    case 1:
                        mNewsTextView.setTextColor(Color.parseColor("#e040ece0"));
                        break;
                    case 2:
                        mFindsTextView.setTextColor(Color.parseColor("#e040ece0"));
                        break;
                    case 3:
                        mMeTextView.setTextColor(Color.parseColor("#e040ece0"));
                        break;

                }
                mCurrentPageIndex = position;

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });



    }

    private void initTab() {
        resetTextView();
        mTabSports = (LinearLayout) findViewById(R.id.tab_sports);
        mTabNews = (LinearLayout) findViewById(R.id.tab_news);
        mTabFinds = (LinearLayout) findViewById(R.id.tab_finds);
        mTabMe = (LinearLayout) findViewById(R.id.tab_me);

        mTabSports.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(0);
            }
        });
        mTabNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(1);
            }
        });
        mTabFinds.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(2);
            }
        });
        mTabMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mViewPager.setCurrentItem(3);
            }
        });
    }

    private void initTabline() {
        mTabline = (ImageView) findViewById(R.id.iv_tabline);
        Display display = getWindow().getWindowManager().getDefaultDisplay();
        DisplayMetrics outMetrics = new DisplayMetrics();
        display.getMetrics(outMetrics);
        mScreen_1_4 = outMetrics.widthPixels / 4;
        ViewGroup.LayoutParams lp = mTabline.getLayoutParams();
        lp.width = mScreen_1_4;
        mTabline.setLayoutParams(lp);
    }

    private void resetTextView() {
        mSportsTextView.setTextColor(getResources().getColor(R.color.staticText));
        mFindsTextView.setTextColor(getResources().getColor(R.color.staticText));
        mNewsTextView.setTextColor(getResources().getColor(R.color.staticText));
        mMeTextView.setTextColor(getResources().getColor(R.color.staticText));
    }

}

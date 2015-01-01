package sysu.project.lee.sportslife.News.UI;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.webkit.WebSettings.LayoutAlgorithm;
import android.webkit.WebView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.umeng.analytics.social.UMSocialService;

import org.litepal.crud.DataSupport;

import sysu.project.lee.sportslife.News.Utils.AppContext;
import sysu.project.lee.sportslife.News.Utils.HtmlFilter;
import sysu.project.lee.sportslife.News.Utils.ActionLabelUtils;
import sysu.project.lee.sportslife.News.Utils.SeriaHelper;
import sysu.project.lee.sportslife.News.Utils.UIHelper;
import sysu.project.lee.sportslife.R;
import sysu.project.lee.sportslife.User.UserEntity;
import sysu.project.lee.sportslife.Utils.ToastUtils;

@SuppressLint("JavascriptInterface")
@SuppressWarnings("deprecation")
public class ItemDetail extends FragmentActivity
{
	private ImageView collectBtn;
    private ImageView naviBackBtn;
	private ImageView shareBtn;
//	private ImageButton commentBtn;
	private TextView countTv;//评论列表
	private static WebView mWebView;
	private String sectionTitle;
	private String sectionUrl;
	private String title;
	private String pubdate;
	private String itemDetail;
	private String link;
	private String firstImgUrl;
	private UMSocialService mController;
	private boolean isFavorite;//文章是否已收藏
	private String css = UIHelper.WEB_STYLE;
	private int[] favoIcons = {
			R.drawable.ic_favorite_outline_white_48dp,
			R.drawable.ic_favorite_white_48dp
	};//0为空

    private FeedItem mCurrentItem = null;
	private UserEntity mCurrentUser = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) 
	{
		super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mCurrentItem = (FeedItem) getIntent().getSerializableExtra("CLICKED_ITEM");
        mCurrentUser = mCurrentItem.getUser();
        Log.i("NewsDB", "-----user----->"+mCurrentUser.toString());
		initView();
		loadData();
//		initComments();
	}

	@SuppressLint("HandlerLeak")
	private Handler mHandler = new Handler(){

		@Override
		public void handleMessage(Message msg)
		{
			Intent intent = new Intent();
			intent.setAction(ActionLabelUtils.FAVORITE_ACTION);
			sendBroadcast(intent);
			super.handleMessage(msg);
		}
	};
	
//	private void initComments()
//	{
//		String key = MD5.Md5(link);
//		mController = UMServiceFactory.getUMSocialService(AppConfig.UM_BASE_KEY + key,
//					RequestType.SOCIAL);
//		mController.getComments(this, new FetchCommetsListener()
//		{
//			@Override
//			public void onStart()
//			{
//			}
//			@Override
//			public void onComplete(int status, List<UMComment> comments, SocializeEntity entity)
//			{
//				if(status == 200 && comments != null && !comments.isEmpty())
//				{
//					countTv.setText(comments.size() + "");
//				}
//			}
//		}, -1);
//	}

	@SuppressLint("SetJavaScriptEnabled")
	private void initView()
	{
        setContentView(R.layout.news_item_detail);
        SharedPreferences prefs = AppContext.getPrefrences(this);
//		if(prefs.getBoolean("day_night_mode", false))
//		{
////			setTheme(R.style.AppNightTheme);
//			css = UIHelper.WEB_STYLE_NIGHT;
//			favoIcons = new int[]{
//					R.drawable.btn_favorite_empty_night,
//					R.drawable.btn_favorite_full_night
//			};
//		}

//        isFavorite = getIntent().getBooleanExtra("is_favorite", false);
        isFavorite = mCurrentItem.isFavorite();
        naviBackBtn = (ImageView) findViewById(R.id.navi_back);
        naviBackBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        shareBtn = (ImageView) findViewById(R.id.btn_share);
        //分享
//		shareBtn.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v)
//			{
//				mController.openShare(ItemDetail.this, false);
//			}
//
//		});
//		commentBtn = (ImageButton) findViewById(R.id.fid_btn_comment);
//		commentBtn.setOnClickListener(new OnClickListener(){
//			@Override
//			public void onClick(View v)
//			{
//				openCommentUi();
//			}
//		});
		collectBtn = (ImageView) findViewById(R.id.btn_add_to_favorite);
		if(isFavorite)
			collectBtn.setImageResource(R.drawable.ic_favorite_white_48dp);
		collectBtn.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v)
			{
                /*
				DbManager helper = new DbManager(ItemDetail.this, DbManager.DB_NAME, null, 1);
				final SQLiteDatabase db = helper.getWritableDatabase();
				*/
				//已收藏，取消收藏
				if(isFavorite)
				{
					collectBtn.setImageResource(favoIcons[0]);
//					FavoItemDbHelper.removeRecord(db, link);
                    DataSupport.delete(FeedItem.class, mCurrentItem.getId());
                    Toast.makeText(ItemDetail.this, "取消了收藏", Toast.LENGTH_SHORT).show();
                    isFavorite = false;
				}
				else
				{
					//加入收藏
					isFavorite = true;
					collectBtn.setImageResource(favoIcons[1]);
//					FavoItemDbHelper
//							.insert(db, title, pubdate, itemDetail,
//									link, firstImgUrl, sectionTitle, sectionUrl);
                    mCurrentItem.setUser(mCurrentUser);
                    if(mCurrentItem.save()){
                        Toast.makeText(ItemDetail.this, "收藏成功!", Toast.LENGTH_SHORT)
                                .show();

                        UserEntity userEntity = new UserEntity();
                        List<FeedItem> list = mCurrentUser.getFeeditemList();
                        Log.i("UserDB","-----currentuserFeeditems:"+list.size());
                        list.add(mCurrentItem);
                        userEntity.setFeeditemList(list);
                        userEntity.update(mCurrentUser.getId());

                        ToastUtils.show(ItemDetail.this, "文章已经存入帐号");
                    }
                }
				Intent intent = new Intent();
				intent.putExtra("link", link);
				intent.putExtra("is_favorite", isFavorite);
				intent.setAction(ActionLabelUtils.FAVORITE_ACTION);
				sendBroadcast(intent);
				
				new Thread()
				{
					@Override
					public void run()
					{
						SeriaHelper helper = SeriaHelper.newInstance();
						File cache = AppContext.getSectionCache(sectionUrl);
						ItemListEntity entity = (ItemListEntity) helper
								.readObject(cache);
						ArrayList<FeedItem> items = entity.getItemList();
						for (FeedItem f : items)
						{
							if (f.getLink().equals(link))
								f.setFavorite(isFavorite);
						}
						entity.setItemList(items);
						helper.saveObject(entity, cache);
					}
				}.start();
			}
		});
//		countTv = (TextView) findViewById(R.id.fid_tv_comment_count);
		mWebView = (WebView) findViewById(R.id.webview_detail);
		mWebView.getSettings().setLayoutAlgorithm(LayoutAlgorithm.SINGLE_COLUMN);
		mWebView.getSettings().setJavaScriptEnabled(true);
	}
	
	private void loadData()
	{
		Intent intent = getIntent();
		sectionTitle = intent.getStringExtra("section_title");
		sectionUrl = intent.getStringExtra("section_url");
//		firstImgUrl = intent.getStringExtra("first_img_url");
        firstImgUrl = mCurrentItem.getFirstImageUrl();
		
		StringBuffer sb = new StringBuffer();
        /*
		title = intent.getStringExtra("title");
		pubdate = intent.getStringExtra("pubdate");

		link = intent.getStringExtra("link");
        */
        title = mCurrentItem.getTitle();
        pubdate = mCurrentItem.getPubdate();
        itemDetail = intent.getStringExtra("item_detail");
        link = mCurrentItem.getLink();


		//过滤style
		itemDetail = itemDetail.replaceAll(HtmlFilter.regexpForStyle, "");
		//过滤img宽和高
		itemDetail = itemDetail.replaceAll("(<img[^>]*?)\\s+width\\s*=\\s*\\S+", "$1");
		itemDetail = itemDetail.replaceAll(
				"(<img[^>]*?)\\s+height\\s*=\\s*\\S+", "$1");
//		//图片双击
//		 itemDetail = itemDetail.replaceAll("(<img[^>]+src=\")(\\S+)\"",
//					"$1$2\" onClick=\"javascript:mWebViewImageListener.onImageClick('$2')\"");
//		 mWebView.addJavascriptInterface(this, "mWebViewImageListener");
		 //是否加载图片
		SharedPreferences pref = AppContext.getPrefrences(this);
		if(!pref.getBoolean("pref_imageLoad", true))
		{
			itemDetail = itemDetail.replaceAll("(<|;)\\s*(IMG|img)\\s+([^;^>]*)\\s*(;|>)", "");
		}
		sb.append("<h1>" + title + "</h1>");
		sb.append("<body>" + itemDetail + "</body>");
		mWebView.loadDataWithBaseURL(null, css + sb.toString(), "text/html", "UTF-8", null);
	}
	
	public void onImageClick(String url)
	{
		Intent intent = new Intent();
		intent.putExtra("url", url);
		intent.setClass(this, ImageDialog.class);
		startActivity(intent);
	}
	
//	private void openCommentUi()
//	{
//		Intent intent = new Intent();
//		intent.setClass(ItemDetail.this, CommentUI.class);
//		ItemDetail.this.startActivity(intent);
//	}
}


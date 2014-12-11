/**
 * 
 */
package sysu.project.lee.sportslife.News.UI;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import java.util.ArrayList;

import sysu.project.lee.sportslife.News.Utils.AppContext;
import sysu.project.lee.sportslife.News.Utils.ImageLoader;
import sysu.project.lee.sportslife.R;


/**
 * @author:	zcloud
 *
 * @description: TODO
 *
 * @date: 2013/10/14
 *
 */
public class ItemListAdapter extends BaseAdapter
{
	public static final String tag = "ItemListAdapter";
	private LayoutInflater inflater;
	private ArrayList<FeedItem> items;
	private ArrayList<String> imageUrls = new ArrayList<String>();
	private ImageLoader loader = new ImageLoader();
	private static int[] colors;//是否已阅读显示的颜色
	private boolean loadImg = false;
	
	public ItemListAdapter(Context context, ArrayList<FeedItem> items, boolean isNight)
	{
		this.items = items;
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		Bitmap defBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher);
		loader.setDefBitmap(defBitmap);
		Resources res = context.getResources();
		colors = new int[]{
				res.getColor(R.color.black),
				res.getColor(R.color.gray)
		};
		if(isNight)
		{
			colors = new int[]{
					res.getColor(R.color.white),
					res.getColor(R.color.gray)
			};
		}
		//是否加载图片
		SharedPreferences prefs = AppContext.getPrefrences(context);
		loadImg = prefs.getBoolean("pref_imageLoad", true);
	}



    @Override
	public int getCount()
	{
		return items.size();
	}

	@Override
	public FeedItem getItem(int position)
	{
		return items.get(position);
	}

	@Override
	public long getItemId(int position)
	{
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
	{
		ViewHolder viewHolder = null;
		int colorState = 0;
		
		if(convertView == null)
		{
			convertView = inflater.inflate(R.layout.news_list_item, null);
			viewHolder = new ViewHolder();
			viewHolder.itemIv = (ImageView) convertView.findViewById(R.id.news_item_img);
			viewHolder.titleTv = (TextView) convertView.findViewById(R.id.news_item_title);
			viewHolder.pubdateTv = (TextView) convertView.findViewById(R.id.news_item_time);
			convertView.setTag(viewHolder);
		}
		else
		{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		FeedItem item = items.get(position);
		String title = item.getTitle();
		if(title.length() > 30)
			title = title.substring(0, 30);
		if(item.isReaded())
			colorState = 1;
//		else
//			colorState = 0;
		viewHolder.titleTv.setTextColor(colors[colorState]);
		viewHolder.titleTv.setText(title);
		viewHolder.pubdateTv.setText(item.getPubdate());
		imageUrls = item.getImageUrls();
		if(imageUrls.isEmpty() || !loadImg)
		{
			viewHolder.itemIv.setVisibility(View.GONE);
		}
		else
		{
			loader.loadImage(imageUrls.get(0), viewHolder.itemIv, viewHolder.itemIv.getWidth(), viewHolder.itemIv.getHeight());
		}
		return convertView;
	}

	private static final class ViewHolder
	{
		ImageView itemIv;
		TextView titleTv;
		TextView pubdateTv;
	}

	public void addItemsToHead(ArrayList<FeedItem> newItems)
	{
		items.addAll(0, newItems);
		notifyDataSetChanged();
	}
}

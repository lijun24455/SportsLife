package sysu.project.lee.sportslife.News.Utils;

import java.io.File;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.widget.ImageView;


/**
 * @description 加载图片工具类
 * @author lee
 */
public class ImageLoader
{
	public static final String tag = "ImageLoader";
	private static HashMap<String, SoftReference<Bitmap>> cache;
	private static Map<ImageView, String> imageViews;
	private static ExecutorService pool;
	private Bitmap defBitmap;
	
	static
	{
		cache = new HashMap<String, SoftReference<Bitmap>>();
		pool = Executors.newFixedThreadPool(5);
		imageViews = Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
	}

    /**
     * 从缓存中获得图片
     *
     * @param url   String类型，图片地址的url
     * @return  Bitmap类型，缓存的图片
     */
	public Bitmap getCacheImage(String url)
	{
		Bitmap bmp = null;
		
		if(cache.containsKey(url))
		{
			bmp = cache.get(url).get();
		}
		return bmp;
	}

    /**
     * @deprecated 当缓存中没有需要的图片时候，加载图片方法
     * @param url   String类型，图片地址URL
     * @param imageView ImageView类型，图片显示控件
     * @param width int类型，图片宽度
     * @param height    int类型，图片高度
     */
	public void loadImage(String url, ImageView imageView, int width, int height)
	{
		imageViews.put(imageView, url);
		Bitmap bmp = getCacheImage(url);
		
		if(bmp != null)
		{
			imageView.setImageBitmap(bmp);
		}
		else
		{
			File file = AppContext.getSdImgCache(url);
			if(file.exists())
			{
				try
				{
					bmp = BitmapFactory.decodeFile(file.getAbsolutePath());
					imageView.setImageBitmap(bmp);
				} catch (OutOfMemoryError e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				//下载网络图片
				imageView.setImageBitmap(defBitmap);
				loadNetImage(url, imageView, width, height);
			}
		}
	}
	
	@SuppressLint("HandlerLeak")
	private void loadNetImage(final String url, final ImageView imageView, final int width, final int height)
	{
		final Handler handler = new Handler()
		{
			@Override
			public void handleMessage(Message msg)
			{
				String tag = imageViews.get(imageViews);
				if(tag != null && tag.equals(url))
				{
					if(msg.obj != null)
					{
						imageView.setImageBitmap((Bitmap) msg.obj);
					}
				}
			}
		};
		
		pool.execute(new Runnable(){
			@Override
			public void run()
			{
				try
				{
					InputStream is = HttpUtils.getInputStream(url);
					Bitmap bmp = BitmapFactory.decodeStream(is);
//					bmp = Bitmap.createScaledBitmap(bmp, width, height, true);
					cache.put(url, new SoftReference<Bitmap>(bmp));
					
					Message msg = handler.obtainMessage();
					msg.obj = bmp;
					handler.sendMessage(msg);
					ImageUtils.saveImageToSD(bmp, url);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	public void setDefBitmap(Bitmap defBitmap)
	{
		this.defBitmap = defBitmap;
	}
}
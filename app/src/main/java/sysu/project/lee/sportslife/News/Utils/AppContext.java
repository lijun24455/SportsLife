package sysu.project.lee.sportslife.News.Utils;

import java.io.File;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;



/**
 * 网络环境检测，缓存调整工具类
 *
 */
public class AppContext
{
	
	public static boolean isNetworkAvailable(Context context)
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if(networkInfo == null || networkInfo.isConnected() == false)
		{
			return false;
		}
		return true;
	}

	public static boolean isWifi(Context context)
	{
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE); 
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return networkInfo.getType() == ConnectivityManager.TYPE_WIFI;
	}
	
	public static SharedPreferences getPrefrences(Context context)
	{
		return PreferenceManager.getDefaultSharedPreferences(context);
	}
	
	//清楚缓存
	public static void clearCache(Context context)
	{
		clearSdCache();
//		clearWebViewCache(context);
	}
	
	public static void clearSdCache()
	{
		FileUtils.deleteDirectory(AppConfig.APP_SECTION_DIR);
		FileUtils.deleteDirectory(AppConfig.APP_IMAGE_CACHE_DIR);
	}
	

	public static File getSdImgCache(String url)
	{
		return new File(AppConfig.APP_IMAGE_CACHE_DIR
				+ File.separator + MD5.Md5(url));
	}
	
	public static File getSectionCache(String url)
	{
		return new File(AppConfig.APP_SECTION_DIR 
				+ File.separator + MD5.Md5(url));
	}
}

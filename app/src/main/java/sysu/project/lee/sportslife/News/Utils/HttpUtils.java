package sysu.project.lee.sportslife.News.Utils;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpStatus;

import android.util.Log;

/**
 * @author:	lee
 *
 * @description: Http链接工具类
 *
 */
public class HttpUtils
{
	private static HttpURLConnection conn = null;
	public static final String tag = "HttpUtils";
	private static final int TIMEOUT = 1000*10;
	
	/**
     * @deprecated 根据URL获得输入流对象
	 * @param url   String类型，网络地址URL
	 * @return InputStream类型，输入流对象
	 * @throws Exception 
	 */
	public static InputStream getInputStream(String url) throws Exception
	{
		InputStream is = null;
		URL httpURL = null;
		
		httpURL = new URL(url);
		conn = (HttpURLConnection) httpURL.openConnection();
		conn.setConnectTimeout(TIMEOUT);
		conn.setReadTimeout(TIMEOUT);
		if(conn.getResponseCode() == HttpStatus.SC_OK)
		{
			Log.d(tag, "connected!");
			is = conn.getInputStream();
		}
		return is;
	}
	
	public static void disConnect()
	{
		if(null != conn)
		{
			conn.disconnect();
		}
	}
}

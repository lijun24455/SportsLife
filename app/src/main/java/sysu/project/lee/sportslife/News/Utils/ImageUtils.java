package sysu.project.lee.sportslife.News.Utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Matrix;


/**
 * 图片工具集合类
 * @author lee
 */
public class ImageUtils
{
	public static final CompressFormat mCompressForamat = CompressFormat.JPEG;
	public static final int mQuality = 70;

    /**
     * @deprecated 获得制定尺寸的缩放图片
     * @param bitmap    Bitmap类型，需要被处理的图片
     * @param w     int类型，需要缩放到的宽度
     * @param h     int类型，需要缩放到的高度
     * @return      Bitmap类型，返回缩放后的图片
     */
	public static Bitmap zoomBitmap(Bitmap bitmap, int w, int h)
	{
		Bitmap newBitmap = null;
		if (bitmap != null)
		{
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			
			float scaleWidth = ((float) w/width);
			float scaleHeight = ((float) h/height);
			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			newBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
		}
		return newBitmap;
	}
	
	
	public static void saveImageToSD(InputStream is, String path, String fileName)
	{
		FileUtils.saveToFile(is, path, fileName);
	}

    /**
     * @deprecated 将图片保存到SD卡
     * @param bmp   Bitmap类型，要保存的图片
     * @param url   String类型，图片URL地址
     */
	public static void saveImageToSD(Bitmap bmp, String url)
	{
		try
		{
			File file = FileUtils.newAbsoluteFile(AppConfig.APP_IMAGE_CACHE_DIR 
					+ File.separator + MD5.Md5(url));
			FileOutputStream fos = new FileOutputStream(file);
			bmp.compress(mCompressForamat, mQuality, fos);
		}
		catch(IOException e1)
		{
			e1.printStackTrace();
		}
		
	}
	
	 public static int dip2px(Context context, float dpValue) 
	 {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (dpValue * scale + 0.5f);  
	  }  
	
	 public static int px2dip(Context context, float pxValue)
	 {  
	        final float scale = context.getResources().getDisplayMetrics().density;  
	        return (int) (pxValue / scale + 0.5f);  
	 }
}
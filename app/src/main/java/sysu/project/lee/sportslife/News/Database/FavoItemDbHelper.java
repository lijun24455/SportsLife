package sysu.project.lee.sportslife.News.Database;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
 * 新闻资讯收藏夹数据操作工具类，内实现插入删除操作
 */

public class FavoItemDbHelper
{	
	public static final String TITLE = "title";
	public static final String PUBDATE = "pubdate";
	public static final String DETAIL = "item_detail";
	public static final String LINK = "link";
	public static final String FIRST_IMG_URL = "first_img_url";
	public static final String SECTION_TITLE = "table_name";
	public static final String SECTION_URL = "table_url";

    /**
     * 插入收藏条目
     *
     * @param db    数据库实例
     * @param title         String类型，标题
     * @param pubdate       String类型，发布时间
     * @param itemDetail    String类型，内容
     * @param link          String类型，链接
     * @param firstImgUrl   String类型，第一张图片Url地址
     * @param sectionTitle  String类型，模块标题
     * @param sectionUrl    String类型，模块Url地址
     */
	public static void insert(SQLiteDatabase db, String title, 
			String pubdate, String itemDetail, String link, 
			String firstImgUrl, String sectionTitle, String sectionUrl)
	{
		ContentValues values = new ContentValues();
		values.put(TITLE, title);
		values.put(PUBDATE, pubdate);
		values.put(DETAIL, itemDetail);
		values.put(LINK, link);
		values.put(FIRST_IMG_URL, firstImgUrl);
		values.put(SECTION_TITLE, sectionTitle);
		values.put(SECTION_URL, sectionUrl);
		
		db.insert(DbConstant.FAVO_TABLE_NAME, null, values);
		db.close();
	}

    /**
     * 删除指定收藏条目
     *
     * @param db    数据库实例
     * @param link  String类型，条目链接
     */
	public static void removeRecord(SQLiteDatabase db, String link)
	{
		db.delete(DbManager.FAVORITE_ITEM_TABLE_NAME, "link=?", new String[]{link});
		db.close();
	}
}

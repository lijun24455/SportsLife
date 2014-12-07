package sysu.project.lee.sportslife.News;

import java.io.File;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;


import sysu.project.lee.sportslife.News.Database.DbConstant;
import sysu.project.lee.sportslife.News.Utils.FileUtils;
import sysu.project.lee.sportslife.News.Utils.MD5;

public class SectionHelper
{
	
	public static void removeRecord(SQLiteDatabase db, String url)
	{
		db.delete(DbConstant.SECTION_TABLE_NAME, "url=?", new String[]{url});
		db.close();
	}
	
	public static void insert(SQLiteDatabase db, String tableName, String title, String url)
	{
		ContentValues values = new ContentValues();
		values.put("table_name", tableName);
		values.put("title", title);
		values.put("url", url);
		db.insert(DbConstant.SECTION_TABLE_NAME, null, values);
	}

	public static File newSdCache(String url)
	{
		String name = AppConfig.APP_SECTION_DIR + File.separator
					+ MD5.Md5(url);
		return FileUtils.newAbsoluteFile(name);
	}

	public static File getSdCache(String url)
	{
		return new File(AppConfig.APP_SECTION_DIR + File.separator + MD5.Md5(url));
	}
}

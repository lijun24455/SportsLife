package sysu.project.lee.sportslife.News.Database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 新闻资讯收藏夹数据操作类
 */
public class DbManager extends SQLiteOpenHelper
{
	public static final String FAVORITE_ITEM_TABLE_NAME = "favorite_item";
	
	private static final String CREATE_SECTION_TABLE =
			"create table" + " " + DbConstant.SECTION_TABLE_NAME 
			+ "(title text, url text, table_name text)";
	
	private static final String CREATE_FAVORITE_TABLE = "create table"
			+ " " + FAVORITE_ITEM_TABLE_NAME
			+ "(title text, pubdate text, item_detail text, "
			+ "link text, first_img_url text, table_name text, table_url text)";
	

	public DbManager(Context context, String name, CursorFactory factory,
			int version)
	{
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(CREATE_SECTION_TABLE);
		db.execSQL(CREATE_FAVORITE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

	}

}

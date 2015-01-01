/**
 * 
 */
package sysu.project.lee.sportslife.News.Utils;

import java.io.File;


import sysu.project.lee.sportslife.News.Utils.FileUtils;

/**
 *  存储相关工具类
 *
 */
public class AppConfig
{
	//SD卡目录
	public static final String APP_ROOT_DIR = FileUtils.getSDRootPath() + File.separator + "SportsLife";
	public static final String APP_CACHE_DIR = APP_ROOT_DIR + File.separator + "cache";
	public static final String APP_SECTION_DIR = APP_CACHE_DIR + File.separator + "sections";
	public static final String APP_IMAGE_CACHE_DIR = APP_CACHE_DIR + File.separator + "images";
	public static final String APP_IMAGE_DIR = APP_ROOT_DIR + File.separator + "images";

	
}

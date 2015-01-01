package sysu.project.lee.sportslife.User;

import android.os.Environment;

import java.io.File;

import sysu.project.lee.sportslife.News.Utils.FileUtils;


/**
 * Created by lee on 14年12月13日.
 */
public class Utils {
    public static final String APP_ROOT_DIR = FileUtils.getSDRootPath() + File.separator + "SportsLife";
    public static String IMAGE_PATH_BASE_DIR = APP_ROOT_DIR + File.separator + "Image";

    public static boolean hasSdcard(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }else{
            return false;
        }

    }


}

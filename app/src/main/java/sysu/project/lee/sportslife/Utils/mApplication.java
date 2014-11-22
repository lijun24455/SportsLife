package sysu.project.lee.sportslife.Utils;

import android.app.Application;
import android.content.Context;

/**
 * Created by lee on 14年11月20日.
 */
public class mApplication extends Application{
    private static Context context;

    @Override
    public void onCreate() {
        //获取Context
        context = getApplicationContext();
    }

    //返回
    public static Context getContextObject(){
        return context;
    }

}

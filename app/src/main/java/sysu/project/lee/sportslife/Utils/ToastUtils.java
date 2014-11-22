package sysu.project.lee.sportslife.Utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by lee on 14年11月20日.
 */
public class ToastUtils {

    public static void show(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
}

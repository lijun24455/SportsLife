package sysu.project.lee.sportslife.Utils;

/**
 * Created by lee on 14年10月25日.
 */
public interface HttpCallbackListener {
    void onFinish(String response);
    void onError(Exception e);
}

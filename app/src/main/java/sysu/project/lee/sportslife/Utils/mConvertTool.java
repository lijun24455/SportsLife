package sysu.project.lee.sportslife.Utils;

/**
 * Created by lee on 14年11月25日.
 */
public class mConvertTool {

    public static String parseSecondToTimeFormat(String second){

        int date = Integer.parseInt(second);

        String strSecond = date % 60 + "";
        String strMinutes = (date / 60) % 60 + "";
        String strHours = (date / 3600) + "";

        if (strSecond.length() == 1){
            strSecond = 0 + strSecond;
        }
        if (strMinutes.length() == 1){
            strMinutes = 0 + strMinutes;
        }
        if (strHours.length() == 1){
            strHours = 0 + strHours;
        }

        return strHours+"''"+strMinutes+"''"+strSecond;
    }

}

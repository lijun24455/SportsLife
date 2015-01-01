package sysu.project.lee.sportslife.News.Utils;


/**
 *  日期格式转换工具类
 *
 */
public class DateUtils
{

	/**
     * @deprecated  将日期数据格式化为mm月dd日XX：XX的格式
     *
	 * @param date String类型，日期数据字符串
	 * @return String类型，mm月
	 */
	public static String rfcNormalDate(String date)
	{
		String[] strs = date.split("\\s+|:");
		
		return enNumberMonth(strs[2])
					   + "月" + strs[1] + "日" + ' ' + strs[4]
					   + ":" + strs[5];
	}
	
	private static int enNumberMonth(String month)
	{
		String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun",
				"Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
		
		for(int i = 0; i < months.length; i++)
		{
			if(months[i].equalsIgnoreCase(month))
				return i + 1;
		}
		return 0;
	}
}
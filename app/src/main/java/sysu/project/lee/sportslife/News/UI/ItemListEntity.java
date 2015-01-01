package sysu.project.lee.sportslife.News.UI;


import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * 资讯列表实体类
 */
public class ItemListEntity implements Serializable
{
	private ArrayList<FeedItem> itemList = new ArrayList<FeedItem>();

    /**
     * 获得资讯列表
     *
     * @return  ArrayList类型，资讯列表
     */
	public ArrayList<FeedItem> getItemList()
	{
		return itemList;
	}

	public void setItemList(ArrayList<FeedItem> itemList)
	{
		this.itemList = itemList;
	}

    /**
     * 获得资讯列表数据源中的第一条数据
     *
     * @return FeedItem类型，第一条资讯
     */
	public FeedItem getFirstItem()
	{
		return itemList.get(0);
	}

    public String toString(){
        return "entity of ItemListEntity is created already!!";
    }

}

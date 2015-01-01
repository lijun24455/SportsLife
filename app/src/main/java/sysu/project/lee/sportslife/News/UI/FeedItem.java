package sysu.project.lee.sportslife.News.UI;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;

import sysu.project.lee.sportslife.User.UserEntity;


/**
 * 资讯实体类
 *
 * */

@SuppressWarnings("serial")
public class FeedItem extends DataSupport implements Serializable
{
    private long Id;
    private UserEntity user;
    private long userentity_id;
    private String title;
    private String content;
    private String link;
    private String pubdate;
    private String category;
    private String firstImageUrl;
    private boolean readed = false;
    private boolean favorite = false;
    private ArrayList<String> imageUrls = new ArrayList<String>();


    public long getUserentity_id() {
        return userentity_id;
    }

    public void setUserentity_id(long userentity_id) {
        this.userentity_id = userentity_id;
    }

    public UserEntity getUser() {
        return user;
    }

    public void setUser(UserEntity user) {
        this.user = user;
    }


	public String getFirstImageUrl()
	{
		return firstImageUrl;
	}
    public void setFirstImageUrl(String imageUrl)
	{
		this.firstImageUrl = imageUrl;
	}
	
	public String getTitle() 
	{
		return title;
	}
	public void setTitle(String title) 
	{
		this.title = title;
	}
	public String getLink() 
	{
		return link;
	}
	public void setLink(String link)
	{
		this.link = link;
	}
	public String getPubdate() 
	{
		return pubdate;
	}
	public void setPubdate(String pubdate)
	{
		this.pubdate = pubdate;
	}
	public String getCategory() 
	{
		return category;
	}
	public void setCategory(String category)
	{
		this.category = category;
	}
	public ArrayList<String> getImageUrls()
	{
		return imageUrls;
	}
	public void setImageUrls(ArrayList<String> imageUrls)
	{
		this.imageUrls = imageUrls;
	}
	public boolean getReaded()
	{
		return readed;
	}
    public boolean isReaded()
    {
        return readed;
    }
	public void setReaded(boolean readed)
	{
		this.readed = readed;
	}
	public boolean getFavorite()
	{
		return favorite;
	}
	public void setFavorite(boolean favorite)
	{
		this.favorite = favorite;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}

    public long getId() {
        return Id;
    }
    public boolean isFavorite()
    {
        return favorite;
    }

    @Override
    public String toString() {
        return "FeedItem{" +
                "Id=" + Id +
                ", title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", userentity_id=" + userentity_id +
                ", favorite=" + favorite +
                '}';
    }
}

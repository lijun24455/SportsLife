package sysu.project.lee.sportslife.User;

import org.litepal.crud.DataSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import sysu.project.lee.sportslife.News.UI.FeedItem;
import sysu.project.lee.sportslife.News.UI.Section;

/**
 * 用户实体类
 * Created by lee on 14年12月13日.
 */
public class UserEntity extends DataSupport implements Serializable{
    private int Id;

    private String mName;

    private String mEmail;
    private String mPassword;
    private String mPhotoPath;
    private List<FeedItem> feeditemList = new ArrayList<FeedItem>();

    private List<Section> sectionList = new ArrayList<Section>();

    public List<Section> getSectionList() {
        return DataSupport.where("userentity_id = ?", String.valueOf(Id)).find(Section.class);
    }

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public void setSectionList(List<Section> sectionList) {
        this.sectionList = sectionList;
    }

    public List<FeedItem> getFeeditemList() {
        return feeditemList;
    }

    public void setFeeditemList(List<FeedItem> feeditemList) {
        this.feeditemList = feeditemList;
    }


    public String getmEmail() {
        return mEmail;
    }

    public void setmEmail(String mEmail) {
        this.mEmail = mEmail;
    }

    public String getmPassword() {
        return mPassword;
    }

    public void setmPassword(String mPassword) {
        this.mPassword = mPassword;
    }

    public String getmPhotoPath() {
        return mPhotoPath;
    }

    public void setmPhotoPath(String mPhotoPath) {
        this.mPhotoPath = mPhotoPath;
    }


    public int getId() {
        return Id;
    }


    @Override
    public String toString() {
        return "UserEntity{" +
                "Id=" + Id +
                ", mEmail='" + mEmail + '\'' +
                ", mPassword='" + mPassword + '\'' +
                ", mPhotoPath='" + mPhotoPath + '\'' +
                '}';
    }
    public List<FeedItem> getFavoriteFeedItem(){
        return DataSupport.where("userentity_id = ?", String.valueOf(Id)).find(FeedItem.class);
    }
}

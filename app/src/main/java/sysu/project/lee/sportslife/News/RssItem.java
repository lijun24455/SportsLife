package sysu.project.lee.sportslife.News;

/**
 * Created by lee on 14年12月3日.
 */
public class RssItem {

    private String title;

    private String link;
    private String pubdate;
    private String description;
    public static final String TITLE = "title";

    public static final String PUBDATE = "pubdate";
    public RssItem(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getPubdate() {
        return pubdate;
    }

    public void setPubdate(String pubdate) {
        this.pubdate = pubdate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "RssItem{" +
                "title='" + title + '\'' +
                ", link='" + link + '\'' +
                ", pubdate='" + pubdate + '\'' +
                ", description='" + description + '\'' +
                '}';
    }
}

package sysu.project.lee.sportslife.Utils;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import sysu.project.lee.sportslife.News.NewsItem;
import sysu.project.lee.sportslife.News.RssFeed;
import sysu.project.lee.sportslife.News.RssItem;

/**
 * Created by lee on 14年12月3日.
 */
public class RssHandler extends DefaultHandler {

    private RssFeed rssFeed;
    private RssItem rssItem;

    private String lastElementname = "";

    private final int RSS_TITLE = 1;
    private final int RSS_LINK = 2;
    private final int RSS_PUBDATE = 3;
    private final int RSS_DESCRIPTION = 4;

    private int currentFlag = 0;

    public RssHandler(){

    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);

        // 获取字符串
        String text = new String(ch, start, length);
        Log.i("i", "要获取的内容：" + text);

        switch (currentFlag) {
            case RSS_TITLE:
                rssItem.setTitle(text);
                currentFlag = 0;// 设置完后，重置为开始状态
                break;
            case RSS_PUBDATE:
                rssItem.setPubdate(text);
                currentFlag = 0;// 设置完后，重置为开始状态
                break;
            case RSS_LINK:
                rssItem.setLink(text);
                currentFlag = 0;// 设置完后，重置为开始状态
                break;
            case RSS_DESCRIPTION:
                rssItem.setDescription(text);
                currentFlag = 0;// 设置完后，重置为开始状态
                break;
            default:
                break;
        }

    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if ("chanel".equals(localName)) {
            // 这个标签内没有我们关心的内容，所以不作处理，currentFlag=0
            currentFlag = 0;
            return;
        }
        if ("item".equals(localName)) {
            rssItem = new RssItem();
            return;
        }
        if ("title".equals(localName)) {
            currentFlag = RSS_TITLE;
            return;
        }
        if ("description".equals(localName)) {
            currentFlag = RSS_DESCRIPTION;
            return;
        }
        if ("link".equals(localName)) {
            currentFlag = RSS_LINK;
            return;
        }
        if ("pubDate".equals(localName)) {
            currentFlag = RSS_PUBDATE;
            return;
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);

        // 如果解析一个item节点结束，就将rssItem添加到rssFeed中。
        if ("item".equals(localName)) {

            rssFeed.addItem(rssItem);
            return;
        }
    }

    @Override
    public void startDocument() throws SAXException {
        super.startDocument();
        rssFeed = new RssFeed();
        rssItem = new RssItem();
    }

    @Override
    public void endDocument() throws SAXException {
        super.endDocument();
    }

    public RssFeed getRssFeed(){
        return rssFeed;
    }
}

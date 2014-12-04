package sysu.project.lee.sportslife.Utils;

import android.content.Context;


import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;

import sysu.project.lee.sportslife.News.NewsItem;

/**
 * Created by lee on 14年12月1日.
 */
public class XMLUtility {


    public static ArrayList<NewsItem> getNewsList(Context context, String response) {
        ArrayList<NewsItem> data = null;

        String tmp = null;
        NewsItem newsItem = null;

        try {
            XmlPullParserFactory factory= XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(response));

            int eventType = xmlPullParser.getEventType();

            while(eventType != XmlPullParser.END_DOCUMENT){

                switch (eventType){
                    case XmlPullParser.START_DOCUMENT:
                        data = new ArrayList<NewsItem>();
                        break;
                    case XmlPullParser.START_TAG:
                        if ("item".equals(xmlPullParser.getName())){
                            newsItem = new NewsItem();
                        }else if ("title".equals(xmlPullParser.getName())){
                            tmp = xmlPullParser.nextText();
                            newsItem.setTitle(tmp);
                        }else if ("description".equals(xmlPullParser.getName())){
                            tmp = xmlPullParser.nextText();
                            newsItem.setDescription(tmp);
                        }else if ("pubDate".equals(xmlPullParser.getName())) {
                            tmp = xmlPullParser.nextText();
                            newsItem.setPubDate(tmp);
                        }else if ("link".equals(xmlPullParser.getName())) {
                            tmp = xmlPullParser.nextText();
                            newsItem.setLink(tmp);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if ("item".equals(xmlPullParser.getName())){
                            data.add(newsItem);
                        }
                        break;
                }
                eventType = xmlPullParser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return data;

    }
}

package sysu.project.lee.sportslife.News;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.Charset;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import info.monitorenter.cpdetector.io.ASCIIDetector;
import info.monitorenter.cpdetector.io.CodepageDetectorProxy;
import info.monitorenter.cpdetector.io.JChardetFacade;
import info.monitorenter.cpdetector.io.ParsingDetector;
import info.monitorenter.cpdetector.io.UnicodeDetector;
import sysu.project.lee.sportslife.Utils.RssHandler;

/**
 * Created by lee on 14年12月3日.
 */
public class RssFeed_SAXParser {

    public RssFeed getFeed(String urlStr) throws ParserConfigurationException,
            SAXException, IOException {
        URL url = new URL(urlStr);
        SAXParserFactory saxParserFactory = SAXParserFactory.newInstance(); // 构建SAX解析工厂
        SAXParser saxParser = saxParserFactory.newSAXParser(); // 解析工厂生产解析器
        XMLReader xmlReader = saxParser.getXMLReader(); // 通过saxParser构建xmlReader阅读器

        RssHandler rssHandler = new RssHandler();
        xmlReader.setContentHandler(rssHandler);
        // 使用url打开流，并将流作为 xmlReader解析的输入源并解析

        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        // 向代理对象添加探测器
        detector.add(JChardetFacade.getInstance());
        // 得到编码字符集对象
        Charset charset = detector.detectCodepage(url);
        // 得到编码名称
        String encodingName = charset.name();
        System.out.println(encodingName);

        InputSource inputSource = null;
        InputStream stream = null;

        // 如果是GB2312编码
        if ("GBK".equals(encodingName)) {
            stream = url.openStream();
            // 通过InputStreamReader设定编码方式
            InputStreamReader streamReader = new InputStreamReader(stream,
                    encodingName);
            inputSource = new InputSource(streamReader);
            xmlReader.parse(inputSource);
            return rssHandler.getRssFeed();
        } else {
            // 是utf-8编码
            inputSource = new InputSource(url.openStream());
            inputSource.setEncoding("UTF-8");
            xmlReader.parse(inputSource);
            return rssHandler.getRssFeed();
        }
    }

    /**
     * 获得远程URL文件的编码格式
     */
    public static String getReomoteURLFileEncode(URL url) {

        CodepageDetectorProxy detector = CodepageDetectorProxy.getInstance();
        detector.add(new ParsingDetector(false));
        detector.add(JChardetFacade.getInstance());
        detector.add(ASCIIDetector.getInstance());
        detector.add(UnicodeDetector.getInstance());
        java.nio.charset.Charset charset = null;
        try {
            System.out.println(url);
            charset = detector.detectCodepage(url);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (charset != null) {
            return charset.name();
        } else {
            return "utf-8";
        }
    }


}

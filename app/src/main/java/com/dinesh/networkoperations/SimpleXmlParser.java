package com.dinesh.networkoperations;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dinesh on 10/20/2016.
 */
public class SimpleXmlParser {
    public class NewsItem {
        public String title;
        public String link;
        public String description;

        NewsItem(String t, String l, String d) {
            title = t;
            link = l;
            description = d;
        }
    }
    public List<NewsItem> parse (InputStream xml){
        List<NewsItem> newsItems = null;
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(xml, null);
            parser.nextTag();
            newsItems = readXML(parser);
        }catch (XmlPullParserException e){

        }catch (IOException e){

        }
    return newsItems;
    }
    public List<NewsItem> readXML(XmlPullParser parser)throws XmlPullParserException, IOException {
        List<NewsItem> newsItem = new ArrayList<>();
        parser.require(XmlPullParser.START_TAG,null, "rss");
        while(parser.next()!= XmlPullParser.START_TAG){
            continue;
        }
        parser.require(XmlPullParser.START_TAG,null,"channel");

        while (parser.next()!= XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }

            String name = parser.getName();
            if (name.equals("item")) {
                newsItem.add(readItem(parser));
            } else {
                skip(parser);
            }
        }
        return newsItem;

    }
    public NewsItem readItem(XmlPullParser parser) throws XmlPullParserException, IOException{
        parser.require(XmlPullParser.START_TAG, null, "item");
        String title=" ";
        String link= " ";
        String description =" ";
        while (parser.next()!= XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("title")) {
                title = readTitle(parser);

            } else if (name.equals("link")) {
                link = readLink(parser);

            } else if (name.equals("description")) {
                description = readDescription(parser);
            } else {
                skip(parser);
            }
        }
            return new NewsItem(title, link, description);

    }

    public String readTitle(XmlPullParser parser) throws XmlPullParserException,IOException{
        String title;
        parser.require(XmlPullParser.START_TAG,null,"title");
        title= readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "title");
        return title;

    }
    public String readLink(XmlPullParser parser) throws XmlPullParserException,IOException{
        String title;
        parser.require(XmlPullParser.START_TAG,null,"link");
        title= readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "link");
        return title;

    }
    public String readDescription(XmlPullParser parser) throws XmlPullParserException,IOException{
        String title;
        parser.require(XmlPullParser.START_TAG,null,"description");
        title= readText(parser);
        parser.require(XmlPullParser.END_TAG, null, "description");
        return title;

    }
    public String readText(XmlPullParser parser) throws XmlPullParserException,IOException{
        String text = null;
        if (parser.next() == XmlPullParser.TEXT){
            text = parser.getText();
            parser.nextTag();
                }
        return text;

    }
    public void skip(XmlPullParser parser) throws XmlPullParserException, IOException{
        if (parser.getEventType()!= XmlPullParser.START_TAG){
            throw new IllegalStateException();
        }
        int  depth = 1;

        while ( depth!=0){
            int next = parser.next();

            if (next == XmlPullParser.END_TAG){
                depth--;
            }else if (next == XmlPullParser.START_TAG){
                depth++;
            }
        }
    }

}

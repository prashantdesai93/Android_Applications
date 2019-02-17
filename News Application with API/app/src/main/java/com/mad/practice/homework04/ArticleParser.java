package com.mad.practice.homework04;

import android.util.Log;
import android.util.Xml;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * Created by hp on 2/22/2018.
 */

public class ArticleParser {
    static public class NewsParser {

        static ArrayList<Article> parseNews(InputStream in) {

            try {



                XmlPullParser parser = XmlPullParserFactory.newInstance().newPullParser();

                parser.setInput(in,"UTF-8");

                ArrayList<Article> newslist = new ArrayList<Article>();

                Article tempnews=null;

                int event=parser.getEventType();
                parser.next();
                int tempflag=0;

                while (event!=XmlPullParser.END_DOCUMENT && tempflag==0)
                {
                    switch(event)
                    {
                        case XmlPullParser.START_TAG:   if(parser.getName().equals("item"))
                        {
                            tempflag=1;
                        }
                        else
                        {
                            event=parser.next();
                        }

                            break;
                        default: event=parser.next();
                            break;
                    }
                }

                while (event!=XmlPullParser.END_DOCUMENT)
                {
                    switch(event)
                    {
                        case XmlPullParser.START_TAG:
                            if(parser.getName().equals("item"))
                                tempnews= new Article();
                            if(parser.getName().equals("title"))
                                tempnews.setTitle(parser.nextText().trim());
                            else if(parser.getName().equals("description"))
                                tempnews.setDescription(parser.nextText().trim());
                            else if(parser.getName().equals("pubDate"))
                                tempnews.setPublishedAt(parser.nextText().trim());
                            else if(parser.getName().equals("link"))
                                tempnews.setLink(parser.nextText().trim());
                            else if( parser.getName().equals("media:content"))
                            {
                                   tempnews.setUrlToImg(parser.getAttributeValue(null,"url"));
                            }

                            break;

                        case XmlPullParser.END_TAG: if (parser.getName().equals("item"))
                        {
                            newslist.add(tempnews);
                            tempnews=null;
                        }
                            break;

                        default: break;
                    }

                    event=parser.next();
                }

                return newslist;

            } catch (Exception e) {
                Log.d("debug", e.getMessage());
            }

            return null;
        }
    }

}

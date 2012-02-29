package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;

import com.sun.cnpi.rss.elements.Item;
import com.sun.cnpi.rss.elements.PubDate;
import com.sun.cnpi.rss.elements.Rss;
import com.sun.cnpi.rss.parser.RssParser;
import com.sun.cnpi.rss.parser.RssParserException;
import com.sun.cnpi.rss.parser.RssParserFactory;

import models.Choice;
import models.Feed;
import models.Topic;

/* This class is in charge of subscribing to, parsing, and saving data from different RSS feeds (mostly
 * news sources)
 */
public class RSSEngine {
	public static enum ReadState {
		NONE, TITLE, DESCRIPTION, CONTENT, LINK, PUBDATE
	}
	
	public static void fetchNews() {
		List<Feed> feeds= Feed.findAll();
		Iterator<Feed> feedIter = feeds.iterator();
		while (feedIter.hasNext()) {
			fetchFeed(feedIter.next());
		}
	}
	
	public static void fetchFeed(Feed feed) {
		try {
			RssParser rp = RssParserFactory.createDefault();
			Rss rss = rp.parse(new URL(feed.link));
			
			Collection items = rss.getChannel().getItems();
	        if(items != null && !items.isEmpty())
	        {
	        	Iterator i = items.iterator();
	        	Date recentUpdate = feed.lastUpdate;
	            while (i.hasNext())
	            {
	            	try {
		            	Topic topic = new Topic();
		                Item item = (Item)i.next();
		                DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
						Date date = formatter.parse(item.getPubDate().getText());

		                if (date.after(feed.lastUpdate)) {
		                	if (date.after(recentUpdate)) {
		                		recentUpdate = date;
		                	}
		                	
		                    String title = StringEscapeUtils.unescapeHtml(item.getTitle().getText());
			                String link = StringEscapeUtils.unescapeHtml(item.getLink().getText());
			                String description = StringEscapeUtils.unescapeHtml(item.getDescription().getText());
			                if (title.length() > 255) {
			                	title = title.substring(0, 255);
			                }
			                topic.title = title;
			                topic.link = link;
			                topic.description = description;
			                topic.tags.addAll(feed.tags);
			                topic.save();
		                } else {
		                	break;
		                }
	            	} catch (NullPointerException npe) {
	            		continue;
	            	} catch (ParseException e) {
						e.printStackTrace();
					}
	            }
	            feed.lastUpdate = recentUpdate;
	            feed.save();
	        }
		} catch (RssParserException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

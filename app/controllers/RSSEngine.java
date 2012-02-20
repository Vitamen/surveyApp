package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;

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
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			URL newsURL;
			newsURL = new URL(feed.link);
			InputStream is;
			// Setup a new eventReader
			is = newsURL.openStream();
			
			XMLEventReader eventReader = inputFactory.createXMLEventReader(is);
			Date mostRecentUpdate = new Date(0);
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					if (event.asStartElement().getName().getLocalPart().compareToIgnoreCase("item") == 0) {
						Date updateDate = fetchTopic(feed, eventReader);
						if (updateDate.after(mostRecentUpdate)) {
							mostRecentUpdate = updateDate;
						}
					}
				}
			}
			
			if (mostRecentUpdate.after(feed.lastUpdate)) {
				feed.lastUpdate = mostRecentUpdate;
				feed.save();
			}
		} catch (MalformedURLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e) {
			throw new RuntimeException(e);
		} catch (XMLStreamException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static Date fetchTopic(Feed feed, XMLEventReader eventReader) throws XMLStreamException {
		Topic topic = new Topic();
		Date updateDate = null;
		boolean shouldSave = false;
		ReadState readState = ReadState.NONE;
		while (eventReader.hasNext()) {
			XMLEvent event = eventReader.nextEvent();
			
			if (event.isStartElement()) {
				String startEventElementString = event.asStartElement().getName().getLocalPart();
				String startEventNamespaceString = event.asStartElement().getName().getNamespaceURI();
				if (!startEventNamespaceString.isEmpty()) {
					continue;
				}
				if (startEventElementString.compareToIgnoreCase("title") == 0) {
					readState = ReadState.TITLE;
				} else if (startEventElementString.compareToIgnoreCase("content") == 0) {
					readState = ReadState.CONTENT;
				} else if (startEventElementString.compareToIgnoreCase("description") == 0) {
					readState = ReadState.DESCRIPTION;
				} else if (startEventElementString.compareToIgnoreCase("link") == 0) {
					readState = ReadState.LINK;
				} else if (startEventElementString.compareToIgnoreCase("pubDate") == 0) {
					readState = ReadState.PUBDATE;
				}
			} else if (event.isCharacters()) {
				String data = event.asCharacters().getData();
				switch (readState) {
					case NONE:
						break;
					case TITLE:
						topic.title = data;
						break;
					case CONTENT:
						topic.content = data;
						break;
					case DESCRIPTION:
						topic.description = data;
						break;
					case LINK:
						topic.link = data;
						break;
					case PUBDATE:
						Date date = null;
						try {
							DateFormat formatter = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
							date = formatter.parse("Sat, 24 Apr 2010 14:01:00 GMT");
						} catch (ParseException e) {
							e.printStackTrace();
						}
						if (date.after(feed.lastUpdate)) {
							shouldSave = true;
						}
						updateDate = date;
					default:
						break;
				}
			} else if (event.isEndElement()){
				String endEventString = event.asEndElement().getName().getLocalPart();
				readState = ReadState.NONE;
				if (endEventString.compareToIgnoreCase("item") == 0) {
					if (topic != null) {
						topic.save();
						return updateDate;
					}
				}
			}
		}
		return updateDate;
	}
}

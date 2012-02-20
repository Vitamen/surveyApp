package controllers;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;

/* This class is in charge of subscribing to, parsing, and saving data from different RSS feeds (mostly
 * news sources)
 */
public class RSSEngine {

	public static void fetchNews() {
		try {
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			URL newsURL;
			newsURL = new URL("http://feeds.feedburner.com/TechCrunch/");
			InputStream is;
			// Setup a new eventReader
			is = newsURL.openStream();
			
			XMLEventReader eventReader = inputFactory.createXMLEventReader(is);
			
			while (eventReader.hasNext()) {
				XMLEvent event = eventReader.nextEvent();
				if (event.isStartElement()) {
					System.out.println("Start Element: "+event.asStartElement().getName().getLocalPart());
				} else if (event.isCharacters()) {
					System.out.println("         --> " + event.asCharacters().getData());
				} else if (event.isEndElement()){
					System.out.println("End Element: "+event.asEndElement().getName().getLocalPart());
				}
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
}

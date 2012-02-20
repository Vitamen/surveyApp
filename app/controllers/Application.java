package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

	public static String[] feedLinks = {"http://feeds.feedburner.com/TechCrunch/"};
	
    public static void index() {
    	if (generateFeeds()) {
        	RSSEngine.fetchNews();
        	Topic topic1 = fetchTopic();
        	Topic topic2 = fetchTopic();
        	
        	renderArgs.put("topic1", topic1);
        	renderArgs.put("topic2", topic2);
            render();
    	}
    }
    
    public static boolean generateFeeds() {
    	Feed.deleteAll();
    	Topic.deleteAll();
    	for (int i = 0; i < feedLinks.length; i++) {
    		Feed feed = new Feed(feedLinks[i]);
    		feed.lastUpdate = new Date(0);
    		feed.save();
    	}
    	return true;
    }

    public static void fetchTopics() {
    	Topic topic1 = fetchTopic();
    	Topic topic2 = fetchTopic();
    }
    
    public static Topic fetchTopic() {
    	Topic topic = new Topic();
    	topic.title = "Title";
    	topic.description = "Description";
    	topic.content = "Content";
    	topic.link = "www.facebook.com";
    	return topic;
    }

    public static void processRequest(int topic) {
    	index();
    }
    
    public void testRequest(){
    	renderJSON("Please Place Json String here");
    }
}
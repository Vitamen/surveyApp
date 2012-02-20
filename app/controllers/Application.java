package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
    	RSSEngine.fetchNews();
    	System.out.println("done fetching news");
    	Topic topic1 = fetchTopic();
    	Topic topic2 = fetchTopic();
    	
    	renderArgs.put("topic1", topic1);
    	renderArgs.put("topic2", topic2);
        render();
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
    	System.out.println(topic);
    	index();
    }
    
    public void testRequest(){
    	renderJSON("Please Place Json String here");
    }
}
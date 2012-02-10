package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class Application extends Controller {

    public static void index() {
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
    	Topic topic = null;
    	return topic;
    }

}
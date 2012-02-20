package controllers;

import play.*;
import play.modules.facebook.FbGraph;
import play.modules.facebook.FbGraphException;
import play.modules.facebook.Parameter;
import play.mvc.*;
import play.mvc.Scope.Session;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

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
    
    public static void displayFriends(){
    	render();
    }
    
    public static void facebookLogin() {
        try {
            JsonObject profile = FbGraph.getObject("me"); // fetch the logged in user
            String email = new String("default");
            if (profile != null)
            	email = profile.get("email").getAsString(); // retrieve the email
            System.out.println(email);
                      
            // do useful things
            Session.current().put("username", email); // put the email into the session (for the Secure module)
            
            System.out.println(Session.current().getAuthenticityToken().toString());
        } catch (FbGraphException fbge) {
            flash.error(fbge.getMessage());
            if (fbge.getType() != null && fbge.getType().equals("OAuthException")) {
                Session.current().remove("username");
            }
        }
        displayFriends();
    }

    public static void facebookLogout() {
    	System.out.println("Logging out");
        Session.current().remove("username");
        FbGraph.destroySession();
        index();
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
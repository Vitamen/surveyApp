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

	static LinkedList<User> allUsers = new LinkedList<User>();
	
    public static void index() {
    	RSSEngine.fetchNews();
    	System.out.println("done fetching news");
    	Topic topic1 = fetchTopic();
    	Topic topic2 = fetchTopic();
    	
    	renderArgs.put("topic1", topic1);
    	renderArgs.put("topic2", topic2);
        render();
    }
    
    public static void getUserLikes(){
    	User loggedInUser = allUsers.get(0);
    	try {
    		String userName = loggedInUser.userName;
    		StringBuffer queryPart = new StringBuffer(userName+"/likes");
			JsonArray userLikes = FbGraph.getConnection(queryPart.toString(), Parameter.with("limit", "10").parameters());
			loggedInUser.addAllLikes(userLikes);
    	} catch (FbGraphException e) {
			e.printStackTrace();
		}
    }
    
    public static void displayFriends(){
    	//Get User Likes
    	getUserLikes();
    	render();
    }
    
    public static void facebookLogin() {
        try {
            JsonObject profile = FbGraph.getObject("me"); // fetch the logged in user
            String email = profile.get("email").getAsString(); // retrieve the email
            User loggedInUser = new User(profile.get("name").toString().replaceAll("\"", ""), profile.get("username").toString().replaceAll("\"", ""), profile.get("id").toString().replaceAll("\"", ""));
            System.out.println("Name: "+profile.get("name").toString()+"\nUser Name: "+profile.get("username").toString()+"\nID: "+profile.get("id"));
            
            allUsers.addFirst(loggedInUser);

            // do useful things
            Session.current().put("username", email); // put the email into the session (for the Secure module)
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
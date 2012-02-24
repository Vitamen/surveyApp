package controllers;

import play.*;
import play.modules.facebook.FbGraph;
import play.modules.facebook.FbGraphException;
import play.modules.facebook.Parameter;
import play.mvc.*;
import play.mvc.Scope.Session;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import models.*;

public class Application extends Controller {

	static LinkedList<User> allUsers = new LinkedList<User>();
	public static String[] feedLinks = {"http://feeds.feedburner.com/TechCrunch/"};
	public static String[] feedCategories = {"Technology"};
	
    public static void index() {
    	generateFeeds();
		// fetch the logged in user
    	System.out.println(Likes.getLikeGroupFromCategory("Software"));
    	RSSEngine.fetchNews();
    	Topic topic1 = fetchTopic();
    	Topic topic2 = fetchTopic();
 
    	Recommendation rec1 = new Recommendation(topic1);
    	Reason likeCategoryReason = Reason.getLikeCategoryReason();
    	rec1.addReason(likeCategoryReason);
    	rec1.save();
    	
    	Recommendation rec2 = new Recommendation(topic2);
    	rec2.addReason(likeCategoryReason);
    	rec2.save();
    	
    	Choice choice = new Choice();
    	choice.addRecommendation(rec1);
    	choice.addRecommendation(rec2);
    	choice.save();
    	
    	renderArgs.put("choice", choice);
    	renderArgs.put("topic1", topic1);
    	renderArgs.put("topic2", topic2);
        render();
    }
    
    public static boolean generateFeeds() {
    	Recommendation.deleteAll();
    	Choice.deleteAll();
    	Reason.deleteAll();
    	Topic.deleteAll();
    	Feed.deleteAll();
    	
    	for (int i = 0; i < feedLinks.length; i++) {
    		Feed feed = new Feed(feedLinks[i]);
    		feed.tags.add(feedCategories[i]);
    		feed.lastUpdate = new Date(0);
    		feed.save();
    	}
    	return true;
    }
    
    public static void getUserLikes(){
    	User loggedInUser = allUsers.get(0);
    	try {
    		String userName = loggedInUser.userName;
    		StringBuffer queryPart = new StringBuffer(userName+"/likes");
			JsonArray userLikes = FbGraph.getConnection(queryPart.toString(), Parameter.with("limit", "1000").parameters());
			
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
    	/*JsonObject profile;
    	User loggedInUser;
		try {
			profile = FbGraph.getObject("me");
			String email = profile.get("email").getAsString(); // retrieve the email
	        loggedInUser = new User(profile.get("name").toString().replaceAll("\"", ""), profile.get("username").toString().replaceAll("\"", ""), profile.get("id").toString().replaceAll("\"", ""));
		} catch (FbGraphException e) {
			e.printStackTrace();
		}*/
		String tag = "Technology";
				//getLeadingLikes(loggedInUser);
		
    	List<Topic> topics = Topic.find("select t from Topic t join t.tags as tag where tag = ?", tag).fetch();
   
        return topics.get(0);
    }

    public static void processRequest(int topic) {
    	index();
    }
    
    public static void processChoice(long choiceId, int selection) {
    	Choice choice = Choice.findById(choiceId);
    	if (choice == null) {
    		System.out.println("ERROR: Could not find choice with id "+choiceId);
    		return;
    	}
    	
    	choice.selection = selection;
    	index();
    }
    
    public void testRequest(){
    	renderJSON("Please Place Json String here");
    }
}

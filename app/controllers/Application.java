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
	public static String[] feedLinks = {"http://feeds.feedburner.com/TechCrunch/", 
		"http://www.wpxi.com/feeds/categories/news/",
		"http://rss.cnn.com/rss/si_topstories.rss",
		"http://feeds.feedburner.com/ChicagoBreakingSports",
		"http://sports.espn.go.com/espn/rss/news",
		"http://rss.cnn.com/rss/money_latest.rss",
		"http://online.wsj.com/xml/rss/3_7014.xml",
		"http://www.economist.com/topics/us-economy/index.xml",
		
		"http://rss.cnn.com/rss/cnn_allpolitics.rss",
		"http://rss.cnn.com/rss/cnn_travel.rss",
		"http://rss.cnn.com/rss/cnn_showbiz.rss",
		"http://www.mtv.com/rss/news/news_full.jhtml",
		"http://feeds.ew.com/entertainmentweekly/music",
		"http://rss.ew.com/web/ew/rss/media/movies/index.xml",
		"http://www.mtv.com/rss/news/movies_full.jhtml",
		"http://feeds.ew.com/entertainmentweekly/books",
		
		"http://www.engadget.com/rss.xml",
		"http://feeds.cbsnews.com/tech_talk",
		"http://www.wpxi.com/feeds/categories/events/",
		"http://www.wpxi.com/feeds/categories/news",
		"http://feeds.chicagotribune.com/chicagotribune/cars/",
		"http://news.yahoo.com/fashion/",
		"http://feeds.nytimes.com/nyt/rss/FashionandStyle",
		"http://www.wwd.com/rss/2/news/fashion",
		"http://feeds.feedburner.com/epicurious/epiblog"
	};
	public static String[] feedCategories = {"Technology", 
		"Local",
		"Sports",
		"Sports",
		"Sports",
		"Business",
		"Finance",
		"Finance",
		
		"Politics",
		"Travel",
		"Entertainment",
		"Entertainment",
		"Music",
		"Movies",
		"Movies",
		"Books",
		
		"Technology",
		"Technology",
		"Local",
		"Local",
		"Cars",
		"Fashion",
		"Fashion",
		"Fashion",
		"Food"
		};
	
    public static void index() {
    	generateFeeds();
    	String currentUser = Session.current().get("user");
    	if (currentUser != null) {
    		RecommendationEngine.index();
    	} else {
    		render();
    	}
    }

    public static boolean getUserLikes(){
    	User loggedInUser = User.findById(Long.parseLong(Session.current().get("user"))); 
    	if (loggedInUser == null) {
    		return false;
    	}
    	try {
    		String userName = loggedInUser.userName;
    		StringBuffer queryPart = new StringBuffer(userName+"/likes");
			JsonArray userLikes = FbGraph.getConnection(queryPart.toString(), Parameter.with("limit", "1000").parameters());
			
			loggedInUser.addAllLikes(userLikes);
    	} catch (FbGraphException e) {
			e.printStackTrace();
		}
    	return true;
    }
    
    public static void displayFriends(){
    	//Get User Likes
    	getUserLikes();
    	render();
    }
    
    /* Facebook Session Stuff */
    public static void facebookLogin() {
        try {
            JsonObject profile = FbGraph.getObject("me"); // fetch the logged in user
            List<User> userList = User.find("byUserId", profile.get("id").toString().replaceAll("\"", "")).fetch();
            User user;
            
            if (userList == null || userList.size() == 0) {
	             user = new User(profile.get("name").toString().replaceAll("\"", ""), profile.get("username").toString().replaceAll("\"", ""), profile.get("id").toString().replaceAll("\"", ""));
	             user.save();
            } else {
            	user = userList.get(0);
            }
            
            Session.current().put("user", user.id);
            // do useful things
            Session.current().put("username", "xxx"); // put the email into the session (for the Secure module)
        } catch (FbGraphException fbge) {
            flash.error(fbge.getMessage());
            if (fbge.getType() != null && fbge.getType().equals("OAuthException")) {
                Session.current().remove("username");
            }
        }
        RecommendationEngine.index();
    }

    public static void facebookLogout() {
    	System.out.println("Logging out");
        Session.current().remove("username");
        FbGraph.destroySession();
        index();
    }

    public static void login(String username, String password){
    	System.out.println(username);
    	JsonObject js = new JsonObject();
    	js.addProperty("status","ok");
    	renderJSON(js.toString());
    }

    public static void login(String username){
    	System.out.println(username);
    	JsonObject js = new JsonObject();
    	js.addProperty("status","ok");
    	renderJSON(js.toString());
    }

    public static JsonObject getUserInformation(String userName, String authenticationToken){
    	try {
			JsonObject user = FbGraph.getObject(userName, Parameter.with("access_token", authenticationToken).parameters());
		} catch (FbGraphException e) {
			System.out.println("There was an error in the getUserInformationMethod");
			e.printStackTrace();
		}
    	JsonObject obj = new JsonObject();
    	obj.addProperty("test", "Is Extracting");
    	return obj;
    }
    
    /* Database Stuff */
    public static boolean generateFeeds() {
    	clearAll();
    	
    	for (int i = 0; i < feedLinks.length; i++) {
    		Feed feed = new Feed(feedLinks[i]);
    		feed.tags.add(feedCategories[i]);
    		feed.lastUpdate = new Date(0);
    		feed.save();
    	}
    	return true;
    }
    
    public static boolean clearAll() {
    	List<Choice> choices = Choice.findAll();
    	Iterator<Choice> choiceItor = choices.iterator();
    	while (choiceItor.hasNext()) {
    		choiceItor.next().delete();
    	}
    	List<Recommendation> recommendations = Recommendation.findAll();
    	Iterator<Recommendation> recItor = recommendations.iterator();
    	while (recItor.hasNext()) {
    		recItor.next().delete();
    	}
    	List<Topic> topics = Topic.findAll();
    	Iterator<Topic> topicItor = topics.iterator();
    	while (topicItor.hasNext()) {
    		topicItor.next().delete();
    	}
    	List<Feed> feeds = Feed.findAll();
    	Iterator<Feed> feedItor = feeds.iterator();
    	while (feedItor.hasNext()) {
    		feedItor.next().delete();
    	}
    	return true;
    }
    
    public void testRequest(){
    	renderJSON("Please Place Json String here");
    }
}

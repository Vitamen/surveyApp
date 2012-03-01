package controllers;

import play.*;
import play.modules.facebook.FbGraph;
import play.modules.facebook.FbGraphException;
import play.modules.facebook.Parameter;
import play.mvc.*;
import play.mvc.Scope.Session;
import play.test.Fixtures;

import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import models.*;

public class Application extends Controller {
	
	
    public static void index() {
    	//generateFeeds();
    	String currentUser = Session.current().get("user");
    	User user = User.find("byUserId", currentUser).first();
    	if (user != null) {
    		RecommendationEngine.index();
    	} else {
    		render();
    	}
    	render();
    }

    public static boolean getUserLikes(){
    	User user = User.find("byUserId", Session.current().get("user")).first(); 
    	if (user == null) {
    		return false;
    	}
    	try {
    		String userId = user.userId;
    		StringBuffer queryPart = new StringBuffer(userId+"/likes");
			JsonArray userLikes = FbGraph.getConnection(queryPart.toString(), Parameter.with("limit", "1000").parameters());
			
			user.addAllLikes(userLikes);
    	} catch (FbGraphException e) {
			e.printStackTrace();
		}
    	return true;
    }
    
    public static void displayFriends(){
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
	             user = new User(profile.get("name").toString().replaceAll("\"", ""), profile.get("name").toString().replaceAll("\"", ""), profile.get("id").toString().replaceAll("\"", ""));
	             user.save();
            } else {
            	user = userList.get(0);
            }

            Session.current().put("user", user.userId);
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
        Session.current().remove("user");
        FbGraph.destroySession();
        Application.index();
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

    public static void getUserInformation(String userName, String authenticationToken){
    	try {
			JsonObject user = FbGraph.getObject(userName, Parameter.with("access_token", authenticationToken).parameters());
			if (user!=null){
				System.out.println("User: "+ user.get("username").toString()+" auth token"+authenticationToken);
			}
			else
				System.out.println("User not being created");
    	} catch (FbGraphException e) {
			System.out.println("There was an error in the getUserInformationMethod");
			e.printStackTrace();
		}
    	JsonObject obj = new JsonObject();
    	obj.addProperty("test", "Is Extracting");
    	
    	
    	JsonObject article = new JsonObject();
    	article.addProperty("Heading1", "Microsoft To Replace “Live” Branding With “Microsoft Account” In Windows 8");
    	article.addProperty("Content1","The long-running “Live” name Microsoft has placed on its many connected services (Mail, messenger, photos, etc) is coming to an end in Windows 8, as part of their ongoing, major brand rehaul. Zune, of course, has been on its way out for some time, but will receive the coup de grace in Windows 8.Their main services are being rolled into bundled applications with a native Metro look and simpler names — Mail instead of Windows Live Mail, Photos instead of Windows Live Photo Gallery, and so on. The new apps will be tightly integrated, as we’ve seen in demos, and will retain much of the Live cross-service functionality. They’ll be unified by a single “Microsoft Account.”But Live isn’t going away entirely: the name is too strong to take away from Xbox Live and its subsidiary components, and in fact Xbox Live may be coming to Windows as the main entertainment brand — for music, games, and video content. This will replace Zune, which Microsoft has been gradually sweeping under the rug over the past two years. Zune fans mustn’t despair, though: Zune pass functionality will remain intact, and chances are the old desktop player and Zune hardware will continue to be supported in some way. And the fact is that Zune has left an indelible mark on Microsoft’s operations, pioneering the look and feel found in Windows Phone 7 and Windows 8.Smaller services, like Writer and Games for Windows Live, will likely be rolled into existing products. It’s in major brand shakedowns like this that one starts to realize just how many platforms and pieces of software Microsoft actually has and supports. This coalescence of services is probably coming as a huge relief to the company, though the labor involved in repurposing them is, naturally, Herculean. Conspicuously absent from the lineup mentioned is Messenger, which may be seeing some integration with Skype. A multi-service messenger/video-chat app with Skype built in seems likely, though Skype would definitely have to have a discrete presence as well for power users. No doubt they’ll leave behind many irate users who want things to remain the same — and indeed how Microsoft intends to accommodate these legacy users isn’t clear. Their new clean-break approach maroons many people on the old Windows XP/7 mainland, where they’ll likely remain until the launch quakes of Windows 8 clear away and the new land is safe for colonization.");
    	article.addProperty("ImageTag1", "http://tctechcrunch2011.files.wordpress.com/2012/02/winlive.jpg?w=100&h=70&crop=1");
    	article.addProperty("Heading2", "Microsoft To Replace “Live” Branding With “Microsoft Account” In Windows 8");
    	article.addProperty("Content2","The long-running “Live” name Microsoft has placed on its many connected services (Mail, messenger, photos, etc) is coming to an end in Windows 8, as part of their ongoing, major brand rehaul. Zune, of course, has been on its way out for some time, but will receive the coup de grace in Windows 8.Their main services are being rolled into bundled applications with a native Metro look and simpler names — Mail instead of Windows Live Mail, Photos instead of Windows Live Photo Gallery, and so on. The new apps will be tightly integrated, as we’ve seen in demos, and will retain much of the Live cross-service functionality. They’ll be unified by a single “Microsoft Account.”But Live isn’t going away entirely: the name is too strong to take away from Xbox Live and its subsidiary components, and in fact Xbox Live may be coming to Windows as the main entertainment brand — for music, games, and video content. This will replace Zune, which Microsoft has been gradually sweeping under the rug over the past two years. Zune fans mustn’t despair, though: Zune pass functionality will remain intact, and chances are the old desktop player and Zune hardware will continue to be supported in some way. And the fact is that Zune has left an indelible mark on Microsoft’s operations, pioneering the look and feel found in Windows Phone 7 and Windows 8.Smaller services, like Writer and Games for Windows Live, will likely be rolled into existing products. It’s in major brand shakedowns like this that one starts to realize just how many platforms and pieces of software Microsoft actually has and supports. This coalescence of services is probably coming as a huge relief to the company, though the labor involved in repurposing them is, naturally, Herculean. Conspicuously absent from the lineup mentioned is Messenger, which may be seeing some integration with Skype. A multi-service messenger/video-chat app with Skype built in seems likely, though Skype would definitely have to have a discrete presence as well for power users. No doubt they’ll leave behind many irate users who want things to remain the same — and indeed how Microsoft intends to accommodate these legacy users isn’t clear. Their new clean-break approach maroons many people on the old Windows XP/7 mainland, where they’ll likely remain until the launch quakes of Windows 8 clear away and the new land is safe for colonization.");
    	article.addProperty("ImageTag2", "http://tctechcrunch2011.files.wordpress.com/2012/02/winlive.jpg?w=100&h=70&crop=1");
    	article.addProperty("Heading3", "Microsoft To Replace “Live” Branding With “Microsoft Account” In Windows 8");
    	article.addProperty("Content3","The long-running “Live” name Microsoft has placed on its many connected services (Mail, messenger, photos, etc) is coming to an end in Windows 8, as part of their ongoing, major brand rehaul. Zune, of course, has been on its way out for some time, but will receive the coup de grace in Windows 8.Their main services are being rolled into bundled applications with a native Metro look and simpler names — Mail instead of Windows Live Mail, Photos instead of Windows Live Photo Gallery, and so on. The new apps will be tightly integrated, as we’ve seen in demos, and will retain much of the Live cross-service functionality. They’ll be unified by a single “Microsoft Account.”But Live isn’t going away entirely: the name is too strong to take away from Xbox Live and its subsidiary components, and in fact Xbox Live may be coming to Windows as the main entertainment brand — for music, games, and video content. This will replace Zune, which Microsoft has been gradually sweeping under the rug over the past two years. Zune fans mustn’t despair, though: Zune pass functionality will remain intact, and chances are the old desktop player and Zune hardware will continue to be supported in some way. And the fact is that Zune has left an indelible mark on Microsoft’s operations, pioneering the look and feel found in Windows Phone 7 and Windows 8.Smaller services, like Writer and Games for Windows Live, will likely be rolled into existing products. It’s in major brand shakedowns like this that one starts to realize just how many platforms and pieces of software Microsoft actually has and supports. This coalescence of services is probably coming as a huge relief to the company, though the labor involved in repurposing them is, naturally, Herculean. Conspicuously absent from the lineup mentioned is Messenger, which may be seeing some integration with Skype. A multi-service messenger/video-chat app with Skype built in seems likely, though Skype would definitely have to have a discrete presence as well for power users. No doubt they’ll leave behind many irate users who want things to remain the same — and indeed how Microsoft intends to accommodate these legacy users isn’t clear. Their new clean-break approach maroons many people on the old Windows XP/7 mainland, where they’ll likely remain until the launch quakes of Windows 8 clear away and the new land is safe for colonization.");
    	article.addProperty("ImageTag3", "http://tctechcrunch2011.files.wordpress.com/2012/02/winlive.jpg?w=100&h=70&crop=1");
    	renderJSON(article.toString());
    }
    
    public static void getRSSFeeds(String userId) {
    	System.out.println("Request for "+ userId);
    	getRSSFeedsWithAuthToken(userId, "BAAFTZB1ThIZBQBACYExOvxBc569YgOr8YtjiETSbq8BkG6wnqegV2U8wCrEZBihZAGsU2h2wZBogtwTOAH5ZAb8QMY6qi6sHhviEHWHpIWjCxFFpHEdq0XOegD3LCNI4KMqrqwcjmCEwZDZD");
    }
    
    public static void getRSSFeedsWithAuthToken(String userId, String auth_token){
    	System.out.println("here");
    	User friend = null;
    	try {
			JsonObject user = FbGraph.getObject(userId, Parameter.with("access_token", auth_token).parameters());
			if (user!=null){
				System.out.println("User: "+ user.get("name").toString());
				String ui = user.get("id").toString().replaceAll("\"", "");

				friend = User.find("byUserId", ui).first();
				
				if (friend == null) {
					friend = new User(user.get("name").toString().replaceAll("\"", ""), user.get("name").toString().replaceAll("\"", ""), ui);
					friend.save();
				}
				Session.current().put("user", friend.userId);
				friend.getLikesWithAuthToken(auth_token);
			}
			else {
				System.out.println("User not being created");
				return;
			}
    	} catch (FbGraphException e) {
			System.out.println("There was an error in the getUserInformationMethod");
			e.printStackTrace();
		}
    	
    	
    	JsonObject obj = new JsonObject();
    	obj.addProperty("test", "Is Extracting");
    	
    	//SOmething needs to happen here so that relevant articles are created
    	
    	
    	JsonObject article = new JsonObject();
    	Topic topic1 = RecommendationEngine.fetchTopicForUser(friend, 0);
    	Topic topic2 = RecommendationEngine.fetchTopicForUser(friend, 1);
    	Topic topic3 = RecommendationEngine.fetchTopicForUser(friend, 2);
    	article.addProperty("Heading1", topic1.title);
    	article.addProperty("Content1", topic1.description);
    	article.addProperty("ImageTag1", topic1.feed.imageUrl);
    	article.addProperty("Link1", topic1.link);
    	
    	article.addProperty("Heading2", topic2.title);
    	article.addProperty("Content2", topic2.description);
    	article.addProperty("ImageTag2", topic2.feed.imageUrl);
    	article.addProperty("Link2", topic2.link);
    	
    	article.addProperty("Heading3", topic3.title);
    	article.addProperty("Content3", topic3.description);
    	article.addProperty("ImageTag3", topic3.feed.imageUrl);
    	article.addProperty("Link3", topic3.link);
    	
    	renderJSON(article.toString());
    }
    
    
    
    /* Database Stuff */
    public static boolean generateFeeds() {
    	clearAll();
    	
    	for (int i = 0; i < StaticData.feedLinks.length; i++) {
    		Feed feed = new Feed(StaticData.feedLinks[i]);
    		feed.tags.add(StaticData.feedCategories[i]);
    		feed.name = StaticData.feedNames[i];
    		feed.imageUrl = StaticData.feedImageUrls[i];
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

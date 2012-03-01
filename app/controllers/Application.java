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
		"http://feeds.feedburner.com/epicurious/epiblog",
		
		"http://news.yahoo.com/rss/",
		"http://feeds.cbsnews.com/CBSNewsMain",
		"http://feeds.feedburner.com/EducationWeekCurriculumAndLearning",
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
		"Food",
		
		"Generic",
		"Generic",
		"Education"
		};
	
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
<<<<<<< Updated upstream
			JsonArray userLikes = FbGraph.getConnection(queryPart.toString(), Parameter.with("limit", "1000").parameters());
			
=======
			//JsonArray userLikes = FbGraph.getConnection(queryPart.toString(), Parameter.with("limit", "1000").parameters());
    		JsonArray userLikes = FbGraph.getConnection(queryPart.toString(), Parameter.with("access_token", "BAAFTZB1ThIZBQBACYExOvxBc569YgOr8YtjiETSbq8BkG6wnqegV2U8wCrEZBihZAGsU2h2wZBogtwTOAH5ZAb8QMY6qi6sHhviEHWHpIWjCxFFpHEdq0XOegD3LCNI4KMqrqwcjmCEwZDZD").parameters());
    		System.out.println("User LIkes JSON Array size"+userLikes.size());
>>>>>>> Stashed changes
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
    	System.out.println("Logging out");
        Session.current().remove("username");
        FbGraph.destroySession();
        System.out.println("FACEBOOK LOGOUT BEING HIT");
        Application.index();
        render();
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
    
    public static void getRSSFeeds(String userId){
    	try {
    		System.out.println("User ID which can't be null is "+ userId);
    		//System.out.println("\n\n\n\n\nTrying to Work\n\n\n\n");
			JsonObject user = FbGraph.getObject(userId, Parameter.with("access_token", "BAAFTZB1ThIZBQBACYExOvxBc569YgOr8YtjiETSbq8BkG6wnqegV2U8wCrEZBihZAGsU2h2wZBogtwTOAH5ZAb8QMY6qi6sHhviEHWHpIWjCxFFpHEdq0XOegD3LCNI4KMqrqwcjmCEwZDZD").parameters());
			if (user!=null){
				System.out.println("User: "+ user.get("name").toString());
				String ui = user.get("id").toString().replaceAll("\"", "");
				User friend = User.find("byUserId", ui).first();
				if (friend == null) {
					friend = new User(user.get("name").toString().replaceAll("\"", ""), user.get("name").toString().replaceAll("\"", ""), ui);
					friend.save();
				}
				Session.current().put("user", friend.userId);
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
    	Topic topic1 = RecommendationEngine.fetchTopic(0);
    	Topic topic2 = RecommendationEngine.fetchTopic(1);
    	Topic topic3 = RecommendationEngine.fetchTopic(2);
    	article.addProperty("Heading1", topic1.title);
    	article.addProperty("Content1", topic1.description);
    	article.addProperty("ImageTag1", "http://tctechcrunch2011.files.wordpress.com/2012/02/winlive.jpg?w=100&h=70&crop=1");
    	article.addProperty("Link1", topic1.link);
    	
    	article.addProperty("Heading2", topic2.title);
    	article.addProperty("Content2", topic2.description);
    	article.addProperty("ImageTag2", "http://tctechcrunch2011.files.wordpress.com/2012/02/winlive.jpg?w=100&h=70&crop=1");
    	article.addProperty("Link2", topic2.link);
    	
    	article.addProperty("Heading3", topic3.title);
    	article.addProperty("Content3", topic3.description);
    	article.addProperty("ImageTag3", "http://tctechcrunch2011.files.wordpress.com/2012/02/winlive.jpg?w=100&h=70&crop=1");
    	article.addProperty("Link3", topic3.link);
    	
    	renderJSON(article.toString());
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

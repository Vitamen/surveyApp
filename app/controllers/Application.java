package controllers;

import play.*;
import play.modules.facebook.FbGraph;
import play.modules.facebook.FbGraphException;
import play.modules.facebook.Parameter;
import play.mvc.*;
import play.mvc.Scope.RenderArgs;
import play.mvc.Scope.Session;
import play.test.Fixtures;
import processing.MapManupilator;
import processing.similarityAlgo;
import sun.security.action.GetLongAction;

import java.util.*;

import org.jsoup.Jsoup;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import models.*;

public class Application extends Controller {
	
static String access_token = "AAACEdEose0cBAOZAofIflHZBPLVxZCYme0G3Y7ZCPLZANDb3Hixnmj7ya1MuzVkSWvNgRfpz4x0HnedYwXCdIj1jOvkw7ypnG1C8aq1HxFmgkMMxhkTV7";


	public static void index() {
    	
		
		//WTF IS ALL THIS SHIT , WHY DSNT IT SIMPLY LOAD A PAGE
		//generateFeeds();
    	String currentUser = Session.current().get("user");
    	User user = User.find("byUserId", currentUser).first();
    	if (user != null) {
    		//RecommendationEngine.index();
    		//If the user is logged in simply display the friends to start with
    		displayFriends(user.userId);
    	} else {
    		render();
    	}
    	//render();
    }

	/*
	 * This function will get the user likes json array and store the information to the database
	 * 
	 */
    public static boolean getUserLikes(){
    	User user = User.find("byUserId", Session.current().get("user")).first(); 
    	if (user == null) {
    		return false;
    	}
    	try {
    		String userId = user.userId;
    		StringBuffer queryPart = new StringBuffer(userId+"/likes");
    		JsonArray userLikes = FbGraph.getConnection(queryPart.toString(), Parameter.with("access_token",access_token).parameters());
    		System.out.println("User LIkes JSON Array size"+userLikes.size());
			user.addAllLikes(userLikes);
			
    	} catch (FbGraphException e) {
			e.printStackTrace();
		}
    	return true;
    }
    
    
    /*
     * This method will now display a page with a simple system for the user to select a friend
     */
    public static void displayFriends(String userId){
    	//Should try to change it so that it gets the user id from the session
    	StringBuffer queryPart = new StringBuffer(userId+"/friends");
        
		JsonArray friends;
		try {
			friends = FbGraph.getConnection(queryPart.toString());
			ArrayList<String> names = new ArrayList<String>();
			for(int i = 0; i< friends.size(); i++)
			{
				
				JsonObject obj = friends.get(i).getAsJsonObject();
				String name = obj.get("name").toString();
				name=name.replaceAll("\"", "");
				names.add(name);
			}
			
			Collections.sort(names);
			System.out.println("Putting friends in the renderArgs method");
			renderArgs.put("allFriends", names);
	        renderArgs.put("FriendsJsonArray", friends);
	        
	        render();
	        
		} catch (FbGraphException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
    }
    
    /* Facebook Session Stuff
     * 
     *  ^That is not fucking commenting 
     *  This method should do NOTHING BUT manage user Login and session state
     *
     */
    public static void facebookLogin() {
        try {
            JsonObject profile = FbGraph.getObject("me");
            // fetch the logged in user
            List<User> userList = User.find("byUserId", profile.get("id").toString().replaceAll("\"", "")).fetch();
            User user;
            
            if (userList == null || userList.size() == 0) {
	             user = new User(profile.get("name").toString().replaceAll("\"", ""), profile.get("name").toString().replaceAll("\"", ""), profile.get("id").toString().replaceAll("\"", ""));
	             user.save();
            } else {
            	user = userList.get(0);
            }
            
    		 
            Session.current().put("user", user.userId);
            
            //Call the page with the friends list
            displayFriends(user.userId);
            
            
        } catch (FbGraphException fbge) {
        	System.out.println("ERROORRRRR ********** NOOOOOOO");
            flash.error(fbge.getMessage());
            if (fbge.getType() != null && fbge.getType().equals("OAuthException")) {
                Session.current().remove("username");
            }
        }
       
//       Choice choice = new Choice();
//       List<Recommendation> list=RecommendationEngine.generateChoiceOfSize(3).recommendations;
//       for (int i = 0; i < list.size(); i++){
//    	   choice.recommendations.add(list.get(i));
//       }
       
      // System.out.println("Size of recommendation list "+list.size());
       //renderArgs.put("choice", choice);
       //The class cast exception happens when you are passing the Choice object from the Applicaiton.java to Recommendation.java
       
       //Rendering template
       //renderTemplate("Recommendation/verticalTopics.html");
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
    	getRSSFeedsWithAuthToken(userId, access_token);
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
    
    
    /*
     * 
     * I WAS NOT ABLE TO UNDERSTAND SOME OLDER CODE
     * I ALSO DID NOT WANT TO BREAK IT SO COPIED SOME OF IT BELOW in the second function
     * 
     */
    public static void friendSelected(String friendName){
    	
    	System.out.println("CURRENT USER LIKES");
    	
    	//We want to create a hashmap of user likes 
    	HashMap<String, Double> currentUserLikeMap = getLikesMap(Session.current().get("user"));
    	System.out.println("User like map Size : " + currentUserLikeMap.size() );
    	
    	//We want to create a hashmap of friends likes
    	HashMap<String, Double> friendUserLikeMap = getLikesMap(friendName);
    	System.out.println("Friend like map Size : " + friendUserLikeMap.size() );
    	
		similarityAlgo sm = new similarityAlgo();
		MapManupilator mapMP = new MapManupilator();
		
		//Generate common user likes or marger get get union of likes if no common
		HashMap<String, Double> userLikeMap;
		HashMap<String, Double> tempMap;
		
		userLikeMap = mapMP.mergeHashMaps(currentUserLikeMap, friendUserLikeMap);
		
		tempMap = mapMP.intersectHashMaps(currentUserLikeMap, friendUserLikeMap);
		
		if(tempMap.keySet().size() > 5){
			userLikeMap = tempMap;
			System.out.println("INTERSECTION");
		}
		
		//Remove the stopwords , aka common words
		//Optimize this so that the words dont get putin in the first place
		
		
		
		System.out.println("Size: " + userLikeMap);
		
		
		//We now have all the user like information extracted and want to recommend something 
		//User cos similarity and the clusters to layout the items.
		
		
		
		if (Cluster.count() < 5){
			System.out.println("THERE ARE NOT ENOUGH CLUSTERS");
		}else{
			
			//THIS SHOULD BE IN PERSISTANT STORE , IT IS STUPID TO DO THIS EVERYTIME
			//This was the quickest way to get this fucntion working.
			
			//Create Cluster Mapping Vecotr
			HashMap<String, HashMap<String, Double>> clusterConfidenceMaps = new HashMap<String,HashMap<String, Double>>();
			
			//Fill it with a map for each cluster
			List<Cluster> allClusters = Cluster.findAll();
			for(Cluster c : allClusters){
					clusterConfidenceMaps.put(c.id.toString(),mapMP.createFrequencyMap(c));
			}
			
			//Run COS similarity on each maps
			HashMap<String, Double> cosSimilarities = new HashMap<String,Double>();
			for( String id:clusterConfidenceMaps.keySet()){
				cosSimilarities.put(id, sm.cosine_similarity(userLikeMap, clusterConfidenceMaps.get(id)));
			}
			
			//Print all cos values:
			Double max = 0.0;
			long maxid = 0;
			for(String id: cosSimilarities.keySet()){
				if (cosSimilarities.get(id) > max){
					max = cosSimilarities.get(id);
					maxid = Long.parseLong(id);
				}
				System.out.println(cosSimilarities.get(id));
			}
			
			Cluster x = Cluster.findById(maxid);
			//System.out.println(x.topics.get(0).description);
			  Random randomGenerator = new Random();
			      int randomTopic = randomGenerator.nextInt((int) (Topic.count()-1));
			     
			     List<Topic> allTopics = Topic.findAll();
			      
			
			
			//TOPIC 1
			String title = allTopics.get(randomTopic).title;
			renderArgs.put("t1t",title);
			String desc = allTopics.get(randomTopic).description;
			renderArgs.put("t1d",desc.toString());
			
			String title2;
			String desc2;
			
			if(x != null){
				//TOPIC 2 is the computed Topic
				int generatedTopic = randomGenerator.nextInt(x.topics.size());
				 title2 = x.topics.get(generatedTopic).title;	
				desc2 = x.topics.get(generatedTopic).description;
				
			}else{
				randomTopic = randomGenerator.nextInt((int) (Topic.count()-1));
				title2 = allTopics.get(randomTopic).title;
				desc2 = allTopics.get(randomTopic).description;
			}
			renderArgs.put("t2t",title2);
			renderArgs.put("t2d",desc2.toString());
			
			//TOPIC 3
			//Generate new random
			randomTopic = randomGenerator.nextInt((int) (Topic.count()-1));
			String title3 = allTopics.get(randomTopic).title;
			renderArgs.put("t3t",title3);
			String desc3 = allTopics.get(randomTopic).description;
			renderArgs.put("t3d",desc3.toString());
	       
			render();

		}
		
    }
    
    
    /*
     * This method takes a user and returns a hashmaps of <Words in likes, frequency of words >
     * 
     */
    private static HashMap<String, Double> getLikesMap(String uid){

    	HashMap <String,Double> likesMap = new HashMap<String, Double>(); 
    	
    	//Check user id not null
    	if (uid == null) {
    		return likesMap;
    	}
    	//Get user and process likes to create a vector
    	try {

    		MapManupilator mapMP = new MapManupilator();
    		//Fetch like data for User
    		StringBuffer queryPart = new StringBuffer(uid+"/likes");
    		System.out.println("Query : " + queryPart.toString());
    		JsonArray allLikes = FbGraph.getConnection(queryPart.toString());//, Parameter.with("access_token", access_token).parameters());
    		
    		//If any liks available process the words
    		if (allLikes != null){
    			for (int i = 0; i < allLikes.size(); i++)
    			{
    				JsonObject tempJsonObject = (JsonObject)allLikes.get(i);
    				
    				//Get like words and phrases
    				String category = tempJsonObject.get("category").toString().replaceAll("\"", "");
    				String name = tempJsonObject.get("name").toString().replaceAll("\"", "");
    				String id = tempJsonObject.get("id").toString().replaceAll("\"", "");
    				
    				//Add the entire name first and then the tokenize the name and add all componenets
    				//if(!likesMap.containsKey(category)){
						category = category.replace("/", " ");
					//}
    			
    				//Add all the words in the like to the hashmap 
    				StringTokenizer st = new StringTokenizer(name + " " + category);
    				while(st.hasMoreTokens()){
    					String nextToken = st.nextToken();
    					
    					//If already present then get increment count or jsut put and count = 1.0
    					if(!likesMap.containsKey(nextToken)){
    						if(!mapMP.stopWordChecker(nextToken)){
    							likesMap.put(nextToken,3.0);
    						}
    						
    					}
    					else{
    						likesMap.put(nextToken, likesMap.get(nextToken) + 1.0);
    					}
    				}
    			}
    		}
    	} 
    	catch (FbGraphException e) {
    		e.printStackTrace();
    	}
    	
    	//Remove Stopwrods 
    	
    	
    	//Return map if empty or with words from the likes mapped to count.
    	return likesMap;
    }





}

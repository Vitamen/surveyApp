package controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.apache.commons.lang.StringEscapeUtils;

import com.google.gson.JsonObject;
import com.sun.cnpi.rss.elements.Item;
import com.sun.cnpi.rss.elements.Rss;
import com.sun.cnpi.rss.parser.RssParser;
import com.sun.cnpi.rss.parser.RssParserException;
import com.sun.cnpi.rss.parser.RssParserFactory;

import models.Choice;
import models.Feed;
import models.LikeFrequency;
import models.LikeFrequencyComparator;
import models.LikeGroup;
import models.Likes;
import models.Reason;
import models.Recommendation;
import models.Topic;
import models.User;

import play.modules.facebook.FbGraph;
import play.modules.facebook.FbGraphException;
import play.mvc.Controller;
import play.mvc.Scope.Session;

public class RecommendationEngine extends Controller{

	public static void index() {
		/* Set up stuff */
		/*
		LikeGroup.generateLikeGroupsFromStaticArray();
		Application.getUserLikes();
		Application.generateFeeds();
		RSSEngine.fetchNews();*/
		Session.current().put("user", 1243350056);
		Date date = new Date();
		Random random = new Random(date.getTime());
		int i = random.nextInt(2);
		if (i == 0) {
			renderArgs.put("choice", likeRankingChoice());
		} else {
			renderArgs.put("choice", genericVsCalculatedChoice());
		}
    	renderTemplate("Recommendation/index.html");
	}
	
	public static Recommendation recommendationForFriendsWithUserIds(List<Long> userIds) {
		List<User> people = new ArrayList<User>();
		
		for (int i = 0; i < userIds.size(); i++) {
			User user = User.find("byUserId", userIds.get(i)).first();
			people.add(user);
		}
		
		return recommendationForFriends(people);
	}
	
	public static Recommendation recommendationForFriends(List<User> people) {
		String tag = LikeGroup.getLikeGroupFromCategory(findLikeIntersection(people));
		
    	List<Topic> topics = Topic.find("select t from Topic t join t.tags as tag where tag = ?", tag).fetch();
    	
    	/* If no topic is found, return a generic result */
    	if (topics == null || topics.size() == 0) {
    		System.out.println("ERROR: We found no topics! Going to generic search results");
    		topics =  Topic.find("select t from Topic t join t.tags as tag where tag = ?", "Generic").fetch();
    	}

    	Topic topic = getRandomTopicFrom(topics);
    	
    	Recommendation rec = new Recommendation(topic);
    	rec.reasons.add(Reason.getReasonWithType(Reason.LIKE | Reason.MUTUAL));
    	rec.save();
    	return rec;
	}
	
	public static String findLikeIntersection(List<User> people) {
		Map<String, Integer> likeFrequencyMap = new HashMap<String, Integer>();
		for (int i = 0; i < people.size(); i++) {
			User user = people.get(i);
			if (user.allUserLikes == null || user.allUserLikes.size() == 0 ||
					user.frequencyOfLikes == null || user.frequencyOfLikes.size() == 0) {
				user.getLikes();
			}
			List<LikeFrequency> likeFrequencies = user.frequencyOfLikes;
			Collections.sort(likeFrequencies, new LikeFrequencyComparator());
			for (int j = 0; j < likeFrequencies.size(); j++) {
				LikeFrequency lf = likeFrequencies.get(j);
				if (likeFrequencyMap.containsKey(lf.likeCategory)) {
					likeFrequencyMap.put(lf.likeCategory, likeFrequencyMap.get(lf.likeCategory)+lf.frequency);
				} else {
					likeFrequencyMap.put(lf.likeCategory, lf.frequency);
				}
			}
		}
		Map.Entry<String, Integer> maxEntry= null;
		Iterator iter = likeFrequencyMap.entrySet().iterator();
		
		while (iter.hasNext()) {
			Map.Entry<String, Integer> lfmEntry = (Entry<String, Integer>) iter.next();
			
			if (maxEntry == null) {
				maxEntry = lfmEntry;
			} else {
				if (lfmEntry.getValue() > maxEntry.getValue()) {
					maxEntry = lfmEntry;
				}
			}
		}
			
		return maxEntry.getKey();
	}
	
	/* Choice Generation */
	public static Choice genericVsCalculatedChoice() {
		List<Topic> topics = Topic.find("select t from Topic t join t.tags as tag where tag = ?", "Generic").fetch();
		Topic topic1 = getRandomTopicFrom(topics);
		
		User user = User.find("byUserId", Session.current().get("user")).first();
    	String tag = "Generic";
		if (user != null) {
			if (user.frequencyOfLikes == null || user.frequencyOfLikes.size() == 0) {
				Application.getUserLikes();
			}
			List<LikeFrequency> likeFrequencies = user.frequencyOfLikes;
			Collections.sort(likeFrequencies, new LikeFrequencyComparator());
			LikeFrequency lf;
			if (likeFrequencies.size() > 0) {
				lf = likeFrequencies.get(0);
				tag = LikeGroup.getLikeGroupFromCategory(lf.likeCategory);
			}
		} else {
			System.out.println("ERROR: Could not find user in session.");
			return null;
		}
		
		List<Topic> topics2 = Topic.find("select t from Topic t join t.tags as tag where tag = ?", tag).fetch();
		Topic topic2 = getRandomTopicFrom(topics2);
		
		Recommendation rec1 = new Recommendation(topic1);
    	Reason genericReason = Reason.getCategoryReason(Reason.GENERIC);
    	rec1.addReason(genericReason);
    	rec1.save();
    	
    	Recommendation rec2 = new Recommendation(topic2);
    	Reason likeCategoryReason = Reason.getCategoryReason(Reason.LIKE);
    	rec2.addReason(likeCategoryReason);
    	rec2.save();
    	
    	Choice choice = new Choice();
    	choice.addRecommendation(rec1);
    	choice.addRecommendation(rec2);
    	choice.save();
		
		return choice;
	}
	
	public static Choice likeRankingChoice() {
		Topic topic1 = fetchTopic(0);
    	Topic topic2 = fetchTopic(1);
 
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
    	
    	return choice;
	}
    
    public static Topic fetchTopic(int seed) {
    	User user = User.find("byUserId", Session.current().get("user")).first();
    	
    	return fetchTopicForUser(user, seed);
    }
    
    public static Topic fetchTopicForUser(User user, int seed) {
    	JsonObject profile;
    
    	String tag;
    	if (seed == 0) {
    		tag = "Technology";
    	} else {
    		tag = "Fashion";
    	}
    	
    	if (LikeGroup.count() == 0) {
    		LikeGroup.generateLikeGroupsFromStaticArray();
    	}
    	
    	if (Feed.count() == 0) {
    		Application.generateFeeds();
    		RSSEngine.fetchNews();
    	}
    	
		if (user != null) {
			if (user.frequencyOfLikes == null || user.frequencyOfLikes.size() == 0) {
				System.out.println("ERROR: This should not happen");
			}
			List<LikeFrequency> likeFrequencies = user.frequencyOfLikes;
			Collections.sort(likeFrequencies, new LikeFrequencyComparator());
			LikeFrequency lf;
			if (likeFrequencies.size() > seed) {
				lf = likeFrequencies.get(seed);
				tag = LikeGroup.getLikeGroupFromCategory(lf.likeCategory);
			}
		} else {
			System.out.println("ERROR: Could not find user in session.");
			return null;
		}

    	System.out.println("Looking for topic for tag:"+ tag);
    	List<Topic> topics = Topic.find("select t from Topic t join t.tags as tag where tag = ?", tag).fetch();
    	
    	/* If no topic is found, return a generic result */
    	if (topics == null || topics.size() == 0) {
    		System.out.println("ERROR: We found no topics! Going to generic search results");
    		topics =  Topic.find("select t from Topic t join t.tags as tag where tag = ?", "Generic").fetch();
    	}

    	return getRandomTopicFrom(topics);
    }
    
    /* Response */
    public static void processChoice(long choiceId, int selection) {
    	Choice choice = Choice.findById(choiceId);
    	System.out.println(choice+" "+selection);
    	if (choice == null) {
    		System.out.println("ERROR: Could not find choice with id "+choiceId);
    		index();
    	}
    	choice.selection = selection;
    	choice.save();
    	index();
    }
    
    /* Helpers */
    public static Topic getRandomTopicFrom(List<Topic> topics) {
    	Date date = new Date();
    	Random random = new Random(date.getTime());
    	
    	int i = random.nextInt(topics.size());
    	return topics.get(i);
    }
}

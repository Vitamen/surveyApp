package controllers;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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
    	
    	User user = User.find("byUserId", Session.current().get("user")).first();
    	
		if (user != null) {
			System.out.println("Got a user :"+user);
			if (user.frequencyOfLikes == null || user.frequencyOfLikes.size() == 0) {
				Application.getUserLikes();
			}
			List<LikeFrequency> likeFrequencies = user.frequencyOfLikes;
			Collections.sort(likeFrequencies, new LikeFrequencyComparator());
			LikeFrequency lf;
			System.out.println("Found "+likeFrequencies.size()+" Likes");
			if (likeFrequencies.size() > seed) {
				lf = likeFrequencies.get(seed);
				tag = LikeGroup.getLikeGroupFromCategory(lf.likeCategory);
				System.out.println("Setting tag to "+tag);
			}
		} else {
			System.out.println("ERROR: Could not find user in session.");
			return null;
		}

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

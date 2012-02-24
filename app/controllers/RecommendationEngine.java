package controllers;

import java.util.Collections;
import java.util.List;

import com.google.gson.JsonObject;

import models.Choice;
import models.LikeFrequency;
import models.LikeFrequencyComparator;
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
		Application.getUserLikes();
		RSSEngine.fetchNews();
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
    	
    	renderArgs.put("choice", choice);
    	renderTemplate("Recommendation/index.html");
	}
	
	/* Choice Generation */
	public static Choice getChoice() {
		return getChoice(2);
	}
	
	public static Choice getChoice(int numRecommendations) {
		Choice choice = new Choice();
		return choice;
	}
    
    public static Topic fetchTopic(int seed) {
    	JsonObject profile;
    	User user = User.findById(Long.parseLong(Session.current().get("user")));
    	user.addAllLikes(null);
		String tag = "Technology";
    	
		if (user != null) {
			System.out.println("Found a user!");
			List<LikeFrequency> likeFrequencies = user.frequencyOfLikes;
			Collections.sort(likeFrequencies, new LikeFrequencyComparator());
			LikeFrequency lf;
			if (likeFrequencies.size() > seed) {
				lf = likeFrequencies.get(seed);
				tag = Likes.getLikeGroupFromCategory(lf.likeCategory);
			}
			System.out.println("Found a common tag: "+tag);
		}
		
    	List<Topic> topics = Topic.find("select t from Topic t join t.tags as tag where tag = ?", tag).fetch();
   
    	if (topics == null || topics.size() == 0) {
    		return null;
    	}
    	return topics.get(0);
    }
    
    
    /* Response */
    public static void processChoice(long choiceId, int selection) {
    	Choice choice = Choice.findById(choiceId);
    	if (choice == null) {
    		System.out.println("ERROR: Could not find choice with id "+choiceId);
    		return;
    	}
    	
    	choice.selection = selection;
    	index();
    }
}

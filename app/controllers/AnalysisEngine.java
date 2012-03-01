package controllers;

import play.*;
import play.db.jpa.JPA;
import play.mvc.*;

import java.util.*;

import javax.persistence.Query;

import models.*;

public class AnalysisEngine extends Controller {

	public static void makeSelection(long id, int selection) {
		Choice choice = Choice.findById(id);
		if (choice != null) {
			choice.selection = selection;
			choice.save();
		}
	}
	
	public static void charts() {
		getRssRanking();
		getRankingEffectiveness();
		getGenericEffectiveness();
		renderTemplate("Analysis/charts.html");
	}
	
	public static void reasonComparator(int reasonOneType, int reasonTwoType) {
		Reason reasonOne = Reason.find("byTypeAndIsCategory", reasonOneType, true).first();
		Reason reasonTwo = Reason.find("byTypeAndIsCategory", reasonTwoType, true).first();
		
		List<Choice> choicesOne = Choice.find("SELECT DISTINCT c " +
				"FROM Choice c JOIN c.recommendations r WHERE " +
				"? MEMBER OF r.reasons", reasonOne).fetch();
		
		List<Choice> choicesTwo = Choice.find("SELECT DISTINCT c " +
				"FROM Choice c JOIN c.recommendations r WHERE " +
				"? MEMBER OF r.reasons", reasonTwo).fetch();
		
		renderArgs.put("choiceOne", choicesOne.size());
		renderArgs.put("choiceTwo", choicesTwo.size());
		renderTemplate("Analysis/index.html");
	}
	
	public static void likeRankingEffectiveness() {
		Reason reason = Reason.getLikeCategoryReason();
		List<Choice> choices = Choice.find("SELECT DISTINCT c " +
				"FROM Choice c JOIN c.recommendations r WHERE " +
				"? MEMBER OF r.reasons", reason).fetch();	
		int totalSelected = 0, preferredSelected = 0;
		
		for (int i = 0; i < choices.size(); i++) {
			Choice choice = choices.get(i);
			if (choice.selection == -1) {
				continue;
			} else if (choice.selection == 0) {
				preferredSelected++;
				totalSelected++;
			} else if (choice.selection == 1) {
				totalSelected++;
			}
		}
		renderArgs.put("preferred", preferredSelected);
		renderArgs.put("total", totalSelected);
		renderTemplate("Analysis/likeRanking.html");
	}
	
	public static boolean getRankingEffectiveness() {
		Reason reason = Reason.getLikeCategoryReason();
		List<Choice> choices = Choice.find("SELECT DISTINCT c " +
				"FROM Choice c JOIN c.recommendations r WHERE " +
				"? MEMBER OF r.reasons", reason).fetch();	
		int totalSelected = 0, preferredSelected = 0;
		
		for (int i = 0; i < choices.size(); i++) {
			Choice choice = choices.get(i);
			if (choice.selection == -1) {
				continue;
			} else if (choice.selection == 0) {
				preferredSelected++;
				totalSelected++;
			} else if (choice.selection == 1) {
				totalSelected++;
			}
		}
		renderArgs.put("preferred", preferredSelected);
		renderArgs.put("total", totalSelected);
		return true;
	}
	
	public static boolean getGenericEffectiveness() {
		Reason genericReason = Reason.getReasonWithType(Reason.GENERIC);
		List<Choice> choices = Choice.find("SELECT DISTINCT c " +
				"FROM Choice c JOIN c.recommendations r WHERE " +
				"? MEMBER OF r.reasons", genericReason).fetch();
		
		int calculatedSelected = 0, totalSelected = 0;
		
		for (int i = 0; i < choices.size(); i++) {
			Choice choice = choices.get(i);
			if (choice.selection == -1) {
				continue;
			} else if (choice.recommendations.get(1).reasons.contains(genericReason)) {
				continue;
			} else if (choice.selection == 0) {
				totalSelected++;
			} else if (choice.selection == 1) {
				calculatedSelected++;
				totalSelected++;
			}
		}
		
		renderArgs.put("generic_calculated", calculatedSelected);
		renderArgs.put("generic_total", totalSelected);
		return true;
	}
	
	public static boolean getRssRanking() {
		List<Choice> choices = Choice.findAll();
		List<Feed> feeds = Feed.findAll();
		Map<String, Float> feedSelectedMap = new HashMap<String, Float>();
		Map<String, Integer> feedTotalMap = new HashMap<String, Integer>();
		
		for (int i = 0; i < feeds.size(); i++) {
			Feed feed = feeds.get(i);
			feedSelectedMap.put(feed.name, (float) 0.0);
			feedTotalMap.put(feed.name, 0);
		}

		for (int i = 0; i < choices.size(); i++) {
			Choice choice = choices.get(i);
			if (choice.selection == -1) {
				continue;
			} else if (choice.recommendations.get(0).topic.feed.equals(choice.recommendations.get(1).topic.feed)){
				continue;
			} else {
				Feed selectedFeed = choice.recommendations.get(choice.selection).topic.feed;
				Feed rejectedFeed = choice.recommendations.get((choice.selection+1)%2).topic.feed;
				
				feedSelectedMap.put(selectedFeed.name, feedSelectedMap.get(selectedFeed.name)+ 1);
				feedTotalMap.put(selectedFeed.name, feedTotalMap.get(selectedFeed.name)+1);

				feedTotalMap.put(rejectedFeed.name, feedTotalMap.get(rejectedFeed.name)+1);
			}
		}
		Iterator it = feedSelectedMap.entrySet().iterator();
		Set<String> feedsToRemove = new HashSet<String>();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry)it.next();
	        String feedName = (String) pair.getKey();
	        if (feedTotalMap.get(feedName) == 0) {
	        	feedsToRemove.add(feedName);
	        } else {
	        	feedSelectedMap.put(feedName, feedSelectedMap.get(feedName)/feedTotalMap.get(feedName));
	        }
	    }
	    it = feedsToRemove.iterator();
	    while(it.hasNext()) {
	    	feedSelectedMap.remove(it.next());
	    }
		System.out.println("we have "+feedSelectedMap.size());
		renderArgs.put("feed_map", feedSelectedMap);
		
		return true;
	}
}

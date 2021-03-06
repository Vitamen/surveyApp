package controllers;

import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.lang.StringEscapeUtils;
import org.jsoup.Jsoup;

import com.aliasi.chunk.Chunk;
import com.aliasi.cluster.KMeansClusterer;
import com.aliasi.io.LogLevel;
import com.aliasi.io.Reporter;
import com.aliasi.util.FeatureExtractor;
import com.google.gson.JsonObject;
import com.sun.cnpi.rss.elements.Item;
import com.sun.cnpi.rss.elements.Rss;
import com.sun.cnpi.rss.parser.RssParser;
import com.sun.cnpi.rss.parser.RssParserException;
import com.sun.cnpi.rss.parser.RssParserFactory;

import extractor.extractorThread;

import models.Choice;
import models.Cluster;
import models.Feed;
import models.LikeFrequency;
import models.LikeFrequencyComparator;
import models.LikeGroup;
import models.Likes;
import models.Reason;
import models.Recommendation;
import models.Tag;
import models.Topic;
import models.User;

import play.modules.facebook.FbGraph;
import play.modules.facebook.FbGraphException;
import play.mvc.Controller;
import play.mvc.Scope.Session;
import pt.voiceinteraction.keyphraseextraction.KeyPhrase;

public class RecommendationEngine extends Controller{

	public static void index() {
		//Topic topic = Topic.findById((long)22334);
		//System.out.println(topic.description);
		
		
		//System.out.println(topics.size());
		
		
		//LikeGroup.generateLikeGroupsFromStaticArray();
		Application.generateFeeds();
		RSSEngine.fetchNews();
		System.out.println("FOO ");
		List<Topic> topics = Topic.all().fetch();
        ArrayBlockingQueue<Topic> bq  = new ArrayBlockingQueue(topics.size(),true);
        
        
		for (int i = 0; i < topics.size(); i++) {
			//addTagsToTopic(topics.get(i));
			bq.add(topics.get(i));
		}
		addTagsToAllTopics(bq);
		
		runKMeans();
		//runKMeans();
		/* Set up stuff */
		
		/*
		LikeGroup.generateLikeGroupsFromStaticArray();
		Application.getUserLikes();
		Application.generateFeeds();
		RSSEngine.fetchNews();*/
		/*
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
    	  
    	*/
    	
	}
	
	public static boolean fetchNews() {
		List<Topic> topics = Topic.findAll();
		if (topics.size() == 0) {
			RSSEngine.fetchNews();
			topics = Topic.findAll();
			System.out.println(topics.size());
		}
	
		return true;
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
	public static Choice generateChoiceOfSize (int numRecs) {
		Choice choice = new Choice();
		List<Topic> topics = Topic.findAll();
		System.out.println("Size of topics "+topics.size());
		
		for (int i = 0; i < numRecs - 1; i++) {
			Topic topic = getRandomTopicFrom(topics);
			Recommendation recommendation = new Recommendation(topic);
			Reason genericReason = Reason.getCategoryReason(Reason.GENERIC);
			recommendation.addReason(genericReason);
			recommendation.save();
			choice.addRecommendation(recommendation);
		}
		
		/* TODO get calculated choice */
		Topic topic = getRandomTopicFrom(topics);
		Recommendation recommendation = new Recommendation(topic);
		Reason calculated = Reason.getCategoryReason(Reason.LIKE);
		recommendation.addReason(calculated);
		recommendation.save();
		choice.recommendations.add(recommendation);
		System.out.println("Returned with choice");
		return choice;
	}
	
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
			System.out.println("Got a user :"+user);
			if (user.frequencyOfLikes == null || user.frequencyOfLikes.size() == 0) {
				System.out.println("ERROR: This should not happen");
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
    	System.out.println(topics.size());
    	
    	
    	int i = random.nextInt(topics.size());
    	return topics.get(i);
    }
    
    public static int addTagsToTopic(Topic topic) {
    	while(topic.tags.size() > 1) {
    		topic.tags.remove(1);
    		topic.save();
    	}
    	
        try {
            int nrKeyphrases = 0;
           


            EnglishKeyPhraseExtractor extractor = new EnglishKeyPhraseExtractor("data/English_KEModel_manualData",
                    "data/models/en_US/hub4_all.np.4g.hub97.1e-9.clm",
                    "data/models/en_US/left3words-wsj-0-18.tagger",
                    "data/stopwords/stopwords_en.txt");
           
            StringTokenizer st = new StringTokenizer(topic.description);
            int numberOfWords = st.countTokens();
            nrKeyphrases = Math.max(5, Math.min(30, numberOfWords / 30));
            String[] texts = new String[]{
            		Jsoup.parse(topic.description).text()
            };
            
            
            for (KeyPhrase keyPhrase : extractor.getKeyphrases(nrKeyphrases, Arrays.asList(texts))) {
                System.out.println("Got keyphrase: "+keyPhrase.getKeyPhrase());
                
                /* Create the tag and attach it */
                Tag tag = new Tag();
                tag.name = keyPhrase.getKeyPhrase().toLowerCase();
                tag.confidence = keyPhrase.getConfidence();
                tag.rank = keyPhrase.getRank();
                tag.save();
            	topic.tags.add(tag);
            	topic.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    
    
    public static int addTagsToAllTopics(ArrayBlockingQueue<Topic> bq ) {
		
    	   try {
			EnglishKeyPhraseExtractor extractor = new EnglishKeyPhraseExtractor("data/English_KEModel_manualData",
			           "data/models/en_US/hub4_all.np.4g.hub97.1e-9.clm",
			           "data/models/en_US/left3words-wsj-0-18.tagger",
			           "data/stopwords/stopwords_en.txt");
			
			for (int i=0 ; i<3 ; i++){
				extractorThread extr = new extractorThread(extractor,bq);
				extr.doJob();
				extractorThread extr2 = new extractorThread(extractor,bq);
				extr2.doJob();
				extractorThread extr3 = new extractorThread(extractor,bq);
				extr3.doJob();
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	   
    	   
    	
    	return 0;
    }
    
    
    
    public static void runKMeans() {
    	
    	List<Topic> topics = Topic.all().fetch();
    	HashSet<Topic> hs = new HashSet<Topic>(topics);
    	
		final HashMap<String, Double> master = createHashMap(hs);
		
    	FeatureExtractor<Topic> featureExtractor = new FeatureExtractor<Topic>() {
				@Override
				public Map<String, ? extends Number> features(Topic topic) {
					HashMap<String, Double> hm = new HashMap<String, Double>(master);
			
					List<Tag> tags = topic.tags;
					
					//TODO : Can be further optimized by tokenizing the tags and setting them to 1 aswell
					for (int i = 1; i < tags.size(); i++) {
						Tag tag = tags.get(i);
						StringTokenizer st = new StringTokenizer(tag.name);
	    				if (st.countTokens() > 1) {
	    					while (st.hasMoreTokens()) {
	    						String token = st.nextToken();
	    						
	    						if(hm.containsKey(token)){
	    							hm.put(token,hm.get(token)+tag.confidence);
	    						}
	    					}
	    				}
						if (hm.containsKey(tag)){
							hm.put(tag.name,hm.get(tag)+tag.confidence);
						}
						
					}
					return hm;
				}
    	};
    	
    	
    	KMeansClusterer<Topic> kmc = new KMeansClusterer<Topic>(featureExtractor,20,10000, true,0.9);
    	

    	//Set<Set<Topic> > topicClusters = kmc.cluster(hs);
    	
    	List<Cluster> c = Cluster.findAll();
    	for (Cluster item : c){
    		item.topics.clear();
    		item.save();
    	}
    	Cluster.findAll().clear();
    	
    	clusterHelper(kmc.cluster(hs),kmc);
    	
    	System.out.println("ENDED");
    	//render();
    }
    
    private static HashMap<String, Double> createHashMap(HashSet topics){
    	HashMap<String, Double> master = new HashMap<String, Double>();
    	Iterator<Topic> it = topics.iterator();
    	
    	
    	while(it.hasNext()){
    		//Insert all the tags into the master map if they are not already present
    		for (Tag tag : it.next().tags){
    			if(!master.containsKey(tag.name)){
    				tag.name = tag.name.toLowerCase();
    				StringTokenizer st = new StringTokenizer(tag.name);
    				if (st.countTokens() > 1) {
    					while (st.hasMoreTokens()) {
    						String token = st.nextToken();
    						
    						if(!master.containsKey(token)){
    						master.put(token,0.0);
    						}
    					}
    				}
    				master.put(tag.name, 0.0);
    			}
    		}	
    	}
    	return master;
    }

    /*
     * We want to take the origional clusters and recluster again and again
     * Till we have n buckets with no bucket having more than k items.
     * 
     */
    private static void clusterHelper(Set<Set<Topic> > topicClusters , KMeansClusterer<Topic> kmc){

    	//Purge all old clusters
    	//Figure out Cascade
    	//Cluster.deleteAll();
    	
    
    	//Cluster.findAll().
    	
    	
    	Iterator<Set<Topic>> topicClusterIter = topicClusters.iterator();
    	int sizeOfMaxCluster = 300;

    	while(topicClusterIter.hasNext()) {
    		Set<Topic> topicCluster = topicClusterIter.next();
    		
    		if (topicCluster.size() > sizeOfMaxCluster) {
    			//System.out.println("Cluster Junked");
    			//System.out.println("Num Topics: " +  topicCluster.size());
    			//clusterHelper(kmc.cluster(topicCluster),kmc);
    			
    		}
    		//else{
    			//retainedClusters.add(topicCluster);
    			//ADD CLUSTER TO DATABASE
    			Cluster c = new Cluster();
    			c.topics = new ArrayList<Topic>();
    			c.topics.addAll(topicCluster);
    			c.save();
    			
//    			Iterator<Topic> topicIter = topicCluster.iterator();
//    			System.out.println();
//    			System.out.println("NEW CLUSTER: ");
//    			HashMap<String,Integer> hmp = new HashMap<String,Integer>();
//    			while(topicIter.hasNext()) {
//    				Topic nextTopic = topicIter.next();
//
//    				//    			//Make a tally to find the most popular keyword in each cluster
//    				//    			for (Tag t :nextTopic.tags){
//    				//    				if (hmp.containsKey(t.name)){
//    				//    					hmp.put(t.name, hmp.get(t.name)+1);
//    				//    				}
//    				//    				else{
//    				//    					hmp.put(t.name,0);
//    				//    				}
//    				//    				System.out.print(t.name + ",");
//    				//    			}
//
//    				System.out.println();
//    			}
    			
    		//}
    	}
    	
    	
    	
    	return;
    }

    
    
    
}


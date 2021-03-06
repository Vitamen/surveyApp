package models;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import play.db.jpa.Model;
import play.modules.facebook.FbGraph;
import play.modules.facebook.FbGraphException;
import play.modules.facebook.Parameter;

@Entity
public class User extends Model{
	public String name;
	public String userName;
	public String userId;
	
	@ManyToMany
	public List<Likes> allUserLikes;
	@ManyToMany
	public List<LikeFrequency> frequencyOfLikes;
	
	
	public User(String name, String userName, String userId){
		this.name = name;
		this.userName = userName;
		this.userId = userId;
		allUserLikes = new LinkedList<Likes>();
		frequencyOfLikes = new LinkedList<LikeFrequency>();
	}
	
	public void getLikes() {
		try {
    		String userId = this.userId;
    		StringBuffer queryPart = new StringBuffer(userId+"/likes");
			JsonArray userLikes = FbGraph.getConnection(queryPart.toString(), Parameter.with("limit", "1000").parameters());
			
			this.addAllLikes(userLikes);
    	} catch (FbGraphException e) {
			e.printStackTrace();
		}
	}
	
	public void getLikesWithAuthToken(String auth_token) {
    	try {
    		String userId = this.userId;
    		StringBuffer queryPart = new StringBuffer(userId+"/likes");
    		JsonArray userLikes = FbGraph.getConnection(queryPart.toString(), Parameter.with("access_token", auth_token).parameters());
			this.addAllLikes(userLikes);
    	} catch (FbGraphException e) {
			System.out.println("There was an error in the getUserInformationMethod");
			e.printStackTrace();
		}
	}
	
	public void addLike(Likes newLike){
		allUserLikes.add(newLike);
	}
	
	public void addAllLikes(JsonArray allLikes){
		
		HashMap<String, Integer> userLikesToFrequency = new HashMap<String, Integer>();
		if (allLikes != null){
			for (int i = 0; i < allLikes.size(); i++)
			{
				JsonObject tempJsonObject = (JsonObject)allLikes.get(i);
				String category = LikeGroup.getLikeGroupFromCategory(tempJsonObject.get("category").toString().replaceAll("\"", ""));
				if (category.compareToIgnoreCase("Unknown") == 0) {
					continue;
				}

				String name = tempJsonObject.get("name").toString().replaceAll("\"", "");
				String id = tempJsonObject.get("id").toString().replaceAll("\"", "");
				
				Likes tempLike = new Likes(name, category, id);
				addLike(tempLike);

				String likeCategory = tempLike.category;
				if (userLikesToFrequency.containsKey(likeCategory)){
					int freq = userLikesToFrequency.get(likeCategory);
					userLikesToFrequency.put(likeCategory, ++freq);
				}
				else{
					userLikesToFrequency.put(likeCategory, 1);
				}
			}
		}
		
		Set userCategories = userLikesToFrequency.keySet();
		Iterator itrCategories = userCategories.iterator();
		while (itrCategories.hasNext()){
			String likeCat = (String)itrCategories.next();
			LikeFrequency tempLikeFrequecy = new LikeFrequency(likeCat, (int)userLikesToFrequency.get(likeCat));
			frequencyOfLikes.add(tempLikeFrequecy);
		}	
		Collections.sort(frequencyOfLikes, new LikeFrequencyComparator());
	}
	
	public void printFrequencyOfLikes(){
		Iterator itr = frequencyOfLikes.iterator();
		while (itr.hasNext()){
			LikeFrequency temp = (LikeFrequency)itr.next();
		}
	}
	
	
	public String toString(){
		return "User Name: "+ userName+ "\nUser ID: "+ userId;
	}
	
}

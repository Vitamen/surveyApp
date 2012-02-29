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
				String name = tempJsonObject.get("name").toString().replaceAll("\"", "");
				String id = tempJsonObject.get("id").toString().replaceAll("\"", "");
				System.out.println(tempJsonObject.get("category").toString());
				Likes tempLike = new Likes(name, category, id);
				addLike(tempLike);
				String likeCategory = tempLike.category;
				if (userLikesToFrequency.containsKey(likeCategory)){
					int freq = userLikesToFrequency.get(likeCategory);
					userLikesToFrequency.put(likeCategory, freq++);
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

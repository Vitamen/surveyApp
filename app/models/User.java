package models;

import java.util.LinkedList;

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
	
	//Use @ManyToMany entity
	
	public LinkedList<Likes> allUserLikes;
	
	public User(String name, String userName, String userId)
	{
		this.name = name;
		this.userName = userName;
		this.userId = userId;
		allUserLikes = new LinkedList<Likes>();
	}
	
	public void addLike(Likes newLike){
		allUserLikes.add(newLike);
	}
	
	public void addAllLikes(JsonArray allLikes){
		if (allLikes != null){
			for (int i = 0; i < allLikes.size(); i++)
			{
				JsonObject tempJsonObject = (JsonObject)allLikes.get(i);
				String category = tempJsonObject.get("category").toString();
				Likes tempLike = new Likes(tempJsonObject.get("name").toString(), tempJsonObject.get("category").toString(), tempJsonObject.get("id").toString());
				addLike(tempLike);
			}
		}
	}
	
	public String toString(){
		return "User Name: "+ userName+ "\nUser ID: "+ userId;
	}
	
}

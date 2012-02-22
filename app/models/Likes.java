package models;

import play.db.jpa.Model;


public class Likes extends Model{
	public String likesName;
	public String category;
	public String likesId;
	
	public Likes(String likesName, String category, String likesId)
	{
		this.likesName = likesName;
		this.category = category;
		this.likesId = likesId;
	}
}
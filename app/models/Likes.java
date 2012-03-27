package models;

import javax.persistence.Entity;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import play.db.jpa.Model;

import play.db.jpa.Model;

@Entity
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
	
	public boolean sameCategoryAs(Likes like) {
		return this.category.compareToIgnoreCase(like.category) == 0;
	}
}

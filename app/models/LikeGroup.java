package models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Scanner;
import java.util.Set;

import javax.persistence.*;
import play.db.jpa.Model;


@Entity
public class LikeGroup extends Model{
	public String likeCategory;
	public String likeGroup;
	
	public static String[] categories = {
			"Travel/leisure",
			"Movie",
			"Musician/band",
			"Internet/software",
			"Clothing",
			"Business/economy",
			"Tv show",
			"Local business",
			
			"Professional sports team",
			"Shopping/retail",
			"Sports league",
			"Magazine",
			"Games/toys",
			"Tv network",
			"Entertainment",
			"Amateur sports team",
			"Restaurant/cafe",
			"Sports/recreation/activities",
			"Club",
			"Computers/technology",
			
			"Food/beverages",
			"School sports team",
			"University"
			};
	public static String[] groups = {
			"Travel",
			"Movie",
			"Music",
			"Technology",
			"Fashion",
			"Business",
			"Entertainment",
			"Local",
			
			"Sports",
			"Shopping",
			"Sports",
			"Entertainment",
			"Games",
			"Entertainment",
			"Entertainment",
			"Sports",
			"Food",
			"Sports",
			"Entertainment",
			"Technology",
			
			"Food",
			"Sports",
			"Education"
			};
	public static void generateLikeGroupsFromStaticArray() {
		for (int i = 0; i < categories.length; i++) {
			LikeGroup lg = LikeGroup.find("byLikeCategory", categories[i]).first();
			if (lg == null) {
				lg = new LikeGroup();
				lg.likeCategory = categories[i];
				lg.likeGroup = groups[i];
				lg.save();
			}
		}
	}
	
	public static String getLikeGroupFromCategory(String category) {
		LikeGroup likeGroup = LikeGroup.find("byLikeCategory", category).first();
		String group = null;
		
		if (likeGroup == null) {
			System.out.println("System does not recognize the type "+category+". Please enter category for this type");
			Scanner scanner = new Scanner(System.in);
			group = scanner.next();
			
			likeGroup = new LikeGroup();
			likeGroup.likeCategory = category;
			likeGroup.likeGroup = group;
			likeGroup.save();
		} else {
			group = likeGroup.likeGroup;
		}
		return group;
	}
}

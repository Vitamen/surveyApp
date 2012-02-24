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

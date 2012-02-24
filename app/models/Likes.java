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
	
	private static HashMap<String, Set<String> > likeMap;
	
	public Likes(String likesName, String category, String likesId)
	{
		this.likesName = likesName;
		this.category = category;
		this.likesId = likesId;
	}
	
	public static void generateLikeMap() {
		likeMap = new HashMap<String, Set<String> >();
		String[][] likeTypes = {
				{"Computer", "Electronics", "Software", "Computers/Technology", "Internet/software", "TeleCommunication"}
		};
		String[] likeGroups = {
				"Technology"
		};

		HashSet<String> set = new HashSet<String>();
		for (int i = 0; i < likeTypes.length; i++) {
			String[] likeTypeArray = likeTypes[i];
			for (int j = 0; j < likeTypeArray.length; j++) {
				set.add(likeTypeArray[j]);
			}
			likeMap.put(likeGroups[i], set);
		}
	}
	
	public static String getLikeGroupFromCategory(String type) {
		if (likeMap == null) {
			generateLikeMap();
		}
		
		Set <String> groupSet = likeMap.keySet();
		Iterator <String> groupIter = groupSet.iterator();
		while (groupIter.hasNext()) {
			String group = groupIter.next();
			if (likeMap.get(group).contains(type)) {
				return group;
			}
		}
		System.out.println("System does not recognize the type "+type+". Please enter category for this type");
		Scanner scanner = new Scanner(System.in);
		String group = scanner.next();
		
		if (likeMap.containsKey(group)) {
			likeMap.get(group).add(type);
		} else {
			HashSet<String> tempTypeSet = new HashSet<String>();
			likeMap.put(group, tempTypeSet);
		}
		System.out.println("here");
		return group;
	}
}

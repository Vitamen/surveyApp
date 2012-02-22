package models;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import play.db.jpa.Model;


public class Likes extends Model{
	public String likesName;
	public String category;
	public String likesId;
	
	private static HashMap<Set<String>, String> likeMap;
	
	public Likes(String likesName, String category, String likesId)
	{
		this.likesName = likesName;
		this.category = category;
		this.likesId = likesId;
	}
	
	public static void generateLikeMap() {
		likeMap = new HashMap<Set<String>, String>();
		String[][] likeTypes = {
				{"Computer", "Electronics", "Software", "Computers/Technology", "Internet/Software", "TeleCommunication"}
		};
		String[] likeCategories = {
				"Technology"
		};

		HashSet<String> set = new HashSet<String>();
		for (int i = 0; i < likeTypes.length; i++) {
			String[] likeTypeArray = likeTypes[i];
			for (int j = 0; j < likeTypeArray.length; j++) {
				set.add(likeTypeArray[j]);
			}
			likeMap.put(set, likeCategories[i]);
		}
	}
	
	public static String getLikeCategoryFromType(String type) {
		if (likeMap == null) {
			generateLikeMap();
		}
		
		Set < Set<String> > typeSet = likeMap.keySet();
		Iterator < Set<String> > typeIter = typeSet.iterator();
		while (typeIter.hasNext()) {
			Set<String> types = typeIter.next();
			if (types.contains(type)) {
				return likeMap.get(types);
			}
		}
		
		return "";
	}
}
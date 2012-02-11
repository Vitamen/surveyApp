package models;

import java.util.*;

public class Choice {
	
	public List<Recommendation> recommendations;
	
	public Choice() {
		recommendations = new ArrayList<Recommendation>();
	}
	
	public void addRecommendation(Recommendation rec) {
		recommendations.add(rec);
	}
}

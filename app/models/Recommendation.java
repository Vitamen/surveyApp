package models;

import java.util.*;

public class Recommendation {
	public Topic topic;
	public List<Reason> reasons;
	
	public Recommendation(Topic topic) {
		this.topic = topic;
		reasons = new ArrayList<Reason>();
	}
	
	public void addReason(Reason reason) {
		reasons.add(reason);
	}
}

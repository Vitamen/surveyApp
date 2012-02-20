package models;

import play.*;
import play.db.jpa.Model;
import play.mvc.*;

import java.util.*;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

@Entity
public class Recommendation extends Model{
	public Topic topic;
	
	@OneToMany
	public List<Reason> reasons;
	
	public Recommendation(Topic topic) {
		this.topic = topic;
		reasons = new ArrayList<Reason>();
	}
	
	public void addReason(Reason reason) {
		reasons.add(reason);
	}
}

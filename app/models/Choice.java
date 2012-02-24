package models;

import play.*;

import play.db.jpa.Model;
import play.mvc.*;

import java.util.*;

import javax.persistence.*;

@Entity
public class Choice extends Model{
	
	@ManyToMany 
	@JoinColumn
	public List<Recommendation> recommendations;
	public int selection;
	
	public Choice() {
		recommendations = new ArrayList<Recommendation>();
		selection = -1; //-1 means choice has not been made yet.
	}
	
	public void addRecommendation(Recommendation rec) {
		recommendations.add(rec);
	}
}

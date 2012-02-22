package models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Feed extends Model {
	@ElementCollection
	public List<String> tags;
	
	public String link;
	public Date lastUpdate;
	
	public Feed(String fl) {
		link = fl;
		tags = new ArrayList<String> ();
	}
}

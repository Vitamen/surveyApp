package models;

import java.util.List;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;

import play.db.jpa.Model;

@Entity
public class Cluster extends Model {

	public String name;
	
	@OneToMany(fetch=FetchType.EAGER,cascade={CascadeType.REMOVE,CascadeType.ALL})
	public List<Topic> topics;
	
	//public Map<String,Integer> CategoryCount;
	
}

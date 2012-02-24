package models;

import java.util.ArrayList;
import java.util.List;

import play.db.jpa.Model;

import javax.persistence.*;

@Entity
public class Topic extends Model {
	@ElementCollection
	public List<String> tags;
	
	public String title;
	public String description;
	public String content;
	public String link;
	
	public Topic() {
		tags = new ArrayList<String>();
	}
}

package models;

import java.util.ArrayList;
import java.util.List;

import play.db.jpa.Model;

import javax.persistence.*;

@Entity
public class Topic extends Model {
	@ElementCollection
	public List<Tag> tags;
	
	@ManyToOne
	public Feed feed;
	
	public String title;
	
	@Column (columnDefinition="TEXT")
	public String description;
	
	@Lob
	public String link;
	
	public Topic() {
		tags = new ArrayList<Tag>();
		title = "";
		description = "";
		link = "";
	}
}

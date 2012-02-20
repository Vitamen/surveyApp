package models;

import play.db.jpa.Model;

import javax.persistence.*;

@Entity
public class Topic extends Model {
	public String title;
	public String description;
	public String content;
	public String link;
}

package models;

import java.util.Date;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class Feed extends Model{
	public String link;
	public Date lastUpdate;
	
	public Feed(String fl) {
		link = fl;
	}
}

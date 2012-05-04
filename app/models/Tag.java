package models;

import java.util.ArrayList;
import java.util.List;

import play.db.jpa.Model;

import javax.persistence.*;

@Entity
public class Tag extends Model {
	public String name;
	public double confidence;
	public int rank;
	
	public Tag() {
		name = "";
		confidence = 0;
		rank = 0;
	}
}

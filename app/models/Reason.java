package models;

import play.*;
import play.db.jpa.Model;
import play.mvc.*;

import java.util.*;

import javax.persistence.*;

@Entity
public class Reason extends Model{
	public static final int LIKE = 1;
	public static final int CHECKIN = 1<<2;
	public static final int SUBSCRIPTION = 1<<4;
	public static final int GROUP = 1<<6;
	
	public static final int GENERIC = 1<<3;

	public static final int SOLO = 1;
	public static final int MUTUAL = 1<<1;

	@ElementCollection
	List<Long> linkedFriends;

	public int type;
	public boolean isCategory;
	
	public static Reason getReasonWithType(int type) {
		Reason reason = Reason.find("byType", type).first();
		if (reason == null) {
			reason = new Reason();
			reason.type = type;
			reason.isCategory = false;
			reason.save();
		}
		
		return reason;
	}
	
	public static Reason getLikeCategoryReason() {
		Reason reason = Reason.find("byTypeAndIsCategory", LIKE, true).first();
		if (reason == null) {
			reason = new Reason();
			reason.type = LIKE;
			reason.isCategory = true;
			reason.save();
		}
		
		return reason;
	}
	
	public static Reason getCategoryReason(int type) {
		Reason reason = Reason.find("byTypeAndIsCategory", type, true).first();
		if (reason == null) {
			reason = new Reason();
			reason.type = type;
			reason.isCategory = true;
			reason.save();
		}
		
		return reason;
	}
}

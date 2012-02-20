package models;

import play.*;
import play.db.jpa.Model;
import play.mvc.*;

import java.util.*;

import javax.persistence.*;

@Entity
public class Reason extends Model{
	public static final int LIKE = 1;
	public static final int LIKE_CATEGORY = 1<<1;
	public static final int CHECKIN = 1<<2;
	public static final int CHECKIN_CATEGORY = 1<<3;
	public static final int SUBSCRIPTION = 1<<4;
	public static final int SUBSCRIPTION_CATEGORY = 1<<5;
	public static final int GROUP = 1<<6;
	public static final int GROUP_CATEGORY = 1<<7;

	public static final int SOLO = 1;
	public static final int MUTUAL = 1<<1;

	@ElementCollection
	List<Long> linkedFriends;

	int reasonType;
	int reason;
	int categoryReason;
}

package models;

import java.util.*;

public class Reason {
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

	List<Long> linkedFriends;

	int reasonType;
	int reason;
	int categoryReason;
}

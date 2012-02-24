package models;

import java.util.Comparator;

public class LikeFrequencyComparator implements Comparator<LikeFrequency>{

	@Override
	public int compare(LikeFrequency arg0, LikeFrequency arg1) {
		if (arg0.frequency>arg1.frequency)
			return -1;
		else if (arg0.frequency == arg1.frequency)
			return 0;
		else
			return 1;
		
	}
	

}

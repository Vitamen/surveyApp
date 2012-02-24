package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class LikeFrequency extends Model implements Comparable{

	public String likeCategory;
	public int frequency;
	public LikeFrequency(String likeCat, int i) {
		likeCategory = likeCat;
		frequency = i;
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof LikeFrequency))
			throw new ClassCastException("Invalid Object");
		
		int freq = ((LikeFrequency)o).frequency;
		if (this.frequency > freq)
			return 1;
		else if (this.frequency < freq)
			return -1;
		else
			return 0;
		
	}
}

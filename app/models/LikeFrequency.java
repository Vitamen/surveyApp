package models;

import javax.persistence.Entity;

import play.db.jpa.Model;

@Entity
public class LikeFrequency extends Model implements Comparable{

	String likeCategory;
	int frequency;
	public LikeFrequency(String likeCat, int i) {
		likeCategory = likeCat;
		frequency = i;
	}

	@Override
	public int compareTo(Object o) {
		if (!(o instanceof LikeFrequency))
			throw new ClassCastException("Invalid Object");
		
		int freq = ((LikeFrequency)o).getFrequency();
		if (this.getFrequency() > freq)
			return 1;
		else if (this.getFrequency() < freq)
			return -1;
		else
			return 0;
		
	}
	
	public void setFrequency(int frequency)
	{
		this.frequency= frequency;
	}
	
	public int getFrequency(){
		return frequency;
	}
	
	public String getLikeCategory(){
		return likeCategory;
	}
	
	public void setLikeCategory(String likeCategory)
	{
		this.likeCategory = likeCategory;
	}
}

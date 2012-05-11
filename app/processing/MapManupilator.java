package processing;

import java.util.HashMap;

import models.Cluster;
import models.Tag;
import models.Topic;

public class MapManupilator {

	
	public void MapManupilator(){
		
	}
	
	
	public HashMap<String, Double> createFrequencyMap(Cluster c){
		
		HashMap<String, Double> clusterWordRank = new HashMap<String, Double>();
		
		//Loop over all topics and add words to map
		for(Topic t : c.topics){
			for(Tag tag: t.tags){
				if(!clusterWordRank.containsKey(tag)){
					clusterWordRank.put(tag.name,tag.confidence);
				}
				else{
					//Merge the values by adding them
					clusterWordRank.put(tag.name, clusterWordRank.get(tag) + tag.confidence);
				}
			}
		}
		
		return clusterWordRank;
	}
	
	public HashMap<String, Double> mergeHashMaps(HashMap<String,Double> m1 , HashMap<String,Double> m2){
		
		for(String key:m2.keySet()){
			if(!m1.containsKey(key)){
				m1.put(key,m2.get(key));
			}
			else{
				//Merge the values by adding them
				m1.put(key, m2.get(key) + m1.get(key));
			}
		}
		return m1;
	}
	
	public HashMap<String, Double> intersectHashMaps(HashMap<String,Double> m1 , HashMap<String,Double> m2){
		
		for(String key:m2.keySet()){
			if(!m1.containsKey(key)){
				m1.put(key,m2.get(key));
			}
			else{
				//Merge the values by adding them
				m1.put(key, m2.get(key) + m1.get(key));
			}
		}
		
		m1.keySet().retainAll(m2.keySet());
		return m1;
	}
}

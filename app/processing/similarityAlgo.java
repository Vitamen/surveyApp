package processing;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class similarityAlgo {

	 public similarityAlgo(){
		 
	 }
	
	public static double cosine_similarity(Map<String, Double> v1, Map<String, Double> v2) {

	
	 ArrayList<String> both = new ArrayList();
     both.addAll(v1.keySet());
     both.addAll(v2.keySet());
     
     Double[] vecA = new Double[both.size()];
     Double[] vecB = new Double[both.size()];
     
     for(int i=0; i < both.size() ; i ++){ 
    	 if (v1.containsKey(both.get(i))){
    		 vecA[i] = (v1.get(both.get(i)));
    	 }else{
    		 vecA[i] = (double) 0.0;
    	 }
    	 
    	 if (v2.containsKey(both.get(i))){
    		 vecB[i] = (v2.get(both.get(i)));
    	 }else{
    		 vecB[i] = (double) 0.0;
    	 }
     }
     return CalculateCosineSimilarity(vecA,vecB);
	
	}
	

private static double CalculateCosineSimilarity(Double[] vecA, Double[] vecB)
{
	Double dotProduct = DotProduct(vecA, vecB);
	Double magnitudeOfA = Magnitude(vecA);
	Double magnitudeOfB = Magnitude(vecB);

	return dotProduct/(magnitudeOfA*magnitudeOfB);
}

private static double DotProduct(Double[] vecA, Double[] vecB)
{
	// I'm not validating inputs here for simplicity.            
	double dotProduct = 0;
	for (int i = 0; i < vecA.length; i++)
	{
		dotProduct += (vecA[i] * vecB[i]);
	}

	return dotProduct;
}


// Magnitude of the vector is the square root of the dot product of the vector with itself.
private static double Magnitude(Double[] vector)
{
	return Math.sqrt(DotProduct(vector, vector));
}

}

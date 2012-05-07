package processing;

import java.util.Arrays;
import java.util.StringTokenizer;

import models.Topic;

import play.jobs.Job;
import pt.voiceinteraction.keyphraseextraction.KeyPhrase;
import controllers.EnglishKeyPhraseExtractor;

public class processTags extends Job{

	public void processTags(){
		
	}

public static void dojob(Topic topic){
		
		while(topic.tags.size() > 1) {
    		topic.tags.remove(1);
    		topic.save();
    	}
    	
        try {
            int nrKeyphrases = 0;
            StringTokenizer st = new StringTokenizer(topic.description);
            int numberOfWords = st.countTokens();
            nrKeyphrases = Math.max(5, Math.min(30, numberOfWords / 30));

            EnglishKeyPhraseExtractor extractor = new EnglishKeyPhraseExtractor("data/English_KEModel_manualData",
                    "data/models/en_US/hub4_all.np.4g.hub97.1e-9.clm",
                    "data/models/en_US/left3words-wsj-0-18.tagger",
                    "data/stopwords/stopwords_en.txt");
           
            String[] texts = new String[]{
            		topic.description
            };
            for (KeyPhrase keyPhrase : extractor.getKeyphrases(nrKeyphrases, Arrays.asList(texts))) {
                System.out.println("Got keyphrase: "+keyPhrase.getKeyPhrase());
            	topic.tags.add(keyPhrase.getKeyPhrase());
            	topic.save();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
	}
	
	
}

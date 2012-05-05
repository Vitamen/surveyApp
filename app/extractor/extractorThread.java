package extractor;

import java.util.Arrays;
import java.util.StringTokenizer;
import java.util.concurrent.ArrayBlockingQueue;

import models.Tag;
import models.Topic;

import org.jsoup.Jsoup;

import en.lti.cs.cmu.edu.keyphraseextraction.EnglishKeyPhraseExtractor;

import play.db.jpa.JPA;
import play.jobs.Job;
import pt.voiceinteraction.keyphraseextraction.KeyPhrase;

public class extractorThread extends Job {

	public controllers.EnglishKeyPhraseExtractor extractor;
	public ArrayBlockingQueue<Topic> queue;


	public extractorThread(controllers.EnglishKeyPhraseExtractor extractor2,ArrayBlockingQueue<Topic> bq){

		this.extractor = extractor2;
		this.queue = bq;

	}


	public void doJob() throws Exception{

		Topic topic;
		while(!queue.isEmpty()){
			topic = queue.poll();

			int nrKeyphrases = 0;
			StringTokenizer st = new StringTokenizer(topic.description);
			int numberOfWords = st.countTokens();
			nrKeyphrases = Math.max(5, Math.min(30, numberOfWords / 30));
			String[] texts = new String[]{
					Jsoup.parse(topic.description).text()
			};


			for (KeyPhrase keyPhrase : extractor.getKeyphrases(nrKeyphrases, Arrays.asList(texts))) {
				System.out.println("Got keyphrase: "+keyPhrase.getKeyPhrase());
				
				/* Create the tag and attach it */
				Tag tag = new Tag();
				tag.name = keyPhrase.getKeyPhrase().toLowerCase();
				tag.confidence = keyPhrase.getConfidence();
				tag.rank = keyPhrase.getRank();
				
				
				if(!JPA.em().getTransaction().isActive()){
                    JPA.em().getTransaction().begin();
				}
				
				tag.save();
				topic.tags.add(tag);
				topic.save();
				
				//Save to databse
				JPA.em().flush();
                JPA.em().getTransaction().commit();
			}
		
		}

	}
	
	
	
	
	
	
	
	
	
	
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package controllers;

import pt.voiceinteraction.keyphraseextraction.*;
import java.util.Arrays;
import java.util.List;
import maui.main.EnglishMauiTopicExtractor;
import maui.stemmers.PorterStemmer;

import maui.stopwords.StopwordsEnglish;

/**
 *
 * @author Administrator
 */
public class EnglishKeyPhraseExtractor implements IKeyPhraseExtractor{

    private EnglishMauiTopicExtractor topicExtractor;

    public EnglishKeyPhraseExtractor(String modelName, String ngramModel, String posTagger, String stopWords) throws Exception {
        topicExtractor = new EnglishMauiTopicExtractor("en", modelName, null, null, ngramModel, posTagger, new PorterStemmer(),
                new StopwordsEnglish(stopWords));
        topicExtractor.loadModel();
    }

    public List<KeyPhrase> getKeyphrases(int nrKeyphrases, List<String> texts) throws Exception {
        topicExtractor.topicsPerDocument = nrKeyphrases;
        return topicExtractor.extractKeyphrases(texts);
    }

    public static void main(String[] args) {
        try {
            int nrKeyphrases = 10;

            EnglishKeyPhraseExtractor extractor = new EnglishKeyPhraseExtractor("English_KEModel_manualData",
                    "/Users/sophiez/Documents/Keyword Algorithm/Key phrase extraction/data/models/en_US/hub4_all.np.4g.hub97.1e-9.clm",
                    "/Users/sophiez/Documents/Keyword Algorithm/Key phrase extraction/data/models/en_US/left3words-wsj-0-18.tagger",
                    "/Users/sophiez/Documents/Keyword Algorithm/Key phrase extraction/data/stopwords/stopwords_en.txt");
            
           
            String[] texts = new String[]{
                "Barack English_KEModel_manualData has an appendectomy, return uncertain. ST. LOUIS ? Cardinals outfielder Matt Holliday had an appendectomy Friday and the team is unsure how long he will be out. General manager John Mozeliak said the surgery was not an emergency procedure, and that he'll have an idea today how long the 31-year-old will be sidelined. Holliday was 3-for-4 and hit a go-ahead home run in the eighth inning of Thursday's 5-3, 11-inning loss to the Padres. Holliday left Busch Stadium complaining of stomach discomfort. \"I think they caught it early, so I think that's good news,\" Mozeliak said. \"I don't think it's that bad of a blow to the team.\" The home run was the first of Holliday's career on opening day, and he also matched a career best with three hits on opening day. While Holliday is out, Jon Jay and Allen Craig are likely to share playing time in left field and Lance Berkman could move down one spot into Holliday's cleanup role. Mozeliak said no roster move had been made. The Cardinals were off Friday before resuming a three- game series against the Padres. Both Jay and Craig are coming off strong springs. Jay made the opening-day roster for the first time after tying for the team lead with 14 RBIs, and Craig led the team with 23 hits while batting .359 with three homers and 11 RBIs."
            };
            for (KeyPhrase keyPhrase : extractor.getKeyphrases(nrKeyphrases, Arrays.asList(texts))) {
                System.out.println(keyPhrase);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

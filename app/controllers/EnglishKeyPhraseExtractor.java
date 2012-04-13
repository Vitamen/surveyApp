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
}

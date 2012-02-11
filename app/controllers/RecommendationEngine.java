package controllers;

import models.Choice;
import play.mvc.Controller;

public class RecommendationEngine {

	public static Choice getChoice() {
		return getChoice(2);
	}
	
	public static Choice getChoice(int numChoices) {
		Choice choice = new Choice();
		return choice;
	}
}

package controllers;

import play.*;
import play.db.jpa.JPA;
import play.mvc.*;

import java.util.*;

import javax.persistence.Query;

import models.*;

public class AnalysisEngine extends Controller {

	public static void makeSelection(long id, int selection) {
		Choice choice = Choice.findById(id);
		if (choice != null) {
			choice.selection = selection;
			choice.save();
		}
	}
	
	public static void reasonComparator(int reasonOneType, int reasonTwoType) {
		Reason reasonOne = Reason.find("byTypeAndIsCategory", reasonOneType, true).first();
		Reason reasonTwo = Reason.find("byTypeAndIsCategory", reasonTwoType, true).first();
		
		List<Choice> choicesOne = Choice.find("SELECT DISTINCT c " +
				"FROM Choice c JOIN c.recommendations r WHERE " +
				"? MEMBER OF r.reasons", reasonOne).fetch();
		
		List<Choice> choicesTwo = Choice.find("SELECT DISTINCT c " +
				"FROM Choice c JOIN c.recommendations r WHERE " +
				"? MEMBER OF r.reasons", reasonTwo).fetch();
	}
}

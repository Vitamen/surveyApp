package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

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
		Reason reason1 = Reason.getCategoryReason(reasonOneType);
		Reason reason2 = Reason.getCategoryReason(reasonTwoType);
	}
}

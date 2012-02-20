package controllers;

import play.*;
import play.mvc.*;

import java.util.*;

import models.*;

public class AnalysisEngine {

	public static void makeSelection(long id, int selection) {
		Choice choice = Choice.findById(id);
		if (choice != null) {
			choice.selection = selection;
			choice.save();
		}
	}
}

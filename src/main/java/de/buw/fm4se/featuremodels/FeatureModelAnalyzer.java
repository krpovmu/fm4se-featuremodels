package de.buw.fm4se.featuremodels;

import java.util.ArrayList;
import java.util.List;

import de.buw.fm4se.featuremodels.exec.LimbooleExecutor;
import de.buw.fm4se.featuremodels.fm.FeatureModel;

/**
 * This code needs to be implemented by translating FMs to input for Limboole
 * and interpreting the output
 *
 */
public class FeatureModelAnalyzer {

	public static boolean checkConsistent(FeatureModel fm) {
		String formula = FeatureModelTranslator.translateToFormula(fm);

		String result;
		try {
			result = LimbooleExecutor.runLimboole(formula, true);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (result.contains("UNSATISFIABLE")) {
			return false;
		}
		return true;
	}

	public static boolean checkConsistent(String formula) {

		// String formula = FeatureModelTranslator.translateToFormula(fm);

		String result;
		try {
			result = LimbooleExecutor.runLimboole(formula, true);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		if (result.contains("UNSATISFIABLE")) {
			return false;
		}
		return true;
	}

	public static List<String> deadFeatureNames(FeatureModel fm) {
		List<String> deadFeatures = new ArrayList<>();
		String formula = FeatureModelTranslator.translateToFormula(fm);
		
		for (int featureRoot = 0; featureRoot < fm.getRoot().getChildren().size(); featureRoot++) {
			if (fm.getRoot().getChildren().get(featureRoot).getChildren().size() > 0) {
				for (int featureChildren = 0; featureChildren < fm.getRoot().getChildren().get(featureRoot).getChildren().size(); featureChildren++) {
					String FeatureToEvaluateChildren = "!("+formula+" & "+ fm.getRoot().getChildren().get(featureRoot).getChildren().get(featureChildren).getName() + ")";
					if(checkConsistent(formula)== false){
						deadFeatures.add(FeatureToEvaluateChildren);
					}
				}
			} else {
				String FeatureToEvaluateFather = "!("+formula + " & "+ fm.getRoot().getChildren().get(featureRoot).getName()+")";
				if(checkConsistent(formula)== false){
					deadFeatures.add(FeatureToEvaluateFather);
				}
			}
		}
		System.out.println(deadFeatures);
		return deadFeatures;
	}
	
	public static List<String> mandatoryFeatureNames(FeatureModel fm) {
		List<String> mandatoryFeatures = new ArrayList<>();

		// TODO check for mandatory features

		return mandatoryFeatures;
	}

}

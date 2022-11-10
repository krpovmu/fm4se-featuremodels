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
				for (int featureChildren = 0; featureChildren < fm.getRoot().getChildren().get(featureRoot)
						.getChildren().size(); featureChildren++) {
					String FeatureToEvaluateChildren = "(" + formula + " & "
							+ fm.getRoot().getChildren().get(featureRoot).getChildren().get(featureChildren).getName()
							+ ")";
					if (checkConsistent(FeatureToEvaluateChildren) == false) {
						for (int f = 0; f < fm.getRoot().getChildren().get(featureRoot).getChildren().size(); f++) {
							if (deadFeatures.contains(fm.getRoot().getName())) {
								deadFeatures.add(fm.getRoot().getName());
							}
							deadFeatures
									.add(fm.getRoot().getChildren().get(featureRoot).getChildren().get(f).getName());
						}
					}
				}
			} else {
				String FeatureToEvaluateFather = "(" + formula + " & "
						+ fm.getRoot().getChildren().get(featureRoot).getName() + ")";
				if (checkConsistent(FeatureToEvaluateFather) == false) {
					deadFeatures.add(fm.getRoot().getName());
					deadFeatures.add(fm.getRoot().getChildren().get(featureRoot).getName());
				}
			}
		}
//		System.out.println(deadFeatures);
		return deadFeatures;
	}

	public static List<String> mandatoryFeatureNames(FeatureModel fm) {
		List<String> mandatoryFeatures = new ArrayList<>();
		List<String> deadfeautures = deadFeatureNames(fm);

		for (int featureRoot = 0; featureRoot < fm.getRoot().getChildren().size(); featureRoot++) {
			for (int featureChildren = 0; featureChildren < fm.getRoot().getChildren().get(featureRoot).getChildren()
					.size(); featureChildren++) {
				if (deadfeautures.contains(fm.getRoot().getChildren().get(featureRoot).getName())) {
					System.out.println("Dead feature : " + fm.getRoot().getChildren().get(featureRoot).getName());
				} else if (deadfeautures
						.contains(fm.getRoot().getChildren().get(featureRoot).getChildren().get(featureChildren))) {
					System.out.println("Dead feature : "
							+ fm.getRoot().getChildren().get(featureRoot).getChildren().get(featureChildren));
				} else {
					if (fm.getRoot().getChildren().get(featureRoot).isMandatory()
							&& !mandatoryFeatures.contains(fm.getRoot().getChildren().get(featureRoot).getName())) {
						mandatoryFeatures.add(fm.getRoot().getChildren().get(featureRoot).getName());
					} else if (fm.getRoot().getChildren().get(featureRoot).getChildren().get(featureChildren)
							.isMandatory()
							&& !mandatoryFeatures.contains(fm.getRoot().getChildren().get(featureRoot).getChildren()
									.get(featureChildren).getName())) {
						mandatoryFeatures.add(fm.getRoot().getChildren().get(featureRoot).getChildren()
								.get(featureChildren).getName());
					}
				}
			}
		}
//		System.out.println(mandatoryFeatures);
		return mandatoryFeatures;
	}
}

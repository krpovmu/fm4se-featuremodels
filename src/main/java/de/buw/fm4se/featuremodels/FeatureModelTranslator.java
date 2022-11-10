	package de.buw.fm4se.featuremodels;

import java.util.ArrayList;
import java.util.List;

import de.buw.fm4se.featuremodels.fm.Feature;
import de.buw.fm4se.featuremodels.fm.FeatureModel;
import de.buw.fm4se.featuremodels.fm.GroupKind;

public class FeatureModelTranslator {
	public static String translateToFormula(FeatureModel fm) {

		String root = "";
		String limbooleFormula = "";
		List<String> constrains = new ArrayList<>();
		List<String> childrens = new ArrayList<>();

		// Get root
		root = fm.getRoot().getName();

		// Get constrains
		if (fm.getConstraints().size() > 0) {
			for (int k = 0; k < fm.getConstraints().size(); k++) {
				String constrain = "";
				if (fm.getConstraints().get(k).getKind().name() == "REQUIRES") {
					constrain = "(" + fm.getConstraints().get(k).getLeft().getName() + " -> "
							+ fm.getConstraints().get(k).getRight().getName() + ")";
				} else if (fm.getConstraints().get(k).getKind().name() == "EXCLUDES") {
					constrain = "(" + fm.getConstraints().get(k).getLeft().getName() + " -> " + "!"
							+ fm.getConstraints().get(k).getRight().getName() + ")";
				}
				constrains.add(constrain);
			}
		}

		// get childrens
		if (fm.getRoot().getChildren().size() > 0) {
			limbooleFormula = root + " & ";
			// This is the first level of children
			for (int childrenofroot = 0; childrenofroot < fm.getRoot().getChildren().size(); childrenofroot++) {
				if (fm.getRoot().getChildren().get(childrenofroot).isMandatory()) {
					childrens.add("(" + fm.getRoot().getChildren().get(childrenofroot).getName() + " -> " + root
							+ ") & (" + root + " -> " + fm.getRoot().getChildren().get(childrenofroot).getName() + ")");
				} else {
					childrens.add("(" + fm.getRoot().getChildren().get(childrenofroot).getName() + " -> " + root + ")");
				}
			}

			// This is the children of the children
			for (int childrenofchildren_i = 0; childrenofchildren_i < fm.getRoot().getChildren()
					.size(); childrenofchildren_i++) {
				for (int childrenofchildren_j = 0; childrenofchildren_j < fm.getRoot().getChildren()
						.get(childrenofchildren_i).getChildren().size(); childrenofchildren_j++) {
					if (fm.getRoot().getChildren().get(childrenofchildren_i).getChildGroupKind() == GroupKind.OR) {
						String fatherOR = fm.getRoot().getChildren().get(childrenofchildren_i).getName();
						childrens.add("(" + fm.getRoot().getChildren().get(childrenofchildren_i).getChildren()
								.get(childrenofchildren_j).getName() + " -> " + fatherOR + ")");
						if (childrenofchildren_j == (fm.getRoot().getChildren().get(childrenofchildren_i).getChildren()
								.size() - 1)) {
							String featuresOR = "";
							for (int features_i = 0; features_i < fm.getRoot().getChildren().get(childrenofchildren_i)
									.getChildren().size(); features_i++) {
								boolean isLastOR = fm.getRoot().getChildren().get(childrenofchildren_i).getChildren()
										.indexOf(features_i) == (fm.getRoot().getChildren().get(childrenofchildren_i)
												.getChildren().size() - 1) ? true : false;
								if (features_i == 0 && !isLastOR) {
									featuresOR += fm.getRoot().getChildren().get(childrenofchildren_i).getChildren()
											.get(features_i).getName() + " | ";
								} else if (features_i == (fm.getRoot().getChildren().get(childrenofchildren_i)
										.getChildren().size() - 1)) {
									featuresOR += fm.getRoot().getChildren().get(childrenofchildren_i).getChildren()
											.get(features_i).getName();
								} else {
									featuresOR += " | " + fm.getRoot().getChildren().get(childrenofchildren_i)
											.getChildren().get(features_i).getName() + " | ";
								}
							}
							childrens.add("(" + fatherOR + " -> " + "(" + featuresOR + "))");
						}
					}

					else if (fm.getRoot().getChildren().get(childrenofchildren_i)
							.getChildGroupKind() == GroupKind.XOR) {
						// Nodes definition
						String fatherXOR = fm.getRoot().getChildren().get(childrenofchildren_i).getName();
						childrens.add("(" + fm.getRoot().getChildren().get(childrenofchildren_i).getChildren()
								.get(childrenofchildren_j).getName() + " -> " + fatherXOR + ")");
						List<Feature> nodesXOR = fm.getRoot().getChildren().get(childrenofchildren_i).getChildren();
						// XOR definition
						if (childrenofchildren_j == (fm.getRoot().getChildren().get(childrenofchildren_i).getChildren()
								.size() - 1)) {
							// XOR Limboole for N nodes
							String expression = "";
							for (int nodeChild_i = 0; nodeChild_i < nodesXOR.size(); nodeChild_i++) {
								for (int nodeChild_j = 0; nodeChild_j < nodesXOR.size(); nodeChild_j++) {
									boolean isLastXOR_i = nodesXOR
											.indexOf(nodesXOR.get(nodeChild_i)) == (nodesXOR.size() - 1) ? true : false;
									boolean isLastXOR_j = nodesXOR
											.indexOf(nodesXOR.get(nodeChild_j)) == (nodesXOR.size() - 1) ? true : false;
									if (nodesXOR.get(nodeChild_i).getName() == nodesXOR.get(nodeChild_j).getName()) {
										if (!isLastXOR_j) {
											expression += "!" + nodesXOR.get(nodeChild_j).getName() + " & ";
										} else if (nodeChild_j == (nodesXOR.size() - 1)) {
											expression += "!" + nodesXOR.get(nodeChild_j).getName();
										}
									} else {
										if (isLastXOR_j && !isLastXOR_i) {
											expression += nodesXOR.get(nodeChild_j).getName() + " | ";
										} else {
											expression += nodesXOR.get(nodeChild_j).getName() + " & ";
										}
									}
								}
							}
							childrens.add("(" + fatherXOR + ") -> (" + expression + ")");
						}
					}
				}
			}
		}

		// This part add the & to the nodes
		for (int children = 0; children < childrens.size(); children++) {
			if (children == (childrens.size() - 1)) {
				limbooleFormula += childrens.get(children);
			} else {
				limbooleFormula += childrens.get(children) + " & ";
			}
		}

		// adding constrains
		if (constrains.size() > 0) {
			for (int constrain = 0; constrain < constrains.size(); constrain++) {
				limbooleFormula = limbooleFormula + " & " + constrains.get(constrain);
			}
		} else {
			limbooleFormula = root;
		}
		return limbooleFormula;
	}
}
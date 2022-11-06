package de.buw.fm4se.featuremodels;

import de.buw.fm4se.featuremodels.fm.CrossTreeConstraint;
import de.buw.fm4se.featuremodels.fm.Feature;
import de.buw.fm4se.featuremodels.fm.FeatureModel;
import de.buw.fm4se.featuremodels.fm.GroupKind;

/**
 * Example code that recursively prints feature models.
 *
 */
public class PrinterExample {

  public static void main(String[] args) {
    FeatureModel fm = ExampleFmCreator.getSimpleFm();
    //FeatureModelTranslator mt = (fm);
    System.out.println(printFeatureModel(fm));
  }

  /**
   * simply forward the first invocation and add an empty indentation string
   * 
   * @param fm
   * @return
   */
  private static String printFeatureModel(FeatureModel fm) {
    String s = printFeature(fm.getRoot(), "");

    for (CrossTreeConstraint ctc : fm.getConstraints()) {
      s += "\n" + ctc.getLeft().getName() + " " + ctc.getKind() + " " + ctc.getRight().getName();
    }
    
    return s;
  }

  /**
   * print a feature together with its information and all of its children
   * 
   * @param f      the feature to print
   * @param indent indentation to use (will be increased by 2 spaces for each list
   *               of children)
   * @return
   */
  private static String printFeature(Feature f, String indent) {
    return indent + "feature " + f.getName() + " is " + (f.isMandatory() ? "mandatory" : "optional")
        + printChildInfo(f, indent + "  ");
  }

  /**
   * Print information about children: group kind, and all children (recursively)
   * 
   * @param f
   * @param indent
   * @return
   */
  private static String printChildInfo(Feature f, String indent) {
    String s = " and has " + f.getChildren().size() + " children";
    if (!GroupKind.NONE.equals(f.getChildGroupKind())) {
      s += " in a " + f.getChildGroupKind() + "-group";
    }
    for (Feature c : f.getChildren()) {
      s += "\n" + indent + printFeature(c, indent);
    }
    return s;
  }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shubham.develop;

import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;
import weka.attributeSelection.ASEvaluation;
import weka.attributeSelection.ASSearch;
import weka.attributeSelection.AttributeEvaluator;
import weka.attributeSelection.AttributeSelection;
import weka.attributeSelection.AttributeTransformer;
import weka.attributeSelection.GreedyStepwise;
import weka.attributeSelection.Ranker;
import weka.classifiers.meta.AttributeSelectedClassifier;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.gui.TaskLogger;

/**
 *
 * @author Shubham Kumar
 */
public class AttributeSelectionUtil {
    private Object m_Evaluator;
    private Object m_SearchMethod;
    private static int CV = 0;
    private int m_Seed = 1;
    private int m_Folds = 10;
    private int m_ClassIndex;
    
    /** Constructor that takes: 1. evaluator 2. searchMethod
     * and makes adjustments to ensure compatibility
    */
    public AttributeSelectionUtil(Object evaluator, Object searchMethod){
        if(evaluator instanceof AttributeEvaluator) {
            if(!(searchMethod instanceof Ranker)) {
                searchMethod = new Ranker();
            }
        } else {
            if(searchMethod instanceof Ranker) {
                searchMethod = new GreedyStepwise();
            }
        }
        m_Evaluator = evaluator;
        m_SearchMethod = searchMethod;
    }
    
    /** selectAttributes
     * @params: 
     * inst: Input Instances
     * classIndex: index of the attribute containing the target class
     * choice: Indicates whether to perform attribute selection on training data or CV
     * @returns: instance with selected Attributes 
     */
    public Instances selectAttributes(weka.core.Instances inst,int classIndex,int choice) throws Exception {
        Instances retInstances = null;
        ASEvaluation evaluator = (ASEvaluation) m_Evaluator;
        ASSearch searchMethod = (ASSearch) m_SearchMethod;
        AttributeSelection filters = new AttributeSelection();
        filters.setEvaluator(evaluator);
        filters.setSearch(searchMethod);
        AttributeSelectedClassifier cls = new AttributeSelectedClassifier();
        cls.setEvaluator(evaluator);
        cls.setSearch(searchMethod);
        if(choice == CV) {
            int folds = m_Folds;
            int seed = m_Seed;
            if (folds <= 1) {
                throw new Exception("Number of folds must be greater than 1");
            }
        }
        inst.setClassIndex(classIndex);
        AttributeSelection eval = new AttributeSelection();
        eval.setEvaluator(evaluator);
        eval.setSearch(searchMethod);
        eval.setFolds(m_Folds);
        eval.setSeed(m_Seed);
        if (choice == CV) {
            Random rand = new Random(m_Seed);
            if(inst.attribute(classIndex).isNominal()) {
                inst.stratify(m_Folds);
            }
            for (int fold = 0; fold < m_Folds; fold++) {
                Instances train = inst.trainCV(m_Folds, fold, rand);
                eval.selectAttributesCVSplit(train);
            }
        } else {
            eval.SelectAttributes(inst);
        }
        ArrayList<Object> vv = new ArrayList<Object>();
        Vector<Object> configHolder = new Vector<Object>();
        try {
          ASEvaluation eval_copy = evaluator.getClass().newInstance();
          if (evaluator instanceof OptionHandler) {
            ((OptionHandler) eval_copy)
              .setOptions(((OptionHandler) evaluator).getOptions());
          }

          ASSearch search_copy = searchMethod.getClass().newInstance();
          if (searchMethod instanceof OptionHandler) {
            ((OptionHandler) search_copy)
              .setOptions(((OptionHandler) searchMethod).getOptions());
          }
          configHolder.add(eval_copy);
          configHolder.add(search_copy);
        } catch (Exception ex) {
          configHolder.add(evaluator);
          configHolder.add(searchMethod);
        }
        vv.add(configHolder);

        if (evaluator instanceof AttributeTransformer) {
          try {
            Instances transformed =
              ((AttributeTransformer) evaluator).transformedData(inst);
            transformed
              .setRelationName("AT: " + transformed.relationName());

            vv.add(transformed);
            retInstances = transformed;
          } catch (Exception ex) {
            System.err.println(ex);
            ex.printStackTrace();
          }
        } else if (choice != CV) {
          try {
            Instances reducedInst = eval.reduceDimensionality(inst);
            System.out.println(reducedInst.toString());
            vv.add(reducedInst);
            retInstances = reducedInst;
          } catch (Exception ex) {
            ex.printStackTrace();
          }
        }
        if (choice != CV) {
            System.out.println(eval.toResultsString());
        } else {
            System.out.println(eval.CVResultsString());
        }
        return retInstances;
    }
    public void setFolds(int folds) {
        m_Folds = folds;
    }
    public void setSeed(int seed) {
        m_Seed = seed;
    }
    public void setClassIndex(int classIndex) {
        m_ClassIndex = classIndex;
    }
}

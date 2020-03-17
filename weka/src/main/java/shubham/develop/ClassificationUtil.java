/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shubham.develop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.Classifier;
import weka.classifiers.CostMatrix;
import weka.classifiers.Evaluation;
import weka.classifiers.IterativeClassifier;
import weka.classifiers.Sourcable;
import weka.classifiers.UpdateableBatchProcessor;
import weka.classifiers.UpdateableClassifier;
import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.NumericPrediction;
import weka.classifiers.evaluation.Prediction;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.classifiers.evaluation.output.prediction.AbstractOutput;
import weka.classifiers.evaluation.output.prediction.PlainText;
import weka.classifiers.xml.XMLClassifier;
import weka.core.BatchPredictor;
import weka.core.Drawable;
import weka.core.Environment;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;
import weka.core.Utils;
import weka.core.converters.ConverterUtils;
import weka.core.xml.XMLOptions;
import weka.gui.explorer.ClassifierPanel;
import static weka.gui.explorer.ClassifierPanel.setupEval;

/**
 *
 * @author DELL
 */

public class ClassificationUtil {
    
    private boolean mOutputPerClass = true;
    private int mNumFolds = 10;
    private double mPercent = 66;
    /**
     * Function for classification 
     * @params: classifier - the classifier instance
     * inst: the input instances for classification
     * classIndex: index of the target class for classification
     */
    public void classify(Classifier classifier,Instances inst,int opt,int classIndex)throws Exception {
        CostMatrix costmatrix = null;
        classifier.getCapabilities();
        classifier.buildClassifier(inst);
        Evaluation eval = new Evaluation(inst,costmatrix,classifier.getClass().toString());
        eval = ClassifierPanel.setupEval(eval,classifier,inst,costmatrix,null,null,false,true);
        eval.setMetricsToDisplay(Evaluation.getAllEvaluationMetricNames());
        PlainText p[]=new PlainText[1];
        eval.evaluateModel(classifier,inst,p);
        eval.toClassDetailsString();
    }
}

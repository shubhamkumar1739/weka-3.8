/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shubham.develop;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.attributeSelection.AttributeEvaluator;
import weka.attributeSelection.CfsSubsetEval;
import weka.attributeSelection.Ranker;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.bayes.NaiveBayes;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.ConverterUtils.DataSource;
import weka.filters.unsupervised.attribute.PrincipalComponents;

/**
 * The diver class which can run all of the other utility classes
 * @author DELL
 */
public class TestClass {
    
    private Instances m_Instances;
    public static int TRAIN = 1;
    public TestClass()
    {
        
    }
    public Instances loadInstances(String path,int classIndex) throws Exception
    {
        System.out.println(path);
        ArffLoader loader = new ArffLoader();
        loader.setSource(new FileInputStream(new File(path)));
        Instances inst = loader.getDataSet();
        inst.setClassIndex(classIndex);
        m_Instances = inst;
        return inst;
    }
    public void classify(Instances inst,int classIndex) throws Exception
    {
        NaiveBayes classifier = new NaiveBayes();
        ClassificationUtil cUtil = new ClassificationUtil();
        cUtil.classify(classifier,inst,TRAIN,classIndex);
    }
    public Instances selectAttributes(Instances inst, int classIndex, int choice) {
        AttributeSelectionUtil attributeSelection = new AttributeSelectionUtil(new CfsSubsetEval(), new Ranker());
        Instances retInstances = null;
        try {
            retInstances = attributeSelection.selectAttributes(inst,classIndex,choice);
        } catch (Exception ex) {
            Logger.getLogger(TestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
        return retInstances;
    }
    public static void main(String args[])
    {
        try {
            TestClass t = new TestClass();
            String path = System.getenv("SystemDrive")+"/Users/DELL/Documents/Weka/Datasets/iris/iris1.arff";
            int classIndex = 4;
            Instances inst = t.loadInstances(path,classIndex);
            inst = t.selectAttributes(inst,classIndex,1);
            (new ClassificationUtil()).classify(new NaiveBayes(),inst,1,classIndex);
            //(new ClassificationUtil()).classify(new NaiveBayes(),inst,1,classIndex);
            //System.out.println(inst.toString());
        } catch (Exception ex) {
            Logger.getLogger(TestClass.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

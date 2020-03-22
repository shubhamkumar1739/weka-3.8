package shubham.algorithms;


import java.util.HashMap;
import java.util.Random;
import weka.classifiers.AbstractClassifier;
import weka.classifiers.rules.DecisionTableHashKey;
import weka.clusterers.NumberOfClustersRequestable;
import weka.clusterers.RandomizableClusterer;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.OptionHandler;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author DELL
 */
public class FCM extends RandomizableClusterer implements NumberOfClustersRequestable{
    
    protected int m_NumClusters = 3;
    
    protected Instances m_InitialStartPoints;
    
    protected Instances m_ClustersStdDevs;
    
    protected double[][][] m_ClusterNominalCounts;
    protected double[][] m_ClusterMissingCounts;
    
    protected int m_Iterations;
    
    protected double[][] m_MembershipFunction;
    
    protected Instances m_ClusterCentroids;
    
    protected int m_Seed = 2;
    
    protected Instances m_Instances;
    
    protected double r = 1.5;
    protected int m_MaxIterations = 10000;

    public int getM_MaxIterations() {
        return m_MaxIterations;
    }

    public void setM_MaxIterations(int m_MaxIterations) {
        this.m_MaxIterations = m_MaxIterations;
    }

    public double getR() {
        return r;
    }

    public void setR(double r) {
        this.r = r;
    }
    
    public void setSeed(int seed) {
        m_Seed = seed;
    }
    
    public int getSeed() {
        return m_Seed;
    }

    @Override
    public void buildClusterer(Instances data) throws Exception {
        getCapabilities().testWithFail(data);
        m_Iterations = 0;
        
        Instances instances = new Instances(data);
        instances.setClassIndex(-1);
        
        m_Instances = instances;
        
        initClusterCentroids(new Instances(instances));
        for(int i = 0; i < m_ClusterCentroids.numInstances(); i++) {
            System.out.println(m_ClusterCentroids.get(i));
        }
        System.out.println();
        
        updateMembership();
        for(int i = 0; i < m_Instances.numInstances(); i++) {
            for(int j = 0; j < m_NumClusters; j++) {
                System.out.print(m_MembershipFunction[i][j]+"  ");
            }
            System.out.println();
        }
        
        for(int i = 0; i < m_MaxIterations; i++) {
            moveCentroids();
            /*for(int k = 0; k < m_ClusterCentroids.numInstances(); k++) {
                System.out.println(m_ClusterCentroids.get(k));
            }
            System.out.println("Done");*/
            updateMembership();
        }            
    }

    @Override
    public int numberOfClusters() throws Exception {
        return m_NumClusters;
    }

    @Override
    public void setNumClusters(int numClusters) throws Exception {
        m_NumClusters = numClusters;
    }
    
    @Override
    public Capabilities getCapabilities() {
        Capabilities result = super.getCapabilities();
        result.disableAll();
        result.enable(Capability.NUMERIC_ATTRIBUTES);
        result.enable(Capability.NO_CLASS);
        return result;
    }

    private void initClusterCentroids(Instances instances) throws Exception {
        Random rand = new Random(getSeed());
        HashMap<DecisionTableHashKey, Integer> initC = new HashMap<>();
        DecisionTableHashKey hk = null;
        
        m_MembershipFunction = new double[instances.numInstances()][m_NumClusters];
        m_ClusterCentroids = new Instances(instances, m_NumClusters);
        int instIndex;
        for (int j = instances.numInstances() - 1; j >= 0; j--) {
        instIndex = rand.nextInt(j + 1);
        hk =
          new DecisionTableHashKey(instances.instance(instIndex),
            instances.numAttributes(), true);
        if (!initC.containsKey(hk)) {
          m_ClusterCentroids.add(instances.instance(instIndex));
          initC.put(hk, null);
        }
        instances.swap(j, instIndex);

        if (m_ClusterCentroids.numInstances() == m_NumClusters) {
          break;
        }
      }
        
    }

    private void updateMembership() {
        for(int i = 0; i < m_Instances.numInstances(); i++) {
            double denom = 0;
            if(checkCentroid(i)) {
                for(int j = 0; j < m_NumClusters; j++) {
                    Instance a = m_Instances.get(i);
                    Instance b = m_ClusterCentroids.get(j);
                    if(checkEquals(a, b)) {
                        m_MembershipFunction[i][j] = 1;
                    } else {
                        m_MembershipFunction[i][j] = 0;
                    }
                }
            } else {
                for(int k = 0; k < m_ClusterCentroids.numInstances(); k++) {
                    double val = Math.pow(dist(i,k), 1 / ((double)(r-1)));
                    //System.out.println("Val : "+val);
                    denom += 1 / val;
                }
                for(int j = 0; j < m_ClusterCentroids.numInstances(); j++) {
                    double sum = Math.pow(dist(i,j), 1 / ((double)(r - 1)));
                    sum *= denom;
                    m_MembershipFunction[i][j] = 1 / sum;
                }
            }
        }
    }
    
    private boolean checkCentroid(int i) {
        Instance a = m_Instances.get(i);
        for(int l = 0; l < m_NumClusters; l++) {
            Instance b = m_ClusterCentroids.get(l);
            if(checkEquals(a,b))
                return true;
        }
        return false;
    }
    
    public boolean checkEquals(Instance a,Instance b) {
        double epsilon = 1.0e-10;
        int flag = 0;
        for(int m = 0; m < b.numAttributes(); m ++) {
            if(Math.abs(a.value(m) - b.value(m)) < epsilon) {
                flag ++;
            }
        }
        if(flag == b.numAttributes()) {
            return true;
        }
        return false;
    }
    
    private void moveCentroids() {
        
        for(int j = 0; j < m_ClusterCentroids.numInstances(); j++) {
            for(int attr = 0; attr < m_Instances.numAttributes(); attr++) {    
                double num = 0, denom = 0;
                for(int i = 0; i < m_Instances.numInstances(); i++) {
                    num += m_Instances.get(i).value(attr) * Math.pow(m_MembershipFunction[i][j], r);
                    denom += Math.pow(m_MembershipFunction[i][j], r);
                }
                m_ClusterCentroids.get(j).setValue(attr, num / denom);
            }
        }
    }
    
    private double dist(int i, int j) {
        double result = 0;
        for(int attr = 0; attr < m_Instances.numAttributes(); attr++) {
            result += Math.pow((m_Instances.get(i).value(attr) - m_ClusterCentroids.get(j).value(attr)),2);
        }
        return result;
    }

    public int[] getClusters() {
        int clusters[] = new int[m_Instances.numInstances()];
        for(int i = 0; i < m_Instances.numInstances(); i++) {
            double max = 0;
            int maxIndex = 0;
            for(int j = 0; j < m_MembershipFunction[i].length; j++) {
                if(m_MembershipFunction[i][j] > max) {
                    max = m_MembershipFunction[i][j];
                    maxIndex = j + 1;
                }
            }
            clusters[i] = maxIndex;
        }
        return clusters;
    }
    
}

package shubham.develop;

import java.util.Arrays;
import shubham.algorithms.FCM;
import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.unsupervised.attribute.Remove;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author DELL
 */
public class ClusterUtil {
    
    protected static Instances m_Instances;
    protected static int m_NumClusters;
    
    public static void main(String args[]) throws Exception {
        String path = "C://Users//DELL//Documents//Weka//Datasets//agri//Data30.arff";
        TestClass t = new TestClass();
        int classIndex = 18;
        int numClusters = 10;
        m_NumClusters = numClusters;
        Instances inst = t.loadInstances(path, classIndex);
        m_Instances = inst;
        System.out.println(inst.firstInstance());
        FCM fcm = new FCM();
        fcm.setR(2.25);
        fcm.setNumClusters(numClusters);
        
        Remove remove = new Remove();
        int selectAttribute[] = {2,3,4,11,15};
        remove.setAttributeIndicesArray(selectAttribute);
        remove.setInvertSelection(true);
        remove.setInputFormat(inst);
        Instances selectedInstances = Filter.useFilter(inst,remove);
        System.out.println(selectedInstances.firstInstance());
        
        fcm.setNumClusters(numClusters);
        fcm.buildClusterer(selectedInstances);
        int clusters[] = fcm.getClusters();
        System.out.println(Arrays.toString(clusters));
        printStats(clusters);
    }

    private static void printStats(int[] clusters) {
        int stats[][] = new int[m_NumClusters][m_Instances.numInstances()];
        int st[][] = new int[m_NumClusters][3];
        int ptr[] = new int[m_NumClusters];
        for(int i = 0; i < clusters.length; i++) {
            stats[clusters[i]-1][ptr[clusters[i]-1]++] = (int) m_Instances.get(i).classValue() + 1;
        }
        
        for(int i = 0 ; i < m_NumClusters ; i++) {
            System.out.print("Cluster "+(i+1)+ " : ");
            for(int j = 0 ; j < ptr[i]; j++) {
                System.out.print(stats[i][j]+ " ");
                st[i][stats[i][j]-1] ++;
            } 
            System.out.println();
        }
        
        for(int i = 0; i <= st.length ; i++) {
            for(int j = 0; j <= st[0].length; j++) {
                if(i == 0) {
                    if(j == 0) {
                        System.out.print("\t\t");
                    } else {
                        System.out.print("Class"+j+"\t");
                    }
                } else {
                    if(j == 0) {
                        System.out.print("Cluster"+i+"\t");
                    } else {
                        System.out.print(st[i-1][j-1]+"\t");
                    }
                }
            }
            System.out.println();
        }
    }
    
}

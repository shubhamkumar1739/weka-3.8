/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package shubham.develop;

import weka.core.Instances;
import weka.filters.Filter;
import weka.filters.SupervisedFilter;

/**
 *
 * @author Shubham Kumar
 */
public class FilterUtil {
    
    /**
     * Apply filters on the instances
     * @params: 
     * filter: the filter instance
     * classIndex: the index of the Attribute which is the target for classification
     * inst: The instances to which filters are to be applied
     * @return: instances after applying the filter
     */
    public static Instances applyFilter(Filter filter,int classIndex,Instances inst)throws Exception {
        if(filter == null) {
            return inst;
        }
        if ((classIndex < 0) && (filter instanceof SupervisedFilter)) {
            throw new IllegalArgumentException("Class (colour) needs to "
            + "be set for supervised " + "filter.");
        }
        Instances copy = new Instances(inst);
        copy.setClassIndex(classIndex);
        
        Filter filterCopy = Filter.makeCopy(filter);
        filterCopy.setInputFormat(copy);
        Instances newInstances = Filter.useFilter(copy, filterCopy);
        
        if (newInstances == null || newInstances.numAttributes() < 1) {
            throw new Exception("Dataset is empty.");
        }
        return newInstances;
    }
}

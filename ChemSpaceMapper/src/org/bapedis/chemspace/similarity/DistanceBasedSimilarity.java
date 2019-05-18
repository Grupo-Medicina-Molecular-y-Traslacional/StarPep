/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.similarity;

import java.util.LinkedList;
import java.util.List;
import org.bapedis.chemspace.clustering.impl.KMeans;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author Loge
 */
public class DistanceBasedSimilarity extends NormalizableFunction{

    private double maxDistance;
    private int distanceFunction;
    private final List<AlgorithmProperty> properties;
    
    public DistanceBasedSimilarity(AlgorithmFactory factory) {
        super(factory);
        properties = new LinkedList<>();
        distanceFunction = 2;
        populateProperties();
    }
    
    private void populateProperties() {
        try {
            properties.add(AlgorithmProperty.createProperty(this, Integer.class, NbBundle.getMessage(DistanceBasedSimilarity.class, "DistanceBasedSimilarity.distanceFunction.name"), PRO_CATEGORY, NbBundle.getMessage(DistanceBasedSimilarity.class, "DistanceBasedSimilarity.distanceFunction.desc"), "getDistanceFunction", "setDistanceFunction"));
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }
    }  
    
    public int getDistanceFunction() {
        return distanceFunction;
    }

    public void setDistanceFunction(Integer distanceFunction) {
        this.distanceFunction = distanceFunction;
    }        

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        super.initAlgo(workspace, progressTicket); //To change body of generated methods, choose Tools | Templates.       
        
        double distance;
        for(int i=0; i<peptides.length-1; i++){
            for(int j=i+1; j<peptides.length; j++){
                try {
                    distance = calculateDistance(peptides[i], peptides[j]);
                    if ( distance > maxDistance ){
                        maxDistance = distance;
                    }
                } catch (MolecularDescriptorNotFoundException ex) {
                    Exceptions.printStackTrace(ex);
                }
            }
        }
    }
    
    @Override
    public AlgorithmProperty[] getProperties() {
        return properties.toArray(new AlgorithmProperty[0]);
    }    
    
    private double calculateDistance(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException{
        switch(distanceFunction){
            case 1: return manhattan(peptide1, peptide2);
            case 2: return euclidean(peptide1, peptide2);
            case 3: return chebyshev(peptide1, peptide2);
        }
        throw new IllegalArgumentException(NbBundle.getMessage(DistanceBasedSimilarity.class, "DistanceBasedSimilarity.distanceFunction.error"));
    }

    @Override
    protected float computeSimilarity(Peptide peptide1, Peptide peptide2) throws Exception {
        double similarity = 1 - calculateDistance(peptide1, peptide2)/maxDistance;
        return (float)similarity;
    }
    
    private double euclidean(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException{
        double val1, val2, d;
        double sum = 0;
        for (MolecularDescriptor descriptor : features) {
            val1 = normalizedValue(peptide1, descriptor);
            val2 = normalizedValue(peptide2, descriptor);
            d = val1-val2;
            sum += d*d;
        }
        return Math.sqrt(sum);        
    }  
    
    private double manhattan(Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException{
        double val1, val2;
        double sum = 0;
        for (MolecularDescriptor descriptor : features) {
            val1 = normalizedValue(peptide1, descriptor);
            val2 = normalizedValue(peptide2, descriptor);
            sum += Math.abs(val1-val2);
        }
        return sum;        
    }   
    
    private double chebyshev (Peptide peptide1, Peptide peptide2) throws MolecularDescriptorNotFoundException{
        double val1, val2, d;
        double dist = 0;
        for (MolecularDescriptor descriptor : features) {
            val1 = normalizedValue(peptide1, descriptor);
            val2 = normalizedValue(peptide2, descriptor);
            d = Math.abs(val1-val2);
            if (dist < d){
                dist = d;
            }
        }
        return dist;        
    }    
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import org.bapedis.chemspace.model.TwoDSpace;
import org.bapedis.core.io.impl.MyArffWritable;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.util.ArffWriter;
import org.openide.util.Exceptions;
import weka.attributeSelection.PrincipalComponents;
import weka.core.Instance;
import weka.core.Instances;
import org.bapedis.chemspace.spi.TwoDTransformer;

/**
 *
 * @author loge
 */
public class WekaPCATransformer implements TwoDTransformer {    
    private final WekaPCATransformerFactory factory;
    private final PrincipalComponents pca;
    private double varianceCovered;

    public WekaPCATransformer(WekaPCATransformerFactory factory) {
        this.factory = factory;
        pca = new PrincipalComponents();
        varianceCovered = 0.8;
    }   

    @Override
    public WekaPCATransformerFactory getFactory() {
        return factory;
    }

    @Override
    public TwoDSpace transform(Peptide[] peptides, MolecularDescriptor[] features) {
        try {
            ArffWriter.DEBUG = true;
            MyArffWritable writable = new MyArffWritable(peptides, features);
            writable.setOutputOption(MyArffWritable.OUTPUT_OPTION.Z_SCORE);
            File f = ArffWriter.writeToArffFile(writable);
            BufferedReader reader = new BufferedReader(new FileReader(f));
            Instances data = new Instances(reader);
            pca.setCenterData(true);
            pca.setVarianceCovered(varianceCovered);
            pca.buildEvaluator(data);
            Instances resultData = pca.transformedData(data);            

            String[] axisLabels = new String[resultData.numAttributes()];
            for(int i=0; i< axisLabels.length; i++){
                axisLabels[i] = "PCA" + (i+1);
                System.out.println(pca.getEigenValues()[i]);
            }
            
            float[][] coordinates = new float[peptides.length][resultData.numAttributes()];
            Instance in;
            for (int i = 0; i < resultData.numInstances(); i++) {
                in = resultData.instance(i);
                for(int j=0; j<resultData.numAttributes(); j++){
                    coordinates[i][j] = (float)in.value(j);
                }
            }
            return new TwoDSpace(peptides, axisLabels, coordinates);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return null;
    }

    public double getVarianceCovered() {
        return varianceCovered;
    }

    public void setVarianceCovered(double varianceCovered) {
        this.varianceCovered = varianceCovered;
    }
    
}

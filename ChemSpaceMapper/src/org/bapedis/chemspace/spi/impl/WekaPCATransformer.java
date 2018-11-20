/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
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
import org.bapedis.core.io.OUTPUT_OPTION;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.openide.util.Lookup;
import weka.core.Utils;

/**
 *
 * @author loge
 */
public class WekaPCATransformer implements TwoDTransformer {

    private static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final WekaPCATransformerFactory factory;
    private final PrincipalComponents pca;
    private double varianceCovered;
    protected DecimalFormat df;

    public WekaPCATransformer(WekaPCATransformerFactory factory) {
        this.factory = factory;
        pca = new PrincipalComponents();
        varianceCovered = 0.8;
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        df = new DecimalFormat("0.00", symbols);
    }

    @Override
    public WekaPCATransformerFactory getFactory() {
        return factory;
    }

    @Override
    public TwoDSpace transform(Workspace workspace, Peptide[] peptides, MolecularDescriptor[] features) {
        try {
            ArffWriter.DEBUG = true;
            MyArffWritable writable = new MyArffWritable(peptides, features);
            writable.setOutputOption(OUTPUT_OPTION.MIN_MAX);
            File f = ArffWriter.writeToArffFile(writable);
            BufferedReader reader = new BufferedReader(new FileReader(f));
            Instances data = new Instances(reader);
            pca.setCenterData(true);
            pca.setVarianceCovered(varianceCovered);
            pca.buildEvaluator(data);
            Instances resultData = pca.transformedData(data);

            //The Kaiser criterion. We can retain only factors with eigenvalues greater than 1
            double[] eigenValues = pca.getEigenValues();
            double sumOfEigenValues = Utils.sum(eigenValues);

            String[] axisLabels = new String[resultData.numAttributes()];

            int index = eigenValues.length - 1;
            double varExp;
            double cumulativeEigen = 0;
            double cumulativeVar = 0;
            pc.reportMsg("Sum of eigenvalues: " + sumOfEigenValues, workspace);
            pc.reportMsg("Factor, Eigenvalue, Explained variance, Cumulative eigenvalue, Cumulative variance", workspace);
            for (int i = 0; i < axisLabels.length; i++) {
                axisLabels[i] = "PCA" + (i + 1);
                varExp = (eigenValues[index] / sumOfEigenValues) * 100;
                cumulativeEigen += eigenValues[index];
                cumulativeVar += varExp;
                pc.reportMsg((i + 1) + ", " + df.format(eigenValues[index]) + ", " + df.format(varExp) + "%"
                             + ", " + df.format(cumulativeEigen) + ", " + df.format(cumulativeVar) + "%", workspace);
                index--;
            }

            float[][] coordinates = new float[peptides.length][axisLabels.length];
            Instance in;
            for (int i = 0; i < resultData.numInstances(); i++) {
                in = resultData.instance(i);
                for (int j = 0; j < axisLabels.length; j++) {
                    coordinates[i][j] = (float) in.value(j);
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

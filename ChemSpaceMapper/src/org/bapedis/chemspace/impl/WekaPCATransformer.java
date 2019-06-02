/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.LinkedList;
import java.util.List;
import org.bapedis.chemspace.model.CoordinateSpace;
import org.bapedis.core.io.impl.MyArffWritable;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.util.ArffWriter;
import org.openide.util.Exceptions;
import weka.attributeSelection.PrincipalComponents;
import weka.core.Instance;
import weka.core.Instances;
import org.bapedis.core.io.MD_OUTPUT_OPTION;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.ProgressTicket;
import org.openide.util.Lookup;
import weka.core.Utils;

/**
 *
 * @author loge
 */
public class WekaPCATransformer implements Algorithm {

    private static ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private final WekaPCATransformerFactory factory;
    private final PrincipalComponents pca;
    protected MD_OUTPUT_OPTION option;
    private final double varianceCovered;
    protected DecimalFormat df;
    protected Peptide[] peptides;
    protected MolecularDescriptor[] features;
    protected Workspace workspace;
    protected CoordinateSpace xyzSpace;
    protected boolean stopRun;

    public WekaPCATransformer(WekaPCATransformerFactory factory) {
        this.factory = factory;
        pca = new PrincipalComponents();
        varianceCovered = 0.7;
        DecimalFormatSymbols symbols = DecimalFormatSymbols.getInstance();
        symbols.setDecimalSeparator('.');
        df = new DecimalFormat("0.00", symbols);
        option = MD_OUTPUT_OPTION.Z_SCORE;
    }

    public double getVarianceCovered() {
        return varianceCovered;
    }

    public CoordinateSpace getXYZSpace() {
        return xyzSpace;
    }        

    public MD_OUTPUT_OPTION getOption() {
        return option;
    }

    public void setOption(MD_OUTPUT_OPTION option) {
        this.option = option;
    }        

    @Override
    public void initAlgo(Workspace workspace, ProgressTicket progressTicket) {
        this.workspace = workspace;
        AttributesModel attrModel = pc.getAttributesModel(workspace);
        if (attrModel != null) {
            peptides = attrModel.getPeptides().toArray(new Peptide[0]);
            //Load features
            List<MolecularDescriptor> allFeatures = new LinkedList<>();
            for (String key : attrModel.getMolecularDescriptorKeys()) {
                for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                    allFeatures.add(attr);
                }
            }
            features = allFeatures.toArray(new MolecularDescriptor[0]);
        }
        stopRun = false;
    }

    @Override
    public void endAlgo() {
        workspace = null;
        features = null;
        peptides = null;
    }

    @Override
    public boolean cancel() {
        stopRun = true;
        return false;
    }

    @Override
    public AlgorithmProperty[] getProperties() {
        return null;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }

    @Override
    public void run() {
        try {
            MyArffWritable writable = new MyArffWritable(peptides, features, option);
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

            //Axis labels
            String[] axisLabels = new String[resultData.numAttributes()];
            double[] explainedVar = new double[axisLabels.length];
            int index = eigenValues.length - 1;
            double varExp;
            double cumulativeEigen = 0;
            pc.reportMsg("Factor, Eigenvalue, Cumulative eigenvalue, ", workspace);
            for (int i = 0; i < axisLabels.length; i++) {
                varExp = (eigenValues[index] / sumOfEigenValues) * 100;
                explainedVar[i] = varExp;
                cumulativeEigen += eigenValues[index];
                axisLabels[i] = String.format("PCA %d (%s%% explained var.)", (i + 1), df.format(varExp));
                pc.reportMsg(String.format("%s, %s, %s", String.valueOf(i + 1), df.format(eigenValues[index]), df.format(cumulativeEigen) ), workspace);
                index--;
            }
            
            //Coordinates
            float[][] coordinates = new float[peptides.length][axisLabels.length];
            Instance in;
            for (int i = 0; i < resultData.numInstances(); i++) {
                in = resultData.instance(i);
                for (int j = 0; j < axisLabels.length; j++) {
                    coordinates[i][j] = (float) in.value(j);
                }
            }
            
            xyzSpace = new CoordinateSpace(peptides, axisLabels, coordinates, explainedVar);
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }

    }
}

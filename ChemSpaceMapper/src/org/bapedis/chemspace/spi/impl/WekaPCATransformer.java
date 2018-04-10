/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.spi.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import org.bapedis.chemspace.model.Position;
import org.bapedis.chemspace.spi.ThreeDTransformer;
import org.bapedis.core.io.impl.MyArffWritable;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.util.ArffWriter;
import org.openide.util.Exceptions;
import weka.attributeSelection.PrincipalComponents;
import weka.core.Instance;
import weka.core.Instances;

/**
 *
 * @author loge
 */
public class WekaPCATransformer implements ThreeDTransformer {

    PrincipalComponents pca = new PrincipalComponents();

    @Override
    public Position[] transform(Peptide[] peptides, MolecularDescriptor[] features) {
        Position[] positions = null;
        try {
            ArffWriter.DEBUG = true;
            MyArffWritable writable = new MyArffWritable(peptides, features);
            writable.setOutputOption(MyArffWritable.OUTPUT_OPTION.Z_SCORE);
            File f = ArffWriter.writeToArffFile(writable);
            BufferedReader reader = new BufferedReader(new FileReader(f));
            Instances data = new Instances(reader);
            pca.setCenterData(true);
            pca.buildEvaluator(data);
            Instances resultData = pca.transformedData(data);
            
            positions = new Position[peptides.length];
            Instance in;
            for (int i = 0; i < resultData.numInstances(); i++) {
                in = resultData.instance(i);
                float x = (float) in.value(0);
                float y = 0;
                if (resultData.numAttributes() > 1) {
                    y = (float) in.value(1);
                }
                float z = 0;
                if (resultData.numAttributes() > 2) {
                    z = (float) in.value(2);
                }
                positions[i] = new Position(x, y, z);
            }

        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
        return positions;
    }

}

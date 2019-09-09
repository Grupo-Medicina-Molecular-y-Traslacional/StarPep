/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.component;

import java.util.List;
import org.bapedis.chemspace.model.CoordinateSpace;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.util.ArffWritable;

/**
 *
 * @author loge
 */
public class SpaceArffWritable implements ArffWritable {
    
    private final CoordinateSpace xyzSpace;
    private final int xAxis, yAxis, zAxis;

    public SpaceArffWritable(CoordinateSpace xyzSpace) {
        this.xyzSpace = xyzSpace;
        float[][] coordinates = xyzSpace.getCoordinates();
        xAxis = 0;
        yAxis = coordinates[0].length > 1 ? 1 : xAxis;
        zAxis = coordinates[0].length > 2 ? 2 : yAxis;               
    }

    @Override
    public List<String> getAdditionalInfo() {
        return null;
    }

    @Override
    public String getRelationName() {
        return "AMP - Dataset";
    }

    @Override
    public int getNumAttributes() {
        return 5;
    }

    @Override
    public String getAttributeName(int attribute) {
        switch(attribute){
            case 0: return "ID";
            case 1: return "PCA 1";
            case 2: return "PCA 2";
            case 3: return "PCA 3";
            case 4: return "Color"; 
        }
        return null;
    }

    @Override
    public String[] getAttributeDomain(int attribute) {
        return null;
    }

    @Override
    public int getNumInstances() {
        return xyzSpace.getPeptides().length;
    }

    @Override
    public String getAttributeValue(int instance, int attribute) throws Exception {
        Peptide peptide = xyzSpace.getPeptides()[instance];
        switch (attribute) {
            case 0:
                return Integer.toString(peptide.getId());            
            case 1:
                 return Float.toString(xyzSpace.getCoordinates()[instance][xAxis]);
            case 2:
                return Float.toString(xyzSpace.getCoordinates()[instance][yAxis]);
            case 3:
                return Float.toString(xyzSpace.getCoordinates()[instance][zAxis]);
            case 4:
                return peptide.getAttributeValue(PeptideAttribute.CLUSTER_ATTR).toString();
        }
        return "";
    }

    @Override
    public double getAttributeValueAsDouble(int instance, int attribute) throws Exception {
        return Double.valueOf(getAttributeValue(instance, attribute));
    }

    @Override
    public boolean isSparse() {
        return false;
    }

    @Override
    public String getMissingValue(int attribute) {
        return "?";
    }

}

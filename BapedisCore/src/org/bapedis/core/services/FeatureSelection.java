/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.services;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FeatureSelectionModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.Peptide;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class FeatureSelection {

    private final ProjectManager pc;

    public FeatureSelection() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }

    public void doSelection(FeatureSelectionModel fsModel) {
        AttributesModel attrModel = pc.getAttributesModel();
        List<MolecularDescriptor> allFeatures = new LinkedList<>();
        HashMap<String, MolecularDescriptor[]> mdMap = attrModel.getAllMolecularDescriptors();
        for (Map.Entry<String, MolecularDescriptor[]> entry : mdMap.entrySet()) {
            for (MolecularDescriptor attr : entry.getValue()) {
                allFeatures.add(attr);
            }
        }
        Peptide[] peptides = attrModel.getPeptides();
//        for()
    }

}

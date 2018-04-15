/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.MolecularDescriptorTag;
import org.bapedis.core.spi.alg.ToolMenuItem;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author beltran, loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 0)
public class AllDescriptorsFactory implements AlgorithmFactory, MolecularDescriptorTag, ToolMenuItem {

    private AllDescriptorsPanel panel;

    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(AllDescriptorsFactory.class, "AllDescriptors.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(AllDescriptorsFactory.class, "AllDescriptors.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        if (panel == null) {
            panel = new AllDescriptorsPanel();
        }
        return panel;
    }

    @Override
    public Algorithm createAlgorithm() {
        AllDescriptors algo = new AllDescriptors(this);
        return algo;
    }

    @Override
    public boolean addSeparatorBefore() {
        return false;
    }

    @Override
    public boolean addSeparatorAfter() {
        return true;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof AllDescriptorsFactory) {
            return getName().equals(((AllDescriptorsFactory) obj).getName());
        }
        return false;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.algo.impl;

import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.AlgorithmSetupUI;
import org.bapedis.core.spi.algo.ToolMenuItem;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author beltran, loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 0)
public class AllDescriptorsFactory implements AlgorithmFactory, ToolMenuItem {

    private AllDescriptorsPanel panel;

    @Override
    public AlgorithmCategory getCategory() {
        return AlgorithmCategory.MolecularDescriptor;
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
    public int getQualityRank() {
        return -1;
    }

    @Override
    public int getSpeedRank() {
        return -1;
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

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.graphmining.clustering;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.ClusteringTag;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
//@ServiceProvider(service = AlgorithmFactory.class, position = 50)
public class CommunityStructureFactory implements ClusteringTag{

    CommunityStructureSetupUI setupUI = new CommunityStructureSetupUI();
    
    @Override
    public String getCategory() {
        return NbBundle.getMessage(CommunityStructureFactory.class, "CommunityStructureFactory.category");
    }

    @Override
    public String getName() {
         return NbBundle.getMessage(CommunityStructureFactory.class, "CommunityStructureFactory.name");
    }

    @Override
    public String getDescription() {
         return NbBundle.getMessage(CommunityStructureFactory.class, "CommunityStructureFactory.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new CommunityStructure(this);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.clustering.impl;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.bapedis.core.spi.alg.ClusteringTag;
import org.bapedis.core.util.BinaryLocator;
import org.bapedis.core.util.RUtil;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author loge
 */
//@ServiceProvider(service = AlgorithmFactory.class, position = 50)
public class HierarchicalRClustererFactory implements ClusteringTag {
    
    public HierarchicalRClustererFactory() {
        if (!RUtil.RSCRIPT_BINARY.isFound()) {
            BinaryLocator.locate(RUtil.RSCRIPT_BINARY);            
        }
        
        if (!RUtil.RSCRIPT_BINARY.isFound()){
            String lastPath = NbPreferences.forModule(RClusterer.class).get(RClusterer.RSCRIPT_PATH, null);
            RUtil.RSCRIPT_BINARY.setLocation(lastPath);
        }
    }

    @Override
    public String getCategory() {
        return NbBundle.getMessage(HierarchicalRClustererFactory.class, "HierarchicalRClustererFactory.category");
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(HierarchicalRClustererFactory.class, "HierarchicalRClustererFactory.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(HierarchicalRClustererFactory.class, "HierarchicalRClustererFactory.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return null;
    }

    @Override
    public Algorithm createAlgorithm() {
        HierarchicalRClusterer rclusterer = new HierarchicalRClusterer(this);
        rclusterer.populateProperties();
        
        return rclusterer;
    }

}

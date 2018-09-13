/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.appearance.plugin;

import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.gephi.desktop.appearance.AppearanceSetupUIPanel;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.bapedis.core.spi.alg.NetworkTag;

/**
 *
 * @author loge
 */
@ServiceProvider(service = AlgorithmFactory.class, position = 10)
public class AppearanceAlgorithmFactory implements AlgorithmFactory, NetworkTag{

    private final AppearanceSetupUIPanel setupUI = new AppearanceSetupUIPanel();
    
    @Override
    public String getCategory() {
        return null;
    }

    @Override
    public String getName() {
        return NbBundle.getMessage(AppearanceAlgorithm.class, "AppearanceAlgorithm.name");
    }

    @Override
    public String getDescription() {
        return NbBundle.getMessage(AppearanceAlgorithm.class, "AppearanceAlgorithm.desc");
    }

    @Override
    public AlgorithmSetupUI getSetupUI() {
        return setupUI;
    }

    @Override
    public Algorithm createAlgorithm() {
        return new AppearanceAlgorithm(this);
    }
    
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;
import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class MapperAlgorithmSetupUI implements AlgorithmSetupUI {
    private final BaseChemSpacePanel networkPanel;
    private final BaseChemSpacePanel twoDPanel;
    private final MapperAlgorithmPanel setupPanel;

    public MapperAlgorithmSetupUI() {
        setupPanel = new MapperAlgorithmPanel();
        twoDPanel = createNetworkPanel();
        networkPanel = createTwoDPanel();
    }
    
    private BaseChemSpacePanel createNetworkPanel(){
        List<? extends AlgorithmFactory> factories = new ArrayList<>(Lookup.getDefault().lookupAll(AlgorithmFactory.class));
        for (Iterator<? extends AlgorithmFactory> it = factories.iterator(); it.hasNext();) {
            AlgorithmFactory f = it.next();
            if (f.getCategory() != AlgorithmCategory.ChemSpaceNetwork) {
                it.remove();
            }
        }        
        return new NetworkPanel(factories);
    }
    
    private BaseChemSpacePanel createTwoDPanel(){
        List<? extends AlgorithmFactory> factories = new ArrayList<>(Lookup.getDefault().lookupAll(AlgorithmFactory.class));
        for (Iterator<? extends AlgorithmFactory> it = factories.iterator(); it.hasNext();) {
            AlgorithmFactory f = it.next();
            if (f.getCategory() != AlgorithmCategory.NDChemSpace) {
                it.remove();
            }
        }
        
        return new TwoDPanel(factories);
    }
    
    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        MapperAlgorithm csMapper = (MapperAlgorithm)algo;
        setupPanel.setCheSMapperAlg(csMapper);
        switch(csMapper.getChemSpaceOption()){
            case N_DIMENSIONAL:
                setupPanel.addChemSpacePanel(twoDPanel);
                break;
            case FULL_NETWORK:
            case COMPRESSED_NETWORK:
                setupPanel.addChemSpacePanel(networkPanel);
        }
        return setupPanel;
    }
    
}

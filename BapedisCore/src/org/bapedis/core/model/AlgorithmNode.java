/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.util.HashMap;
import java.util.Map;
import org.bapedis.core.spi.alg.Algorithm;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;

/**
 *
 * @author loge
 */
public class AlgorithmNode extends AbstractNode {

    private Algorithm algorithm;
    private PropertySet[] propertySets;

    public AlgorithmNode(Algorithm algorithm) {
        super(Children.LEAF);
        this.algorithm = algorithm;
        setName(algorithm.getFactory().getName());
        setShortDescription(algorithm.getFactory().getDescription());
    }
        

    @Override
    public PropertySet[] getPropertySets() {
        if (propertySets == null) {
            try {
                Map<String, Sheet.Set> sheetMap = new HashMap<>();
                if (algorithm.getProperties() != null) {
                    for (AlgorithmProperty algoProperty : algorithm.getProperties()) {
                        Sheet.Set set = sheetMap.get(algoProperty.getCategory());
                        if (set == null) {
                            set = Sheet.createPropertiesSet();
                            set.setDisplayName(algoProperty.getCategory());
                            sheetMap.put(algoProperty.getCategory(), set);
                        }
                        set.put(algoProperty.getProperty());
                    }
                }
                propertySets = sheetMap.values().toArray(new PropertySet[0]);
            } catch (Exception ex) {
                Exceptions.printStackTrace(ex);
                return null;
            }
        }
        return propertySets;
    }

    public Algorithm getAlgorithm() {
        return algorithm;
    }

}

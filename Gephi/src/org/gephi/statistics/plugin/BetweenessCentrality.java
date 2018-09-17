/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.statistics.plugin;

import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.gephi.graph.api.GraphModel;
import org.gephi.graph.api.Table;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class BetweenessCentrality extends AbstractCentrality{
    public static final String BETWEENNESS = "betweenesscentrality";
    private final BetweenessCentralityFactory factory;
    private AlgorithmProperty[] property;  
    private boolean normalized;

    public BetweenessCentrality(BetweenessCentralityFactory factory) {
        this.factory = factory;
        
        try {
            String CATEGORY = "Properties";
            property = new AlgorithmProperty[]{AlgorithmProperty.createProperty(this, Boolean.class, NbBundle.getMessage(BetweenessCentrality.class, "Property.normalize.name"), CATEGORY, NbBundle.getMessage(BetweenessCentrality.class, "Property.normalize.desc"), "isNormalized", "setNormalized")};
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
            property = null;
        }        
    }   

    public boolean isNormalized() {
        return normalized;
    }

    public void setNormalized(boolean normalized) {
        this.normalized = normalized;
    }        

    @Override
    protected void addCentralityColumn(GraphModel graphModel) {
        Table nodeTable = graphModel.getNodeTable();
        nodeTable.addColumn(BETWEENNESS, "Betweenness Centrality", Double.class, new Double(0));
    }


    @Override
    public AlgorithmProperty[] getProperties() {
        return property;
    }

    @Override
    public AlgorithmFactory getFactory() {
        return factory;
    }
    
}

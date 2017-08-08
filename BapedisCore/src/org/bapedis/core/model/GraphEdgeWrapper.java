/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import org.gephi.graph.api.Edge;
import org.openide.nodes.Sheet;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class GraphEdgeWrapper extends GraphElementNode {

    public GraphEdgeWrapper(Edge edge) {
        super(edge);
    }

    @Override
    public String getDisplayName() {
        return element.getLabel();
    }

    @Override
    protected Sheet createSheet() {

        Edge edge = (Edge) element;
        Sheet sheet = Sheet.createDefault();
        Property property;

        // Primary set
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);
        set.setName("primary");
        set.setDisplayName(NbBundle.getMessage(GraphNodeWrapper.class, "PropertySet.primary"));

        // Label property
        property = createReadOnlyPropertyField("label", NbBundle.getMessage(GraphElementNode.class, "PropertySet.label"),
                NbBundle.getMessage(GraphElementNode.class, "PropertySet.label.desc"), String.class, element.getLabel());
        set.put(property);

        //Source propery
        property = createReadOnlyPropertyField("source", NbBundle.getMessage(GraphElementNode.class, "PropertySet.edge.source"),
                NbBundle.getMessage(GraphElementNode.class, "PropertySet.edge.source.desc"), String.class, edge.getSource().getAttribute("name"));
        set.put(property);

        //Target propery
        property = createReadOnlyPropertyField("target", NbBundle.getMessage(GraphElementNode.class, "PropertySet.edge.target"),
                NbBundle.getMessage(GraphElementNode.class, "PropertySet.edge.target.desc"), String.class, edge.getTarget().getAttribute("name"));
        set.put(property);

        // Graph set
        set = Sheet.createPropertiesSet();
        sheet.put(set);
        set.setName("graph");
        set.setDisplayName(NbBundle.getMessage(GraphEdgeWrapper.class, "PropertySet.graph"));
        
        try {
            //Color:                
            property = createColorPropertyField();
            set.put(property);            

            //Label size:
            property = createLabelSizePropertyField();
            set.put(property);

            //Label color:
            property = createLabelColorPropertyField();
            set.put(property);

            //Label visible:
            property = createLabelVisiblePropertyField();
            set.put(property);                        
        } catch (NoSuchMethodException ex) {
            Exceptions.printStackTrace(ex);
        }

        return sheet;
    }

}

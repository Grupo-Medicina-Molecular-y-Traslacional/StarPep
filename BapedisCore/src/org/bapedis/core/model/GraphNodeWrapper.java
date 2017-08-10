/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.awt.Color;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.TextProperties;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 *
 * @author Home
 */
public class GraphNodeWrapper extends GraphElementNode {


    public GraphNodeWrapper(Node node) {
        super(node);
    }

    @Override
    public String getDisplayName() {
        return (String) element.getAttribute("name");
    }        

    
    @Override
    protected Sheet createSheet() {
        Node node = (Node) element;
        Sheet sheet = Sheet.createDefault();
        Property property;
        String name, descName;
        
        // Primary set       
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);
        set.setName("primary");
        set.setDisplayName(NbBundle.getMessage(GraphNodeWrapper.class, "PropertySet.primary"));
        
        // Name property
        name = NbBundle.getMessage(GraphNodeWrapper.class, "PropertySet.name");
        descName = NbBundle.getMessage(GraphNodeWrapper.class, "PropertySet.name.desc");
        final String nameValue = (String)node.getAttribute("name");
        property = createReadOnlyPropertyField("name", name, descName, String.class, nameValue);
        set.put(property);
        
        // Label property
        name = NbBundle.getMessage(GraphNodeWrapper.class, "PropertySet.label");
        descName = NbBundle.getMessage(GraphNodeWrapper.class, "PropertySet.label.desc");
        final String labelValue = node.getLabel();
        property = createReadOnlyPropertyField("label", name, descName, String.class, labelValue);
        set.put(property);
                
        // Graph set
        set = Sheet.createPropertiesSet();
        sheet.put(set);
        set.setName("graph");
        set.setDisplayName(NbBundle.getMessage(GraphNodeWrapper.class, "PropertySet.graph"));

        try {
            //Size:
            property = new PropertySupport.Reflection(node, Float.TYPE, "size", "setSize");
            property.setDisplayName(NbBundle.getMessage(GraphNodeWrapper.class, "PropertySet.size.text"));
            property.setName("size");
            set.put(property);            

            //All position coordinates:
            set.put(buildGeneralPositionProperty(node, "x"));
            set.put(buildGeneralPositionProperty(node, "y"));
            set.put(buildGeneralPositionProperty(node, "z"));

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
        } catch (NoSuchMethodException exception) {
            exception.printStackTrace();
        }        

        return sheet;
    }

    /**
     * Used to build property for each position coordinate (x,y,z) in the same
     * way.
     *
     * @return Property for that coordinate
     */
    private Property buildGeneralPositionProperty(Node node, String coordinate) throws NoSuchMethodException {
        //Position:
        Property p = new PropertySupport.Reflection(node, Float.TYPE, coordinate, "set" + coordinate.toUpperCase());
        p.setDisplayName(NbBundle.getMessage(GraphNodeWrapper.class, "PropertySet.position.text", coordinate));
        p.setName(coordinate);
        return p;
    }    
}
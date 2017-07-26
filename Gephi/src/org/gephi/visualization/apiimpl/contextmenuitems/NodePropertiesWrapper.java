/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.visualization.apiimpl.contextmenuitems;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.TextProperties;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 *
 * @author Home
 */
public class NodePropertiesWrapper extends AbstractNode {

    private final Node node;

    public NodePropertiesWrapper(Node node) {
        super(Children.LEAF);
        this.node = node;
    }

    public Color getNodeColor() {
        return new Color(node.r(), node.g(), node.b(), node.alpha());
    }

    public void setNodeColor(Color c) {
        if (c != null) {
            node.setR(c.getRed() / 255f);
            node.setG(c.getGreen() / 255f);
            node.setB(c.getBlue() / 255f);
            node.setAlpha(c.getAlpha() / 255f);
        }
    }

    public Color getLabelColor() {
        TextProperties textProps = node.getTextProperties();
        if (textProps.getAlpha() == 0) {
            return null;//Not specific color for label
        }

        return textProps.getColor();
    }

    public void setLabelColor(Color c) {
        if (c != null) {
            TextProperties textProps = node.getTextProperties();
            textProps.setColor(c);
        }
    }
    
    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Property property;
        // Primary        
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName("primary");
        set.setDisplayName(NbBundle.getMessage(NodePropertiesWrapper.class, "PropertySet.primary"));
        // Name property
        String name = NbBundle.getMessage(NodePropertiesWrapper.class, "PropertySet.name");
        String displayName = NbBundle.getMessage(NodePropertiesWrapper.class, "PropertySet.name.desc");
        property = new PropertySupport.ReadOnly("name", String.class, name, displayName) {

            @Override
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                return node.getAttribute("name");
            }
        };
        set.put(property);
        // Label property
        name = NbBundle.getMessage(NodePropertiesWrapper.class, "PropertySet.label");
        displayName = NbBundle.getMessage(NodePropertiesWrapper.class, "PropertySet.label.desc");
        property = new PropertySupport.ReadOnly("label", String.class, name, displayName) {

            @Override
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                return node.getLabel();
            }
        };
        set.put(property);
        sheet.put(set);
        
        // Graph
        set = Sheet.createPropertiesSet();
        set.setName("graph");
        set.setDisplayName(NbBundle.getMessage(NodePropertiesWrapper.class, "PropertySet.graph"));

        try {
            //Size:
            property = new PropertySupport.Reflection(node, Float.TYPE, "size", "setSize");
            property.setDisplayName(NbBundle.getMessage(NodePropertiesWrapper.class, "PropertySet.size.text"));
            property.setName("size");
            set.put(property);

            //All position coordinates:
            set.put(buildGeneralPositionProperty(node, "x"));
            set.put(buildGeneralPositionProperty(node, "y"));
            set.put(buildGeneralPositionProperty(node, "z"));

            //Color:                
            property = new PropertySupport.Reflection(this, Color.class, "getNodeColor", "setNodeColor");
            property.setDisplayName(NbBundle.getMessage(NodePropertiesWrapper.class, "PropertySet.color.text"));
            property.setName("color");
            set.put(property);

            TextProperties textProperties = node.getTextProperties();

            //Label size:
            property = new PropertySupport.Reflection(textProperties, Float.TYPE, "getSize", "setSize");
            property.setDisplayName(NbBundle.getMessage(NodePropertiesWrapper.class, "PropertySet.label.size.text"));
            property.setName("labelsize");
            set.put(property);

            //Label color:
            property = new PropertySupport.Reflection(this, Color.class, "getLabelColor", "setLabelColor");
            property.setDisplayName(NbBundle.getMessage(NodePropertiesWrapper.class, "PropertySet.label.color.text"));
            property.setName("labelcolor");
            set.put(property);

            //Label visible:
            property = new PropertySupport.Reflection(textProperties, Boolean.TYPE, "isVisible", "setVisible");
            property.setDisplayName(NbBundle.getMessage(NodePropertiesWrapper.class, "PropertySet.label.visible.text"));
            property.setName("labelvisible");
            set.put(property);
        } catch (NoSuchMethodException exception) {
            exception.printStackTrace();
        }
        sheet.put(set);

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
        p.setDisplayName(NbBundle.getMessage(NodePropertiesWrapper.class, "PropertySet.position.text", coordinate));
        p.setName(coordinate);
        return p;
    }    
}

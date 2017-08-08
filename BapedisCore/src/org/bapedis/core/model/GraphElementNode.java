package org.bapedis.core.model;

import java.awt.Color;
import java.lang.reflect.InvocationTargetException;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.TextProperties;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.util.NbBundle;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author loge
 */
public class GraphElementNode extends AbstractNode {

    protected final Element element;

    public GraphElementNode(Element element) {
        super(Children.LEAF);
        this.element = element;
    }

    public Color getElementColor() {
        return new Color(element.r(), element.g(), element.b(), element.alpha());
    }

    public void setElementColor(Color c) {
        if (c != null) {
            element.setR(c.getRed() / 255f);
            element.setG(c.getGreen() / 255f);
            element.setB(c.getBlue() / 255f);
            element.setAlpha(c.getAlpha() / 255f);
        }
    }

    public Color getLabelColor() {
        TextProperties textProps = element.getTextProperties();
        if (textProps.getAlpha() == 0) {
            return null;//Not specific color for label
        }

        return textProps.getColor();
    }

    public void setLabelColor(Color c) {
        if (c != null) {
            TextProperties textProps = element.getTextProperties();
            textProps.setColor(c);
        }
    }

    public float getLabelSize() {
        TextProperties textProperties = element.getTextProperties();
        return textProperties.getSize();
    }

    public void setLabelSize(float size) {
        TextProperties textProperties = element.getTextProperties();
        textProperties.setSize(size);
    }

    public boolean isLabelVisible() {
        TextProperties textProperties = element.getTextProperties();
        return textProperties.isVisible();
    }

    public void setLabelVisible(boolean visible) {
        TextProperties textProperties = element.getTextProperties();
        textProperties.setVisible(visible);
    }

    protected PropertySupport.ReadOnly createReadOnlyPropertyField(String name, String displayName, String description, Class type, final Object value) {
        PropertySupport.ReadOnly property = new PropertySupport.ReadOnly(name, type, displayName, description) {

            @Override
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                return value;
            }
        };
        String strValue = value.toString();

        // Set the font color for read only property. Default is a gray color.
        property.setValue("htmlDisplayValue", "<font color='000000'>" + strValue + "</font>");
//      property.setValue("suppressCustomEditor", true);
        return property;
    }

    protected PropertySupport.Reflection createColorPropertyField() throws NoSuchMethodException {
        PropertySupport.Reflection property = new PropertySupport.Reflection(this, Color.class, "getElementColor", "setElementColor");
        property.setDisplayName(NbBundle.getMessage(GraphElementNode.class, "PropertySet.color.text"));
        property.setName("color");
        return property;
    }

    protected PropertySupport.Reflection createLabelSizePropertyField() throws NoSuchMethodException {
        PropertySupport.Reflection property = new PropertySupport.Reflection(this, Float.TYPE, "getLabelSize", "setLabelSize");
        property.setDisplayName(NbBundle.getMessage(GraphElementNode.class, "PropertySet.label.size.text"));
        property.setName("labelsize");
        return property;
    }

    protected PropertySupport.Reflection createLabelColorPropertyField() throws NoSuchMethodException {
        PropertySupport.Reflection property = new PropertySupport.Reflection(this, Color.class, "getLabelColor", "setLabelColor");
        property.setDisplayName(NbBundle.getMessage(GraphElementNode.class, "PropertySet.label.color.text"));
        property.setName("labelcolor");
        return property;
    }

    protected PropertySupport.Reflection createLabelVisiblePropertyField() throws NoSuchMethodException {
        PropertySupport.Reflection property = new PropertySupport.Reflection(this, Boolean.TYPE, "isLabelVisible", "setLabelVisible");
        property.setDisplayName(NbBundle.getMessage(GraphElementNode.class, "PropertySet.label.visible.text"));
        property.setName("labelvisible");
        return property;
    }

}

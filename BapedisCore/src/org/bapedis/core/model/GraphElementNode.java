package org.bapedis.core.model;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Element;
import org.gephi.graph.api.Node;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
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

    @Override
    public String getDisplayName() {
        if (element instanceof Edge) {
            return element.getLabel();
        }
        return (String) element.getAttribute("name");
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();

        // Label property
        PropertySupport.ReadOnly labelProperty;
        labelProperty = createPropertyField("label", NbBundle.getMessage(GraphElementNode.class, "PropertySet.node.label"),
                NbBundle.getMessage(GraphElementNode.class, "PropertySet.node.label.desc"), String.class, element.getLabel());

        if (element instanceof Edge) {
            //Source propery
            PropertySupport.ReadOnly sourceProperty = createPropertyField("source", NbBundle.getMessage(GraphElementNode.class, "PropertySet.node.source"),
                    NbBundle.getMessage(GraphElementNode.class, "PropertySet.node.source.desc"), String.class, ((Edge) element).getSource().getAttribute("name"));
            set.put(sourceProperty);

            //Target propery
            PropertySupport.ReadOnly targetProperty = createPropertyField("target", NbBundle.getMessage(GraphElementNode.class, "PropertySet.node.target"),
                    NbBundle.getMessage(GraphElementNode.class, "PropertySet.node.target.desc"), String.class, ((Edge) element).getTarget().getAttribute("name"));
            set.put(targetProperty);
        } else {
            // Label property
            PropertySupport.ReadOnly nameProperty = createPropertyField("name", NbBundle.getMessage(GraphElementNode.class, "PropertySet.node.name"),
                    NbBundle.getMessage(GraphElementNode.class, "PropertySet.node.name.desc"), String.class, element.getAttribute("name"));
            set.put(nameProperty);
        }

        //Label propery
        set.put(labelProperty);

        sheet.put(set);
        
        return sheet;
    }

    private PropertySupport.ReadOnly createPropertyField(String name, String displayName, String description, Class type, final Object value) {
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

}

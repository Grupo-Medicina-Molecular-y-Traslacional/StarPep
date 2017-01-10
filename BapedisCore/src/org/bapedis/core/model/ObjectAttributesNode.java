/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.lookup.Lookups;

/**
 * A node representation for the class ObjectAttributes
 * @author loge
 */
public class ObjectAttributesNode extends AbstractNode {

    protected ObjectAttributes objAttr;

    public ObjectAttributesNode(ObjectAttributes objAttr) {
        this(objAttr, Children.LEAF, Lookups.singleton(objAttr));
    }

    public ObjectAttributesNode(ObjectAttributes objAttr, Children children) {
        this(objAttr, children, Lookups.singleton(objAttr));
    }

    public ObjectAttributesNode(ObjectAttributes objAttr, Children children, Lookup lookup) {
        super(children, lookup);
        this.objAttr = objAttr;
    }

    public ObjectAttributes getObjectAttributes() {
        return objAttr;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = super.createSheet();
        Sheet.Set set = sheet.get(Sheet.PROPERTIES);
        if (set == null) {
            set = Sheet.createPropertiesSet();
            sheet.put(set);
        }
        for (Attribute attr : objAttr.getAttributes()) {
            final Object value = objAttr.getAttributeValue(attr);
            PropertySupport.ReadOnly property = new PropertySupport.ReadOnly(attr.id, attr.getType(), attr.id, value.toString()) {

                @Override
                public Object getValue() throws IllegalAccessException, InvocationTargetException {
                    return value;
                }
            };
            String strValue = value.toString();
            if (value instanceof String[]) {
                strValue = Arrays.toString((String[]) value);
                strValue = strValue.substring(1, strValue.length() - 1);
            }
            // Set the font color for read only property. Default is a gray color.
            property.setValue("htmlDisplayValue", "<font color='000000'>" + strValue + "</font>");
//                property.setValue("suppressCustomEditor", true);
            set.put(property);
        }
        return sheet;
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import javax.swing.Action;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.db.ui.actions.ShowPeptideDetails;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;

/**
 *
 * @author loge
 */
public class NeoPeptideNode extends PeptideNode {

    public NeoPeptideNode(NeoPeptide peptide) {
        super(peptide);
    }

    @Override
    public Action[] getActions(boolean context) {
        Action[] nodeActions = new Action[1];
        nodeActions[0] = new ShowPeptideDetails();
        return nodeActions;
    }

    private PropertySupport.ReadOnly createPropertyField(String name, String displayName, String description, Class type, final Object value) {
        PropertySupport.ReadOnly property = new PropertySupport.ReadOnly(name, type, displayName, description) {

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
//      property.setValue("suppressCustomEditor", true);
        return property;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();

        for (PeptideAttribute attr : peptide.getAttributes()) {
            PropertySupport.ReadOnly property = createPropertyField(attr.getId(), attr.getId(), attr.getId(), attr.getType(), peptide.getAttributeValue(attr));
            set.put(property);
        }
        sheet.put(set);
        // Neighbor set
        set = Sheet.createPropertiesSet();
        set.setName("other name");
        set.setDisplayName("other display name");
        
        for (NeoNeighbor neighbor : ((NeoPeptide) peptide).getNeighbors()) {
            PropertySupport.ReadOnly property = createPropertyField(neighbor.getLabel(), neighbor.getLabel(), neighbor.getLabel(), String.class, neighbor.getName());
            set.put(property);
        }
        sheet.put(set);
        return sheet;
    }

}

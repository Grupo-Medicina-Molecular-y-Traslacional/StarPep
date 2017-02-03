/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.model;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;
import javax.swing.Action;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.db.ui.actions.ShowPeptideDetails;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;

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
        Action[] nodeActions = new Action[]{
            new ShowPeptideDetails(),
            SystemAction.get(PropertiesAction.class)};
        return nodeActions;
//        Action[] result = new Action[]{
//            new RefreshPropsAction(),
//            null,
//            SystemAction.get(OpenLocalExplorerAction.class),
//            null,
//            SystemAction.get(NewAction.class),
//            null,
//            SystemAction.get(ToolsAction.class),
//            SystemAction.get(PropertiesAction.class),};
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
//            strValue = strValue.substring(1, strValue.length() - 1);
        }
        // Set the font color for read only property. Default is a gray color.
        property.setValue("htmlDisplayValue", "<font color='000000'>" + strValue + "</font>");
//      property.setValue("suppressCustomEditor", true);
        return property;
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();

        // Primary
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName("primary");
        set.setDisplayName(NbBundle.getMessage(NeoPeptideNode.class, "PropertySet.primary"));
        PropertySupport.ReadOnly property;
        // Id property
        property = createPropertyField("id", NbBundle.getMessage(NeoPeptideNode.class, "PropertySet.id"),
                NbBundle.getMessage(NeoPeptideNode.class, "PropertySet.id.desc"), String.class, peptide.getId());
        set.put(property);
        // Sequence property
        property = createPropertyField("seq", NbBundle.getMessage(NeoPeptideNode.class, "PropertySet.seq"),
                NbBundle.getMessage(NeoPeptideNode.class, "PropertySet.seq.desc"), String.class, peptide.getSequence());
        set.put(property);
        // Length property
        property = createPropertyField("length", NbBundle.getMessage(NeoPeptideNode.class, "PropertySet.length"),
                NbBundle.getMessage(NeoPeptideNode.class, "PropertySet.length.desc"), Integer.class, peptide.getLength());
        set.put(property);
        sheet.put(set);

        // Annotations
        List<NeoNeighbor> neighbors;
        String name;
        String desc;
        int count;
        for (AnnotationType aType : AnnotationType.values()) {
            count = 1;
            name = aType.name().toLowerCase();
            set = Sheet.createPropertiesSet();
            set.setName(name);
            set.setDisplayName(NbBundle.getMessage(NeoPeptideNode.class, "PropertySet." + name));
            neighbors = ((NeoPeptide) peptide).getAnnotations(aType);
            for (NeoNeighbor neighbor : neighbors) {
                desc = Arrays.toString(neighbor.getXref());
                property = createPropertyField(name + count, NbBundle.getMessage(NeoPeptideNode.class, "PropertySet." + name),
                        desc, String.class, neighbor.getName());
                set.put(property);
                count++;
            }
            sheet.put(set);
        }
        // Databases
//        set = Sheet.createPropertiesSet();
//        set.setName("databases");
//        set.setDisplayName(NbBundle.getMessage(NeoPeptideNode.class, "PropertySet.databases"));
//        StringTokenizer tokenizer;
//        String label, value;
//        for (String xref : ((NeoPeptide) peptide).getXref()) {
//            tokenizer = new StringTokenizer(xref, ":");
//            label = tokenizer.nextToken().trim();
//            value = tokenizer.nextToken().trim();
//            PropertySupport.ReadOnly property = createPropertyField(label, label, label, String.class, value);
//            set.put(property);
//        }
//        sheet.put(set);
        // Descriptors
        set = Sheet.createPropertiesSet();
        set.setName("attributes");
        set.setDisplayName(NbBundle.getMessage(NeoPeptideNode.class, "PropertySet.attributes"));
        for (PeptideAttribute attr : peptide.getAttributes()) {
            property = createPropertyField(attr.getId(), attr.getId(), attr.getId(), attr.getType(), peptide.getAttributeValue(attr));
            set.put(property);
        }
        set.put(property);
        set.setValue("tabName", NbBundle.getMessage(NeoPeptideNode.class, "PropertySet.attributes.tabName"));
        sheet.put(set);
        return sheet;
    }

}

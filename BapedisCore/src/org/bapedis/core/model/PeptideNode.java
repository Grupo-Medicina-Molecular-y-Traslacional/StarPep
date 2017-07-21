/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.awt.Image;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import javax.swing.Action;
import org.bapedis.core.ui.actions.AddToQueryModel;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class PeptideNode extends AbstractNode {

    protected Peptide peptide;

    public PeptideNode(Peptide peptide) {
        this(peptide, Children.LEAF, Lookups.singleton(peptide));
    }

    public PeptideNode(Peptide peptide, Children children, Lookup lookup) {
        super(children, lookup);
        this.peptide = peptide;
    }

    @Override
    public String getDisplayName() {
        return peptide.getId();
    }

    public PeptideNode(Peptide peptide, Children children) {
        this(peptide, children, Lookups.singleton(peptide));
    }

    @Override
    public Image getIcon(int type) {
        return ImageUtilities.loadImage("org/bapedis/core/resources/molecule.png", true);
    }

    public Peptide getPeptide() {
        return peptide;
    }

    @Override
    public Action[] getActions(boolean context) {
        return new Action[]{};
    }        

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();

        // Primary
        Sheet.Set set = Sheet.createPropertiesSet();
        set.setName("primary");
        set.setDisplayName(NbBundle.getMessage(PeptideNode.class, "PropertySet.primary"));
        PropertySupport.ReadOnly property;
        // Id property
        property = createPropertyField("id", NbBundle.getMessage(PeptideNode.class, "PropertySet.id"),
                NbBundle.getMessage(PeptideNode.class, "PropertySet.id.desc"), String.class, peptide.getId());
        set.put(property);
        // Sequence property
        property = createPropertyField("seq", NbBundle.getMessage(PeptideNode.class, "PropertySet.seq"),
                NbBundle.getMessage(PeptideNode.class, "PropertySet.seq.desc"), String.class, peptide.getSequence());
        set.put(property);
        // Length property
        property = createPropertyField("length", NbBundle.getMessage(PeptideNode.class, "PropertySet.length"),
                NbBundle.getMessage(PeptideNode.class, "PropertySet.length.desc"), Integer.class, peptide.getLength());
        set.put(property);
        sheet.put(set);

        // Annotations
        NodeIterable neighbors;
        Edge edge;
        String name;
        String desc = "";
        int count;
        for (AnnotationType aType : AnnotationType.values()) {
            count = 1;
            name = aType.name().toLowerCase();
            set = Sheet.createPropertiesSet();
            set.setName(name);
            set.setDisplayName(aType.getDisplayName());
            neighbors = peptide.getNeighbors(aType);
            for (Node neighbor : neighbors) {
                edge = peptide.getEdge(neighbor, aType);
                if (edge != null) {
                    desc = Arrays.toString((String[]) edge.getAttribute("xref"));
                } else {
                    desc = name;
                }
                property = createPropertyField(name + count, aType.getDisplayName(),
                        desc, String.class, neighbor.getAttribute("name"));
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
//        set = Sheet.createPropertiesSet();
//        set.setName("attributes");
//        set.setDisplayName(NbBundle.getMessage(NeoPeptideNode.class, "PropertySet.attributes"));
//        for (PeptideAttribute attr : peptide.getAttributes()) {
//            property = createPropertyField(attr.getId(), attr.getId(), attr.getId(), attr.getType(), peptide.getAttributeValue(attr));
//            set.put(property);
//        }
//        set.put(property);
//        set.setValue("tabName", NbBundle.getMessage(NeoPeptideNode.class, "PropertySet.attributes.tabName"));
//        sheet.put(set);
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
        if (value instanceof String[]) {
            strValue = Arrays.toString((String[]) value);
//            strValue = strValue.substring(1, strValue.length() - 1);
        }
        // Set the font color for read only property. Default is a gray color.
        property.setValue("htmlDisplayValue", "<font color='000000'>" + strValue + "</font>");
//      property.setValue("suppressCustomEditor", true);
        return property;
    }

}

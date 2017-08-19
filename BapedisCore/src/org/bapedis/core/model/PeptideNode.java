/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import javax.swing.Action;
import org.bapedis.core.spi.data.PeptideDAO;
import org.bapedis.core.ui.actions.SelectNodeOnGraph;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Node;
import org.gephi.graph.api.NodeIterable;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class PeptideNode extends AbstractNode implements PropertyChangeListener {

    protected Peptide peptide;
    private final Action[] actions;
    protected Sheet.Set descriptors;

    public PeptideNode(Peptide peptide) {
        this(peptide, Children.LEAF, Lookups.singleton(peptide));
    }

    public PeptideNode(Peptide peptide, Children children, Lookup lookup) {
        super(children, lookup);
        this.peptide = peptide;
        peptide.addDescriptorChangeListener(this);
        actions = new Action[]{new SelectNodeOnGraph(peptide.getGraphNode()), SystemAction.get(PropertiesAction.class)};
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
        return actions;
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
        descriptors = Sheet.createPropertiesSet();
        descriptors.setName("descriptors");
        descriptors.setValue("tabName", NbBundle.getMessage(PeptideNode.class, "PropertySet.attributes.tabName"));
        descriptors.setDisplayName(NbBundle.getMessage(PeptideNode.class, "PropertySet.descriptors"));
        for (PeptideAttribute attr : peptide.getAttributes()) {
            if (!(attr.equals(PeptideDAO.ID) || attr.equals(PeptideDAO.SEQ))) {
                property = createPropertyField(attr.getId(), attr.getDisplayName(), attr.getDisplayName(), attr.getType(), peptide.getAttributeValue(attr));
                descriptors.put(property);
            }
        }
        sheet.put(descriptors);

        return sheet;
    }

    private PropertySupport.ReadOnly createPropertyField(String name, String displayName, String description, Class type, final Object value) {
        PropertySupport.ReadOnly property = new PropertySupport.ReadOnly(name, type, displayName, description) {

            @Override
            public Object getValue() throws IllegalAccessException, InvocationTargetException {
                return value;
            }
        };
        String strValue = "";
        if (value.getClass().isArray()) {
            if (value instanceof String[]) {
                strValue = Arrays.toString((String[]) value);
//            strValue = strValue.substring(1, strValue.length() - 1);
            }
//            else{
//                System.out.println(value.getClass());
//            }
        } else {
            strValue = value.toString();
        }

        // Set the font color for read only property. Default is a gray color.
        property.setValue("htmlDisplayValue", "<font color='000000'>" + strValue + "</font>");
//      property.setValue("suppressCustomEditor", true);
        return property;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(Peptide.DESCRIPTOR_CHANGE) && descriptors != null) {
            PeptideAttribute attr = (PeptideAttribute) evt.getNewValue();
            PropertySupport.ReadOnly property = createPropertyField(attr.getId(), attr.getDisplayName(), attr.getDisplayName(), attr.getType(), peptide.getAttributeValue(attr));
            descriptors.put(property);
        }
    }

}

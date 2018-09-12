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
import org.bapedis.core.project.ProjectManager;
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
    protected Sheet sheet;

    public PeptideNode(Peptide peptide) {
        this(peptide, Children.LEAF, Lookups.singleton(peptide));
    }

    public PeptideNode(Peptide peptide, Children children) {
        this(peptide, children, Lookups.singleton(peptide));
    }

    public PeptideNode(Peptide peptide, Children children, Lookup lookup) {
        super(children, lookup);
        this.peptide = peptide;
        actions = new Action[]{new SelectNodeOnGraph(peptide.getGraphNode()), SystemAction.get(PropertiesAction.class)};
        peptide.addMolecularFeatureChangeListener(this);
    }

    @Override
    public String getDisplayName() {
        return peptide.getId();
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
        sheet = Sheet.createDefault();

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
        
        // Descriptors
        for (PeptideAttribute attr : peptide.getAttributes()) {
            if (attr instanceof MolecularDescriptor) {
                setMolecularFeature((MolecularDescriptor)attr);
            }
        }        

        return sheet;
    }

    private void setMolecularFeature(MolecularDescriptor attr) {
        String category = attr.getCategory();
        Sheet.Set set = sheet.get(category);
        if (set == null) {
            set = Sheet.createPropertiesSet();
            set.setName(category);
//            set.setValue("tabName", NbBundle.getMessage(PeptideNode.class, "PropertySet.md.tabName"));
            set.setDisplayName(category);
            sheet.put(set);
        }
        assert (peptide.getAttributeValue(attr) != null);
        PropertySupport.ReadOnly property = createPropertyField(attr.getId(), attr.getDisplayName(), attr.getDisplayName(), attr.getType(), peptide.getAttributeValue(attr));
        set.put(property);
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
        if (sheet != null) {
            if (evt.getPropertyName().equals(Peptide.CHANGED_ATTRIBUTE)) {
                if (evt.getNewValue() != null) {
                    PeptideAttribute attr = (PeptideAttribute) evt.getNewValue();
                    if (attr instanceof MolecularDescriptor) {
                        setMolecularFeature((MolecularDescriptor)attr);
                    }
                } else if (evt.getOldValue() != null) {
                    PeptideAttribute attr = (PeptideAttribute) evt.getOldValue();
                    if (attr instanceof MolecularDescriptor) {
                        String category = ((MolecularDescriptor)attr).getCategory();
                        Sheet.Set set = sheet.get(category);
                        if (set != null) {
                            set.remove(attr.getId());
                            if (set.getProperties().length == 0) {
                                sheet.remove(category);
                            }
                        }
                    }
                }
            }
        }
    }

}

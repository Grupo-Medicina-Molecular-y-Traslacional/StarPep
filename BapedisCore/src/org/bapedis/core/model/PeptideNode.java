/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import javax.swing.Action;
import org.bapedis.core.ui.actions.SelectNodeOnGraph;
import org.bapedis.core.ui.actions.View3DStructure;
import org.openide.actions.PropertiesAction;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.actions.SystemAction;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class PeptideNode extends AbstractNode implements PropertyChangeListener {

    protected static final Action[] globalActions = Utilities.actionsForPath("Actions/EditPeptides").toArray(new Action[0]);
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

        Action[] localActions = new Action[]{new SelectNodeOnGraph(peptide.getGraphNode()),
                                             new View3DStructure(peptide),
                                             SystemAction.get(PropertiesAction.class)};
        
        actions = new Action[localActions.length + globalActions.length];
        System.arraycopy(globalActions, 0, actions, 0, globalActions.length);
        System.arraycopy(localActions, 0, actions, globalActions.length, localActions.length);
        
        peptide.addMolecularFeatureChangeListener(this);
    }

    @Override
    public String getDisplayName() {
        return peptide.getID();
    }

//    @Override
//    public Image getIcon(int type) {
//        return ImageUtilities.loadImage("org/bapedis/core/resources/molecule.png", true);
//    }

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
        set.setDisplayName(NbBundle.getMessage(PeptideNode.class, "PropertySet.node"));
        PropertySupport.ReadOnly property;
        // Id property
        property = createPropertyField("id", NbBundle.getMessage(PeptideNode.class, "PropertySet.id"),
                NbBundle.getMessage(PeptideNode.class, "PropertySet.id.desc"), String.class, peptide.getID());
        set.put(property);

        // Sequence property
        property = createPropertyField("seq", NbBundle.getMessage(PeptideNode.class, "PropertySet.seq"),
                NbBundle.getMessage(PeptideNode.class, "PropertySet.seq.desc"), String.class, peptide.getSequence());
        set.put(property);
        sheet.put(set);
        
        // Length property
        property = createPropertyField("length", NbBundle.getMessage(PeptideNode.class, "PropertySet.length"),
                NbBundle.getMessage(PeptideNode.class, "PropertySet.length.desc"), Integer.class, peptide.getLength());
        set.put(property);
        sheet.put(set);        

        // Features         
//        for (PeptideAttribute attr : peptide.getAttributes()) {
//            if (attr instanceof MolecularDescriptor) {
//                setMolecularFeature((MolecularDescriptor) attr);
//            } else if (attr.isVisible() && attr != Peptide.ID && attr != Peptide.SEQ && attr != Peptide.LENGHT) {
//                setOtherFeatures(attr);
//            }
//        }

        return sheet;
    }

    private void setMolecularFeature(MolecularDescriptor attr) {
        Sheet.Set set = sheet.get("molecularFeatures");
        if (set == null) {
            set = Sheet.createPropertiesSet();
            set.setName("molecularFeatures");
            set.setDisplayName(NbBundle.getMessage(PeptideNode.class, "PropertySet.molecularFeatures"));
            sheet.put(set);
        }
        assert (peptide.getAttributeValue(attr) != null);
        PropertySupport.ReadOnly property = createPropertyField(attr.getId(), attr.getDisplayName(), attr.getCategory(), attr.getType(), peptide.getAttributeValue(attr));
        set.put(property);
    }

    private void removeMolecularFeatures(MolecularDescriptor attr) {
        Sheet.Set set = sheet.get("molecularFeatures");
        if (set != null) {
            set.remove(attr.getId());
            if (set.getProperties().length == 0) {
                sheet.remove("molecularFeatures");
            }else{
                sheet.put(set);
            }
        }
    }

    private void setOtherFeatures(PeptideAttribute attr) {
        if (attr.isVisible()) {
            Sheet.Set set = sheet.get("otherFeatures");
            if (set == null) {
                set = Sheet.createPropertiesSet();
                set.setName("otherFeatures");
                set.setDisplayName(NbBundle.getMessage(PeptideNode.class, "PropertySet.otherFeatures"));
                set.setValue("tabName", NbBundle.getMessage(PeptideNode.class, "PropertySet.otherFeatures.tabName"));
                sheet.put(set);
            }
            assert (peptide.getAttributeValue(attr) != null);
            PropertySupport.ReadOnly property = createPropertyField(attr.getId(), attr.getDisplayName(), attr.getDisplayName(), attr.getType(), peptide.getAttributeValue(attr));
            set.put(property);
        }
    }

    private void removeOtherFeatures(PeptideAttribute attr) {
        if (attr.isVisible()) {
            Sheet.Set set = sheet.get("otherFeatures");
            if (set != null) {
                set.remove(attr.getId());
                if (set.getProperties().length == 0) {
                    sheet.remove("otherFeatures");
                }
            }
        }
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
            }
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
                        setMolecularFeature((MolecularDescriptor) attr);
                    } else {
                        setOtherFeatures(attr);
                    }
                } else if (evt.getOldValue() != null) {
                    PeptideAttribute attr = (PeptideAttribute) evt.getOldValue();
                    if (attr instanceof MolecularDescriptor) {
                        removeMolecularFeatures(((MolecularDescriptor) attr));
                    } else {
                        removeOtherFeatures(attr);
                    }
                }
            }
        }
    }

}

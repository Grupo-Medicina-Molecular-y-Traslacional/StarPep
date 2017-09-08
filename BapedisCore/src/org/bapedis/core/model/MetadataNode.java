/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class MetadataNode extends AbstractNode {

//    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MetadataNode.class, "metadataNode");
    protected final Metadata metadata;

    public MetadataNode(Metadata metadata) {
        super(metadata.hasChilds() ? Children.create(new MetadataChildFactory(metadata), false) : Children.LEAF);
        this.metadata = metadata;
        setDisplayName(metadata.getName());
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();

        // Label property
        PropertySupport.ReadOnly labelProperty;
        labelProperty = createPropertyField("label", NbBundle.getMessage(MetadataNode.class, "PropertySet.label"),
                NbBundle.getMessage(MetadataNode.class, "PropertySet.label.desc"), String.class, metadata.getName());

        // Name property
        PropertySupport.ReadOnly nameProperty = createPropertyField("name", NbBundle.getMessage(GraphElementNode.class, "PropertySet.name"),
                NbBundle.getMessage(MetadataNode.class, "PropertySet.name.desc"), String.class, metadata.getName());
        set.put(nameProperty);

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

//    @Override
//    public String getHtmlDisplayName() {
//        Metadata metadata = getLookup().lookup(Metadata.class);
//        if (metadata.isSelected()) {
//            return "<b>" + getDisplayName() + "</b>";
//        }
//        return getDisplayName();
//    }
//    @Override
//    public void propertyChange(PropertyChangeEvent evt) {
//        Metadata metadata = getLookup().lookup(Metadata.class);
//        if (evt.getPropertyName().equals(Metadata.SELECTED)) {
//            fireDisplayNameChange("", metadata.getName());
//        }
//    }
//    @Override
//    public Transferable drag() throws IOException {
//        return this;
//    }
//
//    @Override
//    public DataFlavor[] getTransferDataFlavors() {
//        return new DataFlavor[]{DATA_FLAVOR};
//    }
//
//    @Override
//    public boolean isDataFlavorSupported(DataFlavor flavor) {
//        return flavor == DATA_FLAVOR;
//    }
//
//    @Override
//    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
//        if (flavor == DATA_FLAVOR) {
//            return metadata;
//        } else {
//            throw new UnsupportedFlavorException(flavor);
//        }
//    }
}

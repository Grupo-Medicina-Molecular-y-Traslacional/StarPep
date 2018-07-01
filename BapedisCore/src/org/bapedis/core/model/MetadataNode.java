/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import org.bapedis.core.project.ProjectManager;
import org.gephi.graph.api.Edge;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.PropertySupport;
import org.openide.nodes.Sheet;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class MetadataNode extends AbstractNode {

//    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MetadataNode.class, "metadataNode");
    protected final Edge edge;

    public MetadataNode(Edge edge) {
        super(Children.LEAF);
        this.edge = edge;
    }

    @Override
    public String getDisplayName() {
        return super.getDisplayName(); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Property property;

        // Primary set
        Sheet.Set set = Sheet.createPropertiesSet();
        sheet.put(set);
        set.setName("primary");
        set.setDisplayName(NbBundle.getMessage(MetadataNode.class, "PropertySet.primary"));
        
        //Source propery
        property = createReadOnlyPropertyField("source", NbBundle.getMessage(MetadataNode.class, "PropertySet.edge.source"),
                NbBundle.getMessage(MetadataNode.class, "PropertySet.edge.source.desc"), String.class, edge.getSource().getAttribute(ProjectManager.NODE_TABLE_PRO_NAME));
        set.put(property);
        
        // Label property
        property = createReadOnlyPropertyField("label", NbBundle.getMessage(MetadataNode.class, "PropertySet.label"),
                NbBundle.getMessage(MetadataNode.class, "PropertySet.label.desc"), String.class, edge.getLabel());
        set.put(property);

        //Target propery
        property = createReadOnlyPropertyField("target", NbBundle.getMessage(MetadataNode.class, "PropertySet.edge.target"),
                NbBundle.getMessage(MetadataNode.class, "PropertySet.edge.target.desc"), String.class, edge.getTarget().getAttribute(ProjectManager.NODE_TABLE_PRO_NAME));
        set.put(property);

        sheet.put(set);

        // Database reference
        String[] xrefs = (String[]) edge.getAttribute("dbRef");
        set = Sheet.createPropertiesSet();
        set.setName("DbRef");
        set.setDisplayName(NbBundle.getMessage(MetadataNode.class, "PropertySet.dbRef"));
        StringTokenizer tokenizer;
        String label, value;
        for (String xref : xrefs) {
            tokenizer = new StringTokenizer(xref, ":");
            label = tokenizer.nextToken().trim();
            value = tokenizer.nextToken().trim();
            property = createReadOnlyPropertyField(label, label, NbBundle.getMessage(MetadataNode.class, "PropertySet.dbRef.desc"), String.class, value);
            set.put(property);
        }

        sheet.put(set);
        return sheet;
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

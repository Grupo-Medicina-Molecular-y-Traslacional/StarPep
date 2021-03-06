/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.lang.reflect.InvocationTargetException;
import javax.swing.Action;
import org.bapedis.core.ui.actions.RemoveAllFromQueryModel;
import org.bapedis.core.ui.actions.RemoveFromQueryModel;
import org.bapedis.core.ui.actions.RemoveOthersFromQueryModel;
import org.gephi.graph.api.Node;
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
public class QueryNode extends AbstractNode {
    protected static final String displayName = NbBundle.getMessage(QueryNode.class, "QueryNode.displayName");
    protected final Metadata metadata;
    protected final QueryModel queryModel;
    protected final Action[] actions;
    protected static Action removeAll = new RemoveAllFromQueryModel();

    public QueryNode(QueryModel model) {
        this(model, null);
    }

    public QueryNode(QueryModel queryModel, Metadata metadata) {
        super(metadata == null ? Children.create(new QueryModelChildFactory(queryModel), false)
                : Children.LEAF,
                metadata == null ? null : Lookups.singleton(metadata));
        this.metadata = metadata;
        this.queryModel = queryModel;
        actions = new Action[]{new RemoveFromQueryModel(metadata), new RemoveOthersFromQueryModel(metadata), removeAll};
        if (metadata != null) {
            setDisplayName(metadata.getName());
        }
    }

    @Override
    public String getDisplayName() {
        if (metadata != null && metadata.getGraphNode() != null){
            return metadata.getGraphNode().getLabel();
        }
        return displayName; 
    }        

    @Override
    public Action[] getActions(boolean context) {
        actions[0].setEnabled(metadata != null);
        actions[1].setEnabled(metadata != null && queryModel.countElements() > 1);
        actions[2].setEnabled(queryModel.countElements() > 0);
        return actions;
    }

    @Override
    public String getHtmlDisplayName() {
        if (metadata != null) {
            return "<font color='AAAAAA'><i>" + metadata.getAnnotationType().getRelationType() + ":</i></font>"
                    + "<i> " + metadata.getName() + "</i>";
        }
        return NbBundle.getMessage(QueryNode.class, "QueryNode.rootContext.name");
    }

    @Override
    protected Sheet createSheet() {
        Sheet sheet = Sheet.createDefault();
        Sheet.Set set = Sheet.createPropertiesSet();

        // Primary set
        set.setName("primary");
        set.setDisplayName(NbBundle.getMessage(MetadataNode.class, "PropertySet.node"));        

        // ID property
        if (metadata != null) {            
            PropertySupport.ReadOnly labelProperty;
            labelProperty = createPropertyField("id", NbBundle.getMessage(MetadataNode.class, "PropertySet.id"),
                    NbBundle.getMessage(MetadataNode.class, "PropertySet.id.desc"), String.class, metadata.getID());
            set.put(labelProperty);
        }  
        
        // Name property
        PropertySupport.ReadOnly nameProperty = createPropertyField("name", NbBundle.getMessage(GraphElementNode.class, "PropertySet.name"),
                NbBundle.getMessage(MetadataNode.class, "PropertySet.name.desc"), String.class, metadata==null? NbBundle.getMessage(QueryNode.class, "QueryNode.rootContext.name"): metadata.getName());
        set.put(nameProperty);
        
        // Label property
        if (metadata != null && metadata.getGraphNode() != null) {            
            PropertySupport.ReadOnly labelProperty;
            labelProperty = createPropertyField("label", NbBundle.getMessage(MetadataNode.class, "PropertySet.label"),
                    NbBundle.getMessage(MetadataNode.class, "PropertySet.label.desc"), String.class, metadata.getGraphNode().getLabel());
            set.put(labelProperty);
        }        

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
//    public PasteType getDropType(Transferable t, int action, int index) {
//        if (t.isDataFlavorSupported(MetadataNode.DATA_FLAVOR)) {
//            try {
//                final Metadata transferData = (Metadata) t.getTransferData(MetadataNode.DATA_FLAVOR);
//                if (transferData != null) {
//                    return new PasteType() {
//                        @Override
//                        public Transferable paste() throws IOException {
//                            queryModel.add((Metadata) transferData);
//                            return null;
//                        }
//                    };
//                }
//            } catch (UnsupportedFlavorException ex) {
//                Exceptions.printStackTrace(ex);
//            } catch (IOException ex) {
//                Exceptions.printStackTrace(ex);
//            }
//        }
//        return null;
//    }
}

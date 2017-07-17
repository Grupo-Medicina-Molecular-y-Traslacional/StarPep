/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.Action;
import org.bapedis.core.ui.actions.RemoveFromQueryModel;
import org.bapedis.core.ui.actions.RemoveOthersFromQueryModel;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Exceptions;
import org.openide.util.NbBundle;
import org.openide.util.datatransfer.PasteType;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author Home
 */
public class QueryNode extends AbstractNode {

    protected final Metadata metadata;
    protected final QueryModel queryModel;
    protected final Action[] actions;

    public QueryNode(QueryModel model) {
        this(model, null);
    }

    public QueryNode(QueryModel queryModel, Metadata metadata) {
        super(metadata == null ? Children.create(new QueryModelChildFactory(queryModel), false)
                : Children.LEAF,
                Lookups.singleton(queryModel));
        this.metadata = metadata;
        this.queryModel = queryModel;
        actions = new Action[]{new RemoveFromQueryModel(metadata), new RemoveOthersFromQueryModel(metadata)};
    }

    @Override
    public Action[] getActions(boolean context) {
        actions[0].setEnabled(metadata != null);        
        actions[1].setEnabled(metadata != null && queryModel.countElements() > 1);
        return actions;
    }

    @Override
    public PasteType getDropType(Transferable t, int action, int index) {
        if (t.isDataFlavorSupported(MetadataNode.DATA_FLAVOR)) {
            try {
                final Metadata transferData = (Metadata) t.getTransferData(MetadataNode.DATA_FLAVOR);
                if (transferData != null) {
                    return new PasteType() {
                        @Override
                        public Transferable paste() throws IOException {
                            queryModel.add((Metadata) transferData);
                            return null;
                        }
                    };
                }
            } catch (UnsupportedFlavorException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    @Override
    public String getHtmlDisplayName() {
        if (metadata != null){
        return "<font color='AAAAAA'><i>" + metadata.getAnnotationType().getRelationType() +  ":</i></font>" +
               "<i> " + metadata.getName() + "</i>";
        }
        return NbBundle.getMessage(QueryNode.class, "QueryNode.rootContext.name");
    }
    
    
}

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
import org.neo4j.graphdb.Label;
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
        super(Children.create(new QueryModelChildFactory(queryModel), true), Lookups.singleton(queryModel));
        this.metadata = metadata;
        this.queryModel = queryModel;
        if (metadata == null) {
            setDisplayName(NbBundle.getMessage(QueryNode.class, "QueryNode.rootContext.name"));
        } else {
            setDisplayName(metadata.getName());
        }
        actions = new Action[]{new RemoveFromQueryModel()};
    }

    @Override
    public Action[] getActions(boolean context) {
        for (Action a : actions) {
            a.setEnabled(metadata != null);
        }
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

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
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

    protected final QueryModel queryModel;

    public QueryNode(QueryModel model) {
        super(Children.create(new QueryModelChildFactory(model), true), Lookups.singleton(model));
        this.queryModel = model;
        setDisplayName(NbBundle.getMessage(QueryNode.class, "QueryNode.rootContext.name"));
    }

     @Override
    public PasteType getDropType(Transferable t, int action, int index) {
        if (t.isDataFlavorSupported(LibraryNode.DATA_FLAVOR)) {
            try {
                final Object transferData = t.getTransferData(LibraryNode.DATA_FLAVOR);
                if (transferData != null) {
                    return new PasteType() {
                        @Override
                        public Transferable paste() throws IOException {
                            if (transferData instanceof Label) {
                                queryModel.add((Label) transferData);
                            } else if (transferData instanceof Metadata) {
                                queryModel.add((Metadata) transferData);
                            }
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

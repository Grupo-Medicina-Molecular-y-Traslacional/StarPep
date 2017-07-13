/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import org.bapedis.core.services.ProjectManager;
import org.neo4j.graphdb.Label;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author loge
 */
public class MyLibraryNode extends AbstractNode {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MyLibraryNode.class, "libraryNode");
    protected final MyTransferableImpl transferable;
    protected final Action[] actions;

    public MyLibraryNode() {
        this(Children.create(new MyLibraryChildFactory(), false));
    }

    public MyLibraryNode(Children children) {
        this(children, null);
    }

    public MyLibraryNode(Children children, Lookup lookup) {
        super(children, lookup);
        transferable = new MyTransferableImpl();
        actions = new Action[]{new AddToQuery(), new RemoveFromQuery()};
    }

    @Override
    public Action[] getActions(boolean context) {
        actions[0].setEnabled(transferable.transferData != null);
        actions[1].setEnabled(transferable.transferData != null);
        return actions;
    }

    @Override
    public Transferable drag() throws IOException {
        return transferable;
    }

    protected class MyTransferableImpl implements Transferable {

        private Object transferData;

        public void setTransferData(Object transferData) {
            this.transferData = transferData;
        }

        @Override
        public DataFlavor[] getTransferDataFlavors() {
            return new DataFlavor[]{DATA_FLAVOR};
        }

        @Override
        public boolean isDataFlavorSupported(DataFlavor flavor) {
            return flavor == DATA_FLAVOR;

        }

        @Override
        public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
            if (flavor == DATA_FLAVOR) {
                return transferData;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }

    protected class AddToQuery extends AbstractAction {

        public AddToQuery() {
            putValue(NAME, NbBundle.getMessage(MyLibraryNode.class, "MyLibraryNode.action.addToQuery"));
//            putValue(SMALL_ICON, ImageUtilities.loadImage("org/bapedis/core/resources/add.png", true));
        }                

        @Override
        public void actionPerformed(ActionEvent e) {
            QueryModel queryModel = Lookup.getDefault().lookup(ProjectManager.class).getQueryModel();
            if (transferable.transferData instanceof Label) {
                queryModel.add((Label) transferable.transferData);
            } else if (transferable.transferData instanceof Metadata) {
                queryModel.add((Metadata) transferable.transferData);
            }
        }

    }

    protected class RemoveFromQuery extends AbstractAction {

        public RemoveFromQuery() {
            putValue(NAME, NbBundle.getMessage(MyLibraryNode.class, "MyLibraryNode.action.removeFromQuery"));
//            putValue(SMALL_ICON, ImageUtilities.loadImage("org/bapedis/core/resources/remove.png", true));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            QueryModel queryModel = Lookup.getDefault().lookup(ProjectManager.class).getQueryModel();
            if (transferable.transferData instanceof Label) {
                queryModel.remove((Label) transferable.transferData);
            } else if (transferable.transferData instanceof Metadata) {
                queryModel.remove((Metadata) transferable.transferData);
            }
        }

    }
}

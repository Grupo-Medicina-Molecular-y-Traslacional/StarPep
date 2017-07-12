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
import java.util.List;
import javax.swing.Action;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.Lookup;
import org.openide.util.Utilities;

/**
 *
 * @author loge
 */
public class MyLibraryNode extends AbstractNode {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MyLibraryNode.class, "libraryNode");
    protected final Transferable transferable;

    public MyLibraryNode() {
        super(Children.create(new MyLibraryChildFactory(), false));
        transferable = new TransferableImpl();
    }

    public MyLibraryNode(Children children) {
        super(children);
        transferable = new TransferableImpl();
    }

    public MyLibraryNode(Children children, Lookup lookup) {
        super(children, lookup);
        transferable = new TransferableImpl();
    }

    @Override
    public Action[] getActions(boolean context) {
        List<? extends Action> nodeActions
                = Utilities.actionsForPath("Actions/ShowDataFromLibrary/Peptides");

        return nodeActions.toArray(new Action[0]);
    }

    public Transferable getTransferable() {
        return transferable;
    }        

    private class TransferableImpl implements Transferable {

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
                return MyLibraryNode.this;
            } else {
                throw new UnsupportedFlavorException(flavor);
            }
        }
    }

}

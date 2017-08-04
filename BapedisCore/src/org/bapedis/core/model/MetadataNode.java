/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.model;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import javax.swing.Action;
import org.bapedis.core.ui.actions.AddToQueryModel;
import org.bapedis.core.ui.actions.RemoveFromQueryModel;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.util.lookup.Lookups;

/**
 *
 * @author loge
 */
public class MetadataNode extends AbstractNode implements Transferable, PropertyChangeListener {

    public static final DataFlavor DATA_FLAVOR = new DataFlavor(MetadataNode.class, "metadataNode");
    protected final Action[] actions;
    protected final Metadata metadata;

    public MetadataNode(Metadata metadata) {
        super(metadata.hasChilds()? Children.create(new MetadataChildFactory(metadata), false) : Children.LEAF, Lookups.singleton(metadata));
        metadata.addPropertyChangeListener(this);
        this.metadata = metadata;
        setDisplayName(metadata.getName());
        actions = new Action[]{new AddToQueryModel(metadata), new RemoveFromQueryModel(metadata)};
    }

    @Override
    public Action[] getActions(boolean context) {
        actions[0].setEnabled(!metadata.isSelected());
        actions[1].setEnabled(metadata.isSelected());
        return actions;
    }

//    @Override
//    public String getHtmlDisplayName() {
//        Metadata metadata = getLookup().lookup(Metadata.class);
//        if (metadata.isSelected()) {
//            return "<b>" + getDisplayName() + "</b>";
//        }
//        return getDisplayName();
//    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        Metadata metadata = getLookup().lookup(Metadata.class);
        if (evt.getPropertyName().equals(Metadata.SELECTED)) {
            fireDisplayNameChange("", metadata.getName());
        }
    }

    @Override
    public Transferable drag() throws IOException {
        return this;
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
            return metadata;
        } else {
            throw new UnsupportedFlavorException(flavor);
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui;

import java.util.Collection;
import org.bapedis.core.ui.components.AttributesPanel;
import org.bapedis.db.model.NeoNeighbor;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;

/**
 *
 * @author loge
 */
public class NeighborModelPanel extends AttributeModelPanel implements LookupListener {

    private javax.swing.JTabbedPane tabbedPane;
    protected Lookup.Result<NeoNeighbor> neighborLkpResult;

    /**
     * Creates new form NeighborModelPanel
     */
    public NeighborModelPanel() {
        super(new AttributesPanel());
        tabbedPane = new javax.swing.JTabbedPane();
        setLayout(new java.awt.BorderLayout());
        add(tabbedPane, java.awt.BorderLayout.CENTER);
        tabbedPane.add(NbBundle.getMessage(NeighborModelPanel.class, "NeighborModelPanel.nodesPane.text"), attributesViewer);
        neighborLkpResult = Utilities.actionsGlobalContext().lookupResult(NeoNeighbor.class);
        neighborLkpResult.addLookupListener(this);
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends NeoNeighbor> neighbors = neighborLkpResult.allInstances();
        if (neighbors.isEmpty()){
            attributesViewer.clearSelection();
        }
    }

}

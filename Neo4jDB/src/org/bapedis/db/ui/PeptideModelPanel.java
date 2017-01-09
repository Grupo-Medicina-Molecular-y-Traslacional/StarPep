/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui;

import java.awt.BorderLayout;
import java.util.Collection;
import org.bapedis.core.ui.components.AttributesPanel;
import org.bapedis.db.model.NeoNeighbor;
import org.bapedis.db.model.NeoPeptide;
import org.bapedis.db.model.NeoPeptideNode;
import org.netbeans.swing.etable.QuickFilter;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.Utilities;

/**
 *
 * @author loge
 */
public class PeptideModelPanel extends AttributeModelPanel implements LookupListener {

    protected Lookup.Result<NeoNeighbor> neighborLkpResult;

    /**
     * Creates new form PeptideModelPanel
     */
    public PeptideModelPanel() {
        super(new AttributesPanel());
        setLayout(new java.awt.BorderLayout());
        add(attributesViewer, BorderLayout.CENTER);
        neighborLkpResult = Utilities.actionsGlobalContext().lookupResult(NeoNeighbor.class);
        neighborLkpResult.addLookupListener(this);
    }                     
                  
    @Override
    public void resultChanged(LookupEvent le) {
        final Collection<? extends NeoNeighbor> neighbors = neighborLkpResult.allInstances();
        if (!neighbors.isEmpty()) {
            attributesViewer.setQuickFilter(0, new QuickFilter() {

                @Override
                public boolean accept(Object o) {
                    if (o instanceof NeoPeptideNode) {
                        NeoPeptideNode neoNode = (NeoPeptideNode) o;
                        for (NeoNeighbor neighbor : neighbors) {
                            if (neighbor.containsSourcePeptide(neoNode.getLookup().lookup(NeoPeptide.class))) {
                                return true;
                            }
                        }
                    }
                    return false;
                }
            });
        } else {
            attributesViewer.unsetQuickFilter();
        }
    }

}

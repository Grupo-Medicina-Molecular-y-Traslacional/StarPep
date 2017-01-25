/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import org.bapedis.core.ui.actions.GlobalContextSensitiveAction;
import org.bapedis.db.model.NeoPeptide;
import org.bapedis.db.ui.NeoPeptideGraphViewDescription;
import org.bapedis.db.ui.NeoPeptideStructureViewDescription;
import org.bapedis.db.ui.NeoPeptideTextViewDescription;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.LookupEvent;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

/**
 *
 * @author loge
 */
@ActionID(
        category = "View",
        id = "org.bapedis.db.ui.actions.ShowPeptideDetails"
)
@ActionRegistration(
        displayName = "#CTL_ShowPeptideDetails",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Menu/View", position = 310)
})
public class ShowPeptideDetails extends GlobalContextSensitiveAction<NeoPeptide> {

    public ShowPeptideDetails() {
        super(NeoPeptide.class);
        String name = NbBundle.getMessage(RemoveFilter.class, "CTL_ShowPeptideDetails");
        putValue(NAME, name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Collection<? extends NeoPeptide> neoPeptides = Utilities.actionsGlobalContext().lookupAll(NeoPeptide.class);
        if (!neoPeptides.isEmpty()) {
            NeoPeptide neoPeptide = neoPeptides.iterator().next();
            MultiViewDescription[] multiviews = new MultiViewDescription[3];
            int pos = 0;
            multiviews[pos++] = new NeoPeptideTextViewDescription(neoPeptide);
            multiviews[pos++] = new NeoPeptideStructureViewDescription(neoPeptide);
            multiviews[pos++] = new NeoPeptideGraphViewDescription(neoPeptide);
            TopComponent tc = MultiViewFactory.createCloneableMultiView(multiviews, multiviews[0]);
            tc.setDisplayName(neoPeptide.getId());
            tc.open();
            tc.requestActive();
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends NeoPeptide> context = lkpResult.allInstances();
        setEnabled(context.size() == 1);
    }    
    
}

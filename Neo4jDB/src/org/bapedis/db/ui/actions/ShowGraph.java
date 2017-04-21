/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.db.ui.actions;

import java.awt.event.ActionEvent;
import java.util.Collection;
import org.bapedis.core.ui.actions.WorkspaceContextSensitiveAction;
import org.bapedis.db.model.NeoPeptideModel;
import org.bapedis.db.ui.NeoGraphSceneDescription;
import org.bapedis.db.ui.NeoGraphPreViewDescription;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.LookupEvent;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
@ActionID(
        category = "View",
        id = "org.bapedis.db.ui.actions.ShowGraph"
)
@ActionRegistration(
        displayName = "#CTL_ShowGraph",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Menu/View", position = 310)
})
public class ShowGraph extends WorkspaceContextSensitiveAction<NeoPeptideModel> {

    public ShowGraph() {
        super(NeoPeptideModel.class);
        String name = NbBundle.getMessage(ShowGraph.class, "CTL_ShowGraph");
        putValue(NAME, name);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        TopComponent graphTC = WindowManager.getDefault().findTopComponent("GraphTC");
        if (graphTC == null) {
            MultiViewDescription[] multiviews = new MultiViewDescription[2];
            multiviews[0] = new NeoGraphSceneDescription();
            multiviews[1] = new NeoGraphPreViewDescription();
            graphTC = MultiViewFactory.createCloneableMultiView(multiviews, multiviews[0]);
            graphTC.setName("GraphTC");
            graphTC.setDisplayName(NbBundle.getMessage(ShowGraph.class, "CTL_GraphTC"));
        }
        graphTC.open();
        graphTC.requestActive();
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends NeoPeptideModel> context = lkpResult.allInstances();
        setEnabled(context.size() == 1);
    }

}

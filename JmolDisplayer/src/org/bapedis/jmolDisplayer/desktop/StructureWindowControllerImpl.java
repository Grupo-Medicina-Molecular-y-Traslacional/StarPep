/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.jmolDisplayer.desktop;

import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.StructureWindowController;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewFactory;
import org.bapedis.jmolDisplayer.model.StructureData;
import org.bapedis.jmolDisplayer.model.StructureSceneModel;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;

/**
 *
 * @author loge
 */
@ServiceProvider(service = StructureWindowController.class)
public class StructureWindowControllerImpl implements StructureWindowController {

    private ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    private TopComponent strucTopComponent;

    @Override
    public void openStructureWindow(Peptide peptide) {
        Workspace workspace = pc.getCurrentWorkspace();
        StructureSceneModel sceneModel = workspace.getLookup().lookup(StructureSceneModel.class);
        if (sceneModel == null) {
            sceneModel = new StructureSceneModel();
            workspace.add(sceneModel);
        }
        StructureData item = new StructureData(peptide);
        sceneModel.setItem(item);
        
        String[] structures = item.getStructures();
        if (structures.length > 0) {
            sceneModel.setStructure(structures[0]);
        }

        if (strucTopComponent == null) {
            MultiViewDescription[] multiviews = new MultiViewDescription[]{new StructureSceneDescription(peptide)};
            strucTopComponent = MultiViewFactory.createCloneableMultiView(multiviews, multiviews[0]);
            strucTopComponent.setDisplayName(NbBundle.getMessage(StructureWindowControllerImpl.class, "CTL_StructureTC_title"));
        }
        strucTopComponent.open();
        strucTopComponent.requestActive();
    }

    @Override
    public TopComponent getStructureWindow() {
        return strucTopComponent;
    }

}

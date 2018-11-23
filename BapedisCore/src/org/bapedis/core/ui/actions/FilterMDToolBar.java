/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Iterator;
import javax.swing.AbstractAction;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.FeatureSelectionTag;
import org.bapedis.core.spi.alg.impl.FeatureSEFilteringFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.FilterMDToolBar"
)
@ActionRegistration(
        iconBase = "org/bapedis/core/resources/filter_md.png",
        displayName = "#CTL_FilterMD"
)
@ActionReferences({    
    @ActionReference(path = "Toolbars/MD", position = 20)
})
public class FilterMDToolBar extends AbstractAction {
    private final ProjectManager pc;
    private final ActionListener actionListener;

    public FilterMDToolBar() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        
        AlgorithmFactory finteringFactory = null;
        for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
            final AlgorithmFactory factory = it.next();
            if (factory instanceof FeatureSEFilteringFactory){
                finteringFactory = factory;
            }
        }        
        
        actionListener = finteringFactory != null ?ToolAction.createActionListener(finteringFactory, FeatureSelectionTag.class): null;
    }
        
    @Override
    public void actionPerformed(ActionEvent e) {
        if (actionListener != null){
            actionListener.actionPerformed(e);
        }        
    }
    
}

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
import static javax.swing.Action.NAME;
import static javax.swing.Action.SHORT_DESCRIPTION;
import static javax.swing.Action.SMALL_ICON;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.ChemSpaceTag;
import org.bapedis.core.spi.ui.GraphWindowController;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionRegistration;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
//@ActionID(
//        category = "Tools",
//        id = "org.bapedis.core.ui.actions.ChemicalSpaceAction"
//)
//@ActionRegistration(
//        iconBase = "org/bapedis/core/resources/chemSpaceNet.png",
//        displayName = "#CTL_ChemicalSpaceAction"
//)
//@ActionReference(path = "Toolbars/Network", position = 120)
//@NbBundle.Messages({"CTL_ChemicalSpaceAction=Chemical Space Network"})
public class ChemicalSpaceAction extends AbstractAction {

    private final GraphWindowController graphWC;
    private final ProjectManager pc;
    private final ActionListener actionListener;

    public ChemicalSpaceAction() {
        graphWC = Lookup.getDefault().lookup(GraphWindowController.class);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        putValue(NAME, NbBundle.getMessage(ChemicalSpaceAction.class, "ChemicalSpaceAction.name"));
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/chemSpaceNet.png", false));
        putValue(SHORT_DESCRIPTION, NbBundle.getMessage(ChemicalSpaceAction.class, "ChemicalSpaceAction.desc"));
        
        AlgorithmFactory chemSpaceFactory = null;
        for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
            final AlgorithmFactory factory = it.next();
            if (factory instanceof ChemSpaceTag){
                chemSpaceFactory = factory;
            }
        }  
        actionListener = chemSpaceFactory != null ?ToolAction.createActionListener(chemSpaceFactory, ChemSpaceTag.class): null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (actionListener != null){
            actionListener.actionPerformed(e);
        }
    }

}

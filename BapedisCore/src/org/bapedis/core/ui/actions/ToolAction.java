/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collection;
import java.util.Iterator;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.model.AlgorithmModel;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.spi.algo.ToolMenuItem;
import org.openide.DialogDisplayer;
import org.openide.util.actions.Presenter;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
public class ToolAction extends WorkspaceContextSensitiveAction<AttributesModel> implements Presenter.Menu {

    protected final AlgorithmCategory category;
    protected final JMenu main;

    public ToolAction(AlgorithmCategory category) {
        super(AttributesModel.class);
        this.category = category;
        main = new JMenu(category.getDisplayName());
        main.setEnabled(enabled);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public void setEnabled(boolean newValue) {
        super.setEnabled(newValue); //To change body of generated methods, choose Tools | Templates.
        if (main != null) {
            main.setEnabled(newValue);
        }
    }

    @Override
    public JMenuItem getMenuPresenter() {
        JMenuItem item;
        for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
            final AlgorithmFactory factory = it.next();
            if (factory.getCategory() == category) {
                item = new JMenuItem(factory.getName());
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        AlgorithmModel algoModel = pc.getAlgorithmModel();

                        if (algoModel.isRunning()) {
                            DialogDisplayer.getDefault().notify(algoModel.getOwnerWS().getBusyNotifyDescriptor());
                        } else {
                            algoModel.setCategory(category);

                            Workspace currentWs = pc.getCurrentWorkspace();
                            Collection<? extends Algorithm> savedAlgo = currentWs.getLookup().lookupAll(Algorithm.class);
                            Algorithm algorithm = null;
                            for (Algorithm algo : savedAlgo) {
                                if (algo.getFactory() == factory) {
                                    algorithm = algo;
                                    break;
                                }
                            }
                            if (algorithm == null) {
                                algorithm = factory.createAlgorithm();
                                currentWs.add(algorithm);
                            }
                            algoModel.setSelectedAlgorithm(algorithm);

                            TopComponent tc = WindowManager.getDefault().findTopComponent("AlgoExplorerTopComponent");
                            tc.open();
                            tc.requestActive();
                        }
                    }
                });
                ToolMenuItem toolItem = factory instanceof ToolMenuItem ? (ToolMenuItem) factory : null;
                if (toolItem != null && toolItem.addSeparatorBefore()){
                    main.addSeparator();
                }
                main.add(item);
                if (toolItem != null && toolItem.addSeparatorAfter()){
                    main.addSeparator();
                }                
            }
        }
        return main;
    }

}

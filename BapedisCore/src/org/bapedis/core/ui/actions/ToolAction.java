/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.model.AlgorithmModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
public class ToolAction extends AbstractAction implements Presenter.Menu {

    protected final AlgorithmCategory category;
    protected final ProjectManager pc;

    public ToolAction(AlgorithmCategory category) {
        this.category = category;
        pc = Lookup.getDefault().lookup(ProjectManager.class);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public JMenuItem getMenuPresenter() {
        JMenu main = new JMenu(category.getDisplayName());
        JMenuItem item;
        for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
            final AlgorithmFactory factory = it.next();
            if (factory.getCategory() == category) {
                item = new JMenuItem(factory.getName());
                item.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
                        AlgorithmModel algoModel = pc.getAlgorithmModel();
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
                });
                main.add(item);
            }
        }
        return main;
    }

}

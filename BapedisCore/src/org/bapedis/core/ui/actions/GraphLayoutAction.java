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
import static javax.swing.Action.NAME;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.model.AlgorithmModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.util.Lookup;
import org.openide.util.NbBundle.Messages;
import org.openide.util.actions.Presenter;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
@ActionID(
        category = "Tools",
        id = "org.bapedis.core.ui.actions.GraphLayoutAction"
)
@ActionRegistration(
        displayName = "#CTL_GraphLayoutAction"
)
@ActionReferences({
    @ActionReference(path = "Menu/Tools", position = 100)
})
@Messages("CTL_GraphLayoutAction=Graph Layout")
public final class GraphLayoutAction extends AbstractAction implements Presenter.Menu {

    public GraphLayoutAction() {
        putValue(NAME, Bundle.CTL_GraphLayoutAction());
    }

    @Override
    public void actionPerformed(ActionEvent e) {

    }

    @Override
    public JMenuItem getMenuPresenter() {
        List<? extends AlgorithmFactory> factories = new ArrayList<>(Lookup.getDefault().lookupAll(AlgorithmFactory.class));
        for (Iterator<? extends AlgorithmFactory> it = factories.iterator(); it.hasNext();) {
            AlgorithmFactory f = it.next();
            if (f.getCategory() != AlgorithmCategory.GraphLayout) {
                it.remove();
            }
        }
        Collections.sort(factories, new Comparator() {
            @Override
            public int compare(Object o1, Object o2) {
                return ((AlgorithmFactory) o1).getName().compareTo(((AlgorithmFactory) o2).getName());
            }
        });
        JMenu main = new JMenu(AlgorithmCategory.GraphLayout.getDisplayName());
        JMenuItem item;
        for (final AlgorithmFactory factory : factories) {
            item = new JMenuItem(factory.getName());
            item.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
                    AlgorithmModel algoModel = pc.getAlgorithmModel();
                    algoModel.setCategory(AlgorithmCategory.GraphLayout);

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
        return main;
    }
}

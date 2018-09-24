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
import org.bapedis.core.model.AlgorithmModel;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.spi.alg.ToolMenuItem;
import static org.bapedis.core.ui.actions.WorkspaceContextSensitiveAction.pc;
import org.openide.DialogDisplayer;
import org.openide.util.actions.Presenter;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 *
 * @author loge
 */
public class ToolAction extends WorkspaceContextSensitiveAction<AttributesModel> implements Presenter.Menu {

    protected final Class tag;
    protected final JMenu main;

    public ToolAction(String name, Class tag) {
        super(AttributesModel.class);
        this.tag = tag;
        main = new JMenu(name);
        main.setEnabled(enabled);
        populateMenu();
    }

    public static ActionListener createActionListener(AlgorithmFactory factory, Class tagClass) {

        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Workspace currentWS = pc.getCurrentWorkspace();
                if (currentWS.isBusy()) {
                    DialogDisplayer.getDefault().notify(currentWS.getBusyNotifyDescriptor());
                } else {
                    AlgorithmModel algoModel = pc.getAlgorithmModel();
                    Algorithm algorithm = pc.getOrCreateAlgorithm(factory);
                    if (algorithm != null) {
                        algoModel.setTagInterface(tagClass);
                        algoModel.setSelectedAlgorithm(algorithm);
                    }

                    TopComponent tc = WindowManager.getDefault().findTopComponent("AlgoExplorerTopComponent");
                    tc.open();
                    tc.requestActive();
                }
            }
        };
    }

    private void populateMenu() {
        JMenuItem item;
        for (Iterator<? extends AlgorithmFactory> it = pc.getAlgorithmFactoryIterator(); it.hasNext();) {
            final AlgorithmFactory factory = it.next();
            if (tag.isAssignableFrom(factory.getClass())) {
                item = new JMenuItem(factory.getName());
                item.addActionListener(createActionListener(factory, tag));
                JMenu menu = factory.getCategory() == null ? main : getOrCreateMenu(factory.getCategory());
                ToolMenuItem toolItem = factory instanceof ToolMenuItem ? (ToolMenuItem) factory : null;
                if (toolItem != null && toolItem.addSeparatorBefore()) {
                    menu.addSeparator();
                }
                menu.add(item);
                if (toolItem != null && toolItem.addSeparatorAfter()) {
                    menu.addSeparator();
                }
            }
        }
    }

    private JMenu getOrCreateMenu(String category) {
        JMenu menu = null;
        JMenuItem item;
        for (int i = 0; i < main.getItemCount(); i++) {
            item = main.getItem(i);
            if (item instanceof JMenu && item.getName().equals(category)) {
                menu = (JMenu) item;
            }
        }
        if (menu == null) {
            menu = new JMenu(category);
            menu.setName(category);
            main.add(menu);
        }
        return menu;
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
        return main;
    }

}

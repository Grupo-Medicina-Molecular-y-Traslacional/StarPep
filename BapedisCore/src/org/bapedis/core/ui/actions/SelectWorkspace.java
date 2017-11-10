/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.Component;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.ActionReferences;
import org.openide.awt.ActionRegistration;
import org.openide.awt.DropDownButtonFactory;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.actions.Presenter;

@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.SelectWorkspace"
)
@ActionRegistration(
        displayName = "#CTL_SelectWorkspace",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Menu/View", position = 80, separatorAfter = 99),
    @ActionReference(path = "Toolbars/Workspace", position = 50)
})
public class SelectWorkspace extends AbstractAction implements Presenter.Toolbar, Presenter.Menu, WorkspaceEventListener, LookupListener, PropertyChangeListener {

    protected JPopupMenu popup;
    protected JMenu menu;
    protected ButtonGroup popupGroup;
//    protected ButtonGroup menuGroup;
    protected HashMap<Integer, JCheckBoxMenuItem> popupMap;
    protected HashMap<Integer, JCheckBoxMenuItem> menuMap;
    protected ProjectManager pc;
    protected Lookup.Result<Workspace> lkpResult;

    public SelectWorkspace() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        pc.addWorkspaceEventListener(this);
        lkpResult = pc.getLookup().lookupResult(Workspace.class);
        lkpResult.addLookupListener(this);
        popup = new JPopupMenu();
        popupGroup = new ButtonGroup();
        popupMap = new HashMap<>();
        menu = new JMenu(NbBundle.getMessage(SelectWorkspace.class, "CTL_SelectWorkspace"));
//        menuGroup = new ButtonGroup();
        menuMap = new HashMap<>();
        populateMenuItems();
        pc.getCurrentWorkspace().addPropertyChangeListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent ae) {
    }

    private JCheckBoxMenuItem createJMenuItem(final Workspace ws) {
        JCheckBoxMenuItem item = new JCheckBoxMenuItem(ws.getName());
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pc.setCurrentWorkspace(ws);
            }
        });
        if (ws.equals(pc.getCurrentWorkspace())) {
            item.setSelected(true);
        }
        return item;
    }

    @Override
    public Component getToolbarPresenter() {
        Image iconImage = ImageUtilities.loadImage("org/bapedis/core/resources/workspace.png");
        ImageIcon icon = new ImageIcon(iconImage);

        final JButton dropDownButton = DropDownButtonFactory.createDropDownButton(new ImageIcon(
                new BufferedImage(32, 32, BufferedImage.TYPE_BYTE_GRAY)), popup);

        dropDownButton.setIcon(icon);
        dropDownButton.setToolTipText(NbBundle.getMessage(SelectWorkspace.class, "CTL_SelectWorkspace"));
        dropDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                popup.show(dropDownButton, 0, dropDownButton.getHeight());
            }
        });

        popup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                dropDownButton.setSelected(false);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                dropDownButton.setSelected(false);
            }

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            }
        });

        return dropDownButton;
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        oldWs.removePropertyChangeListener(this);
        newWs.addPropertyChangeListener(this);
        JCheckBoxMenuItem item = popupMap.get(newWs.getId());
        item.setSelected(true);
        item = menuMap.get(newWs.getId());
        item.setSelected(true);
    }

    private void populateMenuItems() {
        JCheckBoxMenuItem item;
        for (Iterator<? extends Workspace> it = pc.getWorkspaceIterator(); it.hasNext();) {
            Workspace ws = it.next();
            item = createJMenuItem(ws);
            popup.add(item);
            popupGroup.add(item);
            popupMap.put(ws.getId(), item);
            item = createJMenuItem(ws);
            menu.add(item);
//            menuGroup.add(item);
            menuMap.put(ws.getId(), item);
        }
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends Workspace> workspaces = lkpResult.allInstances();
        popup.removeAll();
        menu.removeAll();
        popupGroup = new ButtonGroup();
//        menuGroup = new ButtonGroup();
        popupMap.clear();
        menuMap.clear();
        populateMenuItems();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        if (propertyName.equals(Workspace.PRO_NAME)) {
            Workspace ws = (Workspace) evt.getSource();
            JCheckBoxMenuItem item = popupMap.get(ws.getId());
            item.setText(evt.getNewValue().toString());
            item = menuMap.get(ws.getId());
            item.setText(evt.getNewValue().toString());
        }
    }

    @Override
    public JMenuItem getMenuPresenter() {
        return menu;
    }

}

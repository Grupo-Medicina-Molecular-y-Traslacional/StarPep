/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.Component;
import java.awt.Cursor;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.AbstractAction;
import static javax.swing.Action.NAME;
import static javax.swing.Action.SMALL_ICON;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingWorker;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
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
import org.openide.windows.WindowManager;

/**
 *
 * @author Loge
 */
@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.CopyWorkspace"
)
@ActionRegistration(
        displayName = "#CTL_CopyWorkspace",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Menu/File", position = 230),
    @ActionReference(path = "Toolbars/Workspace", position = 110)
})
public class CopyWorkspace extends AbstractAction implements Presenter.Toolbar, Presenter.Menu, WorkspaceEventListener, LookupListener, PropertyChangeListener {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);

    protected JPopupMenu popup;
    protected JMenu menu;

    protected HashMap<Integer, JMenuItem> popupMap;
    protected HashMap<Integer, JMenuItem> menuMap;

    protected Lookup.Result<Workspace> lkpResult;

    public CopyWorkspace() {
        popup = new JPopupMenu(NbBundle.getMessage(CopyPeptides.class, "CTL_CopyWorkspace"));
        menu = new JMenu(NbBundle.getMessage(SelectWorkspace.class, "CTL_CopyWorkspace"));

        popupMap = new HashMap<>();
        menuMap = new HashMap<>();

        lkpResult = pc.getLookup().lookupResult(Workspace.class);
        lkpResult.addLookupListener(this);

        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/duplicateWorkspace.png", false));
        putValue(NAME, NbBundle.getMessage(RemoveCurrentWorkspace.class, "CTL_CopyWorkspace"));

        populateMenuItems();
        pc.getCurrentWorkspace().addPropertyChangeListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Component getToolbarPresenter() {
        Image iconImage = ImageUtilities.loadImage("org/bapedis/core/resources/duplicateWorkspace.png");
        ImageIcon icon = new ImageIcon(iconImage);

        final JButton dropDownButton = DropDownButtonFactory.createDropDownButton(new ImageIcon(
                new BufferedImage(32, 32, BufferedImage.TYPE_BYTE_GRAY)), popup);
        dropDownButton.setIcon(icon);
        dropDownButton.setToolTipText(NbBundle.getMessage(RemoveWorkspace.class, "CTL_CopyWorkspace"));
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

    private void populateMenuItems() {
        JMenu popupItem = new JMenu(NbBundle.getMessage(CopyPeptides.class, "CTL_CopyWorkspace"));             
        popupItem.add(createJMenuItem());
        popup.add(popupItem);
        
        menu.add(createJMenuItem());
    }
    
    private JMenuItem createJMenuItem(){
        String name = NbBundle.getMessage(CopyPeptides.class, "CopyPeptidesToWorkspace.newWorkspace.name");
        JMenuItem item = new JMenuItem(name);
        item.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {                
                SwingWorker worker = new CopyPeptidesWorker(pc.getAttributesModel().getPeptides());
                WindowManager.getDefault().getMainWindow().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));  
                worker.execute();                              
            }
        });  
        return item;
    }

    @Override
    public JMenuItem getMenuPresenter() {
        return menu;
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        oldWs.removePropertyChangeListener(this);
        newWs.addPropertyChangeListener(this);
    }

    @Override
    public void resultChanged(LookupEvent le) {
        Collection<? extends Workspace> workspaces = lkpResult.allInstances();
        popup.removeAll();
        menu.removeAll();

        popupMap.clear();
        menuMap.clear();
        populateMenuItems();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        String propertyName = evt.getPropertyName();
        JMenuItem item;
        if (propertyName.equals(Workspace.PRO_NAME)) {
            Workspace ws = (Workspace) evt.getSource();
            item = popupMap.get(ws.getId());
            item.setText(evt.getNewValue().toString());
            item = menuMap.get(ws.getId());
            item.setText(evt.getNewValue().toString());
        }
    }

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.actions;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JPopupMenu;
import org.bapedis.core.project.ProjectManager;
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
import org.openide.util.Utilities;
import org.openide.util.actions.Presenter;

/**
 *
 * @author loge
 */
@ActionID(
        category = "File",
        id = "org.bapedis.core.ui.actions.RemoveWorkspace"
)
@ActionRegistration(
        displayName = "#CTL_RemoveWorkspace",
        lazy = false
)
@ActionReferences({
    @ActionReference(path = "Toolbars/Workspace", position = 200)
})
public class RemoveWorkspace extends AbstractAction implements LookupListener, Presenter.Toolbar {

    protected ProjectManager pc;
    protected Lookup.Result<Workspace> lkpResult;
    protected JPopupMenu popup;
    protected JButton dropDownButton;

    public RemoveWorkspace() {
        popup = new JPopupMenu();
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        lkpResult = pc.getLookup().lookupResult(Workspace.class);
        lkpResult.addLookupListener(this);
        putValue(SMALL_ICON, ImageUtilities.loadImageIcon("org/bapedis/core/resources/removeWorkspace.png", false));
        putValue(NAME, NbBundle.getMessage(RemoveCurrentWorkspace.class, "CTL_RemoveWorkspace"));

        dropDownButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon("org/bapedis/core/resources/removeWorkspace.png", false), popup);
        dropDownButton.setToolTipText(NbBundle.getMessage(RemoveWorkspace.class, "CTL_RemoveWorkspace"));
        dropDownButton.setEnabled(pc.getLookup().lookupAll(Workspace.class).size() > 1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
    }

    @Override
    public Component getToolbarPresenter() {
        List<? extends Action> actions = Utilities.actionsForPath("Actions/RemoveWorkspace");
        if (actions.size() > 0) {
            final Action defaultAction = actions.get(0);
            for (Action a : actions) {
                popup.add(a);
            }
            dropDownButton.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    defaultAction.actionPerformed(e);
                }
            });
        }
        return dropDownButton;
    }

    @Override
    public void resultChanged(LookupEvent le) {
        dropDownButton.setEnabled(lkpResult.allInstances().size() > 1);
    }

}

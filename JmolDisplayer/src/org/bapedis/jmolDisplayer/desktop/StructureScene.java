/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.jmolDisplayer.desktop;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.StringTokenizer;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.MetadataNode;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.jmol.api.JmolViewer;
import org.netbeans.core.api.multiview.MultiViewHandler;
import org.netbeans.core.api.multiview.MultiViews;
import org.netbeans.core.spi.multiview.CloseOperationState;
import org.netbeans.core.spi.multiview.MultiViewDescription;
import org.netbeans.core.spi.multiview.MultiViewElement;
import org.netbeans.core.spi.multiview.MultiViewElementCallback;
import org.bapedis.jmolDisplayer.model.StructureData;
import org.bapedis.jmolDisplayer.model.StructureSceneModel;
import org.openide.awt.UndoRedo;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;
import org.openscience.jmol.app.jmolpanel.JmolPanel;

/**
 *
 * @author loge
 */
public class StructureScene extends JPanel implements MultiViewElement, WorkspaceEventListener, PropertyChangeListener {

    private final JPanel toolBarPanel;
    private final JToolBar toolbar1;
    protected final JComboBox<String> strucCombo;
    protected DefaultComboBoxModel<String> strucComboModel;
    protected final JComboBox<String> displayCombo;
    protected final JCheckBox cbSpin;
    private final JPanel centerPanel;
    private JmolPanel jmolPanel;
    private MultiViewElementCallback callback;
    protected final Lookup lookup;
    protected final InstanceContent content;
    protected StructureSceneModel sceneModel;
    protected final ProjectManager pc;

    public StructureScene() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);

        setLayout(new BorderLayout());
        centerPanel = new JPanel(new BorderLayout());
        add(centerPanel, BorderLayout.CENTER);

        content = new InstanceContent();
        lookup = new AbstractLookup(content);

        toolBarPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        //Toolbar 1
        toolbar1 = new JToolBar();
        toolbar1.setFloatable(false);

        //Structures option
        strucComboModel = new DefaultComboBoxModel<>();
        strucCombo = new JComboBox<>(strucComboModel);
        strucCombo.setPreferredSize(new Dimension(76, 27));
        strucCombo.setToolTipText(NbBundle.getMessage(StructureScene.class, "StructureScene.strucCombo.tooltiptext"));
        strucCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                structureItemStateChanged(e);
            }
        });
        toolbar1.add(strucCombo);

        displayCombo = new JComboBox<>(new String[]{"Cartoons", "Spacefill", "Wire", "Ball and stick"});
        displayCombo.setToolTipText(NbBundle.getMessage(StructureScene.class, "StructureScene.displayCombo.tooltiptext"));
        displayCombo.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                displayItemStateChanged(e);
            }
        });
        displayCombo.setSelectedIndex(0);
        toolbar1.add(displayCombo);

        cbSpin = new JCheckBox("Spin");
        cbSpin.setToolTipText(NbBundle.getMessage(StructureScene.class, "StructureScene.spin.tooltiptext"));
        cbSpin.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                spinItemStateChanged(e);
            }
        });
        toolbar1.add(cbSpin);

        toolbar1.add(Box.createHorizontalGlue());

        toolbar1.addSeparator();
    }

    private void structureItemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED
                && sceneModel != null
                && !sceneModel.getStructure().equals(strucCombo.getSelectedItem())) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    sceneModel.setStructure((String) strucComboModel.getSelectedItem());
                }
            });

        }
    }

    private void loadStructure() {
        if (sceneModel != null) {
            StructureData item = sceneModel.getItem();
            String structure = sceneModel.getStructure();
            Peptide peptide = item.getPeptide();

            centerPanel.removeAll();
            toolBarPanel.removeAll();

            jmolPanel = new JmolPanel(null, null, centerPanel, 600, 600, "", new Point(200, 200));
            centerPanel.add(jmolPanel, BorderLayout.CENTER);

            JmolViewer viewer = jmolPanel.getViewer();
            viewer.script(getScript(structure));
            setDisplayOption();

            toolBarPanel.add(toolbar1);
            JToolBar toolbar2 = jmolPanel.getToolbar();
            toolbar2.setFloatable(false);
            toolBarPanel.add(toolbar2);

            centerPanel.revalidate();
            centerPanel.repaint();

            toolBarPanel.revalidate();
            toolBarPanel.repaint();

            StringTokenizer tokenizer;
            String name;
            
            for(MetadataNode node: item.getMetadataNode(null)){
                content.remove(node);
            }
            
            MetadataNode[] node = item.getMetadataNode(structure);
            if (node != null){
                content.add(node[0]);
            }
        }
    }

    private void displayItemStateChanged(ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED
                && sceneModel != null
                && sceneModel.getDisplayOption() != displayCombo.getSelectedIndex()) {
            sceneModel.setDisplayOption(displayCombo.getSelectedIndex());
        }
    }

    private void spinItemStateChanged(ItemEvent evt) {
        if (sceneModel != null) {
            JmolViewer viewer = jmolPanel.getViewer();
            if (evt.getStateChange() == ItemEvent.SELECTED) {
                viewer.script("spin on");
            } else {
                viewer.script("spin off");
            }
        }
    }

    private void setDisplayOption() {
        if (jmolPanel != null && sceneModel != null) {
            JmolViewer viewer = jmolPanel.getViewer();
            switch (sceneModel.getDisplayOption()) {
                case 0: //Cartoons
                    viewer.script("select protein; cartoons only; color structure;");
                    break;
                case 1: //Spacefill
                    viewer.script("select *; cartoons off; spacefill only; color cpk");
                    break;
                case 2://Wire
                    viewer.script("select *;cartoons off; wireframe -0.1; color cpk");
                    break;
                case 3://Ball and stick
                    viewer.script("select *; cartoons off; spacefill 23%; wireframe 0.15; color cpk");
                    break;
            }
        }
    }

    @Override
    public JComponent getVisualRepresentation() {
        return this;
    }

    @Override
    public JComponent getToolbarRepresentation() {
        return toolBarPanel;
    }

    @Override
    public Action[] getActions() {
        if (callback != null) {
            return callback.createDefaultActions();
        }
        return new Action[]{};
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void componentOpened() {
        pc.addWorkspaceEventListener(this);

        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);
    }

    @Override
    public void componentClosed() {
        pc.removeWorkspaceEventListener(this);
    }

    @Override
    public void componentShowing() {
    }

    @Override
    public void componentHidden() {
    }

    @Override
    public void componentActivated() {
    }

    private void reload() {
        if (sceneModel.getItem() == null) {
            centerPanel.removeAll();
            toolBarPanel.removeAll();

            centerPanel.revalidate();
            centerPanel.repaint();
            toolBarPanel.revalidate();
            toolBarPanel.repaint();                        
        } else {
            strucComboModel = new DefaultComboBoxModel<>();
            for (String structure : sceneModel.getItem().getStructures()) {
                strucComboModel.addElement(structure);
            }
            displayCombo.setSelectedIndex(sceneModel.getDisplayOption());
            strucComboModel.setSelectedItem(sceneModel.getStructure());
            strucCombo.setModel(strucComboModel);
            loadStructure();
        }

    }

    @Override
    public void componentDeactivated() {
    }

    @Override
    public UndoRedo getUndoRedo() {
        return UndoRedo.NONE;
    }

    @Override
    public void setMultiViewCallback(MultiViewElementCallback mvec) {
        this.callback = mvec;
    }

    @Override
    public CloseOperationState canCloseElement() {
        return CloseOperationState.STATE_OK;
    }

    public static String getScript(String code) {
        return "var xid = _modelTitle; if (xid.length != 4) { xid = '"
                + code
                + "'};load @{'=' + xid}";
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        if (oldWs != null) {
            StructureSceneModel oldModel = oldWs.getLookup().lookup(StructureSceneModel.class);
            if (oldModel != null) {
                oldModel.removePropertyChangeListener(this);
            }
        }

        sceneModel = newWs.getLookup().lookup(StructureSceneModel.class);
        if (sceneModel == null) {
            sceneModel = new StructureSceneModel();
            newWs.add(sceneModel);
        }
        sceneModel.addPropertyChangeListener(this);
        reload();
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(sceneModel)) {
            if (evt.getPropertyName().equals(StructureSceneModel.CHANGED_ITEM)) {
                reload();
            } else if (evt.getPropertyName().equals(StructureSceneModel.CHANGED_STRUCTURE)) {
                loadStructure();
            } else if (evt.getPropertyName().equals(StructureSceneModel.CHANGED_DISPLAY_OPTION)) {
                setDisplayOption();
            }
        }
    }
}

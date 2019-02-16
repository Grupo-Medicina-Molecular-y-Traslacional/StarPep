/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.model.StarPepAnnotationType;
import org.bapedis.core.model.StructureNavigatorModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.ui.StructureWindowController;
import org.jdesktop.swingx.JXBusyLabel;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.navigator.NavigatorPanelWithToolbar;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.Utilities;
import org.openide.util.lookup.AbstractLookup;
import org.openide.util.lookup.InstanceContent;

/**
 *
 * @author loge
 */
@NavigatorPanel.Registration(mimeType = "peptide/structure", displayName = "#StructureNavigator.name")
public class StructureNavigator extends javax.swing.JPanel implements WorkspaceEventListener,
        NavigatorPanelWithToolbar, LookupListener, PropertyChangeListener {

    protected final JToolBar toolBar;
    protected Lookup.Result<PeptideNode> peptideLkpResult;
    protected Lookup.Result<AttributesModel> attrModelLkpResult;
    protected AttributesModel currentModel;
    protected final ProjectManager pc;
    protected final InstanceContent content;
    protected final Lookup lookup;
    private ArrayList<String> codes;
    private int selectedIndex;
    protected final JXBusyLabel busyLabel;
    protected final JPanel strucPanel;
    protected final JButton next, prev;
    protected final JLabel structureLabel;
    protected final Map<String, JPanel> map;
    protected final StructureWindowController strucController;

    /**
     * Creates new form StructureNavigator
     */
    public StructureNavigator() {
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        strucController = Lookup.getDefault().lookup(StructureWindowController.class);

        initComponents();

        content = new InstanceContent();
        lookup = new AbstractLookup(content);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(StructureNavigator.class, "StructureNavigator.busyLabel.text"));

        strucPanel = new JPanel();
        scrollPane.setViewportView(strucPanel);

        // Tool bar
        toolBar = new JToolBar();
        toolBar.setFloatable(false);

        prev = new JButton();
        prev.setToolTipText(NbBundle.getMessage(StructureNavigator.class, "StructureNavigator.prevButton.toolTipText"));
        prev.setFocusable(true);
        prev.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/arrow-180.png", false));
        prev.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                prevStructure();
            }
        });
        toolBar.add(prev);

        structureLabel = new JLabel();
        toolBar.add(structureLabel);

        next = new JButton();
        next.setToolTipText(NbBundle.getMessage(StructureNavigator.class, "StructureNavigator.nextButton.toolTipText"));
        next.setFocusable(false);
        next.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/arrow.png", false));
        next.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                nextStructure();
            }
        });
        toolBar.add(next);

        codes = new ArrayList<>();
        selectedIndex = 0;        
        map = new HashMap<>();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPane = new javax.swing.JScrollPane();

        setLayout(new java.awt.BorderLayout());
        add(scrollPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    @Override
    public JComponent getToolbarComponent() {
        return toolBar;
    }

    @Override
    public String getDisplayName() {
        return NbBundle.getMessage(StructureNavigator.class,
                "StructureNavigator.name");
    }

    @Override
    public String getDisplayHint() {
        return NbBundle.getMessage(StructureNavigator.class,
                "StructureNavigator.hint");
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void panelActivated(Lookup lkp) {
        peptideLkpResult = Utilities.actionsGlobalContext().lookupResult(PeptideNode.class);
        peptideLkpResult.addLookupListener(this);

        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);

    }

    @Override
    public void panelDeactivated() {
        peptideLkpResult.removeLookupListener(this);
        removeAttrLookupListener();
        pc.removeWorkspaceEventListener(this);

        if (currentModel != null) {
            currentModel.removeQuickFilterChangeListener(this);
        }

        QueryModel queryModel = pc.getQueryModel();
        queryModel.removePropertyChangeListener(this);

        FilterModel filterModel = pc.getFilterModel();
        filterModel.removePropertyChangeListener(this);

        map.clear();
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        removeAttrLookupListener();
        if (oldWs != null) {
            AttributesModel oldAttrModel = pc.getAttributesModel(oldWs);
            if (oldAttrModel != null) {
                oldAttrModel.removeQuickFilterChangeListener(this);
            }

            QueryModel oldQueryModel = pc.getQueryModel(oldWs);
            oldQueryModel.removePropertyChangeListener(this);

            FilterModel oldFilterModel = pc.getFilterModel(oldWs);
            oldFilterModel.removePropertyChangeListener(this);
        }

        attrModelLkpResult = newWs.getLookup().lookupResult(AttributesModel.class);
        attrModelLkpResult.addLookupListener(this);

        QueryModel queryModel = pc.getQueryModel(newWs);
        queryModel.addPropertyChangeListener(this);

        FilterModel filterModel = pc.getFilterModel(newWs);
        filterModel.addPropertyChangeListener(this);

        currentModel = pc.getAttributesModel(newWs);
        if (currentModel != null) {
            currentModel.addQuickFilterChangeListener(this);
        }

        clear();
        reload();
    }
    
    private void clear() {
        codes.clear();
        selectedIndex = 0;
    }

    private void nextStructure() {
        if (selectedIndex < codes.size() - 1) {
            selectedIndex++;
            reload();
        } else {
            throw new IllegalStateException("Invalid next structure");
        }        
    }

    private void prevStructure() {
        if (selectedIndex > 0) {
            selectedIndex--;
            reload();
        } else {
            throw new IllegalStateException("Invalid previous structure");
        }                
    }

    private void removeAttrLookupListener() {
        if (attrModelLkpResult != null) {
            attrModelLkpResult.removeLookupListener(this);
            attrModelLkpResult = null;
        }
    }

    private void setBusyLabel(boolean busy) {
        scrollPane.setViewportView(busy ? busyLabel : strucPanel);
        busyLabel.setBusy(busy);
        for (Component c : toolBar.getComponents()) {
            c.setEnabled(!busy);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(currentModel)) {
            if (evt.getPropertyName().equals(AttributesModel.CHANGED_FILTER)) {
                clear();
                reload();
            }
        } else if (evt.getSource() instanceof QueryModel) {
            if (evt.getPropertyName().equals(QueryModel.RUNNING)) {
                setBusyLabel(((QueryModel) evt.getSource()).isRunning());
            }
        } else if (evt.getSource() instanceof FilterModel) {
            if (evt.getPropertyName().equals(FilterModel.RUNNING)) {
                setBusyLabel(((FilterModel) evt.getSource()).isRunning());
            }
        }
    }

    private void reload() {
        if (codes.isEmpty()) {
            structureLabel.setText("");
            next.setEnabled(false);
            prev.setEnabled(false);
            strucPanel.removeAll();
        } else {
            final String code = codes.get(selectedIndex);
            structureLabel.setText(String.format("%s (%d/%d)", code, selectedIndex + 1, codes.size()));
            next.setEnabled(selectedIndex < codes.size() -1);
            prev.setEnabled(selectedIndex > 0);

            strucPanel.removeAll();
            if (map.containsKey(code)) {
                strucPanel.add(BorderLayout.CENTER, map.get(code));
            } else {
                strucController.createPanelView(strucPanel, code);
                BorderLayout layout = (BorderLayout)strucPanel.getLayout();                
                JPanel panel = (JPanel) layout.getLayoutComponent(BorderLayout.CENTER);
                map.put(code, panel);
            }
        }
        strucPanel.revalidate();
        strucPanel.repaint();
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(attrModelLkpResult)) {
            Collection<? extends AttributesModel> attrModels = attrModelLkpResult.allInstances();
            if (!attrModels.isEmpty()) {
                if (currentModel != null) {
                    currentModel.removeQuickFilterChangeListener(this);
                }
                currentModel = attrModels.iterator().next();
                currentModel.addQuickFilterChangeListener(this);
                clear();
                reload();
            }
        } else if (le.getSource().equals(peptideLkpResult)) {
            Collection<? extends PeptideNode> peptideNodes = peptideLkpResult.allInstances();
            clear();
            if (!peptideNodes.isEmpty()) {                
                Peptide peptide = peptideNodes.iterator().next().getPeptide();
                String[] crossRefs = peptide.getAnnotationValues(StarPepAnnotationType.CROSSREF);
                StringTokenizer tokenizer;
                String db, code;
                for (String crossRef : crossRefs) {
                    tokenizer = new StringTokenizer(crossRef, ":");
                    db = tokenizer.nextToken();
                    if (db.equals("PDB")) {
                        code = tokenizer.nextToken();
                        codes.add(code.trim());
                    }
                }
                reload();
            }
        }
    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.BorderLayout;
import java.awt.event.ItemEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JToolBar;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AnnotationType;
import org.bapedis.core.model.AnnotationTypeChildFactory;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.services.ProjectManager;
import org.openide.explorer.ExplorerManager;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.netbeans.spi.navigator.NavigatorPanel;
import org.netbeans.spi.navigator.NavigatorPanelWithToolbar;
import org.netbeans.swing.outline.Outline;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.BeanTreeView;
import org.openide.explorer.view.OutlineView;

/**
 *
 * @author loge
 */
@NavigatorPanel.Registration(mimeType = "peptide/metadata", displayName = "#MetadataNavigator.name")
public class MetadataNavigator extends JComponent implements ExplorerManager.Provider,
        WorkspaceEventListener, PropertyChangeListener, LookupListener, NavigatorPanelWithToolbar {

    protected final ExplorerManager explorerMgr;
    private final DefaultComboBoxModel comboBoxModel;
    protected final ProjectManager pc;
    protected final Lookup lookup;
    protected Lookup.Result<AttributesModel> peptideLkpResult;
    protected AttributesModel currentModel;
    protected final JToolBar toolBar;
    protected final JCheckBox showAllCheckBox;
    protected final JComboBox comboBox;
    private boolean activated;

    /**
     * Creates new form LibraryPanel
     */
    public MetadataNavigator() {
        initComponents();
        explorerMgr = new ExplorerManager();

        BeanTreeView view = new BeanTreeView();
        view.setRootVisible(false);
        centerPanel.add(view, BorderLayout.CENTER);
        
        showAllCheckBox = new JCheckBox();
        showAllCheckBox.setSelected(true);
        showAllCheckBox.setText(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.showAllCheckBox.text"));
        showAllCheckBox.setToolTipText(NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.showAllCheckBox.toolTipText"));
        showAllCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showAllCheckBoxActionPerformed(evt);
            }
        });

        comboBoxModel = new DefaultComboBoxModel();
        String NO_SELECTION = NbBundle.getMessage(MetadataNavigator.class, "MetadataNavigator.choose.text");
        comboBoxModel.addElement(NO_SELECTION);

        for (AnnotationType aType : AnnotationType.values()) {
            comboBoxModel.addElement(new AnnotationItem(aType, showAllCheckBox.isSelected()));
        }

        comboBox = new JComboBox(comboBoxModel);
        comboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                comboBoxItemStateChanged(evt);
            }
        });
        comboBoxModel.setSelectedItem(NO_SELECTION);
        showAllCheckBox.setVisible(false);

        toolBar = new JToolBar();
        toolBar.add(comboBox);
        toolBar.add(showAllCheckBox);

        activated = false;
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);

        lookup = ExplorerUtils.createLookup(explorerMgr, getActionMap());
    }

    private void comboBoxItemStateChanged(java.awt.event.ItemEvent evt) {
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            if (comboBox.getSelectedItem() instanceof AnnotationItem) {
                AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();                
                showAllCheckBox.setVisible(true);
                showAllCheckBox.setSelected(item.isShowAll());
                if (!item.isShowAll() && item.isDirty()) {
                    item.reloadRootContext();
                }
                explorerMgr.setRootContext(item.getRootContext());
            } else {
                explorerMgr.setRootContext(Node.EMPTY);
                showAllCheckBox.setVisible(false);
            }
        }
    }

    private void showAllCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {
        if (comboBox.getSelectedItem() instanceof AnnotationItem) {
            AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();
            item.setShowAll(showAllCheckBox.isSelected());            
            item.reloadRootContext();
            explorerMgr.setRootContext(item.getRootContext());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        centerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.BorderLayout());

        centerPanel.setLayout(new java.awt.BorderLayout());
        add(centerPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerMgr;
    }

    private void removeLookupListener() {
        if (peptideLkpResult != null) {
            peptideLkpResult.removeLookupListener(this);
            peptideLkpResult = null;
        }
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        removeLookupListener();
        if (oldWs != null) {
            AttributesModel oldAttrModel = pc.getAttributesModel(oldWs);
            if (oldAttrModel != null) {
                oldAttrModel.removeQuickFilterChangeListener(this);

            }
        }
        peptideLkpResult = newWs.getLookup().lookupResult(AttributesModel.class
        );
        peptideLkpResult.addLookupListener(this);

        AttributesModel peptidesModel = pc.getAttributesModel(newWs);
        if (currentModel != null) {
            currentModel.removeQuickFilterChangeListener(this);
        }
        this.currentModel = peptidesModel;
        if (currentModel != null) {
            currentModel.addQuickFilterChangeListener(this);
        }
        setDirtyMetadata();
    }

    protected void setDirtyMetadata() {
        for (int i = 1; i < comboBoxModel.getSize(); i++) {
            AnnotationItem item = (AnnotationItem) comboBoxModel.getElementAt(i);
            if (!item.isShowAll()) {
                item.setDirty(true);
            }
        }
        if (activated && comboBox.getSelectedItem() instanceof AnnotationItem) {
            AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();
            if (!item.isShowAll()) {
                item.reloadRootContext();
                explorerMgr.setRootContext(item.getRootContext());
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(currentModel)
                && evt.getPropertyName().equals(AttributesModel.CHANGED_FILTER)) {
            setDirtyMetadata();
        }
    }

    @Override
    public void resultChanged(LookupEvent ev) {
        if (ev.getSource().equals(peptideLkpResult)) {
            setDirtyMetadata();
        }
    }

    @Override
    public String
            getDisplayName() {
        return NbBundle.getMessage(MetadataNavigator.class,
                "MetadataNavigator.name");
    }

    @Override
    public String
            getDisplayHint() {
        return NbBundle.getMessage(MetadataNavigator.class,
                "MetadataNavigator.hint");
    }

    @Override
    public JComponent getComponent() {
        return this;
    }

    @Override
    public void panelActivated(Lookup lkp) {
        activated = true;
        if (comboBox.getSelectedItem() instanceof AnnotationItem) {
            AnnotationItem item = (AnnotationItem) comboBox.getSelectedItem();
            if (!item.isShowAll() && item.isDirty()) {
                item.reloadRootContext();
                explorerMgr.setRootContext(item.getRootContext());
            }
        }
    }

    @Override
    public void panelDeactivated() {
        activated = false;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }

    @Override
    public JComponent getToolbarComponent() {
        return toolBar;

    }

    private class AnnotationItem{

        private final AnnotationType annotationType;
        private AbstractNode rootContext;
        private boolean dirty;
        private boolean showAll;

        public AnnotationItem(AnnotationType annotationType, boolean showAll) {
            rootContext = new AbstractNode(Children.create(new AnnotationTypeChildFactory(annotationType, showAll), true));
            this.annotationType = annotationType;
            dirty = false;
            this.showAll = showAll;
        }

        public boolean isShowAll() {
            return showAll;
        }

        public void setShowAll(boolean showAll) {
            this.showAll = showAll;
        }

        public boolean isDirty() {
            return dirty;
        }

        public void setDirty(boolean dirty) {
            this.dirty = dirty;
        }

        public void reloadRootContext() {
            rootContext = new AbstractNode(Children.create(new AnnotationTypeChildFactory(annotationType, showAll), true));
            if (!isShowAll()) {
                dirty = false;
            }
        }

        public AnnotationType getAnnotationType() {
            return annotationType;
        }

        public AbstractNode getRootContext() {
            return rootContext;
        }                

        @Override
        public String toString() {
            return annotationType.getDisplayName();
        }

    }
}

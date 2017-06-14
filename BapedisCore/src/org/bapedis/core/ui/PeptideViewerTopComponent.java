/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.PeptideNode;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.impl.AttributeFilter;
import org.bapedis.core.spi.filters.impl.FilterHelper;
import org.bapedis.core.spi.filters.impl.FilterOperator;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.etable.ETableColumnModel;
import org.netbeans.swing.etable.QuickFilter;
import org.netbeans.swing.outline.Outline;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.windows.WindowManager;
import org.jdesktop.swingx.JXBusyLabel;
import org.openide.awt.Mnemonics;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.bapedis.core.ui//PeptideViewer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "PeptideViewerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "editor", openAtStartup = true)
@ActionID(category = "Window", id = "org.bapedis.core.ui.PeptideViewerTopComponent")
@ActionReference(path = "Menu/Window" /*, position = 333 */)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_PeptideViewerAction",
        preferredID = "PeptideViewerTopComponent"
)
@Messages({
    "CTL_PeptideViewerAction=Peptide nodes",
    "CTL_PeptideViewerTopComponent=Peptide nodes",
    "HINT_PeptideViewerTopComponent=A peptide and neighbor nodes window"
})
public final class PeptideViewerTopComponent extends TopComponent implements
        ExplorerManager.Provider, WorkspaceEventListener, LookupListener, PropertyChangeListener {

    protected final ProjectManager pc;
    protected Lookup.Result<AttributesModel> peptideLkpResult;
    protected final ExplorerManager explorerMgr;
    protected final OutlineView view;

    protected final JXBusyLabel busyLabel;
    protected final JLabel errorLabel;

    public PeptideViewerTopComponent() {
        initComponents();
        setName(Bundle.CTL_PeptideViewerTopComponent());
        setToolTipText(Bundle.HINT_PeptideViewerTopComponent());
        explorerMgr = new ExplorerManager();
        view = new OutlineView(NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewer.nodelColumnLabel"));
        view.setPropertyColumns("seq", NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewer.nodelColumnLabel.seq"),
                "length", NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewer.nodelColumnLabel.length"));
        view.setQuickSearchAllowed(false);
        dataPanel.add(view, BorderLayout.CENTER);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        associateLookup(ExplorerUtils.createLookup(explorerMgr, getActionMap()));

        //Outline configuration
        final Outline outline = view.getOutline();
        outline.setPopupUsedFromTheCorner(true);
        outline.setRootVisible(false);
        outline.setColumnHidingAllowed(false);
        ETableColumnModel columnModel = (ETableColumnModel) outline.getColumnModel();
        //Node column
        ETableColumn column = (ETableColumn) columnModel.getColumn(0);
        column.setMaxWidth(120);
        column.setPreferredWidth(120);
        //Sequence column
        column = (ETableColumn) columnModel.getColumn(1);
        column.setMinWidth(240);
        //Length column
        column = (ETableColumn) columnModel.getColumn(2);
        column.setMaxWidth(240);
        column.setPreferredWidth(240);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        Mnemonics.setLocalizedText(busyLabel, org.openide.util.NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewer.busyLabel.text")); // NOI18N
        centerPanel.add(busyLabel, "busyCard");

        errorLabel = new JLabel(NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewer.errorLabel.text"), new ImageIcon(ImageUtilities.loadImage("org/bapedis/core/resources/sad.png", true)), JLabel.CENTER);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(errorLabel, "errorCard");

    }

    private void populateFilterFields(AttributesModel attrModel) {
        jFieldComboBox.removeAllItems();
        jLabelFilter.setVisible(attrModel != null);
        jFieldComboBox.setVisible(attrModel != null);
        jOperatorComboBox.setVisible(attrModel != null);
        jValueTextField.setVisible(attrModel != null);
        jAddButton.setVisible(attrModel != null);
        if (attrModel != null) {
            for (PeptideAttribute attr : attrModel.getAttributes()) {
                jFieldComboBox.addItem(attr);
            }
            if (jFieldComboBox.getItemCount() > 0) {
                jFieldComboBox.setSelectedIndex(0);
            }
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        topPanel = new javax.swing.JPanel();
        leftPanel = new javax.swing.JPanel();
        rightPanel = new javax.swing.JPanel();
        jLabelFilter = new javax.swing.JLabel();
        jFieldComboBox = new javax.swing.JComboBox();
        jOperatorComboBox = new javax.swing.JComboBox();
        jValueTextField = new javax.swing.JTextField();
        jAddButton = new javax.swing.JButton();
        centerPanel = new javax.swing.JPanel();
        dataPanel = new javax.swing.JPanel();

        setMinimumSize(new java.awt.Dimension(331, 250));
        setPreferredSize(new java.awt.Dimension(514, 152));
        setLayout(new java.awt.GridBagLayout());

        topPanel.setLayout(new java.awt.GridBagLayout());

        leftPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        topPanel.add(leftPanel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabelFilter, org.openide.util.NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jLabelFilter.text")); // NOI18N
        rightPanel.add(jLabelFilter);

        jFieldComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFieldComboBoxActionPerformed(evt);
            }
        });
        rightPanel.add(jFieldComboBox);

        rightPanel.add(jOperatorComboBox);

        jValueTextField.setText(org.openide.util.NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jValueTextField.text")); // NOI18N
        jValueTextField.setPreferredSize(new java.awt.Dimension(150, 26));
        jValueTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jValueTextFieldActionPerformed(evt);
            }
        });
        jValueTextField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                jValueTextFieldKeyTyped(evt);
            }
        });
        rightPanel.add(jValueTextField);

        jAddButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/add.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jAddButton, org.openide.util.NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jAddButton.text")); // NOI18N
        jAddButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jAddButtonActionPerformed(evt);
            }
        });
        rightPanel.add(jAddButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        topPanel.add(rightPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        add(topPanel, gridBagConstraints);

        centerPanel.setLayout(new java.awt.CardLayout());

        dataPanel.setLayout(new java.awt.BorderLayout());
        centerPanel.add(dataPanel, "dataCard");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(centerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddButtonActionPerformed
        applyFilter();
    }//GEN-LAST:event_jAddButtonActionPerformed

    private void applyFilter() {
        FilterOperator operator = (FilterOperator) jOperatorComboBox.getSelectedItem();
        PeptideAttribute attr = (PeptideAttribute) jFieldComboBox.getSelectedItem();
        if (attr != null && operator != null) {
            if (operator.isValid(jValueTextField.getText())) {
                Filter filter = new AttributeFilter(attr, operator, jValueTextField.getText());
                FilterModel filterModel = pc.getFilterModel();
                filterModel.addFilter(filter);
                jValueTextField.setText("");
                TopComponent tc = WindowManager.getDefault().findTopComponent("FilterExplorerTopComponent");
                tc.open();
                tc.requestActive();
            } else {
                String errorMsg = NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jValueTextField.badinput", attr.getDisplayName());
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(errorMsg, NotifyDescriptor.ERROR_MESSAGE));
            }
        }
    }


    private void jValueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jValueTextFieldActionPerformed
        applyFilter();
    }//GEN-LAST:event_jValueTextFieldActionPerformed

    private void jValueTextFieldKeyTyped(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jValueTextFieldKeyTyped
        FilterOperator operator = (FilterOperator) jOperatorComboBox.getSelectedItem();
        char c = evt.getKeyChar();
        if (operator != null) {
            if (c != KeyEvent.VK_BACK_SPACE && c != KeyEvent.VK_DELETE
                    && !operator.isValid(jValueTextField.getText() + c)) {
                evt.consume();
                getToolkit().beep();
            }
        }
    }//GEN-LAST:event_jValueTextFieldKeyTyped

    private void jFieldComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFieldComboBoxActionPerformed
        jOperatorComboBox.removeAllItems();
        PeptideAttribute attr = (PeptideAttribute) jFieldComboBox.getSelectedItem();
        if (attr != null) {
            FilterOperator[] operators = FilterHelper.getOperators(attr.getType());
            for (FilterOperator operator : operators) {
                jOperatorComboBox.addItem(operator);
            }     // TODO add your handling code here:
        }
    }//GEN-LAST:event_jFieldComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JPanel dataPanel;
    private javax.swing.JButton jAddButton;
    private javax.swing.JComboBox jFieldComboBox;
    private javax.swing.JLabel jLabelFilter;
    private javax.swing.JComboBox jOperatorComboBox;
    private javax.swing.JTextField jValueTextField;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        pc.addWorkspaceEventListener(this);
        Workspace currentWorkspace = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWorkspace);
    }

    private void removeLookupListener() {
        if (peptideLkpResult != null) {
            peptideLkpResult.removeLookupListener(this);
            peptideLkpResult = null;
        }
    }

    public void setBusyLabel(boolean busy) {
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        busyLabel.setBusy(busy);
        cl.show(centerPanel, busy ? "busyCard" : "dataCard");
        topPanel.setVisible(!busy);
    }

    public void setErrorLabel() {
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        busyLabel.setBusy(false);
        cl.show(centerPanel, "errorCard");
        topPanel.setVisible(false);
    }

    @Override
    public void componentClosed() {
        removeLookupListener();
        pc.removeWorkspaceEventListener(this);
        FilterModel filterModel = pc.getFilterModel();
        filterModel.removePropertyChangeListener(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        removeLookupListener();
        if (oldWs != null) {
            FilterModel oldFilterModel = pc.getFilterModel(oldWs);
            if (oldFilterModel != null) {
                oldFilterModel.removePropertyChangeListener(this);
            }
        }
        peptideLkpResult = newWs.getLookup().lookupResult(AttributesModel.class);
        peptideLkpResult.addLookupListener(this);
        

        AttributesModel peptidesModel = newWs.getLookup().lookup(AttributesModel.class);
        setData(peptidesModel);
        FilterModel filterModel = pc.getFilterModel(newWs);
        filterModel.addPropertyChangeListener(this);
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)) {
            Collection<? extends AttributesModel> attrModels = peptideLkpResult.allInstances();
            if (!attrModels.isEmpty()) {
                AttributesModel attrModel = attrModels.iterator().next();
                setData(attrModel);
            }
        } 
    }

    protected void setData(AttributesModel attrModel) {
        populateFilterFields(attrModel);
        if (attrModel != null) {
            explorerMgr.setRootContext(attrModel.getRootNode());
        } else {
            explorerMgr.setRootContext(Node.EMPTY);
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerMgr;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof FilterModel) {
            final FilterModel filterModel = (FilterModel) evt.getSource();
            if (filterModel.isEmpty()) {
                view.getOutline().unsetQuickFilter();
            } else {
                view.getOutline().setQuickFilter(0, new QuickFilter() {

                    @Override
                    public boolean accept(Object o) {
                        PeptideNode node = ((PeptideNode) o);
                        Peptide peptide = node.getPeptide();
                        return filterModel.accept(peptide);
                    }
                });
            }
        }
    }

}

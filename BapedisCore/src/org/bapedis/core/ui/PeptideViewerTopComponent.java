/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FilterModel;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.QueryModel;
import org.bapedis.core.spi.filters.Filter;
import org.bapedis.core.spi.filters.impl.AttributeFilter;
import org.bapedis.core.spi.filters.impl.FilterHelper;
import org.bapedis.core.spi.filters.impl.FilterOperator;
import org.bapedis.core.ui.actions.FeatureExplorerAction;
import org.bapedis.core.ui.actions.FeatureExtractionAction;
import org.bapedis.core.ui.actions.FeatureFilteringAction;
import org.bapedis.core.ui.actions.FeatureRemovingAction;
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
import org.openide.awt.DropDownButtonFactory;
import org.openide.explorer.view.NodePopupFactory;
import org.openide.util.Utilities;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;

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
@ActionReference(path = "Menu/Window" , position = 233 )
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
    protected AttributesModel currentModel;
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
//        view.setPropertyColumns(PeptideDAO.SEQ.getId(), PeptideDAO.SEQ.getDisplayName());
//        "length", NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewer.nodelColumnLabel.length"
        view.setQuickSearchAllowed(false);
        NodePopupFactory npf = new NodePopupFactory();
        npf.setShowQuickFilter(false);
        view.setNodePopupFactory(npf);
        scrollPane.setViewportView(view);
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        associateLookup(new ProxyLookup(ExplorerUtils.createLookup(explorerMgr, getActionMap()),
                Lookups.singleton(new MetadataNavigatorLookupHint()), Lookups.singleton(new GraphElementNavigatorLookupHint())));

        //Outline configuration
        final Outline outline = view.getOutline();
        outline.setPopupUsedFromTheCorner(true);
        outline.setRootVisible(false);
        outline.setColumnHidingAllowed(false);
//        ETableColumnModel columnModel = (ETableColumnModel) outline.getColumnModel();
//        //Hide node column
//        ETableColumn column = (ETableColumn) columnModel.getColumn(0);
//        columnModel.setColumnHidden(column, true);

//        column.setMaxWidth(120);
//        column.setPreferredWidth(120);
//        column.setMinWidth(60);
        //Sequence column
//        column = (ETableColumn) columnModel.getColumn(1);
//        column.setMinWidth(240);
        //Length column
//        column = (ETableColumn) columnModel.getColumn(2);
//        column.setMaxWidth(240);
//        column.setPreferredWidth(240);
        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewer.busyLabel.text"));
        centerPanel.add(busyLabel, "busyCard");

        errorLabel = new JLabel(NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewer.errorLabel.text"), new ImageIcon(ImageUtilities.loadImage("org/bapedis/core/resources/sad.png", true)), JLabel.CENTER);
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        centerPanel.add(errorLabel, "errorCard");

        // Left toolbar: Feature Extraction        
        leftToolBar.add(createAddMDButton(), 0);
        
        // Left toolbar: Feature Removing
        JButton removeMDButton = new JButton(new FeatureRemovingAction());
        removeMDButton.setToolTipText(NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.removeMDButton.toolTipText"));
        removeMDButton.setIcon(new ImageIcon(getClass().getResource("/org/bapedis/core/resources/delete_md.gif")));
        removeMDButton.setFocusable(false);
        leftToolBar.add(removeMDButton, 1);
        
        // Left toolbar: Feature filtering
        JButton filterMDButton = new JButton(new FeatureFilteringAction());
        filterMDButton.setToolTipText(NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.filterMDButton.toolTipText"));
        filterMDButton.setIcon(new ImageIcon(getClass().getResource("/org/bapedis/core/resources/filter_md.png")));
        filterMDButton.setFocusable(false);
        leftToolBar.add(filterMDButton, 2);
        
        // Left toolbar: Feature explorer
        JButton columnsButton = new JButton(new FeatureExplorerAction());
        columnsButton.setToolTipText(NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.columnsButton.toolTipText"));
        columnsButton.setIcon(new ImageIcon(getClass().getResource("/org/bapedis/core/resources/select_md.png")));
        columnsButton.setFocusable(false);
        leftToolBar.add(columnsButton, 3);
        
        // Left toolbar: Export button
        leftToolBar.add(createExportButton(), 4);
    }

    private JButton createAddMDButton() {
        FeatureExtractionAction mdAction = new FeatureExtractionAction();
        JMenu menu = (JMenu) mdAction.getMenuPresenter();
        final JPopupMenu popup = menu.getPopupMenu();

        final JButton dropDownButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon("org/bapedis/core/resources/add_md.gif", false), popup);
        dropDownButton.setToolTipText(NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.addMD.tooltiptext"));
        dropDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dropDownButton.isEnabled()) {
                    popup.show(dropDownButton, 0, dropDownButton.getHeight());
                }
            }
        });
        return dropDownButton;
    }

    private JButton createExportButton() {
        final JPopupMenu popup = new JPopupMenu();

        List<? extends Action> actions = Utilities.actionsForPath("Actions/ExportPeptides");
        for (Action action : actions) {
            popup.add(action);
        }

        final JButton dropDownButton = DropDownButtonFactory.createDropDownButton(ImageUtilities.loadImageIcon("org/bapedis/core/resources/export.png", false), popup);
        dropDownButton.setToolTipText(NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.export.tooltiptext"));
        dropDownButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (dropDownButton.isEnabled()) {
                    popup.show(dropDownButton, 0, dropDownButton.getHeight());
                }
            }
        });
        return dropDownButton;
    }

    private void populateVisibleColumns(AttributesModel attrModel) {
        if (attrModel != null) {
            List<String> columns = new LinkedList<>();
            for (PeptideAttribute attr : attrModel.getDisplayedColumns()) {
                columns.add(attr.getId());
                columns.add(attr.getDisplayName());
            }
            view.setPropertyColumns(columns.toArray(new String[0]));
            //Hide node column
            ETableColumnModel columnModel = (ETableColumnModel) view.getOutline().getColumnModel();
            ETableColumn column = (ETableColumn) columnModel.getColumn(0);
            columnModel.setColumnHidden(column, true);
//            ETableColumnModel columnModel = (ETableColumnModel) view.getOutline().getColumnModel();
//            ETableColumn column;
//            for (PeptideAttribute attr : attrs) {
//                column = (ETableColumn) view.getOutline().getColumn(attr.getId());
//                columnModel.setColumnHidden(column, !attr.isVisible());
//            }
        } else {
            view.setPropertyColumns(new String[]{});
        }

    }

    private void populateFilterFields(AttributesModel attrModel) {
        jFieldComboBox.removeAllItems();
        jLabelFilter.setVisible(attrModel != null);
        jFieldComboBox.setVisible(attrModel != null);
        jOperatorComboBox.setVisible(attrModel != null);
        jValueTextField.setVisible(attrModel != null);
        jAddButton.setVisible(attrModel != null);
        if (attrModel != null) {
            for (PeptideAttribute attr : attrModel.getDisplayedColumns()) {
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
        leftToolBar = new javax.swing.JToolBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        detailsPanel = new javax.swing.JPanel();
        jPeptideLabel = new javax.swing.JLabel();
        jFetchedLabel = new javax.swing.JLabel();
        jFilteredLabel = new javax.swing.JLabel();
        rightPanel = new javax.swing.JPanel();
        jLabelFilter = new javax.swing.JLabel();
        jFieldComboBox = new javax.swing.JComboBox();
        jOperatorComboBox = new javax.swing.JComboBox();
        jValueTextField = new javax.swing.JTextField();
        jAddButton = new javax.swing.JButton();
        centerPanel = new javax.swing.JPanel();
        scrollPane = new javax.swing.JScrollPane();

        setMinimumSize(new java.awt.Dimension(331, 250));
        setPreferredSize(new java.awt.Dimension(514, 152));
        setLayout(new java.awt.GridBagLayout());

        topPanel.setLayout(new java.awt.GridBagLayout());

        leftToolBar.setFloatable(false);
        leftToolBar.setRollover(true);
        leftToolBar.add(jSeparator1);

        detailsPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        org.openide.awt.Mnemonics.setLocalizedText(jPeptideLabel, org.openide.util.NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jPeptideLabel.text")); // NOI18N
        detailsPanel.add(jPeptideLabel);

        jFetchedLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/rightArrow.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jFetchedLabel, org.openide.util.NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jFetchedLabel.text")); // NOI18N
        detailsPanel.add(jFetchedLabel);

        jFilteredLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/rightArrow.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jFilteredLabel, org.openide.util.NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jFilteredLabel.text")); // NOI18N
        detailsPanel.add(jFilteredLabel);

        leftToolBar.add(detailsPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        topPanel.add(leftToolBar, gridBagConstraints);

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
        centerPanel.add(scrollPane, "dataCard");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(centerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jAddButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jAddButtonActionPerformed
        addNewFilter();
    }//GEN-LAST:event_jAddButtonActionPerformed

    private void addNewFilter() {
        FilterOperator operator = (FilterOperator) jOperatorComboBox.getSelectedItem();
        PeptideAttribute attr = (PeptideAttribute) jFieldComboBox.getSelectedItem();
        if (attr != null && operator != null) {
            if (operator.isValid(jValueTextField.getText())) {
                TopComponent tc = WindowManager.getDefault().findTopComponent("FilterExplorerTopComponent");
                tc.open();
                tc.requestActive();

                Filter filter = new AttributeFilter(attr, operator, jValueTextField.getText());
                FilterModel filterModel = pc.getFilterModel();
                filterModel.add(filter);
                jValueTextField.setText("");
            } else {
                String errorMsg = NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jValueTextField.badinput", attr.getDisplayName());
                DialogDisplayer.getDefault().notify(new NotifyDescriptor.Message(errorMsg, NotifyDescriptor.ERROR_MESSAGE));
            }
        }
    }

    private void jValueTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jValueTextFieldActionPerformed
        addNewFilter();
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
    private javax.swing.JPanel detailsPanel;
    private javax.swing.JButton jAddButton;
    private javax.swing.JLabel jFetchedLabel;
    private javax.swing.JComboBox jFieldComboBox;
    private javax.swing.JLabel jFilteredLabel;
    private javax.swing.JLabel jLabelFilter;
    private javax.swing.JComboBox jOperatorComboBox;
    private javax.swing.JLabel jPeptideLabel;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JTextField jValueTextField;
    private javax.swing.JToolBar leftToolBar;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JScrollPane scrollPane;
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

    private void setBusyLabel(boolean busy) {
        makeBusy(busy);
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        busyLabel.setBusy(busy);
        cl.show(centerPanel, busy ? "busyCard" : "dataCard");
        topPanel.setVisible(!busy);
    }

    private void setErrorLabel() {
        CardLayout cl = (CardLayout) centerPanel.getLayout();
        busyLabel.setBusy(false);
        cl.show(centerPanel, "errorCard");
        topPanel.setVisible(false);
    }

    @Override
    public void componentClosed() {
        removeLookupListener();
        pc.removeWorkspaceEventListener(this);        
        if (currentModel != null) {
            currentModel.removeQuickFilterChangeListener(this);
        }
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
            AttributesModel oldAttrModel = pc.getAttributesModel(oldWs);
            if (oldAttrModel != null) {
                oldAttrModel.removeQuickFilterChangeListener(this);
            }
            QueryModel oldQueryModel = pc.getQueryModel(oldWs);
            oldQueryModel.removePropertyChangeListener(this);

            FilterModel oldFilterModel = pc.getFilterModel(oldWs);
            oldFilterModel.removePropertyChangeListener(this);

        }
        peptideLkpResult = newWs.getLookup().lookupResult(AttributesModel.class);
        peptideLkpResult.addLookupListener(this);

        QueryModel queryModel = pc.getQueryModel(newWs);
        queryModel.addPropertyChangeListener(this);

        FilterModel filterModel = pc.getFilterModel(newWs);
        filterModel.addPropertyChangeListener(this);

        setBusyLabel(queryModel.isRunning() || filterModel.isRunning());

        AttributesModel peptidesModel = pc.getAttributesModel(newWs);
        setData(peptidesModel);
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)) {
            Collection<? extends AttributesModel> attrModels = peptideLkpResult.allInstances();
            if (!attrModels.isEmpty()) {
                if (currentModel != null) {
                    currentModel.removeQuickFilterChangeListener(this);
                    currentModel.removeDisplayColumnChangeListener(this);
                }
                final AttributesModel attrModel = attrModels.iterator().next();
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        setData(attrModel);
                    }
                });

            }
        }
    }

    private void setData(AttributesModel attrModel) {
        this.currentModel = attrModel;
        populateVisibleColumns(attrModel);
        populateFilterFields(attrModel);
        explorerMgr.setRootContext(currentModel == null ? Node.EMPTY : currentModel.getRootNode());
        setQuickFilter();
        if (currentModel != null) {
            currentModel.addQuickFilterChangeListener(this);
            currentModel.addDisplayColumnChangeListener(this);
            int fetchedData = currentModel.getNodeList().size();
            jFetchedLabel.setText(NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jFetchedLabel.text", fetchedData));
            int filteredData = currentModel.getPeptides().size();            
            jFilteredLabel.setText(NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jFilteredLabel.text", filteredData));
            jFilteredLabel.setVisible(fetchedData != filteredData);
        }
        leftToolBar.setVisible(attrModel != null);
    }

    private void setQuickFilter() {
        QuickFilter quickFilter = currentModel == null ? null : currentModel.getQuickFilter();
        if (quickFilter != null) {
            view.getOutline().setQuickFilter(0, quickFilter);
            int filteredData = currentModel.getPeptides().size();
            jFilteredLabel.setText(NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jFilteredLabel.text", filteredData));
            jFilteredLabel.setVisible(currentModel.getNodeList().size() != filteredData);
        } else {
            view.getOutline().unsetQuickFilter();
            jFilteredLabel.setVisible(false);
        }
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return explorerMgr;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(currentModel)) {
            if (evt.getPropertyName().equals(AttributesModel.CHANGED_FILTER)) {
                setQuickFilter();
            } else if (evt.getPropertyName().equals(AttributesModel.DISPLAY_ATTR_ADDED)) {
                PeptideAttribute attr = (PeptideAttribute) evt.getNewValue();
                jFieldComboBox.addItem(attr);
                populateVisibleColumns(currentModel);
            } else if (evt.getPropertyName().equals(AttributesModel.DISPLAY_ATTR_REMOVED)) {
                PeptideAttribute attr = (PeptideAttribute) evt.getOldValue();
                jFieldComboBox.removeItem(attr);
                populateVisibleColumns(currentModel);
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

}

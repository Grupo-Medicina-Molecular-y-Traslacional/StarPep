/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Collection;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bapedis.core.services.ProjectManager;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.ui.components.PeptideViewer;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.filters.impl.FilterHelper;
import org.bapedis.core.spi.filters.impl.FilterOperator;
import org.netbeans.api.settings.ConvertAsProperties;
import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.etable.ETableColumnModel;
import org.netbeans.swing.outline.Outline;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.DropDownButtonFactory;
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
        ExplorerManager.Provider, WorkspaceEventListener, LookupListener {

    protected ProjectManager pc;
    protected Lookup.Result<AttributesModel> peptideLkpResult;
    protected final ExplorerManager explorerMgr;
    protected final OutlineView view;

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

        //Populate filter panel
        populateFilterFields();
        rightPanel.add(createDropDownButtonSearch());
        jValueTextField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                applyPrimaryFilter();
            }

            public void removeUpdate(DocumentEvent e) {
                applyPrimaryFilter();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });
        
    }

    private void populateFilterFields() {
        jFieldComboBox.addItem(new PeptideAttribute("id", "ID", String.class));
        jFieldComboBox.addItem(new PeptideAttribute("seq", "Sequence", String.class));
        jFieldComboBox.addItem(new PeptideAttribute("length", "Lenght", Integer.class));
        jFieldComboBox.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jOperatorComboBox.removeAllItems();
                PeptideAttribute attr = (PeptideAttribute) jFieldComboBox.getSelectedItem();
                FilterOperator[] operators = FilterHelper.getOperators(attr.getType());
                for (FilterOperator operator : operators) {
                    jOperatorComboBox.addItem(operator);
                }
            }
        });
        jFieldComboBox.setSelectedIndex(0);
    }
    
   private JButton createDropDownButtonSearch() {
        final JPopupMenu filterPopup = new JPopupMenu();
        JMenuItem item = new JMenuItem("add");
        filterPopup.add(item);
        item = new JMenuItem("remove");
        filterPopup.add(item);
        
        Image iconImage = ImageUtilities.loadImage("org/bapedis/core/resources/search.png");
        ImageIcon icon = new ImageIcon(iconImage);
        final JButton dropDownButton = DropDownButtonFactory.createDropDownButton(new ImageIcon(
                new BufferedImage(16, 16, BufferedImage.TYPE_BYTE_GRAY)), filterPopup);

        dropDownButton.setIcon(icon);
//        dropDownButton.setMargin(new java.awt.Insets(2, 4, 0, 4));
        dropDownButton.setToolTipText(NbBundle.getMessage(PeptideViewer.class, "CTL_QuickFilter"));
        dropDownButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                filterPopup.show(dropDownButton, 0, dropDownButton.getHeight());
            }
        });
        return dropDownButton;
    }
   
    private void applyPrimaryFilter() {
//       if value is empty view.set filter: filtermodel
//       else view.set filter: online filter (field, operator, value)
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
        jLabel2 = new javax.swing.JLabel();
        jFieldComboBox = new javax.swing.JComboBox();
        jOperatorComboBox = new javax.swing.JComboBox();
        jValueTextField = new javax.swing.JTextField();
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

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jLabel2.text")); // NOI18N
        rightPanel.add(jLabel2);

        rightPanel.add(jFieldComboBox);

        rightPanel.add(jOperatorComboBox);

        jValueTextField.setText(org.openide.util.NbBundle.getMessage(PeptideViewerTopComponent.class, "PeptideViewerTopComponent.jValueTextField.text")); // NOI18N
        jValueTextField.setPreferredSize(new java.awt.Dimension(150, 26));
        rightPanel.add(jValueTextField);

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

        dataPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(dataPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dataPanel;
    private javax.swing.JComboBox jFieldComboBox;
    private javax.swing.JLabel jLabel2;
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

    public void setBusyLabel() {
        //todo
    }

    public void setErrorLabel() {
        //todo
    }

    @Override
    public void componentClosed() {
        removeLookupListener();
        pc.removeWorkspaceEventListener(this);
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
        peptideLkpResult = newWs.getLookup().lookupResult(AttributesModel.class);
        peptideLkpResult.addLookupListener(this);
        AttributesModel peptidesModel = newWs.getLookup().lookup(AttributesModel.class);
        showData(peptidesModel);
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)) {
            Collection<? extends AttributesModel> attrModels = peptideLkpResult.allInstances();
            if (!attrModels.isEmpty()) {
                AttributesModel attrModel = attrModels.iterator().next();
                showData(attrModel);
            }
        }
    }

    public void showData(AttributesModel attrModel) {
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

}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.util.HashMap;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableModel;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.PeptideAttribute;
import org.jdesktop.swingx.JXList;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author loge
 */
public class MolecularFeaturesPanel extends javax.swing.JPanel {

    protected final JList<MolecularDescriptor> leftList, rightList;
    protected final DefaultListModel<MolecularDescriptor> rightListModel;
    protected final AttributesModel attrModel;
    protected final JButton findButton;
    protected final String ALL_SELECTION;
    protected final HashMap<MolecularDescriptor, StatsPanel> map;
    protected final DescriptorSelectionPanel leftTable;
    protected static final String LAST_LEFT_VIEW = "last_left_view";

    /**
     * Creates new form MolecularFeaturesPanel
     *
     * @param attrModel
     */
    public MolecularFeaturesPanel(final AttributesModel attrModel) {
        initComponents();
        this.attrModel = attrModel;

        // List View
        leftList = new JXList();

        rightListModel = new DefaultListModel<>();
        rightList = new JList<>(rightListModel);
        rightScrollPane.setViewportView(rightList);

        // Fill combobox
        ALL_SELECTION = NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.chooseAll.text");
        DefaultComboBoxModel comboModel = (DefaultComboBoxModel) categoryComboBox.getModel();
        comboModel.addElement(ALL_SELECTION);
        comboModel.setSelectedItem(ALL_SELECTION);
        for (String key : attrModel.getMolecularDescriptorKeys()) {
            comboModel.addElement(key);
        }

        leftPanel.add(new JScrollPane(leftList), "list");

        // Table view
        leftTable = new DescriptorSelectionPanel(attrModel);
        leftTable.setMinimumSize(leftPanel.getMinimumSize());
        leftTable.setPreferredSize(leftPanel.getPreferredSize());
        leftTable.addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                TableModel model = (TableModel) e.getSource();
                boolean flag = false;
                for (int row = 0; row < model.getRowCount(); row++) {
                    if ((boolean) model.getValueAt(row, 0)) {
                        flag = true;
                        break;
                    }
                }
                addToDisplayButton.setEnabled(flag);
            }
        });

        leftPanel.add(new JScrollPane(leftTable), "table");

        // Add tool bar buttons
        findButton = new JButton(leftList.getActionMap().get("find"));
        findButton.setText("");
        findButton.setToolTipText(NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.findButton.toolTipText"));
        findButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/search.png", false));
        findButton.setFocusable(false);

        leftToolBar.add(findButton);

        // Fill displayed column list
        for (PeptideAttribute attr : attrModel.getDisplayedColumns()) {
            if (attr instanceof MolecularDescriptor) {
                rightListModel.addElement((MolecularDescriptor) attr);
            }
        }

        // Configure components...
        infoLabel.setVisible(!attrModel.canAddDisplayColumn());

        leftList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        rightList.clearSelection();
                        setStats(((DefaultListModel<MolecularDescriptor>) leftList.getModel()).get(leftList.getSelectedIndex()));
                        loadButton.setEnabled(true);
                    }
                    addToDisplayButton.setEnabled(!lsm.isSelectionEmpty() && attrModel.canAddDisplayColumn());
                }
            }
        });

        rightList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (!e.getValueIsAdjusting()) {
                    ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                    if (!lsm.isSelectionEmpty()) {
                        leftList.clearSelection();
                        setStats(rightListModel.get(rightList.getSelectedIndex()));
                        loadButton.setEnabled(true);
                    }
                    removeFromDisplayButton.setEnabled(!lsm.isSelectionEmpty());
                }
            }
        });

        removeFromDisplayButton.setEnabled(false);
        loadButton.setEnabled(false);
        featureTextField.setEnabled(false);
        map = new HashMap<>();
        
        // Default view 
        String leftView = NbPreferences.forModule(MolecularFeaturesPanel.class).get(LAST_LEFT_VIEW, "list");
        setLeftView(leftView);        
    }

    private void setLeftView(String leftView) {
        CardLayout cl = (CardLayout) leftPanel.getLayout();
        switch (leftView) {
            case "list":
                switcherComboBox.setSelectedIndex(0);
                categoryComboBox.setEnabled(true);
                findButton.setEnabled(true);
                cl.show(leftPanel, "list");
                addToDisplayButton.setEnabled(!leftList.getSelectionModel().isSelectionEmpty());
                sizeLabel.setText(NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.sizeLabel.text", leftList.getModel().getSize()));
                break;
            case "table":
                switcherComboBox.setSelectedIndex(1);
                categoryComboBox.setEnabled(false);
                findButton.setEnabled(false);
                cl.show(leftPanel, "table");
                sizeLabel.setText(NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.sizeLabel.text", leftTable.totalOfFeatures()));
                addToDisplayButton.setEnabled(leftTable.getSelectedDescriptorKeys().size() > 0);
                break;
        }
    }

    private void setStats(MolecularDescriptor attribute) {
        rightBottomPanel.removeAll();
        if (map.containsKey(attribute)) {
            featureTextField.setText(attribute.getDisplayName());
            featureTextField.setEnabled(true);
            rightBottomPanel.add(map.get(attribute), BorderLayout.CENTER);
        } else {
            featureTextField.setText("");
            featureTextField.setEnabled(false);
        }
        rightBottomPanel.revalidate();
        rightBottomPanel.repaint();
    }

    private void addToDisplayedColumns() {
        switch (switcherComboBox.getSelectedIndex()) {
            case 0:
                addToDisplayFromList();
                break;
            case 1:
                addToDisplayFromTable();
                break;
        }
        infoLabel.setVisible(!attrModel.canAddDisplayColumn());
    }

    private void addToDisplayFromList() {
        int[] indices = leftList.getSelectedIndices();
        DefaultListModel<MolecularDescriptor> leftListModel = (DefaultListModel<MolecularDescriptor>) leftList.getModel();
        for (int i = 0; i < indices.length && attrModel.canAddDisplayColumn(); i++) {
            if (rightListModel.indexOf(leftListModel.get(indices[i])) < 0) {
                attrModel.addDisplayedColumn(leftListModel.get(indices[i]));
                rightListModel.addElement(leftListModel.get(indices[i]));
            }

        }
    }

    private void addToDisplayFromTable() {
        Set<String> selected = leftTable.getSelectedDescriptorKeys();
        for (String key : selected) {
            for (MolecularDescriptor md : attrModel.getMolecularDescriptors(key)) {
                if (!attrModel.addDisplayedColumn(md)) {
                    return;
                }
                rightListModel.addElement(md);
            }
        }
    }

    private void removeFromDisplayedColumns() {
        int[] indices = rightList.getSelectedIndices();
        for (int i = indices.length - 1; i >= 0; i--) {
            attrModel.removeDisplayedColumn(rightListModel.get(indices[i]));
            rightListModel.removeElementAt(indices[i]);
        }
        infoLabel.setVisible(!attrModel.canAddDisplayColumn());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        infoLabel = new javax.swing.JLabel();
        rightUpperPanel = new javax.swing.JPanel();
        rightScrollPane = new javax.swing.JScrollPane();
        upperToolBar = new javax.swing.JToolBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        addToDisplayButton = new javax.swing.JButton();
        removeFromDisplayButton = new javax.swing.JButton();
        sizeLabel = new javax.swing.JLabel();
        leftPanel = new javax.swing.JPanel();
        leftToolBar = new javax.swing.JToolBar();
        switcherComboBox = new javax.swing.JComboBox<>();
        categoryComboBox = new javax.swing.JComboBox<>();
        rightBottomPanel = new javax.swing.JPanel();
        rightControlPanel = new javax.swing.JPanel();
        loadButton = new javax.swing.JButton();
        featureTextField = new javax.swing.JTextField();
        centerToolBar = new javax.swing.JToolBar();
        jSeparator2 = new javax.swing.JToolBar.Separator();

        setMinimumSize(new java.awt.Dimension(700, 480));
        setPreferredSize(new java.awt.Dimension(690, 480));
        setLayout(new java.awt.GridBagLayout());

        infoLabel.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        infoLabel.setForeground(java.awt.Color.red);
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.infoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(infoLabel, gridBagConstraints);

        rightUpperPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.rightUpperPanel.border.title"))); // NOI18N
        rightUpperPanel.setToolTipText(org.openide.util.NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.rightUpperPanel.toolTipText")); // NOI18N
        rightUpperPanel.setMaximumSize(new java.awt.Dimension(275, 2147483647));
        rightUpperPanel.setMinimumSize(new java.awt.Dimension(275, 113));
        rightUpperPanel.setPreferredSize(new java.awt.Dimension(275, 113));
        rightUpperPanel.setLayout(new java.awt.GridBagLayout());

        rightScrollPane.setMinimumSize(new java.awt.Dimension(275, 90));
        rightScrollPane.setPreferredSize(new java.awt.Dimension(275, 90));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        rightUpperPanel.add(rightScrollPane, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(rightUpperPanel, gridBagConstraints);

        upperToolBar.setFloatable(false);
        upperToolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        upperToolBar.setRollover(true);
        upperToolBar.setMinimumSize(new java.awt.Dimension(40, 102));
        upperToolBar.setPreferredSize(new java.awt.Dimension(40, 102));
        upperToolBar.add(jSeparator1);

        addToDisplayButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        addToDisplayButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/arrow.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(addToDisplayButton, org.openide.util.NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.addToDisplayButton.text")); // NOI18N
        addToDisplayButton.setToolTipText(org.openide.util.NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.addToDisplayButton.toolTipText")); // NOI18N
        addToDisplayButton.setFocusable(false);
        addToDisplayButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addToDisplayButton.setMaximumSize(new java.awt.Dimension(50, 21));
        addToDisplayButton.setMinimumSize(new java.awt.Dimension(23, 21));
        addToDisplayButton.setPreferredSize(new java.awt.Dimension(50, 21));
        addToDisplayButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addToDisplayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToDisplayButtonActionPerformed(evt);
            }
        });
        upperToolBar.add(addToDisplayButton);

        removeFromDisplayButton.setFont(new java.awt.Font("Tahoma", 1, 11)); // NOI18N
        removeFromDisplayButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/arrow-180.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(removeFromDisplayButton, org.openide.util.NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.removeFromDisplayButton.text")); // NOI18N
        removeFromDisplayButton.setToolTipText(org.openide.util.NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.removeFromDisplayButton.toolTipText")); // NOI18N
        removeFromDisplayButton.setFocusable(false);
        removeFromDisplayButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeFromDisplayButton.setMaximumSize(new java.awt.Dimension(50, 21));
        removeFromDisplayButton.setMinimumSize(new java.awt.Dimension(23, 21));
        removeFromDisplayButton.setPreferredSize(new java.awt.Dimension(50, 21));
        removeFromDisplayButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeFromDisplayButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFromDisplayButtonActionPerformed(evt);
            }
        });
        upperToolBar.add(removeFromDisplayButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        add(upperToolBar, gridBagConstraints);

        sizeLabel.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(sizeLabel, org.openide.util.NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.sizeLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(sizeLabel, gridBagConstraints);

        leftPanel.setMinimumSize(new java.awt.Dimension(275, 23));
        leftPanel.setPreferredSize(new java.awt.Dimension(275, 23));
        leftPanel.setLayout(new java.awt.CardLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(leftPanel, gridBagConstraints);

        leftToolBar.setFloatable(false);
        leftToolBar.setRollover(true);

        switcherComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "List view", "Table view" }));
        switcherComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                switcherComboBoxActionPerformed(evt);
            }
        });
        leftToolBar.add(switcherComboBox);

        categoryComboBox.setMinimumSize(new java.awt.Dimension(200, 27));
        categoryComboBox.setPreferredSize(new java.awt.Dimension(215, 27));
        categoryComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                categoryComboBoxActionPerformed(evt);
            }
        });
        leftToolBar.add(categoryComboBox);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        add(leftToolBar, gridBagConstraints);

        rightBottomPanel.setMinimumSize(new java.awt.Dimension(275, 23));
        rightBottomPanel.setPreferredSize(new java.awt.Dimension(275, 23));
        rightBottomPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(rightBottomPanel, gridBagConstraints);

        rightControlPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(loadButton, org.openide.util.NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.loadButton.text")); // NOI18N
        loadButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loadButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 0);
        rightControlPanel.add(loadButton, gridBagConstraints);

        featureTextField.setEditable(false);
        featureTextField.setText(org.openide.util.NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.featureTextField.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        rightControlPanel.add(featureTextField, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(rightControlPanel, gridBagConstraints);

        centerToolBar.setFloatable(false);
        centerToolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        centerToolBar.setRollover(true);
        centerToolBar.add(jSeparator2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        add(centerToolBar, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void addToDisplayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToDisplayButtonActionPerformed
        addToDisplayedColumns();
    }//GEN-LAST:event_addToDisplayButtonActionPerformed

    private void removeFromDisplayButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFromDisplayButtonActionPerformed
        removeFromDisplayedColumns();
    }//GEN-LAST:event_removeFromDisplayButtonActionPerformed

    private void categoryComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_categoryComboBoxActionPerformed
        final DefaultListModel<MolecularDescriptor> leftListModel = new DefaultListModel<>();
        final String selectedKey = (String) categoryComboBox.getSelectedItem();
        SwingWorker<Void, Void> sw = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                if (selectedKey.equals(ALL_SELECTION)) {
                    for (String key : attrModel.getMolecularDescriptorKeys()) {
                        for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(key)) {
                            leftListModel.addElement(attr);
                        }
                    }
                } else {
                    for (MolecularDescriptor attr : attrModel.getMolecularDescriptors(selectedKey)) {
                        leftListModel.addElement(attr);
                    }
                }
                return null;
            }

            @Override
            protected void done() {
                try {
                    get();
                    leftList.setModel(leftListModel);
                } catch (InterruptedException | ExecutionException ex) {
                    Exceptions.printStackTrace(ex);
                } finally {
                    sizeLabel.setText(NbBundle.getMessage(MolecularFeaturesPanel.class, "MolecularFeaturesPanel.sizeLabel.text", leftListModel.size()));
                }
            }

        };
        sw.execute();
    }//GEN-LAST:event_categoryComboBoxActionPerformed

    private void loadButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loadButtonActionPerformed
        int[] indices = null;
        DefaultListModel<MolecularDescriptor> listModel = null;
        if (!leftList.getSelectionModel().isSelectionEmpty()) {
            indices = leftList.getSelectedIndices();
            listModel = (DefaultListModel<MolecularDescriptor>) leftList.getModel();
        } else if (!rightList.getSelectionModel().isSelectionEmpty()) {
            indices = rightList.getSelectedIndices();
            listModel = rightListModel;
        }
        if (indices != null && listModel != null) {
            MolecularDescriptor attribute;
            StatsPanel panel;
            for (int i = 0; i < indices.length; i++) {
                attribute = listModel.get(indices[i]);
                if (!map.containsKey(attribute)) {
                    panel = new StatsPanel(attrModel, attribute);
                    map.put(attribute, panel);
                }
            }
            if (indices.length > 0) {
                setStats(listModel.get(indices[0]));
            }
        }

    }//GEN-LAST:event_loadButtonActionPerformed

    private void switcherComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_switcherComboBoxActionPerformed
        switch (switcherComboBox.getSelectedIndex()) {
            case 0:
                setLeftView("list");
                NbPreferences.forModule(MolecularFeaturesPanel.class).put(LAST_LEFT_VIEW, "list");
                break;
            case 1:
                setLeftView("table");
                NbPreferences.forModule(MolecularFeaturesPanel.class).put(LAST_LEFT_VIEW, "table");
                break;
        }
    }//GEN-LAST:event_switcherComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToDisplayButton;
    private javax.swing.JComboBox<String> categoryComboBox;
    private javax.swing.JToolBar centerToolBar;
    private javax.swing.JTextField featureTextField;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JPanel leftPanel;
    private javax.swing.JToolBar leftToolBar;
    private javax.swing.JButton loadButton;
    private javax.swing.JButton removeFromDisplayButton;
    private javax.swing.JPanel rightBottomPanel;
    private javax.swing.JPanel rightControlPanel;
    private javax.swing.JScrollPane rightScrollPane;
    private javax.swing.JPanel rightUpperPanel;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JComboBox<String> switcherComboBox;
    private javax.swing.JToolBar upperToolBar;
    // End of variables declaration//GEN-END:variables
}

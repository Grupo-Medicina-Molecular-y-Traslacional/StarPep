/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.util.Iterator;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.jdesktop.swingx.JXList;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class DescriptorSelectionPanel extends javax.swing.JPanel {

    protected final JList<PeptideAttribute> leftList, rightList;
    protected final DefaultListModel<PeptideAttribute> leftListModel, rightListModel;
    protected final AttributesModel attrModel;
    protected final JButton findButton;
    protected final String ALL_SELECTION;

    /**
     * Creates new form DescriptorSelectionPanel
     *
     * @param attrModel
     */
    public DescriptorSelectionPanel(final AttributesModel attrModel) {
        initComponents();
        this.attrModel = attrModel;
        leftListModel = new DefaultListModel<>();
        leftList = new JXList(leftListModel);
        leftScrollPane.setViewportView(leftList);

        rightListModel = new DefaultListModel<>();
        rightList = new JList<>(rightListModel);
        rightScrollPane.setViewportView(rightList);

        // Fill combobox
        ALL_SELECTION = NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.chooseAll.text");
        DefaultComboBoxModel comboModel = (DefaultComboBoxModel) descriptorComboBox.getModel();
        comboModel.addElement(ALL_SELECTION);
        comboModel.setSelectedItem(ALL_SELECTION);
        for (Iterator<PeptideAttribute> it = attrModel.getAttributeIterator(); it.hasNext();) {
            PeptideAttribute attr = it.next();
            if (attr.isMolecularDescriptor() && attr.getOriginAlgorithm() != null
                    && comboModel.getIndexOf(attr.getOriginAlgorithm().getFactory().getName()) < 0) {
                comboModel.addElement(attr.getOriginAlgorithm().getFactory().getName());
            }
        }

        // Add tool bar buttons
        findButton = new JButton(leftList.getActionMap().get("find"));
        findButton.setText("");
        findButton.setToolTipText(NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.findButton.toolTipText"));
        findButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/search.png", false));
        findButton.setFocusable(false);

        leftToolBar.add(findButton);

        // Fill displayed column list
        for (PeptideAttribute attr : attrModel.getDisplayedColumnsModel()) {
            if (attr.isMolecularDescriptor()) {
                rightListModel.addElement(attr);
            }
        }

        // Configure components...
        infoLabel.setVisible(!attrModel.canAddDisplayColumn());        
        
        leftList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (!lsm.isSelectionEmpty()){
                    rightList.clearSelection();
                    loadButton.setEnabled(true);
                }
                addToDisplayButton.setEnabled(!lsm.isSelectionEmpty() && attrModel.canAddDisplayColumn());                
            }
        });

        rightList.getSelectionModel().addListSelectionListener(new ListSelectionListener() {
            @Override
            public void valueChanged(ListSelectionEvent e) {
                ListSelectionModel lsm = (ListSelectionModel) e.getSource();
                if (!lsm.isSelectionEmpty()){
                   leftList.clearSelection();
                   loadButton.setEnabled(true);
                }   
                removeFromDisplayButton.setEnabled(!lsm.isSelectionEmpty());                 
            }
        });         
        
        addToDisplayButton.setEnabled(false);
        removeFromDisplayButton.setEnabled(false);
        loadButton.setEnabled(false);
    }

    private void addToDisplayedColumns() {
        int[] indices = leftList.getSelectedIndices();
        for (int i = 0; i < indices.length && attrModel.canAddDisplayColumn(); i++) {
            if (rightListModel.indexOf(leftListModel.get(indices[i])) < 0) {
                attrModel.addDisplayedColumn(leftListModel.get(indices[i]));
                rightListModel.addElement(leftListModel.get(indices[i]));
            }

        }
        infoLabel.setVisible(!attrModel.canAddDisplayColumn());
    }

    private void removeFromDisplayedColumns() {
        int[] indices = rightList.getSelectedIndices();
        for (int i = indices.length - 1; i >= 0; i--) {
            attrModel.removeAvailableColumn(rightListModel.get(indices[i]));
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

        descriptorComboBox = new javax.swing.JComboBox<>();
        leftScrollPane = new javax.swing.JScrollPane();
        infoLabel = new javax.swing.JLabel();
        rightUpperPanel = new javax.swing.JPanel();
        rightScrollPane = new javax.swing.JScrollPane();
        upperToolBar = new javax.swing.JToolBar();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        addToDisplayButton = new javax.swing.JButton();
        removeFromDisplayButton = new javax.swing.JButton();
        sizeLabel = new javax.swing.JLabel();
        leftToolBar = new javax.swing.JToolBar();
        rightBottomPanel = new javax.swing.JPanel();
        rightControlPanel = new javax.swing.JPanel();
        loadButton = new javax.swing.JButton();
        centerToolBar = new javax.swing.JToolBar();
        jSeparator2 = new javax.swing.JToolBar.Separator();

        setMinimumSize(new java.awt.Dimension(610, 480));
        setPreferredSize(new java.awt.Dimension(610, 480));
        setLayout(new java.awt.GridBagLayout());

        descriptorComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                descriptorComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        add(descriptorComboBox, gridBagConstraints);

        leftScrollPane.setMinimumSize(new java.awt.Dimension(275, 23));
        leftScrollPane.setPreferredSize(new java.awt.Dimension(275, 23));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(leftScrollPane, gridBagConstraints);

        infoLabel.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        infoLabel.setForeground(java.awt.Color.red);
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.infoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 2, 0, 2);
        add(infoLabel, gridBagConstraints);

        rightUpperPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.rightUpperPanel.border.title"))); // NOI18N
        rightUpperPanel.setToolTipText(org.openide.util.NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.rightUpperPanel.toolTipText")); // NOI18N
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
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
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
        org.openide.awt.Mnemonics.setLocalizedText(addToDisplayButton, org.openide.util.NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.addToDisplayButton.text")); // NOI18N
        addToDisplayButton.setToolTipText(org.openide.util.NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.addToDisplayButton.toolTipText")); // NOI18N
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
        org.openide.awt.Mnemonics.setLocalizedText(removeFromDisplayButton, org.openide.util.NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.removeFromDisplayButton.text")); // NOI18N
        removeFromDisplayButton.setToolTipText(org.openide.util.NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.removeFromDisplayButton.toolTipText")); // NOI18N
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
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        add(upperToolBar, gridBagConstraints);

        sizeLabel.setFont(new java.awt.Font("Ubuntu", 0, 12)); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(sizeLabel, org.openide.util.NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.sizeLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(sizeLabel, gridBagConstraints);

        leftToolBar.setFloatable(false);
        leftToolBar.setRollover(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        add(leftToolBar, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(rightBottomPanel, gridBagConstraints);

        rightControlPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT));

        org.openide.awt.Mnemonics.setLocalizedText(loadButton, org.openide.util.NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.loadButton.text")); // NOI18N
        rightControlPanel.add(loadButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        add(rightControlPanel, gridBagConstraints);

        centerToolBar.setFloatable(false);
        centerToolBar.setOrientation(javax.swing.SwingConstants.VERTICAL);
        centerToolBar.setRollover(true);
        centerToolBar.add(jSeparator2);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
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

    private void descriptorComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_descriptorComboBoxActionPerformed
        leftListModel.clear();
        PeptideAttribute attr;
        AlgorithmFactory originFactory;
        String selectedItem = (String) descriptorComboBox.getSelectedItem();
        for (Iterator<PeptideAttribute> it = attrModel.getAttributeIterator(); it.hasNext();) {
            attr = it.next();
            if (attr.isMolecularDescriptor()) {
                if (selectedItem.equals(ALL_SELECTION)) {
                    leftListModel.addElement(attr);
                } else {
                    originFactory = attr.getOriginAlgorithm() != null ? attr.getOriginAlgorithm().getFactory() : null;
                    if (originFactory == null || originFactory.getName().equals(selectedItem)) {
                        leftListModel.addElement(attr);
                    }
                }
            }
        }
        sizeLabel.setText(NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.sizeLabel.text", leftListModel.size()));
    }//GEN-LAST:event_descriptorComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addToDisplayButton;
    private javax.swing.JToolBar centerToolBar;
    private javax.swing.JComboBox<String> descriptorComboBox;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JScrollPane leftScrollPane;
    private javax.swing.JToolBar leftToolBar;
    private javax.swing.JButton loadButton;
    private javax.swing.JButton removeFromDisplayButton;
    private javax.swing.JPanel rightBottomPanel;
    private javax.swing.JPanel rightControlPanel;
    private javax.swing.JScrollPane rightScrollPane;
    private javax.swing.JPanel rightUpperPanel;
    private javax.swing.JLabel sizeLabel;
    private javax.swing.JToolBar upperToolBar;
    // End of variables declaration//GEN-END:variables
}

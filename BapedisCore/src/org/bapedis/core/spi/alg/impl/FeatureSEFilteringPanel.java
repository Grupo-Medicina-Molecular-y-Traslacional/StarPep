/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.spi.alg.impl;

import java.awt.event.ItemEvent;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FeatureSEFilteringPanel extends javax.swing.JPanel implements AlgorithmSetupUI {

    private FeatureSEFiltering algorithm;
    private final NotifyDescriptor errorND, errorNumberOfBinsND;
    private JPanel discretizationPanel;

    public FeatureSEFilteringPanel() {
        initComponents();

        errorND = new NotifyDescriptor.Message(NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.errorND"), NotifyDescriptor.ERROR_MESSAGE);
        errorNumberOfBinsND = new NotifyDescriptor.Message(NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.errorNumberOfBinsND"), NotifyDescriptor.ERROR_MESSAGE);
        //Create document listeners
        jTF_top.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateTopRank();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateTopRank();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        //Create document listeners
        jTF_value1.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateNumberOfBins1();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateNumberOfBins1();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });

        //Create document listeners
        jTF_value2.getDocument().addDocumentListener(new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
                updateNumberOfBins2();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                updateNumberOfBins2();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {

            }
        });
    }

    private void updateNumberOfBins1() {
        try {
            if (!jTF_value1.getText().isEmpty()) {
                int value = Integer.parseInt(jTF_value1.getText());
                if (algorithm.getBinsOption1() == FeatureDiscretization.BinsOption.User_Defined) {
                    algorithm.setNumberOfBins1(value);
                }
            }
        } catch (NumberFormatException ex) {
            DialogDisplayer.getDefault().notify(errorNumberOfBinsND);
        }
    }

    private void updateNumberOfBins2() {
        try {
            if (!jTF_value2.getText().isEmpty()) {
                int value = Integer.parseInt(jTF_value2.getText());
                if (algorithm.getBinsOption2() == FeatureDiscretization.BinsOption.User_Defined) {
                    algorithm.setNumberOfBins2(value);
                }
            }
        } catch (NumberFormatException ex) {
            DialogDisplayer.getDefault().notify(errorNumberOfBinsND);
        }
    }

    private void updateTopRank() {
        try {
            if (!jTF_top.getText().isEmpty()) {
                int topRank = Integer.parseInt(jTF_top.getText());
                if (algorithm.getTopRank() != topRank) {
                    algorithm.setTopRank(topRank);
                }
            } else {
                algorithm.setTopRank(-1);
            }
        } catch (NumberFormatException ex) {
            DialogDisplayer.getDefault().notify(errorND);
            algorithm.setTopRank(-1);
        }
    }
    
    public void setSubsetEvalPanel(boolean flag){
        bottomPanel.setVisible(flag);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled); //To change body of generated methods, choose Tools | Templates.

        if (discretizationPanel != null) {
            discretizationPanel.setEnabled(enabled);
        }

        outputPanel.setEnabled(enabled);

        jRBSelectAll.setEnabled(enabled);
        jRBSelectTop.setEnabled(enabled);
        jTF_top.setEnabled(jRBSelectTop.isSelected() && enabled);

        uselessPanel.setEnabled(enabled);
        jLabel1.setEnabled(enabled);
        jLabel2.setEnabled(enabled);
        infoLabel1.setEnabled(enabled);
        jrankingComboBox.setEnabled(enabled);
        jSpinnerEntropy.setEnabled(enabled);

        jNumberOfBins1Label.setEnabled(enabled);
        jCBNumberOfBins1.setEnabled(enabled);
        jTF_value1.setEnabled((jCBNumberOfBins1.getSelectedIndex() == jCBNumberOfBins1.getItemCount() - 1) && enabled);

        redundancyPanel.setEnabled(enabled);
        jLabel3.setEnabled(enabled);
        redundancyComboBox.setEnabled(enabled);
        jLabel4.setEnabled(redundancyComboBox.getSelectedIndex() != 0 && enabled);
        jSpinnerRedundancy.setEnabled(redundancyComboBox.getSelectedIndex() != 0 && enabled);
        infoLabel2.setEnabled(redundancyComboBox.getSelectedIndex() != 0 && enabled);

        bottomPanel.setEnabled(enabled);
        jCBMerit.setEnabled(enabled);
        jLabel5.setEnabled(enabled);
        jCBNumberOfBins2.setEnabled(enabled);
        jTF_value2.setEnabled((jCBNumberOfBins2.getSelectedIndex() == jCBNumberOfBins2.getItemCount() - 1) && enabled);
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

        buttonGroup1 = new javax.swing.ButtonGroup();
        uselessPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jSpinnerEntropy = new javax.swing.JSpinner();
        jLabel2 = new javax.swing.JLabel();
        jrankingComboBox = new javax.swing.JComboBox<>();
        infoLabel1 = new javax.swing.JLabel();
        jNumberOfBins1Label = new javax.swing.JLabel();
        jCBNumberOfBins1 = new javax.swing.JComboBox<>();
        jTF_value1 = new javax.swing.JTextField();
        redundancyPanel = new javax.swing.JPanel();
        redundancyComboBox = new javax.swing.JComboBox<>();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jSpinnerRedundancy = new javax.swing.JSpinner();
        infoLabel2 = new javax.swing.JLabel();
        outputPanel = new javax.swing.JPanel();
        jTF_top = new javax.swing.JTextField();
        jRBSelectTop = new javax.swing.JRadioButton();
        jRBSelectAll = new javax.swing.JRadioButton();
        bottomPanel = new javax.swing.JPanel();
        jCBMerit = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jCBNumberOfBins2 = new javax.swing.JComboBox<>();
        jTF_value2 = new javax.swing.JTextField();
        extLabel1 = new javax.swing.JLabel();
        extLabel = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(440, 380));
        setLayout(new java.awt.GridBagLayout());

        uselessPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.uselessPanel.border.title"))); // NOI18N
        uselessPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        uselessPanel.add(jLabel1, gridBagConstraints);
        jLabel1.getAccessibleContext().setAccessibleName(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jLabel1.AccessibleContext.accessibleName")); // NOI18N

        jSpinnerEntropy.setModel(new javax.swing.SpinnerNumberModel(10, 1, 50, 1));
        jSpinnerEntropy.setMinimumSize(new java.awt.Dimension(60, 27));
        jSpinnerEntropy.setPreferredSize(new java.awt.Dimension(60, 27));
        jSpinnerEntropy.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerEntropyStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        uselessPanel.add(jSpinnerEntropy, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        uselessPanel.add(jLabel2, gridBagConstraints);

        jrankingComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Shannon Entropy" }));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        uselessPanel.add(jrankingComboBox, gridBagConstraints);

        infoLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel1, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.infoLabel1.text")); // NOI18N
        infoLabel1.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.infoLabel1.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        uselessPanel.add(infoLabel1, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jNumberOfBins1Label, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jNumberOfBins1Label.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        uselessPanel.add(jNumberOfBins1Label, gridBagConstraints);

        jCBNumberOfBins1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<html>The number of peptides ( <i>n</i> )</html>", "<html>Half of the number of <i>n</i></html>", "<html>One-third of the number of <i>n</i></html>", "<html>User defined value</html>" }));
        jCBNumberOfBins1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBNumberOfBins1ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        uselessPanel.add(jCBNumberOfBins1, gridBagConstraints);

        jTF_value1.setText(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jTF_value1.text")); // NOI18N
        jTF_value1.setMinimumSize(new java.awt.Dimension(50, 27));
        jTF_value1.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        uselessPanel.add(jTF_value1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(uselessPanel, gridBagConstraints);

        redundancyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.redundancyPanel.border.title"))); // NOI18N
        redundancyPanel.setLayout(new java.awt.GridBagLayout());

        redundancyComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "None", "Pearson", "Spearman" }));
        redundancyComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                redundancyComboBoxItemStateChanged(evt);
            }
        });
        redundancyComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redundancyComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        redundancyPanel.add(redundancyComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel4, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jLabel4.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        redundancyPanel.add(jLabel4, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel3, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jLabel3.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        redundancyPanel.add(jLabel3, gridBagConstraints);

        jSpinnerRedundancy.setModel(new javax.swing.SpinnerNumberModel(0.9d, 0.0d, 1.0d, 0.01d));
        jSpinnerRedundancy.setMinimumSize(new java.awt.Dimension(60, 27));
        jSpinnerRedundancy.setPreferredSize(new java.awt.Dimension(60, 27));
        jSpinnerRedundancy.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jSpinnerRedundancyStateChanged(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        redundancyPanel.add(jSpinnerRedundancy, gridBagConstraints);

        infoLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel2, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.infoLabel2.text")); // NOI18N
        infoLabel2.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.infoLabel2.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        redundancyPanel.add(infoLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(redundancyPanel, gridBagConstraints);

        outputPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.outputPanel.border.title"))); // NOI18N
        outputPanel.setLayout(new java.awt.GridBagLayout());

        jTF_top.setText(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jTF_top.text")); // NOI18N
        jTF_top.setMinimumSize(new java.awt.Dimension(50, 20));
        jTF_top.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        outputPanel.add(jTF_top, gridBagConstraints);

        buttonGroup1.add(jRBSelectTop);
        org.openide.awt.Mnemonics.setLocalizedText(jRBSelectTop, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jRBSelectTop.text")); // NOI18N
        jRBSelectTop.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jRBSelectTopItemStateChanged(evt);
            }
        });
        jRBSelectTop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSelectTopActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        outputPanel.add(jRBSelectTop, gridBagConstraints);

        buttonGroup1.add(jRBSelectAll);
        jRBSelectAll.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jRBSelectAll, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jRBSelectAll.text")); // NOI18N
        jRBSelectAll.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRBSelectAllActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        outputPanel.add(jRBSelectAll, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(outputPanel, gridBagConstraints);

        bottomPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.bottomPanel.border.title"))); // NOI18N
        bottomPanel.setLayout(new java.awt.GridBagLayout());

        jCBMerit.setSelected(true);
        org.openide.awt.Mnemonics.setLocalizedText(jCBMerit, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jCBMerit.text")); // NOI18N
        jCBMerit.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jCBMeritItemStateChanged(evt);
            }
        });
        jCBMerit.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBMeritActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        bottomPanel.add(jCBMerit, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel5, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jLabel5.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(5, 10, 0, 0);
        bottomPanel.add(jLabel5, gridBagConstraints);

        jCBNumberOfBins2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<html>Sturges's rule</html>", "<html>Rice's rule</html>", "<html>The square root of the number of <i>n</i></html>", "<html>User defined value</html>" }));
        jCBNumberOfBins2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCBNumberOfBins2ActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        bottomPanel.add(jCBNumberOfBins2, gridBagConstraints);

        jTF_value2.setText(org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.jTF_value2.text")); // NOI18N
        jTF_value2.setMinimumSize(new java.awt.Dimension(50, 27));
        jTF_value2.setPreferredSize(new java.awt.Dimension(90, 27));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        bottomPanel.add(jTF_value2, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(extLabel1, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.extLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        bottomPanel.add(extLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(bottomPanel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(extLabel, org.openide.util.NbBundle.getMessage(FeatureSEFilteringPanel.class, "FeatureSEFilteringPanel.extLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(extLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void redundancyComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redundancyComboBoxActionPerformed
        if (algorithm != null && algorithm.getRedundancyOption() != redundancyComboBox.getSelectedIndex()) {
            algorithm.setRedundancyOption(redundancyComboBox.getSelectedIndex());
        }
    }//GEN-LAST:event_redundancyComboBoxActionPerformed

    private void redundancyComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_redundancyComboBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            boolean enable = redundancyComboBox.getSelectedIndex() != FeatureSEFiltering.REDUNDANCY_NONE;
            jSpinnerRedundancy.setEnabled(enable);
            jLabel4.setEnabled(enable);
            infoLabel2.setEnabled(enable);
        }
    }//GEN-LAST:event_redundancyComboBoxItemStateChanged

    private void jSpinnerEntropyStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerEntropyStateChanged
        int threshold = (int) jSpinnerEntropy.getModel().getValue();
        if (algorithm.getThresholdPercent() != threshold) {
            algorithm.setThresholdPercent(threshold);
        }
    }//GEN-LAST:event_jSpinnerEntropyStateChanged

    private void jSpinnerRedundancyStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jSpinnerRedundancyStateChanged
        double threshold = (double) jSpinnerRedundancy.getModel().getValue();
        if (algorithm.getRedundancyCutoff() != threshold) {
            algorithm.setRedundancyCutoff(threshold);
        }
    }//GEN-LAST:event_jSpinnerRedundancyStateChanged

    private void jRBSelectTopItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jRBSelectTopItemStateChanged
        jTF_top.setEnabled(jRBSelectTop.isSelected());
    }//GEN-LAST:event_jRBSelectTopItemStateChanged

    private void jRBSelectTopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSelectTopActionPerformed
        if (algorithm.getSelectionOption() != FeatureSEFiltering.SELECT_TOP) {
            algorithm.setSelectionOption(FeatureSEFiltering.SELECT_TOP);
        }
    }//GEN-LAST:event_jRBSelectTopActionPerformed

    private void jRBSelectAllActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRBSelectAllActionPerformed
        if (algorithm.getSelectionOption() != FeatureSEFiltering.SELECT_ALL) {
            algorithm.setSelectionOption(FeatureSEFiltering.SELECT_ALL);
        }
    }//GEN-LAST:event_jRBSelectAllActionPerformed

    private void jCBNumberOfBins1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNumberOfBins1ActionPerformed
        jTF_value1.setText("");
        jTF_value1.setEnabled(false);
        switch (jCBNumberOfBins1.getSelectedIndex()) {
            case 0:
                if (algorithm.getBinsOption1() != FeatureDiscretization.BinsOption.Number_peptides) {
                    algorithm.setBinsOption1(FeatureDiscretization.BinsOption.Number_peptides);
                }
                break;
            case 1:
                if (algorithm.getBinsOption1() != FeatureDiscretization.BinsOption.Half_number_peptides) {
                    algorithm.setBinsOption1(FeatureDiscretization.BinsOption.Half_number_peptides);
                }
                break;
            case 2:
                if (algorithm.getBinsOption1() != FeatureDiscretization.BinsOption.One_third_number_peptides) {
                    algorithm.setBinsOption1(FeatureDiscretization.BinsOption.One_third_number_peptides);
                }
                break;
            case 3:
                if (algorithm.getBinsOption1() != FeatureDiscretization.BinsOption.User_Defined) {
                    algorithm.setBinsOption1(FeatureDiscretization.BinsOption.User_Defined);
                }
                jTF_value1.setText(String.valueOf(algorithm.getNumberOfBins1()));
                jTF_value1.setEnabled(true);
                break;
            default:
                jTF_value1.setText("");
                jTF_value1.setEnabled(false);

        }
    }//GEN-LAST:event_jCBNumberOfBins1ActionPerformed

    private void jCBMeritActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBMeritActionPerformed
        algorithm.setMerit(jCBMerit.isSelected());
    }//GEN-LAST:event_jCBMeritActionPerformed

    private void jCBNumberOfBins2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCBNumberOfBins2ActionPerformed
        jTF_value2.setText("");
        jTF_value2.setEnabled(false);
        switch (jCBNumberOfBins2.getSelectedIndex()) {
            case 0:
                if (algorithm.getBinsOption2() != FeatureDiscretization.BinsOption.Sturges_Rule) {
                    algorithm.setBinsOption2(FeatureDiscretization.BinsOption.Sturges_Rule);
                }
                break;
            case 1:
                if (algorithm.getBinsOption2() != FeatureDiscretization.BinsOption.Rice_Rule) {
                    algorithm.setBinsOption2(FeatureDiscretization.BinsOption.Rice_Rule);
                }
                break;
            case 2:
                if (algorithm.getBinsOption2() != FeatureDiscretization.BinsOption.Square_root_number_peptides) {
                    algorithm.setBinsOption2(FeatureDiscretization.BinsOption.Square_root_number_peptides);
                }
                break;
            case 3:
                if (algorithm.getBinsOption2() != FeatureDiscretization.BinsOption.User_Defined) {
                    algorithm.setBinsOption2(FeatureDiscretization.BinsOption.User_Defined);
                }
                jTF_value2.setText(String.valueOf(algorithm.getNumberOfBins2()));
                jTF_value2.setEnabled(true);
                break;
            default:
                jTF_value2.setText("");
                jTF_value2.setEnabled(false);
        }
    }//GEN-LAST:event_jCBNumberOfBins2ActionPerformed

    private void jCBMeritItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jCBMeritItemStateChanged
        boolean visible = jCBMerit.isSelected();
        jLabel5.setVisible(visible);
        jCBNumberOfBins2.setVisible(visible);
        jTF_value2.setVisible(visible);
    }//GEN-LAST:event_jCBMeritItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JLabel extLabel;
    private javax.swing.JLabel extLabel1;
    private javax.swing.JLabel infoLabel1;
    private javax.swing.JLabel infoLabel2;
    private javax.swing.JCheckBox jCBMerit;
    private javax.swing.JComboBox<String> jCBNumberOfBins1;
    private javax.swing.JComboBox<String> jCBNumberOfBins2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jNumberOfBins1Label;
    private javax.swing.JRadioButton jRBSelectAll;
    private javax.swing.JRadioButton jRBSelectTop;
    private javax.swing.JSpinner jSpinnerEntropy;
    private javax.swing.JSpinner jSpinnerRedundancy;
    private javax.swing.JTextField jTF_top;
    private javax.swing.JTextField jTF_value1;
    private javax.swing.JTextField jTF_value2;
    private javax.swing.JComboBox<String> jrankingComboBox;
    private javax.swing.JPanel outputPanel;
    private javax.swing.JComboBox<String> redundancyComboBox;
    private javax.swing.JPanel redundancyPanel;
    private javax.swing.JPanel uselessPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        this.algorithm = (FeatureSEFiltering) algo;

        switch (algorithm.getBinsOption1()) {
            case Number_peptides:
                jCBNumberOfBins1.setSelectedIndex(0);
                break;
            case Half_number_peptides:
                jCBNumberOfBins1.setSelectedIndex(1);
                break;
            case One_third_number_peptides:
                jCBNumberOfBins1.setSelectedIndex(2);
                break;
            case User_Defined:
                jCBNumberOfBins1.setSelectedIndex(3);
                break;
            default:
                jCBNumberOfBins1.setSelectedIndex(-1);
        }

        switch (algorithm.getBinsOption2()) {
            case Sturges_Rule:
                jCBNumberOfBins2.setSelectedIndex(0);
                break;
            case Rice_Rule:
                jCBNumberOfBins2.setSelectedIndex(1);
                break;
            case Square_root_number_peptides:
                jCBNumberOfBins2.setSelectedIndex(2);
                break;
            case User_Defined:
                jCBNumberOfBins2.setSelectedIndex(3);
                break;
            default:
                jCBNumberOfBins2.setSelectedIndex(-1);
        }

        switch (algorithm.getSelectionOption()) {
            case FeatureSEFiltering.SELECT_ALL:
                jRBSelectAll.setSelected(true);
                break;
            case FeatureSEFiltering.SELECT_TOP:
                jRBSelectTop.setSelected(true);
                break;
        }

        jTF_top.setText(algorithm.getTopRank() > 0 ?String.valueOf(algorithm.getTopRank()):"");
        jTF_top.setEnabled(jRBSelectTop.isSelected());

        redundancyComboBox.setSelectedIndex(algorithm.getRedundancyOption());
        int entropyThreshold = algorithm.getThresholdPercent();
        jSpinnerEntropy.getModel().setValue(entropyThreshold);

        double redundancyThreshold = algorithm.getRedundancyCutoff();
        jSpinnerRedundancy.getModel().setValue(redundancyThreshold);

        jCBMerit.setSelected(algorithm.isMerit());

        return this;
    }
}

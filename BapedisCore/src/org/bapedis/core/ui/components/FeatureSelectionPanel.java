/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import org.openide.util.Exceptions;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.FeatureSelectionModel;
import org.bapedis.core.model.MolecularDescriptor;
import org.bapedis.core.model.MolecularDescriptorNotFoundException;
import org.bapedis.core.model.Peptide;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.task.ProgressTicket;
import org.jdesktop.swingx.JXBusyLabel;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.util.Cancellable;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FeatureSelectionPanel extends javax.swing.JPanel {

    protected final AttributesModel attrModel;
    protected final Workspace workspace;
    protected FeatureSelectionModel model;
    protected final JXBusyLabel busyLabel;

    public FeatureSelectionPanel(AttributesModel attrModel, Workspace workspace) {
        initComponents();
        this.attrModel = attrModel;
        this.workspace = workspace;
        model = workspace.getLookup().lookup(FeatureSelectionModel.class);
        if (model == null) {
            model = new FeatureSelectionModel();
            workspace.add(model);
        }

        if (model.isRemoveUseless()) {
            if (model.isRemoveRedundant()) {
                option2Button.setSelected(true);
            } else {
                option1Button.setSelected(true);
            }
        }

        // Configure sliders        
        uselessSlider.setMinimum(FeatureSelectionModel.ENTROPY_CUTOFF_REFS[0]);
        uselessSlider.setMaximum(FeatureSelectionModel.ENTROPY_CUTOFF_REFS[1]);
        uselessSlider.setMajorTickSpacing(10);
        uselessSlider.setMinorTickSpacing(1);
        int val = model.getEntropyCutoff();
        uselessSlider.setValue(val);
        uselessLabel.setText(val + "%");
        uselessSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int val = (int) source.getValue();
                uselessLabel.setText(val + "%");
            }
        });

        Hashtable<Integer, JLabel> uselessLabelTable = new Hashtable<>();
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_CUTOFF_REFS[0], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.weak")));
        uselessLabelTable.put(FeatureSelectionModel.ENTROPY_CUTOFF_REFS[1], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.moderate")));

        uselessSlider.setLabelTable(uselessLabelTable);

        redundantSlider.setMinimum(FeatureSelectionModel.TANIMOTO_CUTOFF_REFS[0]);
        redundantSlider.setMaximum(FeatureSelectionModel.TANIMOTO_CUTOFF_REFS[1]);
        redundantSlider.setMajorTickSpacing(8);
        redundantSlider.setMinorTickSpacing(1);
        val = model.getTanimotoCutoff();
        redundantSlider.setValue(val);
        redundantLabel.setText(val + "%");
        redundantSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {
                JSlider source = (JSlider) e.getSource();
                int val = (int) source.getValue();
                redundantLabel.setText(val + "%");
            }
        });

        Hashtable<Integer, JLabel> redundantLabelTable = new Hashtable<>();
        redundantLabelTable.put(FeatureSelectionModel.TANIMOTO_CUTOFF_REFS[0], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.strong")));
        redundantLabelTable.put(FeatureSelectionModel.TANIMOTO_CUTOFF_REFS[1], new JLabel(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.moderate")));

        redundantSlider.setLabelTable(redundantLabelTable);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setText(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.removing.text"));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setBusy(false);
        busyLabel.setVisible(false);
        rightPanel.add(busyLabel);

    }

    static {
        UIManager.put("Slider.paintValue", false);
    }

    private void refreshInternalPanels() {
        boolean entropy = option2Button.isSelected() || option1Button.isSelected();
        entropyPanel.setEnabled(entropy);
        uselessSlider.setEnabled(entropy);
        uselessLabel.setEnabled(entropy);
        entropyInfoLabel.setEnabled(entropy);

        boolean tanimoto = option2Button.isSelected();
        tanimotoPanel.setEnabled(tanimoto);
        redundantSlider.setEnabled(tanimoto);
        redundantLabel.setEnabled(tanimoto);
        tanimotoInfoLabel.setEnabled(tanimoto);
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
        centerPanel = new javax.swing.JPanel();
        option1Button = new javax.swing.JRadioButton();
        option2Button = new javax.swing.JRadioButton();
        entropyPanel = new javax.swing.JPanel();
        uselessSlider = new javax.swing.JSlider();
        uselessLabel = new javax.swing.JLabel();
        entropyInfoLabel = new javax.swing.JLabel();
        extLabel1 = new javax.swing.JLabel();
        tanimotoPanel = new javax.swing.JPanel();
        redundantSlider = new javax.swing.JSlider();
        redundantLabel = new javax.swing.JLabel();
        tanimotoInfoLabel = new javax.swing.JLabel();
        extLabel2 = new javax.swing.JLabel();
        rightPanel = new javax.swing.JPanel();
        removeButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(440, 250));
        setPreferredSize(new java.awt.Dimension(440, 250));
        setLayout(new java.awt.GridBagLayout());

        centerPanel.setPreferredSize(new java.awt.Dimension(259, 130));
        centerPanel.setLayout(new java.awt.GridBagLayout());

        buttonGroup1.add(option1Button);
        org.openide.awt.Mnemonics.setLocalizedText(option1Button, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.removeUselessRButton.text")); // NOI18N
        option1Button.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                option1ButtonItemStateChanged(evt);
            }
        });
        option1Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option1ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 0, 0, 0);
        centerPanel.add(option1Button, gridBagConstraints);

        buttonGroup1.add(option2Button);
        org.openide.awt.Mnemonics.setLocalizedText(option2Button, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.selectRankedRButton.text")); // NOI18N
        option2Button.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                option2ButtonItemStateChanged(evt);
            }
        });
        option2Button.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                option2ButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        centerPanel.add(option2Button, gridBagConstraints);

        entropyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.entropyPanel.border.title"))); // NOI18N
        entropyPanel.setLayout(new java.awt.GridBagLayout());

        uselessSlider.setPaintLabels(true);
        uselessSlider.setPaintTicks(true);
        uselessSlider.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessSlider.toolTipText")); // NOI18N
        uselessSlider.setValue(0);
        uselessSlider.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        entropyPanel.add(uselessSlider, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(uselessLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.uselessLabel.text")); // NOI18N
        uselessLabel.setMaximumSize(new java.awt.Dimension(31, 14));
        uselessLabel.setMinimumSize(new java.awt.Dimension(31, 14));
        uselessLabel.setPreferredSize(new java.awt.Dimension(31, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        entropyPanel.add(uselessLabel, gridBagConstraints);

        entropyInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(entropyInfoLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.entropyInfoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        entropyPanel.add(entropyInfoLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(extLabel1, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.extLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        entropyPanel.add(extLabel1, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 2, 0, 0);
        centerPanel.add(entropyPanel, gridBagConstraints);

        tanimotoPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.tanimotoPanel.border.title"))); // NOI18N
        tanimotoPanel.setLayout(new java.awt.GridBagLayout());

        redundantSlider.setPaintLabels(true);
        redundantSlider.setPaintTicks(true);
        redundantSlider.setToolTipText(org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantSlider.toolTipText")); // NOI18N
        redundantSlider.setValue(0);
        redundantSlider.setEnabled(false);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        tanimotoPanel.add(redundantSlider, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(redundantLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.redundantLabel.text")); // NOI18N
        redundantLabel.setMaximumSize(new java.awt.Dimension(31, 14));
        redundantLabel.setMinimumSize(new java.awt.Dimension(31, 14));
        redundantLabel.setPreferredSize(new java.awt.Dimension(31, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(3, 0, 0, 0);
        tanimotoPanel.add(redundantLabel, gridBagConstraints);

        tanimotoInfoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(tanimotoInfoLabel, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.tanimotoInfoLabel.text")); // NOI18N
        tanimotoInfoLabel.setMaximumSize(new java.awt.Dimension(23, 14));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHEAST;
        tanimotoPanel.add(tanimotoInfoLabel, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(extLabel2, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.extLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        tanimotoPanel.add(extLabel2, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        centerPanel.add(tanimotoPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        add(centerPanel, gridBagConstraints);

        rightPanel.setMaximumSize(new java.awt.Dimension(110, 58));
        rightPanel.setMinimumSize(new java.awt.Dimension(110, 58));
        rightPanel.setPreferredSize(new java.awt.Dimension(110, 58));
        rightPanel.setLayout(new javax.swing.BoxLayout(rightPanel, javax.swing.BoxLayout.Y_AXIS));

        removeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/delete.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.removeButton.text")); // NOI18N
        removeButton.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        removeButton.setMaximumSize(new java.awt.Dimension(99, 29));
        removeButton.setMinimumSize(new java.awt.Dimension(99, 29));
        removeButton.setPreferredSize(new java.awt.Dimension(99, 29));
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });
        rightPanel.add(removeButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 0, 0);
        add(rightPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void option2ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option2ButtonActionPerformed
        if (model != null) {
            model.setRemoveUseless(true);
            model.setRemoveRedundant(true);
        }
    }//GEN-LAST:event_option2ButtonActionPerformed

    private void option1ButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_option1ButtonActionPerformed
        if (model != null) {
            model.setRemoveUseless(true);
            model.setRemoveRedundant(false);
        }
    }//GEN-LAST:event_option1ButtonActionPerformed

    private void option2ButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_option2ButtonItemStateChanged
        refreshInternalPanels();
    }//GEN-LAST:event_option2ButtonItemStateChanged

    private void option1ButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_option1ButtonItemStateChanged
        refreshInternalPanels();
    }//GEN-LAST:event_option1ButtonItemStateChanged

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if (workspace.isBusy()) {
            DialogDisplayer.getDefault().notify(workspace.getBusyNotifyDescriptor());
        } else {
            FeatureSelector selector = new FeatureSelector(attrModel, workspace, busyLabel);
            selector.execute();
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel entropyInfoLabel;
    private javax.swing.JPanel entropyPanel;
    private javax.swing.JLabel extLabel1;
    private javax.swing.JLabel extLabel2;
    private javax.swing.JRadioButton option1Button;
    private javax.swing.JRadioButton option2Button;
    private javax.swing.JLabel redundantLabel;
    private javax.swing.JSlider redundantSlider;
    private javax.swing.JButton removeButton;
    private javax.swing.JPanel rightPanel;
    private javax.swing.JLabel tanimotoInfoLabel;
    private javax.swing.JPanel tanimotoPanel;
    private javax.swing.JLabel uselessLabel;
    private javax.swing.JSlider uselessSlider;
    // End of variables declaration//GEN-END:variables

}

class FeatureSelector extends SwingWorker<Void, String> {

    private boolean stopRun;
    private final ProgressTicket ticket;
    private final JXBusyLabel busyLabel;
    private final Workspace workspace;
    private final AttributesModel attrModel;
    private final FeatureSelectionModel model;

    public FeatureSelector(AttributesModel attrModel, Workspace workspace, JXBusyLabel busyLabel) {
        this.attrModel = attrModel;
        this.workspace = workspace;
        this.busyLabel = busyLabel;
        model = workspace.getLookup().lookup(FeatureSelectionModel.class);
        stopRun = false;
        ticket = new ProgressTicket(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.removing.task"), new Cancellable() {
            @Override
            public boolean cancel() {
                stopRun = true;
                return true;
            }
        });
    }

    @Override
    protected Void doInBackground() throws Exception {
        if (!model.isRemoveUseless()) {
            return null;
        }
        ticket.start();
        publish("start");
        List<MolecularDescriptor> allFeatures = new LinkedList<>();
        HashMap<String, MolecularDescriptor[]> mdMap = attrModel.getAllMolecularDescriptors();
        for (Map.Entry<String, MolecularDescriptor[]> entry : mdMap.entrySet()) {
            for (MolecularDescriptor attr : entry.getValue()) {
                allFeatures.add(attr);
            }
        }
        Peptide[] peptides = attrModel.getPeptides();
        ticket.switchToDeterminate(allFeatures.size());

        Bin[] bins = new Bin[peptides.length];
        double maxScore = Math.log(peptides.length);
        double threshold = model.getEntropyCutoff() * maxScore / 100;
        double score, min = Double.MAX_VALUE;
        double max = Double.MIN_VALUE;
        String maxName = "";
        String minName = "";
        System.out.println("Max score: " + maxScore);
        System.out.println("cut off: " + threshold);
        LinkedList<MolecularDescriptor> toRemove = new LinkedList<>();
        for (MolecularDescriptor descriptor : allFeatures) {
            if (!stopRun) {
                descriptor.resetSummaryStats(peptides);
                fillBins(descriptor, peptides, bins);
                score = calculateEntropy(bins);
                descriptor.setScore(score);
                if (score < threshold) {
                    toRemove.add(descriptor);
                    System.out.println("Removed: " + descriptor.getDisplayName() + " - score: " + score);
                }
                if (score < min) {
                    min = score;
                    minName = descriptor.getDisplayName();
                }
                if (score > max) {
                    max = score;
                    maxName = descriptor.getDisplayName();
                }
                ticket.progress();
            }
        }
        allFeatures.removeAll(toRemove);
        System.out.println("max: " + maxName + ": " + max);
        System.out.println("min: " + minName + ": " + min);

        // Correlation
        System.out.println("---------------Spearman Correlation--------------");
        min = Double.MAX_VALUE;
        max = Double.MIN_VALUE;
//                    ticket.start();
        MolecularDescriptor[] rankedFeatures = allFeatures.toArray(new MolecularDescriptor[0]);
        Arrays.sort(rankedFeatures, new Comparator<MolecularDescriptor>() {
            @Override
            public int compare(MolecularDescriptor o1, MolecularDescriptor o2) {
                if (o1.getScore() < o2.getScore()) {
                    return -1;
                }
                if (o1.getScore() > o2.getScore()) {
                    return 1;
                }
                return 0;
            }
        });
        ticket.progress("Spearman Correlation");
        ticket.switchToDeterminate(rankedFeatures.length * rankedFeatures.length);
        toRemove.clear();
        double[] column1 = new double[peptides.length];
        double[] column2 = new double[peptides.length];
        double[] rank1, rank2;
        for (int i = 0; i < rankedFeatures.length - 1; i++) {
            if (rankedFeatures[i] != null) {
                for (int k = 0; k < column1.length; k++) {
                    column1[k] = MolecularDescriptor.getDoubleValue(peptides[k], rankedFeatures[i]);
                }
                rank1 = rank(column1);
                for (int j = i + 1; j < rankedFeatures.length; j++) {
                    if (rankedFeatures[j] != null) {
                        for (int k = 0; k < column2.length; k++) {
                            column2[k] = MolecularDescriptor.getDoubleValue(peptides[k], rankedFeatures[j]);
                        }
                        rank2 = rank(column2);
                        score = calculatePearsonCorrelation(rank1, rank2);
                        if (score >= 0.4) {
                            System.out.println(">0.4=" + score + ": " + rankedFeatures[i].getDisplayName() + " - " + rankedFeatures[j].getDisplayName());
                            toRemove.add(rankedFeatures[j]);
                            rankedFeatures[j] = null;
                        }
                        if (score < min) {
                            min = score;
                        }
                        if (score > max) {
                            max = score;
                        }
                    }
                    ticket.progress();
                }
            }
        }
        System.out.println("max: " + max);
        System.out.println("min: " + min);
        System.out.println("Removed: " + toRemove.size());
        return null;
    }

    @Override
    protected void process(List<String> chunks) {
        busyLabel.setBusy(true);
        busyLabel.setVisible(true);
        workspace.setBusy(true);
    }

    @Override
    protected void done() {
        try {
            get();
        } catch (InterruptedException | ExecutionException ex) {
            if (ex.getCause() instanceof MolecularDescriptorNotFoundException) {
                NotifyDescriptor errorND = ((MolecularDescriptorNotFoundException) ex.getCause()).getErrorND();
                DialogDisplayer.getDefault().notify(errorND);
            } else {
                Exceptions.printStackTrace(ex);
            }
        } finally {
            busyLabel.setBusy(false);
            busyLabel.setVisible(false);
            workspace.setBusy(false);
            ticket.finish();
        }
    }

    private void fillBins(MolecularDescriptor descriptor, Peptide[] peptides, Bin[] bins) throws MolecularDescriptorNotFoundException {
        Bin bin;
        double binWidth, lower, upper, min, max, val;
        int binIndex;
        min = descriptor.getMin();
        max = descriptor.getMax();
        binWidth = (max - min) / bins.length;
        lower = min;

        for (int i = 0; i < bins.length; i++) {
            if (i == bins.length - 1) {
                bin = new Bin(lower, max);
            } else {
                upper = min + (i + 1) * binWidth;
                bin = new Bin(lower, upper);
                lower = upper;
            }
            bins[i] = bin;
        }

        for (int i = 0; i < peptides.length; i++) {
            binIndex = bins.length - 1;
            val = MolecularDescriptor.getDoubleValue(peptides[i], descriptor);
            if (val < max) {
                double fraction = (val - min) / (max - min);
                if (fraction < 0.0) {
                    fraction = 0.0;
                }
                binIndex = (int) (fraction * bins.length);
                // rounding could result in binIndex being equal to bins
                // which will cause an IndexOutOfBoundsException - see bug
                // report 1553088
                if (binIndex >= bins.length) {
                    binIndex = bins.length - 1;
                }
            }
            bin = bins[binIndex];
            bin.incrementCount();
        }
    }

    private double calculateEntropy(Bin[] bins) {
        double entropy = 0.;
        double prob;
        for (Bin bin : bins) {
            if (bin.getCount() > 0) {
                prob = (double) bin.getCount() / bins.length;
                entropy -= prob * Math.log(prob);
            }
        }
        return entropy;
    }

    public double[] rank(double[] data) {
        // Array recording initial positions of data to be ranked
        IntDoublePair[] ranks = new IntDoublePair[data.length];
        for (int i = 0; i < data.length; i++) {
            ranks[i] = new IntDoublePair(data[i], i);
        }

        // Sort the IntDoublePairs
        Arrays.sort(ranks);

        // Walk the sorted array, filling output array using sorted positions,
        // resolving ties as we go
        double[] out = new double[ranks.length];
        int pos = 1;  // position in sorted array
        out[ranks[0].getPosition()] = pos;
        List<Integer> tiesTrace = new LinkedList<>();
        tiesTrace.add(ranks[0].getPosition());
        for (int i = 1; i < ranks.length; i++) {
            if (Double.compare(ranks[i].getValue(), ranks[i - 1].getValue()) > 0) {
                // tie sequence has ended (or had length 1)
                pos = i + 1;
                if (tiesTrace.size() > 1) {  // if seq is nontrivial, resolve
                    resolveTie(out, tiesTrace);
                }
                tiesTrace = new LinkedList<>();
                tiesTrace.add(ranks[i].getPosition());
            } else {
                // tie sequence continues
                tiesTrace.add(ranks[i].getPosition());
            }
            out[ranks[i].getPosition()] = pos;
        }
        if (tiesTrace.size() > 1) {  // handle tie sequence at end
            resolveTie(out, tiesTrace);
        }
        return out;
    }

    private double calculatePearsonCorrelation(double[] xArray, double[] yArray) {
        if (xArray.length != yArray.length) {
            throw new IllegalArgumentException("Array dimensions mismatch");
        } else if (xArray.length < 2) {
            throw new IllegalArgumentException("Insufficient array dimension");
        }

        double diff1, diff2, num = 0.0, sx = 0.0, sy = 0.0;
        double xMean = MolecularDescriptor.mean(xArray), yMean = MolecularDescriptor.mean(yArray);

        for (int i = 0; i < xArray.length; i++) {
            diff1 = xArray[i] - xMean;
            diff2 = yArray[i] - yMean;
            num += (diff1 * diff2);
            sx += (diff1 * diff1);
            sy += (diff2 * diff2);
        }
        return num / Math.sqrt(sx * sy);
    }
    
 /**
     * Resolve a sequence of ties, using the configured {@link TiesStrategy}.
     * The input <code>ranks</code> array is expected to take the same value
     * for all indices in <code>tiesTrace</code>.  The common value is recoded
     * according to the tiesStrategy. For example, if ranks = <5,8,2,6,2,7,1,2>,
     * tiesTrace = <2,4,7> and tiesStrategy is MINIMUM, ranks will be unchanged.
     * The same array and trace with tiesStrategy AVERAGE will come out
     * <5,8,3,6,3,7,1,3>.
     *
     * @param ranks array of ranks
     * @param tiesTrace list of indices where <code>ranks</code> is constant
     * -- that is, for any i and j in TiesTrace, <code> ranks[i] == ranks[j]
     * </code>
     */
    private void resolveTie(double[] ranks, List<Integer> tiesTrace){
//        // constant value of ranks over tiesTrace
        final double c = ranks[tiesTrace.get(0)];

        // length of sequence of tied ranks
        final int length = tiesTrace.size();
        
        // Replace ranks with average 
        double value = (2 * c + length - 1) / 2d;        
        double sum = 0;
        for(int pos: tiesTrace){
            sum+= pos;
        }
        double avg = sum/length; 
                
        Iterator<Integer> iterator = tiesTrace.iterator();
        while (iterator.hasNext()) {
            ranks[iterator.next()] = value;
        }        
    }    

}

class Bin {

    /**
     * The number of items in the bin.
     */
    private int count;

    /**
     * The start boundary.
     */
    private double startBoundary;

    /**
     * The end boundary.
     */
    private double endBoundary;

    /**
     * Creates a new bin.
     *
     * @param startBoundary the start boundary.
     * @param endBoundary the end boundary.
     */
    Bin(double startBoundary, double endBoundary) {
        if (startBoundary > endBoundary) {
            throw new IllegalArgumentException(
                    "Bin:  startBoundary > endBoundary.");
        }
        this.count = 0;
        this.startBoundary = startBoundary;
        this.endBoundary = endBoundary;
    }

    /**
     * Returns the number of items in the bin.
     *
     * @return The item count.
     */
    public int getCount() {
        return this.count;
    }

    /**
     * Increments the item count.
     */
    public void incrementCount() {
        this.count++;
    }

    /**
     * Returns the start boundary.
     *
     * @return The start boundary.
     */
    public double getStartBoundary() {
        return this.startBoundary;
    }

    /**
     * Returns the end boundary.
     *
     * @return The end boundary.
     */
    public double getEndBoundary() {
        return this.endBoundary;
    }

    /**
     * Returns the bin width.
     *
     * @return The bin width.
     */
    public double getBinWidth() {
        return this.endBoundary - this.startBoundary;
    }

}

/**
 * Represents the position of a double value in an ordering. Comparable
 * interface is implemented so Arrays.sort can be used to sort an array of
 * IntDoublePairs by value. Note that the implicitly defined natural ordering is
 * NOT consistent with equals.
 */
class IntDoublePair implements Comparable<IntDoublePair> {

    /**
     * Value of the pair
     */
    private final double value;

    /**
     * Original position of the pair
     */
    private final int position;

    /**
     * Construct an IntDoublePair with the given value and position.
     *
     * @param value the value of the pair
     * @param position the original position
     */
    IntDoublePair(double value, int position) {
        this.value = value;
        this.position = position;
    }

    /**
     * Compare this IntDoublePair to another pair. Only the
     * <strong>values</strong> are compared.
     *
     * @param other the other pair to compare this to
     * @return result of <code>Double.compare(value, other.value)</code>
     */
    public int compareTo(IntDoublePair other) {
        return Double.compare(value, other.value);
    }

    // N.B. equals() and hashCode() are not implemented; see MATH-610 for discussion.
    /**
     * Returns the value of the pair.
     *
     * @return value
     */
    public double getValue() {
        return value;
    }

    /**
     * Returns the original position of the pair.
     *
     * @return position
     */
    public int getPosition() {
        return position;
    }
}

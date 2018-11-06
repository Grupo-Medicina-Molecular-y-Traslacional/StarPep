/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.awt.Cursor;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.swing.BorderFactory;
import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.bapedis.chemspace.model.TwoDSpace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.task.ProgressTicket;
import org.gephi.graph.api.GraphModel;
import org.jdesktop.swingx.JXBusyLabel;
import org.openide.util.Cancellable;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class TwoDChemSpacePanel extends javax.swing.JPanel implements PropertyChangeListener {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected MapperAlgorithm csMapper;
    protected TwoDEmbedder twoDEmbedder;
    protected final JXBusyLabel busyLabel;
    protected final DefaultComboBoxModel<String> modelX, modelY;

    /**
     * Creates new form NDChemSpacePanel
     */
    public TwoDChemSpacePanel() {
        initComponents();

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setVisible(false);
        upperPanel.add(busyLabel, 0);

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                if (csMapper != null) {
                    csMapper.addRunningListener(TwoDChemSpacePanel.this);
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if (csMapper != null) {
                    csMapper.removeRunningListener(TwoDChemSpacePanel.this);
                }
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });

        modelX = new DefaultComboBoxModel<>();
        modelY = new DefaultComboBoxModel<>();

        jXComboBox.setModel(modelX);
        jYComboBox.setModel(modelY);
    }

    public void setUp(MapperAlgorithm csMapper) {
        this.csMapper = csMapper;
        twoDEmbedder = csMapper.getTwoDEmbedderAlg();
    }

    private void setRunning(boolean running) {
        busyLabel.setBusy(running);
        busyLabel.setVisible(running);
        jApplyButton.setEnabled(!running);
        jXComboBox.setEnabled(!running);
        jYComboBox.setEnabled(!running);
        if (running) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    private void setupScatterPlot(boolean running) {
        scatterPanel.removeAll();
        scatterPanel.setBorder(null);
        TwoDSpace twoSpace = twoDEmbedder.getTwoDSpace();
        if (!running && twoSpace != null && twoSpace.getScatterPlot() != null) {
            scatterPanel.setBorder(BorderFactory.createTitledBorder(NbBundle.getMessage(TwoDChemSpacePanel.class, "TwoDChemSpacePanel.scatterPanel.borderTitle")));
            scatterPanel.add(twoSpace.getScatterPlot());
        } 
        scatterPanel.revalidate();
        scatterPanel.repaint();
    }

    private void setupAxis(boolean running) {
        if (running) {
            modelX.removeAllElements();
            modelY.removeAllElements();
        } else {
            TwoDSpace twoDSpace = twoDEmbedder.getTwoDSpace();

            String[] axisLabels = twoDSpace.getAxisLabels();
            for (String axis : axisLabels) {
                modelX.addElement(axis);
                modelY.addElement(axis);
            }
            modelX.setSelectedItem(axisLabels[twoDSpace.getxAxis()]);
            modelY.setSelectedItem(axisLabels[twoDSpace.getyAxis()]);
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
        java.awt.GridBagConstraints gridBagConstraints;

        settingPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jXComboBox = new javax.swing.JComboBox<>();
        jLabel2 = new javax.swing.JLabel();
        jYComboBox = new javax.swing.JComboBox<>();
        scatterPanel = new javax.swing.JPanel();
        upperPanel = new javax.swing.JPanel();
        jApplyButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        settingPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TwoDChemSpacePanel.class, "TwoDChemSpacePanel.settingPanel.border.title"))); // NOI18N
        settingPanel.setLayout(new java.awt.GridBagLayout());

        org.openide.awt.Mnemonics.setLocalizedText(jLabel1, org.openide.util.NbBundle.getMessage(TwoDChemSpacePanel.class, "TwoDChemSpacePanel.jLabel1.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        settingPanel.add(jLabel1, gridBagConstraints);

        jXComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jXComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        settingPanel.add(jXComboBox, gridBagConstraints);

        org.openide.awt.Mnemonics.setLocalizedText(jLabel2, org.openide.util.NbBundle.getMessage(TwoDChemSpacePanel.class, "TwoDChemSpacePanel.jLabel2.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        settingPanel.add(jLabel2, gridBagConstraints);

        jYComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jYComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 3;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        settingPanel.add(jYComboBox, gridBagConstraints);

        scatterPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(TwoDChemSpacePanel.class, "TwoDChemSpacePanel.scatterPanel.border.title"))); // NOI18N
        scatterPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 4;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        settingPanel.add(scatterPanel, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 5, 5);
        add(settingPanel, gridBagConstraints);

        upperPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));

        jApplyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/apply.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(jApplyButton, org.openide.util.NbBundle.getMessage(TwoDChemSpacePanel.class, "TwoDChemSpacePanel.jApplyButton.text")); // NOI18N
        jApplyButton.setToolTipText(org.openide.util.NbBundle.getMessage(TwoDChemSpacePanel.class, "TwoDChemSpacePanel.jApplyButton.toolTipText")); // NOI18N
        jApplyButton.setEnabled(false);
        jApplyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jApplyButtonActionPerformed(evt);
            }
        });
        upperPanel.add(jApplyButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        add(upperPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void jApplyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jApplyButtonActionPerformed
        if (csMapper != null) {
            TwoDSpace twoD = twoDEmbedder.getTwoDSpace();
            twoD.setxAxis(jXComboBox.getSelectedIndex());
            twoD.setyAxis(jYComboBox.getSelectedIndex());

            setRunning(true);
            setupScatterPlot(true);
            GraphNodePositionUpdater updater = new GraphNodePositionUpdater();
            updater.execute();
        }
    }//GEN-LAST:event_jApplyButtonActionPerformed

    private void jXComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jXComboBoxActionPerformed
        jApplyButton.setEnabled(true);
    }//GEN-LAST:event_jXComboBoxActionPerformed

    private void jYComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jYComboBoxActionPerformed
        jApplyButton.setEnabled(true);
    }//GEN-LAST:event_jYComboBoxActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jApplyButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JComboBox<String> jXComboBox;
    private javax.swing.JComboBox<String> jYComboBox;
    private javax.swing.JPanel scatterPanel;
    private javax.swing.JPanel settingPanel;
    private javax.swing.JPanel upperPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (csMapper != null) {
            if (evt.getSource().equals(csMapper)) {
                if (evt.getPropertyName().equals(MapperAlgorithm.RUNNING)) {
                    setupAxis(csMapper.isRunning());
                    setupScatterPlot(csMapper.isRunning());
                }
            }
        }
    }

    class GraphNodePositionUpdater extends SwingWorker<Void, Void> {

        private final AtomicBoolean stopRun;
        private final ProgressTicket ticket;

        public GraphNodePositionUpdater() {
            stopRun = new AtomicBoolean(false);
            ticket = new ProgressTicket(NbBundle.getMessage(NetworkThresholdUpdater.class, "GraphNodePositionUpdater.task.name"), new Cancellable() {
                @Override
                public boolean cancel() {
                    stopRun.set(true);
                    return true;
                }
            });
        }

        @Override
        protected Void doInBackground() throws Exception {
            ticket.start();
            GraphModel graphModel = pc.getGraphModel();
            twoDEmbedder.updateGraphNodePositions(graphModel, ticket, stopRun);
            return null;
        }

        @Override
        protected void done() {
            try {
                get();
            } catch (InterruptedException | ExecutionException ex) {
                Exceptions.printStackTrace(ex);
            } finally {
                setRunning(false);
                setupScatterPlot(false);
                ticket.finish();
            }
        }

    }
}
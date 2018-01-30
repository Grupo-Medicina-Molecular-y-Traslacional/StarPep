/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import org.bapedis.core.spi.algo.impl.FeatureSelectionPanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.spi.algo.impl.FeatureSelectionAlgo;
import org.bapedis.core.spi.algo.impl.FeatureSelectionFactory;
import org.bapedis.core.task.AlgorithmExecutor;
import org.jdesktop.swingx.JXBusyLabel;
import org.openide.DialogDisplayer;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class FeatureFilterPanel extends javax.swing.JPanel implements PropertyChangeListener {

    protected static final AlgorithmExecutor executor = Lookup.getDefault().lookup(AlgorithmExecutor.class);
    protected Workspace workspace;
    protected FeatureSelectionAlgo algo;
    protected final JXBusyLabel busyLabel;
    protected final FeatureSelectionPanel settingPanel;

    /**
     * Creates new form FeatureFilterPanel
     * @param workspace
     */
    public FeatureFilterPanel(Workspace workspace) {
        initComponents();
        
        this.workspace = workspace;
        
        algo = workspace.getLookup().lookup(FeatureSelectionAlgo.class);
        if (algo == null) {
            algo = (FeatureSelectionAlgo) new FeatureSelectionFactory().createAlgorithm();
            workspace.add(algo);
        }            

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setText(NbBundle.getMessage(FeatureSelectionPanel.class, "FeatureSelectionPanel.removing.text"));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        boolean running = algo.isRunning();
        busyLabel.setBusy(running);
        busyLabel.setVisible(running);
        rightPanel.add(busyLabel);
        
        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                algo.addPropertyChangeListener(FeatureFilterPanel.this);
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                algo.removePropertyChangeListener(FeatureFilterPanel.this);
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });   
        
        settingPanel = (FeatureSelectionPanel)algo.getFactory().getSetupUI().getSettingPanel(algo);
        centerPanel.add(settingPanel, BorderLayout.CENTER);
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

        centerPanel = new javax.swing.JPanel();
        rightPanel = new javax.swing.JPanel();
        removeButton = new javax.swing.JButton();

        setMinimumSize(new java.awt.Dimension(440, 380));
        setPreferredSize(new java.awt.Dimension(540, 380));
        setLayout(new java.awt.GridBagLayout());

        centerPanel.setPreferredSize(new java.awt.Dimension(259, 130));
        centerPanel.setLayout(new java.awt.BorderLayout());
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
        org.openide.awt.Mnemonics.setLocalizedText(removeButton, org.openide.util.NbBundle.getMessage(FeatureFilterPanel.class, "FeatureFilterPanel.removeButton.text")); // NOI18N
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

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
        if (workspace.isBusy()) {
            DialogDisplayer.getDefault().notify(workspace.getBusyNotifyDescriptor());
        } else {                        
            executor.execute(algo);
        }
    }//GEN-LAST:event_removeButtonActionPerformed

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource().equals(algo) && evt.getPropertyName().equals(FeatureSelectionAlgo.RUNNING)){
            boolean running = algo.isRunning();
            busyLabel.setBusy(running);
            busyLabel.setVisible(running);  
            settingPanel.refreshState();
        }        
    }    

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JButton removeButton;
    private javax.swing.JPanel rightPanel;
    // End of variables declaration//GEN-END:variables
}

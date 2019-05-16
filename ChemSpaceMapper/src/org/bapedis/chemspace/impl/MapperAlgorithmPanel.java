/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.JToolBar;
import javax.swing.SwingConstants;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
import org.jdesktop.swingx.JXBusyLabel;
import org.jdesktop.swingx.JXHyperlink;
import org.openide.DialogDisplayer;
import org.openide.WizardDescriptor;
import org.openide.util.ImageUtilities;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class MapperAlgorithmPanel extends javax.swing.JPanel implements AlgorithmSetupUI, PropertyChangeListener {

    protected final JXHyperlink openWizardLink, scatter3DLink;
    protected final JXBusyLabel busyLabel;
    protected MapperAlgorithm csMapper;
    protected final NetworkPanel networkPanel;
    protected final ClusterPanel clusterPanel;
    protected final JToolBar toolBar;

    /**
     * Creates new form MapperAlgorithmPanel
     */
    public MapperAlgorithmPanel() {
        initComponents();

        // Tool bar
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        topPanel.add(toolBar);

        scatter3DLink = new JXHyperlink();
        configureScatter3DLink();
        toolBar.add(scatter3DLink);

        toolBar.addSeparator();
        
        openWizardLink = new JXHyperlink();
        configureOpenWizardLink();
        toolBar.add(openWizardLink);

        // Network panel
        networkPanel = new NetworkPanel();
        tab1.add(networkPanel, BorderLayout.CENTER);
        
        // Cluster panel
        clusterPanel = new ClusterPanel();
        tab2.add(clusterPanel, BorderLayout.CENTER);
        
        //Busy label
        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.busyLabel.text"));
        centerPanel.add(busyLabel, "busyCard");

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                if (csMapper != null) {
                    csMapper.addRunningListener(MapperAlgorithmPanel.this);
                }
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if (csMapper != null) {
                    csMapper.removeRunningListener(MapperAlgorithmPanel.this);
                }
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    private void configureOpenWizardLink() {
        openWizardLink.setIcon(ImageUtilities.loadImageIcon("org/bapedis/chemspace/resources/wizard.png", false));
        openWizardLink.setText(NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.openWizardLink.text"));
        openWizardLink.setClickedColor(new java.awt.Color(0, 51, 255));
        openWizardLink.setFocusPainted(false);
        openWizardLink.setFocusable(false);
        openWizardLink.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                if (csMapper != null) {
                    WizardDescriptor wiz = MapperAlgorithmFactory.createWizardDescriptor(csMapper);
                    if (DialogDisplayer.getDefault().notify(wiz) == WizardDescriptor.FINISH_OPTION) {
                        MapperAlgorithmFactory.setUp(csMapper, wiz);
                    }
                }
            }
        });
    }

    private void configureScatter3DLink() {
        scatter3DLink.setIcon(ImageUtilities.loadImageIcon("org/bapedis/chemspace/resources/coordinates.png", false));
        scatter3DLink.setText(NbBundle.getMessage(ClusterPanel.class, "MapperAlgorithmPanel.scatter3DButton.text"));
        scatter3DLink.setToolTipText(NbBundle.getMessage(ClusterPanel.class, "MapperAlgorithmPanel.scatter3DButton.toolTipText"));
        scatter3DLink.setClickedColor(new java.awt.Color(0, 51, 255));
        scatter3DLink.setFocusPainted(false);
        scatter3DLink.setFocusable(false);
        scatter3DLink.addActionListener(new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {

            }
        });
    }

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        this.csMapper = (MapperAlgorithm) algo;
        networkPanel.setUp(csMapper);
        clusterPanel.setUp(csMapper);
        setBusy(csMapper.isRunning());
        return this;
    }

    public void setBusy(boolean busy) {
        scatter3DLink.setEnabled(!busy);
        openWizardLink.setEnabled(!busy);
        topPanel.setEnabled(!busy);
        busyLabel.setBusy(busy);
        
        CardLayout cl = (CardLayout)centerPanel.getLayout();
        cl.show(centerPanel, busy? "busyCard": "tabCard");
    }

    public MapperAlgorithm getCheSMapper() {
        return csMapper;
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

        topPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        tab1 = new javax.swing.JPanel();
        tab2 = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        topPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        add(topPanel, gridBagConstraints);

        centerPanel.setLayout(new java.awt.CardLayout());

        tab1.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.tab1.TabConstraints.tabTitle"), tab1); // NOI18N

        tab2.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab(org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.tab2.TabConstraints.tabTitle"), tab2); // NOI18N

        centerPanel.add(jTabbedPane1, "tabCard");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        add(centerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel tab1;
    private javax.swing.JPanel tab2;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (csMapper != null && evt.getSource().equals(csMapper)
                && evt.getPropertyName().equals(MapperAlgorithm.RUNNING)) {            
            clusterPanel.setupClusters();
            networkPanel.setupAxis();
            networkPanel.setupHistogram();
            setBusy((boolean) evt.getNewValue());
        }
    }

}

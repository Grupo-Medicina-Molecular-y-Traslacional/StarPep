/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.awt.CardLayout;
import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
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

    protected final JXHyperlink openWizardLink;
    protected final JXBusyLabel busyLabel;
    protected MapperAlgorithm csMapper;
    protected final ChemSpaceNetworkPanel networkPanel;
    protected final NDChemSpacePanel nDPanel;

    /**
     * Creates new form MapperAlgorithmPanel
     */
    public MapperAlgorithmPanel() {
        initComponents();

        openWizardLink = new JXHyperlink();
        configureOpenWizardLink();
        topRightPanel.add(openWizardLink);

        busyLabel = new JXBusyLabel(new Dimension(20, 20));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        busyLabel.setText(NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.busyLabel.text"));
        centerPanel.add(busyLabel, "busy");
        
        nDPanel = new NDChemSpacePanel();
        centerPanel.add(nDPanel, "nDChemSpace");

        networkPanel = new ChemSpaceNetworkPanel();
        centerPanel.add(networkPanel, "chemSpaceNetwork");                  
        
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
                        refreshChemSpaceOption();
                    }
                }
            }
        });
    }
        
    private void refreshChemSpaceOption() {
        CardLayout centerCL = (CardLayout) centerPanel.getLayout();
        CardLayout topLeftCl = (CardLayout) topLeftPanel.getLayout();
        switch (csMapper.getChemSpaceOption()) {
            case N_DIMENSIONAL_SPACE:
                nDPanel.setUp(csMapper);
                topLeftCl.show(topLeftPanel, "nDChemSpace");
                centerCL.show(centerPanel, "nDChemSpace");
                break;
            case CHEM_SPACE_NETWORK:
                networkPanel.setUp(csMapper);
                topLeftCl.show(topLeftPanel, "chemSpaceNetwork");
                centerCL.show(centerPanel, "chemSpaceNetwork");
                break;
        }
    }

    @Override
    public JPanel getSettingPanel(Algorithm algo) {
        this.csMapper = (MapperAlgorithm) algo;
        refreshChemSpaceOption();
        setBusy(csMapper.isRunning());
        return this;
    }


    public void setBusy(boolean busy) {
        busyLabel.setBusy(busy);
        if (busy) {
            CardLayout centerCL = (CardLayout) centerPanel.getLayout();
            centerCL.show(centerPanel, "busy");
        } else {
            refreshChemSpaceOption();
        }
        openWizardLink.setEnabled(!busy);
        topLeftPanel.setEnabled(!busy);
        topRightPanel.setEnabled(!busy);
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

        topLeftPanel = new javax.swing.JPanel();
        nDLabel = new javax.swing.JLabel();
        csnLabel = new javax.swing.JLabel();
        topRightPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        topLeftPanel.setLayout(new java.awt.CardLayout());

        nDLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(nDLabel, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.nDLabel.text")); // NOI18N
        nDLabel.setToolTipText(org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.nDLabel.toolTipText")); // NOI18N
        topLeftPanel.add(nDLabel, "nDChemSpace");

        csnLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(csnLabel, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.csnLabel.text")); // NOI18N
        csnLabel.setToolTipText(org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.csnLabel.toolTipText")); // NOI18N
        topLeftPanel.add(csnLabel, "chemSpaceNetwork");

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        add(topLeftPanel, gridBagConstraints);

        topRightPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        add(topRightPanel, gridBagConstraints);

        centerPanel.setLayout(new java.awt.CardLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 5, 5);
        add(centerPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel centerPanel;
    private javax.swing.JLabel csnLabel;
    private javax.swing.JLabel nDLabel;
    private javax.swing.JPanel topLeftPanel;
    private javax.swing.JPanel topRightPanel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (csMapper != null && evt.getSource().equals(csMapper)
                && evt.getPropertyName().equals(MapperAlgorithm.RUNNING)) {
            setBusy((boolean) evt.getNewValue());
        }
    }

}

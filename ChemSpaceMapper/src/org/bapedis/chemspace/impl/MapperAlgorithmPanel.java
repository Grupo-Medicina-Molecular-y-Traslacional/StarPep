/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.awt.CardLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmSetupUI;
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
    protected MapperAlgorithm csMapper;
    protected final TwoDChemSpacePanel twoDPanel;
    protected final NetworkPanel csnPanel;
    protected final NetworkPanel ssnPanel;    

    /**
     * Creates new form MapperAlgorithmPanel
     */
    public MapperAlgorithmPanel() {
        initComponents();
        
        openWizardLink = new JXHyperlink();
        configureOpenWizardLink();
        topRightPanel.add(openWizardLink);
        
        twoDPanel = new TwoDChemSpacePanel();
        centerPanel.add(twoDPanel, "2DChemSpace");

        csnPanel = new NetworkPanel();
        centerPanel.add(csnPanel, "chemSpaceNetwork");   
        
        ssnPanel = new NetworkPanel();
        centerPanel.add(ssnPanel, "seqSimilarityNetwork");
        
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
            case TwoD_SPACE:
                twoDPanel.setUp(csMapper);
                topLeftCl.show(topLeftPanel, "2DChemSpace");
                centerCL.show(centerPanel, "2DChemSpace");
                break;
            case CHEM_SPACE_NETWORK:
                csnPanel.setUp(csMapper);
                topLeftCl.show(topLeftPanel, "chemSpaceNetwork");
                centerCL.show(centerPanel, "chemSpaceNetwork");
                break;
            case SEQ_SIMILARITY_NETWORK:
                ssnPanel.setUp(csMapper);
                topLeftCl.show(topLeftPanel, "seqSimilarityNetwork");
                centerCL.show(centerPanel, "seqSimilarityNetwork");
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
        openWizardLink.setEnabled(!busy);
        switch (csMapper.getChemSpaceOption()) {
            case TwoD_SPACE:
                twoDLabel.setEnabled(!busy);
                twoDPanel.setEnabled(!busy);
                break;
            case CHEM_SPACE_NETWORK:
                csnLabel.setEnabled(!busy);
                csnPanel.setEnabled(!busy);
                break;
            case SEQ_SIMILARITY_NETWORK:
                ssnLabel.setEnabled(!busy);
                ssnPanel.setEnabled(!busy);
                break;
        }        
        topLeftPanel.setEnabled(!busy);
        topRightPanel.setEnabled(!busy);
        centerPanel.setEnabled(!busy);
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
        twoDLabel = new javax.swing.JLabel();
        csnLabel = new javax.swing.JLabel();
        ssnLabel = new javax.swing.JLabel();
        topRightPanel = new javax.swing.JPanel();
        centerPanel = new javax.swing.JPanel();

        setLayout(new java.awt.GridBagLayout());

        topLeftPanel.setLayout(new java.awt.CardLayout());

        twoDLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(twoDLabel, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.twoDLabel.text")); // NOI18N
        twoDLabel.setToolTipText(org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.twoDLabel.toolTipText")); // NOI18N
        topLeftPanel.add(twoDLabel, "2DChemSpace");

        csnLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(csnLabel, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.csnLabel.text")); // NOI18N
        csnLabel.setToolTipText(org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.csnLabel.toolTipText")); // NOI18N
        topLeftPanel.add(csnLabel, "chemSpaceNetwork");

        ssnLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(ssnLabel, org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.ssnLabel.text")); // NOI18N
        ssnLabel.setToolTipText(org.openide.util.NbBundle.getMessage(MapperAlgorithmPanel.class, "MapperAlgorithmPanel.ssnLabel.toolTipText")); // NOI18N
        topLeftPanel.add(ssnLabel, "seqSimilarityNetwork");

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
    private javax.swing.JLabel ssnLabel;
    private javax.swing.JPanel topLeftPanel;
    private javax.swing.JPanel topRightPanel;
    private javax.swing.JLabel twoDLabel;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (csMapper != null && evt.getSource().equals(csMapper)
                && evt.getPropertyName().equals(MapperAlgorithm.RUNNING)) {
            setBusy((boolean) evt.getNewValue());
        }
    }

}

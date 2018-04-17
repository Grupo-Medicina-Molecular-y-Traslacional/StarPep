/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.impl;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JPanel;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;
import org.bapedis.chemspace.model.ChemSpaceModel;
import org.bapedis.core.model.AlgorithmNode;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.AlgorithmErrorHandler;
import org.bapedis.core.task.AlgorithmExecutor;
import org.bapedis.core.task.AlgorithmListener;
import org.bapedis.core.ui.components.AlgDescriptionImage;
import org.bapedis.core.ui.components.AlgorithmFactoryItem;
import org.openide.util.NbBundle;
import org.bapedis.core.ui.components.PropertySheetPanel;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.openide.DialogDisplayer;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author loge
 */
public class ChemSpacePanel extends javax.swing.JPanel implements PropertyChangeListener {

    protected static final ProjectManager pc = Lookup.getDefault().lookup(ProjectManager.class);
    protected static final AlgorithmExecutor executor = Lookup.getDefault().lookup(AlgorithmExecutor.class);
    protected MapperAlgorithmPanel parentPanel;
    protected ChemSpaceModel csModel;
    protected final String NO_SELECTION;
    private RichTooltip richTooltip;

    /**
     * Creates new form BaseChemSpacePanel
     */
    public ChemSpacePanel(List<? extends AlgorithmFactory> factories) {
        initComponents();
        NO_SELECTION = NbBundle.getMessage(ChemSpacePanel.class, "ChemSpacePanel.choose.text");
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        comboBoxModel.addElement(NO_SELECTION);
        comboBoxModel.setSelectedItem(NO_SELECTION);

        if (factories != null) {
            for (AlgorithmFactory factory : factories) {
                AlgorithmFactoryItem item = new AlgorithmFactoryItem(factory);
                comboBoxModel.addElement(item);
            }
        }

        algComboBox.setModel(comboBoxModel);
        setEnableState(false);

        addAncestorListener(new AncestorListener() {
            @Override
            public void ancestorAdded(AncestorEvent event) {
                Workspace workspace = pc.getCurrentWorkspace();
                csModel = workspace.getLookup().lookup(ChemSpaceModel.class);
                if (csModel == null) {
                    csModel = new ChemSpaceModel(workspace);
                    workspace.add(csModel);
                }
                csModel.addPropertyChangeListener(ChemSpacePanel.this);
                refreshAlgChooser();
                refreshProperties();
                refreshRunning();
            }

            @Override
            public void ancestorRemoved(AncestorEvent event) {
                if (csModel != null) {
                    csModel.removePropertyChangeListener(ChemSpacePanel.this);
                }
            }

            @Override
            public void ancestorMoved(AncestorEvent event) {
            }
        });
    }

    public MapperAlgorithmPanel getParentPanel() {
        return parentPanel;
    }

    public void setParentPanel(MapperAlgorithmPanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    private void refreshAlgChooser() {
        if (csModel != null) {
            DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) algComboBox.getModel();
            Algorithm alg = csModel.getSelectedAlgorithm();
            AlgorithmFactory factory = (alg != null) ? alg.getFactory() : null;
            if (factory != null) {
                for (int i = 1; i < comboBoxModel.getSize(); i++) {
                    if (factory == ((AlgorithmFactoryItem) comboBoxModel.getElementAt(i)).getFactory()) {
                        comboBoxModel.setSelectedItem(comboBoxModel.getElementAt(i));
                    }
                }
            } else {
                comboBoxModel.setSelectedItem(NO_SELECTION);
            }
            setEnableState(!comboBoxModel.getSelectedItem().equals(NO_SELECTION));
        }

    }

    private void refreshProperties() {
        if (csModel == null || csModel.getSelectedAlgorithm() == null) {
            ((PropertySheetPanel) propSheetPanel).getPropertySheet().setNodes(new Node[0]);
            scrollPane.setViewportView(null);
            scrollPane.setVisible(false);
            propSheetPanel.setVisible(true);
        } else {
            Algorithm selectedAlgorithm = csModel.getSelectedAlgorithm();

            if (selectedAlgorithm.getFactory().getSetupUI() != null) {
                JPanel settingPanel = selectedAlgorithm.getFactory().getSetupUI().getSettingPanel(selectedAlgorithm);
                propSheetPanel.setVisible(false);
                scrollPane.setViewportView(settingPanel);
                scrollPane.setVisible(true);
            } else {
                scrollPane.setViewportView(null);
                scrollPane.setVisible(false);
                ((PropertySheetPanel) propSheetPanel).getPropertySheet().setNodes(new Node[]{new AlgorithmNode(selectedAlgorithm)});
                propSheetPanel.setVisible(true);
            }

            DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) algComboBox.getModel();
            for (int i = 1; i < comboBoxModel.getSize(); i++) {
                AlgorithmFactoryItem item = (AlgorithmFactoryItem) comboBoxModel.getElementAt(i);
                if (item.getFactory().equals(selectedAlgorithm.getFactory()) && !comboBoxModel.getSelectedItem().equals(item)) {
                    comboBoxModel.setSelectedItem(item);
                }
            }
        }
        richTooltip = null;
        revalidate();
        repaint();
    }

    private void refreshRunning() {
        if (csModel != null) {
            boolean running = csModel.isRunning();
            if (parentPanel != null) {
                parentPanel.setBusy(running);
            }
            propSheetPanel.setEnabled(!running);
            if (scrollPane.getViewport().getView() != null) {
                scrollPane.getViewport().getView().setEnabled(!running);
            }
            algComboBox.setEnabled(!running);
            if (running) {
                applyButton.setText(NbBundle.getMessage(ChemSpacePanel.class, "ChemSpacePanel.stopButton.text"));
//                applyButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/stop.png", false));
                applyButton.setToolTipText(NbBundle.getMessage(ChemSpacePanel.class, "ChemSpacePanel.stopButton.tooltip"));
            } else {
                applyButton.setText(NbBundle.getMessage(ChemSpacePanel.class, "ChemSpacePanel.applyButton.text"));
//                applyButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/run.gif", false));
                applyButton.setToolTipText(NbBundle.getMessage(ChemSpacePanel.class, "ChemSpacePanel.applyButton.toolTipText"));
            }
        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled); //To change body of generated methods, choose Tools | Templates.
        setEnableState(enabled);
    }
        
    private void setEnableState(boolean enabled) {
        infoLabel.setEnabled(enabled);
        applyButton.setEnabled(enabled);
        propSheetPanel.setEnabled(enabled);
        scrollPane.setEnabled(enabled);
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

        algComboBox = new javax.swing.JComboBox<>();
        infoLabel = new javax.swing.JLabel();
        applyButton = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        propSheetPanel = new PropertySheetPanel();

        setLayout(new java.awt.GridBagLayout());

        algComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                algComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        add(algComboBox, gridBagConstraints);

        infoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(ChemSpacePanel.class, "ChemSpacePanel.infoLabel.text")); // NOI18N
        infoLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseExited(java.awt.event.MouseEvent evt) {
                infoLabelMouseExited(evt);
            }
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                infoLabelMouseEntered(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 2;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 5);
        add(infoLabel, gridBagConstraints);

        applyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/chemspace/resources/apply.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(applyButton, org.openide.util.NbBundle.getMessage(ChemSpacePanel.class, "ChemSpacePanel.applyButton.text")); // NOI18N
        applyButton.setToolTipText(org.openide.util.NbBundle.getMessage(ChemSpacePanel.class, "ChemSpacePanel.applyButton.toolTipText")); // NOI18N
        applyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        add(applyButton, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(scrollPane, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = 3;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(propSheetPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void algComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_algComboBoxActionPerformed
        if (csModel != null) {
            if (algComboBox.getSelectedItem() instanceof AlgorithmFactoryItem) {
                AlgorithmFactory factory = ((AlgorithmFactoryItem) algComboBox.getSelectedItem()).getFactory();
                Workspace currentWs = csModel.getWorkspace();
                Collection<? extends Algorithm> savedAlgo = currentWs.getLookup().lookupAll(Algorithm.class);
                Algorithm algorithm = null;
                for (Algorithm algo : savedAlgo) {
                    if (algo.getFactory() == factory) {
                        algorithm = algo;
                        break;
                    }
                }
                boolean addToWS = false;
                if (algorithm == null) {
                    algorithm = factory.createAlgorithm();
                    addToWS = true;
                }

                if (algorithm != null) {
                    if (addToWS) {
                        currentWs.add(algorithm);
                    }
                    csModel.setSelectedAlgorithm(algorithm);
                    setEnableState(true);
                } else {
                    algComboBox.setSelectedItem(NO_SELECTION);
                    setEnableState(false);
                }
            } else {
                csModel.setSelectedAlgorithm(null);
                setEnableState(false);
            }
        }
    }//GEN-LAST:event_algComboBoxActionPerformed

    private void infoLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseExited
        if (richTooltip != null) {
            richTooltip.hideTooltip();
        }
    }//GEN-LAST:event_infoLabelMouseExited

    private void infoLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseEntered
        if (richTooltip == null) {
            if (infoLabel.isEnabled() && csModel != null && csModel.getSelectedAlgorithm() != null) {
                richTooltip = buildTooltip(csModel.getSelectedAlgorithm().getFactory());
            }
        }
        if (richTooltip != null) {
            richTooltip.showTooltip(infoLabel, evt.getLocationOnScreen());
        }
    }//GEN-LAST:event_infoLabelMouseEntered

    private void applyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyButtonActionPerformed
        if (csModel != null) {
            Algorithm algo = csModel.getSelectedAlgorithm();
            if (algo != null) {
                if (csModel.isRunning() && executor.cancel(algo)) {
                    csModel.setRunning(false);
                } else {
                    Workspace currentWS = pc.getCurrentWorkspace();
                    if (currentWS.isBusy()) {
                        DialogDisplayer.getDefault().notify(currentWS.getBusyNotifyDescriptor());
                    } else {
                        csModel.setRunning(true);
                        executor.execute(algo, new AlgorithmListener() {
                            @Override
                            public void algorithmFinished(Algorithm algo) {
                                if (csModel != null) {
                                    csModel.setRunning(false);
                                }
                            }
                        }, new AlgorithmErrorHandler() {
                            @Override
                            public void fatalError(Throwable t) {
                                Exceptions.printStackTrace(t);
                                csModel.setRunning(false);
                            }
                        });
                    }
                }
            }
        }
    }//GEN-LAST:event_applyButtonActionPerformed

    private RichTooltip buildTooltip(AlgorithmFactory factory) {
        String description = factory.getDescription();
        RichTooltip tooltip = new RichTooltip(factory.getName(), description);
        int qualityRank = factory.getQualityRank();
        int speedRank = factory.getSpeedRank();
        if (qualityRank > 0 && qualityRank <= 5 && speedRank > 0 && speedRank <= 5) {
            AlgDescriptionImage algoDescriptionImage = new AlgDescriptionImage(factory);
            tooltip.setMainImage(algoDescriptionImage.getImage());
        }
        return tooltip;
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> algComboBox;
    private javax.swing.JButton applyButton;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JPanel propSheetPanel;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (csModel != null && evt.getSource().equals(csModel)) {
            if (evt.getPropertyName().equals(ChemSpaceModel.CHANGED_ALGORITHM)) {
                refreshProperties();
            } else if (evt.getPropertyName().equals(ChemSpaceModel.RUNNING)) {
                refreshRunning();
            }
        }
    }
}

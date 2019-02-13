/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSeparator;
import org.openide.filesystems.FileObject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.bapedis.core.events.WorkspaceEventListener;
import org.bapedis.core.model.AlgorithmModel;
import org.bapedis.core.model.AlgorithmNode;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.alg.Algorithm;
import org.bapedis.core.spi.alg.AlgorithmFactory;
import org.bapedis.core.task.AlgorithmExecutor;
import org.bapedis.core.ui.components.AlgDescriptionImage;
import org.bapedis.core.ui.components.AlgorithmFactoryItem;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.bapedis.core.ui.components.PropertySheetPanel;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.StatusDisplayer;
import org.openide.explorer.ExplorerUtils;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
import org.openide.util.lookup.Lookups;
import org.openide.util.lookup.ProxyLookup;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * Top component which displays something.
 */
@ConvertAsProperties(
        dtd = "-//org.bapedis.core.ui//AlgoExplorer//EN",
        autostore = false
)
@TopComponent.Description(
        preferredID = "AlgoExplorerTopComponent",
        //iconBase="SET/PATH/TO/ICON/HERE", 
        persistenceType = TopComponent.PERSISTENCE_ALWAYS
)
@TopComponent.Registration(mode = "explorer", openAtStartup = true, position = 533)
@ActionID(category = "Window", id = "org.bapedis.core.ui.AlgoExplorerTopComponent")
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_AlgoExplorerAction",
        preferredID = "AlgoExplorerTopComponent"
)
@Messages({
    "CTL_AlgoExplorerAction=Tool",
    "CTL_AlgoExplorerTopComponent=Tool",
    "HINT_AlgoExplorerTopComponent=Tool window"
})
public final class AlgoExplorerTopComponent extends TopComponent implements WorkspaceEventListener, PropertyChangeListener, LookupListener {

    protected final ProjectManager pc;
    protected final AlgorithmExecutor executor;
    private RichTooltip richTooltip;
    private final AlgoPresetPersistence algoPresetPersistence;
    protected Lookup.Result<AttributesModel> peptideLkpResult;
    protected final String NO_SELECTION;

    public AlgoExplorerTopComponent() {
        initComponents();
        setName(Bundle.CTL_AlgoExplorerTopComponent());
        setToolTipText(Bundle.HINT_AlgoExplorerTopComponent());
        pc = Lookup.getDefault().lookup(ProjectManager.class);
        executor = Lookup.getDefault().lookup(AlgorithmExecutor.class);
        algoPresetPersistence = new AlgoPresetPersistence();
        NO_SELECTION = NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.choose.text");
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        comboBoxModel.addElement(NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.choose.defaultText"));
        algoComboBox.setModel(comboBoxModel);

        associateLookup(new ProxyLookup(Lookups.singleton(new MetadataNavigatorLookupHint()),
                Lookups.singleton(new StructureNavigatorLookupHint()),
                Lookups.singleton(new GraphElementNavigatorLookupHint()),
                Lookups.singleton(new ClusterNavigatorLookupHint())));        
    }

    private void removeLookupListener() {
        if (peptideLkpResult != null) {
            peptideLkpResult.removeLookupListener(this);
            peptideLkpResult = null;
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        algoComboBox = new javax.swing.JComboBox<>();
        infoLabel = new javax.swing.JLabel();
        runButton = new javax.swing.JButton();
        scrollPane = new javax.swing.JScrollPane();
        propSheetPanel = new PropertySheetPanel();
        algoToolBar = new javax.swing.JToolBar();
        presetsButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();

        setLayout(new java.awt.GridBagLayout());

        algoComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                algoComboBoxActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        add(algoComboBox, gridBagConstraints);

        infoLabel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/info.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(infoLabel, org.openide.util.NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.infoLabel.text")); // NOI18N
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

        runButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/run.gif"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(runButton, org.openide.util.NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.runButton.text")); // NOI18N
        runButton.setToolTipText(org.openide.util.NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.runButton.tooltip")); // NOI18N
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(2, 5, 0, 0);
        add(runButton, gridBagConstraints);
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

        algoToolBar.setFloatable(false);
        algoToolBar.setRollover(true);
        algoToolBar.setOpaque(false);

        presetsButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/preset.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(presetsButton, org.openide.util.NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.presetsButton.text_1")); // NOI18N
        presetsButton.setFocusable(false);
        presetsButton.setIconTextGap(0);
        presetsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                presetsButtonActionPerformed(evt);
            }
        });
        algoToolBar.add(presetsButton);

        resetButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/org/bapedis/core/resources/refresh.png"))); // NOI18N
        org.openide.awt.Mnemonics.setLocalizedText(resetButton, org.openide.util.NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.resetButton.text")); // NOI18N
        resetButton.setFocusable(false);
        resetButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });
        algoToolBar.add(resetButton);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        add(algoToolBar, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void infoLabelMouseEntered(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseEntered
        if (richTooltip == null) {
            AlgorithmModel algoModel = pc.getAlgorithmModel();
            if (infoLabel.isEnabled() && algoModel.getSelectedAlgorithm() != null) {
                richTooltip = buildTooltip(algoModel.getSelectedAlgorithm().getFactory());
            }
        }
        if (richTooltip != null) {
            richTooltip.showTooltip(infoLabel, evt.getLocationOnScreen());
        }
    }//GEN-LAST:event_infoLabelMouseEntered

    private void infoLabelMouseExited(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_infoLabelMouseExited
        if (richTooltip != null) {
            richTooltip.hideTooltip();
        }
    }//GEN-LAST:event_infoLabelMouseExited

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        Workspace currentWs = pc.getCurrentWorkspace();
        if (currentWs.isBusy()) {
            DialogDisplayer.getDefault().notify(currentWs.getBusyNotifyDescriptor());
        } else {
            AlgorithmModel algoModel = pc.getAlgorithmModel();
            Algorithm oldAlgo = algoModel.getSelectedAlgorithm();
            AlgorithmFactory factory;
            if (algoComboBox.getSelectedItem() instanceof AlgorithmFactoryItem) {
                factory = ((AlgorithmFactoryItem) algoComboBox.getSelectedItem()).getFactory();
                Algorithm newAlgo = factory.createAlgorithm();
                if (newAlgo != null) {
                    currentWs.remove(oldAlgo);
                    currentWs.add(newAlgo);
                    algoModel.setSelectedAlgorithm(newAlgo);
                }
            }
        }
    }//GEN-LAST:event_resetButtonActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        AlgorithmModel algoModel = pc.getAlgorithmModel();
        Algorithm algo = algoModel.getSelectedAlgorithm();
        if (algo != null) {
            if (algoModel.isRunning()) {
                executor.cancel(algo);
            } else {
                Workspace currentWS = pc.getCurrentWorkspace();
                if (currentWS.isBusy()) {
                    DialogDisplayer.getDefault().notify(currentWS.getBusyNotifyDescriptor());
                } else {
                    algoModel.setRunning(true);
                    executor.execute(algo);
                }
            }
        }
    }//GEN-LAST:event_runButtonActionPerformed

    private void algoComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_algoComboBoxActionPerformed
        AlgorithmModel algoModel = pc.getAlgorithmModel();
        if (algoComboBox.getSelectedItem() instanceof AlgorithmFactoryItem) {
            AlgorithmFactory factory = ((AlgorithmFactoryItem) algoComboBox.getSelectedItem()).getFactory();
            Workspace currentWs = pc.getCurrentWorkspace();
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
                algoModel.setSelectedAlgorithm(algorithm);
                setEnableState(true);
            } else {
                algoComboBox.setSelectedItem(NO_SELECTION);
                setEnableState(false);
            }
        } else {
            algoModel.setSelectedAlgorithm(null);
            setEnableState(false);
        }
    }//GEN-LAST:event_algoComboBoxActionPerformed

    private void presetsButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_presetsButtonActionPerformed
        final AlgorithmModel algoModel = pc.getAlgorithmModel();
        if (algoModel.getSelectedAlgorithm() != null) {
            JPopupMenu menu = new JPopupMenu();
            List<AlgoPresetPersistence.Preset> presets = algoPresetPersistence.getPresets(algoModel.getSelectedAlgorithm());
            if (presets != null && !presets.isEmpty()) {
                for (final AlgoPresetPersistence.Preset p : presets) {
                    JMenuItem item = new JMenuItem(p.toString());
                    item.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            algoPresetPersistence.loadPreset(p, algoModel.getSelectedAlgorithm());
                            refreshProperties(algoModel);
                            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.status.loadPreset", algoModel.getSelectedAlgorithm().getFactory().getName(), p.toString()));
                        }
                    });
                    menu.add(item);
                }
            } else {
                menu.add("<html><i>" + NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.presetsButton.nopreset") + "</i></html>");
            }

            JMenuItem saveItem = new JMenuItem(NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.presetsButton.savePreset"));
            saveItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String lastPresetName = NbPreferences.forModule(AlgoExplorerTopComponent.class).get("AlgoExplorerTopComponent.lastPresetName", "");
                    NotifyDescriptor.InputLine question = new NotifyDescriptor.InputLine(
                            NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.presetsButton.savePreset.input"),
                            NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.presetsButton.savePreset.input.name"));
                    question.setInputText(lastPresetName);
                    if (DialogDisplayer.getDefault().notify(question) == NotifyDescriptor.OK_OPTION) {
                        String input = question.getInputText();
                        if (input != null && !input.isEmpty()) {
                            algoPresetPersistence.savePreset(input, algoModel.getSelectedAlgorithm());
                            StatusDisplayer.getDefault().setStatusText(NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.status.savePreset", algoModel.getSelectedAlgorithm().getFactory().getName(), input));
                            NbPreferences.forModule(AlgoExplorerTopComponent.class).put("AlgoExplorerTopComponent.lastPresetName", input);
                        }
                    }
                }
            });
            menu.add(new JSeparator());
            menu.add(saveItem);
            menu.show(algoToolBar, 0, -menu.getPreferredSize().height);
        }
    }//GEN-LAST:event_presetsButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> algoComboBox;
    private javax.swing.JToolBar algoToolBar;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JButton presetsButton;
    private javax.swing.JPanel propSheetPanel;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton runButton;
    private javax.swing.JScrollPane scrollPane;
    // End of variables declaration//GEN-END:variables
    @Override
    public void componentOpened() {
        pc.addWorkspaceEventListener(this);
        Workspace currentWs = pc.getCurrentWorkspace();
        workspaceChanged(null, currentWs);
    }

    @Override
    public void componentClosed() {
        removeLookupListener();
        pc.removeWorkspaceEventListener(this);
        AlgorithmModel oldModel = pc.getAlgorithmModel();
        oldModel.removePropertyChangeListener(this);
    }

    void writeProperties(java.util.Properties p) {
        // better to version settings since initial version as advocated at
        // http://wiki.apidesign.org/wiki/PropertyFiles
        p.setProperty("version", "1.0");
        // TODO store your settings
    }

    void readProperties(java.util.Properties p) {
        String version = p.getProperty("version");
        // TODO read your settings according to their version
    }

    @Override
    public synchronized void workspaceChanged(Workspace oldWs, Workspace newWs) {
        removeLookupListener();
        Class oldAlgTag = null;
        if (oldWs != null) {
            AlgorithmModel oldModel = pc.getAlgorithmModel(oldWs);
            oldModel.removePropertyChangeListener(this);
            oldAlgTag = oldModel.getTagInterface();
        }
        AlgorithmModel algoModel = pc.getAlgorithmModel(newWs);
        if (algoModel.getTagInterface() == null && oldAlgTag != null) {
            algoModel.setTagInterface(oldAlgTag);
        }
        if (algoModel.getTagInterface() != null) {
            refreshDisplayName(algoModel);
            refreshAlgChooser(algoModel);
            refreshProperties(algoModel);
            refreshRunning(algoModel.isRunning());
        }
        algoModel.addPropertyChangeListener(this);
        if (pc.getAttributesModel() == null) {
            algoComboBox.setEnabled(false);
            setEnableState(false);
        }
        peptideLkpResult = newWs.getLookup().lookupResult(AttributesModel.class);
        peptideLkpResult.addLookupListener(this);

    }

    private void refreshDisplayName(AlgorithmModel algoModel) {
        Class tag = algoModel.getTagInterface();
        String displayName = Bundle.CTL_AlgoExplorerTopComponent();
        try {
            displayName = NbBundle.getMessage(AlgoExplorerTopComponent.class, String.format("AlgoExplorerTopComponent.%s.name", tag.getSimpleName()));
        } catch (MissingResourceException ex) {
            if (algoModel.getSelectedAlgorithm() != null) {
                AlgorithmFactory factory = algoModel.getSelectedAlgorithm().getFactory();
                if (factory.getCategory() != null) {
                    displayName = factory.getCategory();
                } else {
                    displayName = factory.getName();
                }
            }
        }
        setDisplayName(displayName);
    }

    private void refreshAlgChooser(AlgorithmModel algoModel) {
        Class tag = algoModel.getTagInterface();
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        comboBoxModel.addElement(NO_SELECTION);
        comboBoxModel.setSelectedItem(NO_SELECTION);

        List<? extends AlgorithmFactory> factories = new ArrayList<>(Lookup.getDefault().lookupAll(AlgorithmFactory.class));
        for (Iterator<? extends AlgorithmFactory> it = factories.iterator(); it.hasNext();) {
            AlgorithmFactory f = it.next();
            if (!tag.isAssignableFrom(f.getClass())) {
                it.remove();
            }
        }

        for (AlgorithmFactory factory : factories) {
            AlgorithmFactoryItem item = new AlgorithmFactoryItem(factory);
            comboBoxModel.addElement(item);
        }
        algoComboBox.setModel(comboBoxModel);
        setEnableState(!comboBoxModel.getSelectedItem().equals(NO_SELECTION));
    }

    private void refreshProperties(AlgorithmModel algoModel) {
        if (algoModel == null || algoModel.getSelectedAlgorithm() == null) {
            ((PropertySheetPanel) propSheetPanel).getPropertySheet().setNodes(new Node[0]);
            scrollPane.setViewportView(null);
            scrollPane.setVisible(false);
            propSheetPanel.setVisible(true);
        } else {
            Algorithm selectedAlgorithm = algoModel.getSelectedAlgorithm();

            if (selectedAlgorithm.getFactory().getSetupUI() != null) {
                JPanel settingPanel = selectedAlgorithm.getFactory().getSetupUI().getSettingPanel(selectedAlgorithm);
                propSheetPanel.setVisible(false);
                presetsButton.setVisible(false);
                scrollPane.setViewportView(settingPanel);
                scrollPane.setVisible(true);
            } else {
                scrollPane.setViewportView(null);
                scrollPane.setVisible(false);
                ((PropertySheetPanel) propSheetPanel).getPropertySheet().setNodes(new Node[]{new AlgorithmNode(selectedAlgorithm)});
                propSheetPanel.setVisible(true);
                presetsButton.setVisible(true);
            }

            DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) algoComboBox.getModel();
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

    private void setEnableState(boolean enabled) {
        infoLabel.setEnabled(enabled);
        runButton.setEnabled(enabled);
        propSheetPanel.setEnabled(enabled);
        scrollPane.setEnabled(enabled);
        resetButton.setEnabled(enabled);
        presetsButton.setEnabled(enabled);
    }

    private void refreshRunning(boolean running) {
        propSheetPanel.setEnabled(!running);
        setBusy(running);
        if (scrollPane.getViewport().getView() != null) {
            scrollPane.getViewport().getView().setEnabled(!running);
        }
        resetButton.setEnabled(!running);
        presetsButton.setEnabled(!running);
        algoComboBox.setEnabled(!running);
        if (running) {
            runButton.setText(NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.stopButton.text"));
            runButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/stop.png", false));
            runButton.setToolTipText(NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.stopButton.tooltip"));
        } else {
            runButton.setText(NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.runButton.text"));
            runButton.setIcon(ImageUtilities.loadImageIcon("org/bapedis/core/resources/run.gif", false));
            runButton.setToolTipText(NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.runButton.tooltip"));
        }
    }

    private void setBusy(boolean busy) {
        if (busy) {
            setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
        } else {
            setCursor(Cursor.getDefaultCursor());
        }
    }

    @Override
    public synchronized void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof AlgorithmModel) {
            if (evt.getPropertyName().equals(AlgorithmModel.CHANGED_CATEGORY)) {
                AlgorithmModel algoModel = (AlgorithmModel) evt.getSource();
                refreshAlgChooser(algoModel);
            } else if (evt.getPropertyName().equals(AlgorithmModel.CHANGED_ALGORITHM)) {
                AlgorithmModel algoModel = (AlgorithmModel) evt.getSource();
                refreshDisplayName(algoModel);
                refreshProperties(algoModel);
            } else if (evt.getPropertyName().equals(AlgorithmModel.RUNNING)) {
                AlgorithmModel algoModel = (AlgorithmModel) evt.getSource();
                refreshRunning(algoModel.isRunning());
            }
        }
    }

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

    @Override
    public synchronized void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)) {
            Collection<? extends AttributesModel> attrModels = peptideLkpResult.allInstances();
            algoComboBox.setEnabled(!attrModels.isEmpty());
            setEnableState(!algoComboBox.getSelectedItem().equals(NO_SELECTION));
        }
    }

}

class AlgoPresetPersistence {

    private Map<String, List<Preset>> presets = new HashMap<>();

    public AlgoPresetPersistence() {
        loadPresets();
    }

    public void savePreset(String name, Algorithm algorithm) {
        Preset preset = addPreset(new Preset(name, algorithm));

        FileOutputStream fos = null;
        try {
            //Create file if dont exist
            FileObject folder = FileUtil.getConfigFile("algortihmpresets");
            if (folder == null) {
                folder = FileUtil.getConfigRoot().createFolder("algorithmpresets");
            }
            File presetFile = new File(FileUtil.toFile(folder), name + ".xml");

            //Create doc
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = factory.newDocumentBuilder();
            final Document document = documentBuilder.newDocument();
            document.setXmlVersion("1.0");
            document.setXmlStandalone(true);

            //Write doc
            preset.writeXML(document);

            //Write XML file
            fos = new FileOutputStream(presetFile);
            Source source = new DOMSource(document);
            Result result = new StreamResult(fos);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
            transformer.transform(source, result);
        } catch (Exception e) {
            Logger.getLogger("").log(Level.SEVERE, "Error while writing preset file", e);
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException ex) {
                }
            }
        }
    }

    public void loadPreset(Preset preset, Algorithm algorithm) {
        for (AlgorithmProperty p : algorithm.getProperties()) {
            for (int i = 0; i < preset.propertyNames.size(); i++) {
                if (p.getCanonicalName().equalsIgnoreCase(preset.propertyNames.get(i))
                        || p.getProperty().getName().equalsIgnoreCase(preset.propertyNames.get(i))) {//Also compare with property name to maintain compatibility with old presets
                    try {
                        p.getProperty().setValue(preset.propertyValues.get(i));
                    } catch (Exception e) {
                        Logger.getLogger("").log(Level.SEVERE, "Error while setting preset property", e);
                    }
                }
            }
        }
    }

    public List<Preset> getPresets(Algorithm algorithm) {
        return presets.get(algorithm.getClass().getName());
    }

    private void loadPresets() {
        FileObject folder = FileUtil.getConfigFile("algorithmpresets");
        if (folder != null) {
            for (FileObject child : folder.getChildren()) {
                if (child.isValid() && child.hasExt("xml")) {
                    try {
                        InputStream stream = child.getInputStream();
                        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
                        DocumentBuilder builder = factory.newDocumentBuilder();
                        Document document = builder.parse(stream);
                        Preset preset = new Preset(document);
                        addPreset(preset);
                    } catch (Exception e) {
                        Logger.getLogger("").log(Level.SEVERE, "Error while reading preset file", e);
                    }
                }
            }
        }
    }

    private Preset addPreset(Preset preset) {
        List<Preset> algoPresets = presets.get(preset.algorithmClassName);
        if (algoPresets == null) {
            algoPresets = new ArrayList<>();
            presets.put(preset.algorithmClassName, algoPresets);
        }
        for (Preset p : algoPresets) {
            if (p.equals(preset)) {
                return p;
            }
        }
        algoPresets.add(preset);
        return preset;
    }

    public static class Preset {

        private List<String> propertyNames = new ArrayList<>();
        private List<Object> propertyValues = new ArrayList<>();
        private String algorithmClassName;
        private String name;

        private Preset(String name, Algorithm algorithm) {
            this.name = name;
            this.algorithmClassName = algorithm.getClass().getName();
            for (AlgorithmProperty p : algorithm.getProperties()) {
                try {
                    Object value = p.getProperty().getValue();
                    if (value != null) {
                        propertyNames.add(p.getCanonicalName());
                        propertyValues.add(value);
                    }
                } catch (Exception e) {
                }
            }
        }

        private Preset(Document document) {
            readXML(document);
        }

        public void readXML(Document document) {
            NodeList propertiesList = document.getDocumentElement().getElementsByTagName("properties");
            if (propertiesList.getLength() > 0) {
                for (int j = 0; j < propertiesList.getLength(); j++) {
                    org.w3c.dom.Node m = propertiesList.item(j);
                    if (m.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                        Element propertiesE = (Element) m;
                        algorithmClassName = propertiesE.getAttribute("algorithmClassName");
                        name = propertiesE.getAttribute("name");
                        NodeList propertyList = propertiesE.getElementsByTagName("property");
                        for (int i = 0; i < propertyList.getLength(); i++) {
                            org.w3c.dom.Node n = propertyList.item(i);
                            if (n.getNodeType() == org.w3c.dom.Node.ELEMENT_NODE) {
                                Element propertyE = (Element) n;
                                String propStr = propertyE.getAttribute("property");
                                String classStr = propertyE.getAttribute("class");
                                String valStr = propertyE.getTextContent();
                                Object value = parse(classStr, valStr);
                                if (value != null) {
                                    propertyNames.add(propStr);
                                    propertyValues.add(value);
                                }
                            }
                        }
                        break;
                    }
                }
            }
        }

        private Object parse(String classStr, String str) {
            try {
                Class c = Class.forName(classStr);
                if (c.equals(Boolean.class)) {
                    return new Boolean(str);
                } else if (c.equals(Integer.class)) {
                    return new Integer(str);
                } else if (c.equals(Float.class)) {
                    return new Float(str);
                } else if (c.equals(Double.class)) {
                    return new Double(str);
                } else if (c.equals(Long.class)) {
                    return new Long(str);
                } else if (c.equals(String.class)) {
                    return str;
                }
            } catch (ClassNotFoundException ex) {
                return null;
            }
            return null;
        }

        public void writeXML(Document document) {
            Element rootE = document.createElement("algorithmproperties");

            //Properties
            Element propertiesE = document.createElement("properties");
            propertiesE.setAttribute("algorithmClassName", algorithmClassName);
            propertiesE.setAttribute("name", name);
            propertiesE.setAttribute("version", "0.7");
            for (int i = 0; i < propertyNames.size(); i++) {
                Element propertyE = document.createElement("property");
                propertyE.setAttribute("property", propertyNames.get(i));
                propertyE.setAttribute("class", propertyValues.get(i).getClass().getName());
                propertyE.setTextContent(propertyValues.get(i).toString());
                propertiesE.appendChild(propertyE);
            }
            rootE.appendChild(propertiesE);
            document.appendChild(rootE);
        }

        @Override
        public String toString() {
            return name;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Preset other = (Preset) obj;
            if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 37 * hash + (this.name != null ? this.name.hashCode() : 0);
            return hash;
        }
    }

}

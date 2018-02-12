/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
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
import org.bapedis.core.model.AlgorithmCategory;
import org.bapedis.core.model.AlgorithmModel;
import org.bapedis.core.model.AlgorithmNode;
import org.bapedis.core.model.AlgorithmProperty;
import org.bapedis.core.model.AttributesModel;
import org.bapedis.core.model.Workspace;
import org.bapedis.core.project.ProjectManager;
import org.bapedis.core.spi.algo.Algorithm;
import org.bapedis.core.spi.algo.AlgorithmFactory;
import org.bapedis.core.task.AlgorithmExecutor;
import org.bapedis.core.ui.components.richTooltip.RichTooltip;
import org.netbeans.api.settings.ConvertAsProperties;
import org.openide.DialogDisplayer;
import org.openide.NotifyDescriptor;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.awt.StatusDisplayer;
import org.openide.windows.TopComponent;
import org.openide.util.NbBundle.Messages;
import org.openide.explorer.propertysheet.PropertySheet;
import org.openide.filesystems.FileUtil;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
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
@TopComponent.Registration(mode = "explorer", openAtStartup = false, position = 533)
@ActionID(category = "Window", id = "org.bapedis.core.ui.AlgoExplorerTopComponent")
@ActionReference(path = "Menu/Window", position = 533)
@TopComponent.OpenActionRegistration(
        displayName = "#CTL_AlgoExplorerAction",
        preferredID = "AlgoExplorerTopComponent"
)
@Messages({
    "CTL_AlgoExplorerAction=Tool",
    "CTL_AlgoExplorerTopComponent=Tool",
    "HINT_AlgoExplorerTopComponent=This is the Tool window"
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
        ((PropertySheet) propSheetPanel).setDescriptionAreaVisible(false);
        NO_SELECTION = NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.choose.text");
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        comboBoxModel.addElement(NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.choose.defaultText"));
        algoComboBox.setModel(comboBoxModel);
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
        propSheetPanel = new PropertySheet();
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
        org.openide.awt.Mnemonics.setLocalizedText(presetsButton, org.openide.util.NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.presetsButton.text")); // NOI18N
        presetsButton.setFocusable(false);
        presetsButton.setIconTextGap(0);
        presetsButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                presetsButtonActionPerformed(evt);
            }
        });
        algoToolBar.add(presetsButton);

        org.openide.awt.Mnemonics.setLocalizedText(resetButton, org.openide.util.NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.resetButton.text")); // NOI18N
        resetButton.setFocusable(false);
        resetButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
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
    }//GEN-LAST:event_resetButtonActionPerformed

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
            if (algorithm == null) {
                algorithm = factory.createAlgorithm();
                currentWs.add(algorithm);
            }
            algoModel.setSelectedAlgorithm(algorithm);
            setEnableState(true);
        } else {
            algoModel.setSelectedAlgorithm(null);
            setEnableState(false);
        }
    }//GEN-LAST:event_algoComboBoxActionPerformed

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
        pc.removeWorkspaceEventListener(this);
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
    public void workspaceChanged(Workspace oldWs, Workspace newWs) {
        removeLookupListener();
        AlgorithmCategory oldAlgoCategory = null;
        if (oldWs != null) {
            AlgorithmModel oldModel = pc.getAlgorithmModel(oldWs);
            oldModel.removePropertyChangeListener(this);
            oldAlgoCategory = oldModel.getCategory();
        }
        AlgorithmModel algoModel = pc.getAlgorithmModel(newWs);
        if (algoModel.getCategory() == null && oldAlgoCategory != null) {
            algoModel.setCategory(oldAlgoCategory);
        }
        if (algoModel.getCategory() != null) {
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

    private void refreshAlgChooser(AlgorithmModel algoModel) {
        setDisplayName(algoModel.getCategory().getDisplayName());
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        comboBoxModel.addElement(NO_SELECTION);
        comboBoxModel.setSelectedItem(NO_SELECTION);

        List<? extends AlgorithmFactory> factories = new ArrayList<>(Lookup.getDefault().lookupAll(AlgorithmFactory.class));
        for (Iterator<? extends AlgorithmFactory> it = factories.iterator(); it.hasNext();) {
            AlgorithmFactory f = it.next();
            if (f.getCategory() != algoModel.getCategory()) {
                it.remove();
            }
        }
//        Collections.sort(factories, new Comparator() {
//            @Override
//            public int compare(Object o1, Object o2) {
//                return ((AlgorithmFactory) o1).getName().compareTo(((AlgorithmFactory) o2).getName());
//            }
//        });
        for (AlgorithmFactory factory : factories) {
            AlgorithmFactoryItem item = new AlgorithmFactoryItem(factory);
            comboBoxModel.addElement(item);
        }
        algoComboBox.setModel(comboBoxModel);
        setEnableState(!comboBoxModel.getSelectedItem().equals(NO_SELECTION));
    }

    private void refreshProperties(AlgorithmModel algoModel) {
        if (algoModel == null || algoModel.getSelectedAlgorithm() == null) {
            ((PropertySheet) propSheetPanel).setNodes(new Node[0]);
            scrollPane.setViewportView(null);
            scrollPane.setVisible(false);
            propSheetPanel.setVisible(true);
        } else {
            Algorithm selectedAlgorithm = algoModel.getSelectedAlgorithm();

            if (selectedAlgorithm.getFactory().getSetupUI() != null) {
                JPanel settingPanel = selectedAlgorithm.getFactory().getSetupUI().getSettingPanel(selectedAlgorithm);
                settingPanel.setBorder(BorderFactory.createTitledBorder(NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.settingPanel.bordertitle"))); // NOI18N
                propSheetPanel.setVisible(false);
                scrollPane.setViewportView(settingPanel);
                scrollPane.setVisible(true);
            } else {
                scrollPane.setViewportView(null);
                scrollPane.setVisible(false);
                ((PropertySheet) propSheetPanel).setNodes(new Node[]{new AlgorithmNode(selectedAlgorithm)});
                propSheetPanel.setVisible(true);
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
        scrollPane.setEnabled(!running);
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

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() instanceof AlgorithmModel) {
            if (evt.getPropertyName().equals(AlgorithmModel.CHANGED_CATEGORY)) {
                AlgorithmModel algoModel = (AlgorithmModel) evt.getSource();
                refreshAlgChooser(algoModel);
            } else if (evt.getPropertyName().equals(AlgorithmModel.CHANGED_ALGORITHM)) {
                AlgorithmModel algoModel = (AlgorithmModel) evt.getSource();
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
            AlgoDescriptionImage algoDescriptionImage = new AlgoDescriptionImage(factory);
            tooltip.setMainImage(algoDescriptionImage.getImage());
        }
        return tooltip;
    }

    @Override
    public void resultChanged(LookupEvent le) {
        if (le.getSource().equals(peptideLkpResult)) {
            Collection<? extends AttributesModel> attrModels = peptideLkpResult.allInstances();
            algoComboBox.setEnabled(!attrModels.isEmpty());
            setEnableState(!algoComboBox.getSelectedItem().equals(NO_SELECTION));
        }
    }

    private static class AlgorithmFactoryItem {

        private final AlgorithmFactory factory;

        public AlgorithmFactoryItem(AlgorithmFactory factory) {
            this.factory = factory;
        }

        public AlgorithmFactory getFactory() {
            return factory;
        }

        @Override
        public String toString() {
            return factory.getName();
        }
    }

    private static class AlgoDescriptionImage {

        private static final int STAR_WIDTH = 16;
        private static final int STAR_HEIGHT = 16;
        private static final int STAR_MAX = 5;
        private static final int TEXT_GAP = 5;
        private static final int LINE_GAP = 4;
        private static final int Y_BEGIN = 10;
        private static final int IMAGE_RIGHT_MARIN = 10;
        private final Image greenIcon;
        private final Image grayIcon;
        private Graphics g;
        private final String qualityStr;
        private final String speedStr;
        private int textMaxSize;
        private final AlgorithmFactory factory;

        public AlgoDescriptionImage(AlgorithmFactory factory) {
            this.factory = factory;
            greenIcon = ImageUtilities.loadImage("org/bapedis/core/resources/yellow.png");
            grayIcon = ImageUtilities.loadImage("org/bapedis/core/resources/grey.png");
            qualityStr = NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.tooltip.quality");
            speedStr = NbBundle.getMessage(AlgoExplorerTopComponent.class, "AlgoExplorerTopComponent.tooltip.speed");
        }

        public void paint(Graphics g) {
            g.setColor(Color.BLACK);
            g.drawString(qualityStr, 0, STAR_HEIGHT + Y_BEGIN - 2);
            paintStarPanel(g, textMaxSize + TEXT_GAP, Y_BEGIN, STAR_MAX, factory.getQualityRank());
            g.drawString(speedStr, 0, STAR_HEIGHT * 2 + LINE_GAP + Y_BEGIN - 2);
            paintStarPanel(g, textMaxSize + TEXT_GAP, STAR_HEIGHT + LINE_GAP + Y_BEGIN, STAR_MAX, factory.getSpeedRank());
        }

        public Image getImage() {
            //Image size
            BufferedImage im = new BufferedImage(100, 100, BufferedImage.TYPE_INT_ARGB);
            textMaxSize = 0;
            textMaxSize = Math.max(im.getGraphics().getFontMetrics().stringWidth(qualityStr), textMaxSize);
            textMaxSize = Math.max(im.getGraphics().getFontMetrics().stringWidth(speedStr), textMaxSize);
            int imageWidth = STAR_MAX * STAR_WIDTH + TEXT_GAP + textMaxSize + IMAGE_RIGHT_MARIN;

            //Paint
            BufferedImage img = new BufferedImage(imageWidth, 100, BufferedImage.TYPE_INT_ARGB);
            this.g = img.getGraphics();
            paint(g);
            return img;
        }

        public void paintStarPanel(Graphics g, int x, int y, int max, int value) {
            for (int i = 0; i < max; i++) {
                if (i < value) {
                    g.drawImage(greenIcon, x + i * 16, y, null);
                } else {
                    g.drawImage(grayIcon, x + i * 16, y, null);
                }
            }
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

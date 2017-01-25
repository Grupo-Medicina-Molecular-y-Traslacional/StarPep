/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.MenuElement;
import javax.swing.MenuSelectionManager;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.TableColumn;
import org.bapedis.core.model.PeptideAttribute;
import org.bapedis.core.model.AttributesModel;
import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.etable.ETableColumnModel;
import org.netbeans.swing.etable.QuickFilter;
import org.netbeans.swing.outline.Outline;
import org.openide.awt.DropDownButtonFactory;
import org.openide.explorer.ExplorerManager;
import org.openide.explorer.ExplorerUtils;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.Node;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;
//import org.jdesktop.swingx.JXBusyLabel;

/**
 *
 * @author loge
 */
public class AttributesPanel extends javax.swing.JPanel implements ExplorerManager.Provider, Lookup.Provider {

    protected final ExplorerManager manager;
    private final JLabel busyLabel;
    private final JLabel errorLabel;
    protected final OutlineView view;
    protected JPopupMenu quickFilterPopup;
    protected String columnToFilter = null;
    protected boolean matchCase;
    protected AttributesModel attrModel;
    protected final QuickFilter defaultFilter;
    protected final Lookup lookup;

    public AttributesPanel() {
        manager = new ExplorerManager();
        initComponents();
        lookup = ExplorerUtils.createLookup(manager, getActionMap());
        quickFilterPopup = new JPopupMenu();
        matchCase = NbPreferences.forModule(AttributesPanel.class).getBoolean("matchCase", false);
        view = new OutlineView(NbBundle.getMessage(AttributesPanel.class, "AttributesPanel.nodelColumnLabel"));
        view.setQuickSearchAllowed(false);
        
        final Outline outline = view.getOutline();
        outline.setPopupUsedFromTheCorner(true);
        outline.setRootVisible(false);
        busyLabel = new JLabel(NbBundle.getMessage(AttributesPanel.class, "AttributesPanel.busyLabel.text"));
        busyLabel.setHorizontalAlignment(SwingConstants.CENTER);
        errorLabel = new JLabel(NbBundle.getMessage(AttributesPanel.class, "AttributesPanel.errorLabel.text"));
        errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
        dataPanel.add(view, BorderLayout.CENTER);
        filterPanel.add(createDropDownButtonSearch(), 1);
        defaultFilter = createQuickFilter();
        //Quick Filter
        outline.getColumnModel().addColumnModelListener(createColumnModelListener());
        filterTextField.getDocument().addDocumentListener(new DocumentListener() {

            public void insertUpdate(DocumentEvent e) {
                applyDefaultFilter();
            }

            public void removeUpdate(DocumentEvent e) {
                applyDefaultFilter();
            }

            public void changedUpdate(DocumentEvent e) {
            }
        });
    }

    private TableColumnModelListener createColumnModelListener() {
        final JCheckBoxMenuItem matchCaseItem = new JCheckBoxMenuItem(NbBundle.getMessage(AttributesPanel.class, "CTL_MatchCase"), matchCase);
        matchCaseItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                matchCase = matchCaseItem.isSelected();
                NbPreferences.forModule(AttributesPanel.class).putBoolean("matchCase", matchCase);
            }
        });
        final JMenuItem description = new JMenuItem(NbBundle.getMessage(AttributesPanel.class, "CTL_ColumnsToSearch")) {
            @Override
            public void processMouseEvent(MouseEvent e, MenuElement[] path, MenuSelectionManager manager) {
                // Ignore
            }

            @Override
            protected void processMouseEvent(MouseEvent e) {
                // Ignore
            }
        };
        final ETableColumnModel columnModel = (ETableColumnModel) view.getOutline().getColumnModel();
        return new TableColumnModelListener() {
            HashMap<String, JCheckBoxMenuItem> mapItem = new HashMap<>();
            protected ButtonGroup quickFilterButtonGroup = new ButtonGroup();

            @Override
            public void columnAdded(final TableColumnModelEvent te) {
                refreshPopup();
            }

            @Override
            public void columnRemoved(TableColumnModelEvent e) {
                refreshPopup();
            }

            @Override
            public void columnMoved(TableColumnModelEvent e) {
            }

            @Override
            public void columnMarginChanged(ChangeEvent e) {
            }

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {
            }

            private void refreshPopup() {
                quickFilterPopup.removeAll();
                for (int i = 0; i < columnModel.getColumnCount(); i++) {
                    final TableColumn column = columnModel.getColumn(i);
                    if (!columnModel.isColumnHidden(column)) {
                        JCheckBoxMenuItem item;
                        if (!mapItem.containsKey(column.getIdentifier().toString())) {
                            item = new JCheckBoxMenuItem(column.getHeaderValue().toString());
                            item.addActionListener(new ActionListener() {
                                @Override
                                public void actionPerformed(ActionEvent ae) {
                                    columnToFilter = column.getIdentifier().toString();
                                    labelFilter.setText(NbBundle.getMessage(AttributesPanel.class, "AttributesPanel.labelFilter.text", column.getHeaderValue().toString()));
                                    applyDefaultFilter();
                                }
                            });
                            quickFilterButtonGroup.add(item);
                            mapItem.put(column.getIdentifier().toString(), item);
                        } else {
                            item = mapItem.get(column.getIdentifier().toString());
                        }
                        quickFilterPopup.add(item);
                    }
                }
                if (quickFilterPopup.getComponentCount() > 0) {
                    JCheckBoxMenuItem defaultItem = (JCheckBoxMenuItem) quickFilterPopup.getComponent(0);
                    boolean selectedItem = false;
                    for (int i = 0; i < quickFilterPopup.getComponentCount(); i++) {
                        if (((JCheckBoxMenuItem) quickFilterPopup.getComponent(i)).isSelected()) {
                            selectedItem = true;
                        }
                    }
                    if (!selectedItem) {
                        defaultItem.doClick();
                    }
                    quickFilterPopup.add(description, 0);
                    quickFilterPopup.add(matchCaseItem, 0);
                }
            }
        };
    }

    public void showData(AttributesModel attrModel) {
        if (attrModel != null) {
            manager.setRootContext(attrModel.getRootContext());
            PeptideAttribute[] attrs = attrModel.getAttributes();
            String[] columns = new String[attrs.length * 2];
            int pos = 0;
            for (PeptideAttribute attr : attrs) {
                columns[pos++] = attr.getId();
                columns[pos++] = attr.getId();
            }
            view.setPropertyColumns(columns);
            ETableColumnModel columnModel = (ETableColumnModel) view.getOutline().getColumnModel();
            ETableColumn column;
            for (PeptideAttribute attr : attrs) {
                column = (ETableColumn) view.getOutline().getColumn(attr.getId());
                columnModel.setColumnHidden(column, !attr.isVisible());
            }
        } else {
            manager.setRootContext(Node.EMPTY);
            view.setPropertyColumns(new String[]{});
        }
        this.attrModel = attrModel;
        setBusy(false);
    }

    public void setErrorLabel() {
        dataPanel.removeAll();
        dataPanel.add(errorLabel, BorderLayout.CENTER);
        dataPanel.revalidate();
        filterPanel.setVisible(false);
    }

    public void setBusyLabel() {
        setBusy(true);
    }

    protected void setBusy(boolean busy) {
        dataPanel.removeAll();
        if (busy) {
            filterPanel.setVisible(false);
            dataPanel.add(busyLabel, BorderLayout.CENTER);
        } else {
            filterPanel.setVisible(true);
            dataPanel.add(view, BorderLayout.CENTER);
        }
        dataPanel.revalidate();        
    }

    private void applyDefaultFilter() {
        if (filterTextField.getText().isEmpty()) {
            view.getOutline().unsetQuickFilter();
        } else {
            try {
                TableColumn column = view.getOutline().getColumn(columnToFilter);
                view.getOutline().setQuickFilter(column.getModelIndex(), defaultFilter);
                filterTextField.setBackground(Color.WHITE);
            } catch (Exception ex) {
                view.getOutline().unsetQuickFilter();
                filterTextField.setBackground(new Color(254, 150, 150));
//                Logger.getLogger(AttributesPanel.class.getName()).log(Level.WARNING, e.getMessage(), e);
            }
        }
    }

    public void setQuickFilter(int columnIndex, QuickFilter quickFilter) {
        view.getOutline().setQuickFilter(columnIndex, quickFilter);
    }

    public void unsetQuickFilter() {
        applyDefaultFilter();
    }
    
    public void clearSelection(){
        view.getOutline().getSelectionModel().clearSelection();
    }

    private JButton createDropDownButtonSearch() {
        Image iconImage = ImageUtilities.loadImage("org/bapedis/core/resources/search.png");
        ImageIcon icon = new ImageIcon(iconImage);
        final JButton dropDownButton = DropDownButtonFactory.createDropDownButton(new ImageIcon(
                new BufferedImage(16, 16, BufferedImage.TYPE_BYTE_GRAY)), quickFilterPopup);

        dropDownButton.setIcon(icon);
//        dropDownButton.setMargin(new java.awt.Insets(2, 4, 0, 4));
        dropDownButton.setToolTipText(NbBundle.getMessage(AttributesPanel.class, "CTL_QuickFilter"));
        dropDownButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                quickFilterPopup.show(dropDownButton, 0, dropDownButton.getHeight());
            }
        });
        return dropDownButton;
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

        dataPanel = new javax.swing.JPanel();
        filterPanel = new javax.swing.JPanel();
        labelFilter = new javax.swing.JLabel();
        filterTextField = new javax.swing.JTextField();

        setLayout(new java.awt.GridBagLayout());

        dataPanel.setPreferredSize(new java.awt.Dimension(514, 25));
        dataPanel.setLayout(new java.awt.BorderLayout());
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        add(dataPanel, gridBagConstraints);

        filterPanel.setMinimumSize(new java.awt.Dimension(262, 25));
        filterPanel.setPreferredSize(new java.awt.Dimension(514, 30));
        filterPanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.RIGHT, 5, 2));

        org.openide.awt.Mnemonics.setLocalizedText(labelFilter, org.openide.util.NbBundle.getMessage(AttributesPanel.class, "AttributesPanel.labelFilter.text")); // NOI18N
        filterPanel.add(labelFilter);

        filterTextField.setText(org.openide.util.NbBundle.getMessage(AttributesPanel.class, "AttributesPanel.filterTextField.text")); // NOI18N
        filterTextField.setPreferredSize(new java.awt.Dimension(150, 20));
        filterTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                filterTextFieldActionPerformed(evt);
            }
        });
        filterPanel.add(filterTextField);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        add(filterPanel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void filterTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_filterTextFieldActionPerformed
        applyDefaultFilter();
    }//GEN-LAST:event_filterTextFieldActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel dataPanel;
    private javax.swing.JPanel filterPanel;
    private javax.swing.JTextField filterTextField;
    private javax.swing.JLabel labelFilter;
    // End of variables declaration//GEN-END:variables

    /**
     * Create a QuickFilter from the given String.
     *
     * @param qsFilter filter as String
     * @return a QuickFilter from the given String
     */
    private QuickFilter createQuickFilter() {
        return new QuickFilter() {

            @Override
            public boolean accept(Object value) {
                String filterText = filterTextField.getText();
                if (!filterText.isEmpty()) {
                    if (value instanceof String[]) {
                        for (String val : (String[]) value) {
                            if ((matchCase) ? val.contains(filterText) : val.toUpperCase().contains(filterText.toUpperCase())) {
                                return true;
                            }
                        }
                        return false;
                    }
                    return (matchCase) ? value.toString().contains(filterText) : value.toString().toUpperCase().contains(filterText.toUpperCase());
                }
                return true;
            }
        };
    }

    @Override
    public ExplorerManager getExplorerManager() {
        return manager;
    }

    @Override
    public Lookup getLookup() {
        return lookup;
    }
}

/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
Website : http://www.gephi.org

This file is part of Gephi.

DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

Copyright 2011 Gephi Consortium. All rights reserved.

The contents of this file are subject to the terms of either the GNU
General Public License Version 3 only ("GPL") or the Common
Development and Distribution License("CDDL") (collectively, the
"License"). You may not use this file except in compliance with the
License. You can obtain a copy of the License at
http://gephi.org/about/legal/license-notice/
or /cddl-1.0.txt and /gpl-3.0.txt. See the License for the
specific language governing permissions and limitations under the
License.  When distributing the software, include this License Header
Notice in each file and include the License files at
/cddl-1.0.txt and /gpl-3.0.txt. If applicable, add the following below the
License Header, with the fields enclosed by brackets [] replaced by
your own identifying information:
"Portions Copyrighted [year] [name of copyright owner]"

If you wish your version of this file to be governed by only the CDDL
or only the GPL Version 3, indicate your decision by adding
"[Contributor] elects to include this software in this distribution
under the [CDDL or GPL Version 3] license." If you do not indicate a
single choice of license, a recipient has the option to distribute
your version of this file under either the CDDL, the GPL Version 3 or
to extend the choice of license to its licensees as provided above.
However, if you add GPL Version 3 code and therefore, elected the GPL
Version 3 license, then the option applies only if the new code is
made subject to such option by the copyright holder.

Contributor(s):

Portions Copyrighted 2011 Gephi Consortium.
 */
package org.bapedis.core.ui.components;

import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JCheckBox;
import org.bapedis.core.model.GraphElementAttributeColumn;
import org.bapedis.core.model.GraphElementAvailableColumnsModel;
import org.bapedis.core.model.GraphElementDataColumn;

/**
 * UI for selecting available columns of a table in Data laboratory
 * @see AvailableColumnsModel
 * @author Eduardo
 */
public class GraphElementAvailableColumnsPanel extends javax.swing.JPanel {

    private final GraphElementAvailableColumnsModel availableColumnsModel;
    private final GraphElementDataColumn[] columns;
    private JCheckBox[] columnsCheckBoxes;
//    private AvailableColumnsValidator validator;

    /** Creates new form AvailableColumnsPanel
     * @param availableColumnsModel */
    public GraphElementAvailableColumnsPanel(GraphElementAvailableColumnsModel availableColumnsModel) {
        initComponents();
        this.availableColumnsModel = availableColumnsModel;
        columns =  availableColumnsModel.getAllKnownColumns();
        refreshColumns();
        refreshAvailableColumnsControls();
    }

    private void refreshColumns() {        
        columnsCheckBoxes = new JCheckBox[columns.length];
        contentPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        for (int i = 0; i < columns.length; i++) {
            columnsCheckBoxes[i] = new JCheckBox(columns[i].getColumnName(), availableColumnsModel.isColumnAvailable(columns[i]));
            columnsCheckBoxes[i].addActionListener(new ColumnCheckBoxListener(i));
            gbc.gridx=0;
            gbc.gridy=i;
            gbc.anchor = GridBagConstraints.WEST;
            contentPanel.add(columnsCheckBoxes[i], gbc);
            if (columns[i] instanceof GraphElementAttributeColumn && columns[i].getColumn().getId().equals(GraphElementAvailableColumnsModel.DefaultColumnID)){
                columnsCheckBoxes[i].setEnabled(false);
            }
        }
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void refreshAvailableColumnsControls() {
        boolean enabled = availableColumnsModel.canAddAvailableColumn();
        for (JCheckBox cb : columnsCheckBoxes) {
            if (!cb.isSelected()) {
                cb.setEnabled(enabled);
            }
        }
        infoLabel.setVisible(!enabled);
    }

    class ColumnCheckBoxListener implements ActionListener {

        private int index;

        public ColumnCheckBoxListener(int index) {
            this.index = index;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (columnsCheckBoxes[index].isSelected()) {
                availableColumnsModel.addAvailableColumn(columns[index]);
            } else {
                availableColumnsModel.removeAvailableColumn(columns[index]);
            }
            refreshAvailableColumnsControls();
        }
    }



    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        infoLabel = new javax.swing.JLabel();
        scroll = new javax.swing.JScrollPane();
        contentPanel = new javax.swing.JPanel();
        descriptionLabel = new javax.swing.JLabel();

        setMinimumSize(new java.awt.Dimension(336, 280));
        setPreferredSize(new java.awt.Dimension(336, 280));
        setLayout(new java.awt.GridBagLayout());

        infoLabel.setForeground(new java.awt.Color(255, 0, 0));
        infoLabel.setText(org.openide.util.NbBundle.getMessage(GraphElementAvailableColumnsPanel.class, "GraphElementAvailableColumnsPanel.infoLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(infoLabel, gridBagConstraints);

        contentPanel.setLayout(new java.awt.GridBagLayout());
        scroll.setViewportView(contentPanel);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        add(scroll, gridBagConstraints);

        descriptionLabel.setText(org.openide.util.NbBundle.getMessage(GraphElementAvailableColumnsPanel.class, "GraphElementAvailableColumnsPanel.descriptionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        add(descriptionLabel, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel contentPanel;
    private javax.swing.JLabel descriptionLabel;
    private javax.swing.JLabel infoLabel;
    private javax.swing.JScrollPane scroll;
    // End of variables declaration//GEN-END:variables
}

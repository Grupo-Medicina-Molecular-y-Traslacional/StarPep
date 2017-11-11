/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.core.ui.components;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JCheckBox;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import org.openide.util.NbBundle;

/**
 *
 * @author loge
 */
public class CheckBoxHeader extends JCheckBox implements TableCellRenderer {

    public CheckBoxHeader(final JTableHeader header, final int index) {
        setOpaque(false);
        setFont(header.getFont());
        setHorizontalAlignment(SwingConstants.CENTER);
        setToolTipText(NbBundle.getMessage(DescriptorSelectionPanel.class, "DescriptorSelectionPanel.checkAll.text"));

        header.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JTable table = ((JTableHeader) e.getSource()).getTable();
                TableColumnModel columnModel = table.getColumnModel();
                int viewColumn = columnModel.getColumnIndexAtX(e.getX());
                int modelColumn = table.convertColumnIndexToModel(viewColumn);
                if (modelColumn == index) {
                    doClick();
                    TableModel m = table.getModel();
                    boolean flag = isSelected();
                    for (int i = 0; i < m.getRowCount(); i++) {
                        m.setValueAt(flag, i, index);
                    }
                    ((JTableHeader) e.getSource()).repaint();
                }
            }
        });

        header.getTable().getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                TableModel model = (TableModel) e.getSource();
                if (model.getRowCount() > 0) {
                    boolean flag = (boolean) model.getValueAt(0, index);
                    for (int row = 1; row < model.getRowCount() && flag; row++) {
                        flag = flag && (boolean) model.getValueAt(row, index);
                    }
                    setSelected(flag);
                    header.repaint();
                }
            }
        });
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        JTableHeader header = table.getTableHeader();
        Color bg = header.getBackground();
        setBackground(new Color(bg.getRed(), bg.getGreen(), bg.getBlue()));
        return this;
    }
}

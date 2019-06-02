/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bapedis.chemspace.model;

import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Loge
 */
public class PCATableModel extends AbstractTableModel {

    private final String[] columnNames;
    private final double[] varianceExp;

    public PCATableModel(double[] varianceExp) {
        this.columnNames = new String[]{"Factor", "Explained variance", "Cumulative variance"};
        this.varianceExp = varianceExp;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return varianceExp.length;
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        switch(col){
            case 0:
                return "PCA " + (row + 1);
            case 1:
                return varianceExp[row];
            case 2:
                double sum=0;
                for(int i=0; i<=row; i++){
                    sum+=varianceExp[i];
                }
                return sum;
        }
        return null;
    }

    @Override
    public Class getColumnClass(int c) {
        switch (c) {
            case 0:
                return String.class;
            case 1:
                return Double.class;
            case 2:
                return Double.class;
        }
        return null;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        throw new UnsupportedOperationException("Not supported");
    }
}

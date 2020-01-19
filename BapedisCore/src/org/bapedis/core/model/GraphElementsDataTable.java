/*
 Copyright 2008-2015 Gephi
 Authors : Mathieu Bastian <mathieu.bastian@gephi.org>, Mathieu Jacomy, Julian Bilcke, Eduardo Ramos <eduramiba@gmail.com>
 Website : http://www.gephi.org

 This file is part of Gephi.

 DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.

 Copyright 2015 Gephi Consortium. All rights reserved.

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

 Portions Copyrighted 2015 Gephi Consortium.
 */
package org.bapedis.core.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.swing.table.AbstractTableModel;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Element;

/**
 *
 * @author Eduardo Ramos
 */
public class GraphElementsDataTable extends AbstractTableModel {

    private final ArrayList<Element> elements;
    private GraphElementDataColumn[] columns;    

    public GraphElementsDataTable(int initialCapacity, GraphElementDataColumn[] columns) {
        this.elements = new ArrayList<>(initialCapacity);
        this.columns = columns;
    }

    @Override
    public int getRowCount() {
        return elements.size();
    }

    @Override
    public int getColumnCount() {
        return columns.length;
    }

    @Override
    public String getColumnName(int columnIndex) {
        return columns[columnIndex].getColumnName();
    }

    @Override
    public Class<?> getColumnClass(int columnIndex) {
        return columns[columnIndex].getColumnClass();
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return columns[columnIndex].isEditable();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        try {
            return columns[columnIndex].getValueFor(elements.get(rowIndex));
        } catch (Exception e) {
            /**
             * We need to do this because the JTable might repaint itself while
             * datalab still has not detected that the column has been deleted
             * (it does so by polling on graph and table observers). I can't
             * find a better solution...
             */
            return null;
        }
    }

    @Override
    public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        columns[columnIndex].setValueFor(elements.get(rowIndex), aValue);
    }

    public void addRow(Element row) {
        if (row != null) {
            int rowIndex = elements.size();
            elements.add(row);
            fireTableRowsInserted(rowIndex, rowIndex);
        }
    }

    public void addRow(List<Element> rows) {
        if (rows.size() > 0) {
            int firstRow = elements.size();
            elements.addAll(rows);
            int lastRow = elements.size()-1;
            fireTableRowsInserted(firstRow, lastRow);
        }
    }

    public Element getElementAtRow(int row) {
        return elements.get(row);
    }

    public GraphElementDataColumn[] getColumns() {
        return columns;
    }

    public List<Element> getElements() {
        return elements;
    }

    public void resetColumns(GraphElementDataColumn[] columns) {
        Set<GraphElementDataColumn> oldColumns = new HashSet<>(Arrays.asList(this.columns));
        Set<GraphElementDataColumn> newColumns = new HashSet<>(Arrays.asList(columns));

        boolean columnsChanged = !oldColumns.equals(newColumns);
        this.columns = columns;

        if (columnsChanged) {
            fireTableStructureChanged();//Only firing this event if columns change is useful because JXTable will not reset columns width if there is no change
        }
    }
    /**
     * Column at index or null if it's a fake column.
     *
     * @return
     */
    public Column getColumnAtIndex(int i) {
        if (i >= 0 && i < columns.length) {
            return columns[i].getColumn();
        } else {
            return null;
        }
    }
}

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
package org.bapedis.core.model;

import java.util.LinkedHashSet;
import java.util.Set;
import org.gephi.graph.api.Column;
import org.gephi.graph.api.Table;

/**
 * Class to keep available state (in data laboratory) of the columns of a table
 * of a workspace. Useful, but also necessary to limit the maximum number of
 * available columns when there are a lot of columns.
 *
 * @author Eduardo
 */
public class GraphElementAvailableColumnsModel {

    private static final int MAX_AVAILABLE_COLUMNS = 3;
    private final Set<GraphElementDataColumn> availableColumns = new LinkedHashSet<>();
    private final Set<GraphElementDataColumn> allKnownColumns = new LinkedHashSet<>();
    private final String DefaultColumnID = "name";
    private final String[] IgnoredColumnIDs = new String[]{"timeset"};

    public GraphElementAvailableColumnsModel() {
        allKnownColumns.add(new GraphNodeDegreeDataColumn());
    }

    public boolean isColumnAvailable(GraphElementDataColumn column) {
        return availableColumns.contains(column);
    }

    /**
     * Add a column as available if it can be added.
     *
     * @param column Column to add
     * @return True if the column was successfully added, false otherwise (no
     * more columns can be available)
     */
    public synchronized boolean addAvailableColumn(GraphElementDataColumn column) {
        if (canAddAvailableColumn()) {
            if (!availableColumns.contains(column)) {
                availableColumns.add(column);
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * Remove an available column from the model if possible.
     *
     * @param column Column to make not available
     * @return True if the column could be removed
     */
    public synchronized boolean removeAvailableColumn(GraphElementDataColumn column) {
        return availableColumns.remove(column);
    }

    /**
     * Clear all available columns
     */
    public synchronized void removeAllColumns() {
        availableColumns.clear();
    }

    /**
     * Indicates if more columns can be made available a the moment
     *
     * @return
     */
    public boolean canAddAvailableColumn() {
        return availableColumns.size() < MAX_AVAILABLE_COLUMNS;
    }

    /**
     * Return available columns, sorted by index
     *
     * @return
     */
    public GraphElementDataColumn[] getAvailableColumns() {
        return availableColumns.toArray(new GraphElementDataColumn[0]);
    }

    public int getAvailableColumnsCount() {
        return availableColumns.size();
    }

    /**
     * Return all known columns, sorted by index
     *
     * @return
     */
    public GraphElementDataColumn[] getAllKnownColumns() {
        return allKnownColumns.toArray(new GraphElementDataColumn[0]);
    }

    public int getAllKnownColumnsCount() {
        return allKnownColumns.size();
    }

    /**
     * Syncronizes this AvailableColumnsModel to contain the table current
     * columns, checking for deleted and new columns.
     *
     * @param table
     */
    public synchronized void syncronizeTableColumns(Table table) {
        GraphElementDataColumn[] availableColumnsCopy = availableColumns.toArray(new GraphElementDataColumn[0]);

        removeAllColumns();

        if (table.getColumn(DefaultColumnID) != null) {
            addAvailableColumn(new GraphElementAttributeColumn(table.getColumn(DefaultColumnID)));
        }                

        //Keep existing available columns as available.
        //Note: We need to remove all columns and add them all again because there could be a new column with the same title but different index 
        //if the old one with the same title was removed, and we should not keep the old column with same title.
        //availableColumnsCopy.contains(column)
        for (GraphElementDataColumn column : availableColumnsCopy) {
            if (column instanceof GraphElementAttributeColumn) {
                for (Column c : table.toArray()) {
                    if (!c.getId().equals(DefaultColumnID) && !isIgnored(c.getId()) && column.getColumn().equals(c)) {
                        addAvailableColumn(new GraphElementAttributeColumn(c));
                        break;
                    }
                }
            } else {
                addAvailableColumn(column);
            }
        }
        //Detect new columns and make them not available by default
        //!allKnownColumns.contains(column)
        boolean isNew;
        for (Column c : table.toArray()) {
            if (!isIgnored(c.getId())) {
                isNew = true;
                for (GraphElementDataColumn column : allKnownColumns) {
                    if (column.getColumn() != null && column.getColumn().equals(c)) {
                        isNew = false;
                    }
                }
                if (isNew) {
                    allKnownColumns.add(new GraphElementAttributeColumn(c));
                }
            }
        }
    }

    private boolean isIgnored(String id) {
        for (String iid : IgnoredColumnIDs) {
            if (iid.equals(id)) {
                return true;
            }
        }
        return false;
    }
}

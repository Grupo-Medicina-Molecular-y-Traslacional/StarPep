/*
 * Written and authored by Jason Bisanti. Free to use.
 * Created on Feb 24, 2012

 * An extension of {@link OutlineView} with some minor UI improvements:
 *
 * 1) Replaces the default empty border with the default table border
 * <br>
 *  2) Enables the alternative show/hide columns pop-up (this is really a
 * personal preference, not a bug)
 * <br>
 * 3) Provides a visual distinction for a column that has its values filtered
 * <br>
 * This class was created for a tutorial found at: jasonbisanti.blogspot.com
 *
 * @author Jason Bisanti
 */
package org.bapedis.core.ui.components;
import java.awt.Component;
import java.awt.Font;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import org.netbeans.swing.etable.ETable;
import org.netbeans.swing.etable.ETableColumn;
import org.netbeans.swing.outline.Outline;
import org.openide.explorer.view.OutlineView;
import org.openide.nodes.Node;
import org.openide.nodes.Node.Property;
import org.openide.nodes.Node.PropertySet;

public class TreeTable extends OutlineView {
/**
     * Default constructor that initializes the Tree Table column with the name
     * "Nodes".
     */
    public TreeTable()
    {
        this(null);
    }
   
    /**
     * Initializes the Tree Table column name with parameter nodesColumnLabel.
     *
     * @param nodesColumnLabel {@link String}
     */
    public TreeTable(String nodesColumnLabel)
    {
        super(nodesColumnLabel);
       
        // Bug Fix #1: Add a Scroll Pane Border instead of an EmptyBorder
        super.setBorder(UIManager.getBorder("Table.scrollPaneBorder"));
       
        // Bug Fix #2: Provide the alternative popup for showing/hiding columns
        final Outline outline = super.getOutline();
        outline.setPopupUsedFromTheCorner(true);
       
        // Bug Fix #3 (Part 1): Set a HeaderRenderer for each column that is
        // added that differentiates when table columns are sorted and rows are
        // filtered        
        final TableColumnModel columnModel = outline.getColumnModel();
        columnModel.addColumnModelListener(new TableColumnModelListener()
        {
            @Override
            public void columnAdded(TableColumnModelEvent e)
            {
                // Whenever a column is added, set our custom header renderer
                TableColumn column = columnModel.getColumn(e.getToIndex());
                column.setHeaderRenderer(new TreeTableColumnRenderer());
            }
            @Override
            public void columnRemoved(TableColumnModelEvent e){}
            @Override
            public void columnMoved(TableColumnModelEvent e){}
            @Override
            public void columnMarginChanged(ChangeEvent e){}
            @Override
            public void columnSelectionChanged(ListSelectionEvent e){}
        });
       
        // Bug Fix #3 (Part 2): Add a listener for the PropertyChange that is
        // fired when a new row is filtered
        outline.addPropertyChangeListener(Outline.PROP_QUICK_FILTER,
        new PropertyChangeListener()
        {            
            @Override
            public void propertyChange(PropertyChangeEvent evt)
            {
                outline.getTableHeader().repaint();
            }
        });        
    }
   
    /**
     * Adds columns pertaining to each {@link Property} for all {@link Node}s.
     * This method obtains the Properties from parameter rootContext and assumes
     * that the {@link PropertySet}s to be used are located at index 0 for
     * {@link Node} method <i>getPropertySets()</i>.
     *
     * @param rootContext Root {@link Node}
     */
    public void initialize(Node rootContext)
    {
        // Obtain the properties from which we'll set the column names,
        // display names, and descriptions.
        Property[] properties =
                rootContext.getPropertySets()[0].getProperties();
        if(properties != null)
        {
            for(Property col : properties)
            {
                super.addPropertyColumn(col.getName(), col.getDisplayName(),
                        col.getShortDescription());
            }
        }
    }
   
    /**
     * A custom {@link TableCellRenderer} for a {@link JTableHeader} that adds
     * an ascending/descending arrow when column data is sorted and sets bold
     * font when column data is filtered.
     */
    private class TreeTableColumnRenderer implements TableCellRenderer
    {
        /**
         * Our default renderer instance which we'll modify accordingly if a
         * column is sorted and/or filtered.
         */
        private final TableCellRenderer RENDERER =
                new JTable().getTableHeader().getDefaultRenderer();
       
        /** The ASCII {@link String} for an up arrow */
        private final String UP_ARROW = new String(new char[]{8593, ' '});
       
        /** The ASCII {@link String} for a down arrow */
        private final String DOWN_ARROW = new String(new char[]{8595, ' '});
        @Override
        public Component getTableCellRendererComponent(JTable table,
                Object value, boolean isSelected, boolean hasFocus, int row,
                int column)
        {
            // Get original rendered JLabel for the header
            JLabel label = (JLabel) RENDERER.getTableCellRendererComponent(table,
                    value, isSelected, hasFocus, row, column);
           
            if(table instanceof ETable)
            {
                // If we're rendering a filtered column, bold the text
                if(((ETable)table).getQuickFilterColumn() ==
                        table.convertColumnIndexToModel(column))
                {
                    label.setFont(table.getFont().deriveFont(Font.BOLD));
                }
               
                TableColumn col = table.getColumnModel().getColumn(column);
                if(col instanceof ETableColumn)
                {
                    ETableColumn eColumn = (ETableColumn) col;
                   
                    // If column is sorted, prepend the up/down arrow
                    if(eColumn.isSorted())
                    {
                        if(eColumn.isAscending())
                        {
                            label.setText(this.UP_ARROW.concat(label.getText()));
                        }
                        else
                        {
                            label.setText(
                                    this.DOWN_ARROW.concat(label.getText()));
                        }
                    }
                }
            }
           
            return label;
        }        
    }    
}

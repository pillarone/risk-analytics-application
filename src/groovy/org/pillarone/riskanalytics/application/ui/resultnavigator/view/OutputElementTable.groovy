package org.pillarone.riskanalytics.application.ui.resultnavigator.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.table.TableRowFilter
import com.ulcjava.base.application.table.TableRowSorter
import com.ulcjava.base.application.table.ULCTableColumn
import com.ulcjava.base.shared.UlcEventConstants
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.KeyfigureSelectionModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElementTableModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.ITableRowFilterListener
import com.ulcjava.base.application.*

/**
 * Table for the OutputElement's that also views the different categories
 * associated with the elements in different columns.
 *
 * @author martin.melchior
 */
class OutputElementTable extends ULCTable implements ITableRowFilterListener {

    /**
     * A reference to the model underlying the period and statistics keyfigure selection panel.
     */
    KeyfigureSelectionModel keyfigureSelection // quite a hack to put this here, but is convenient for DnD to the CustomTable

    // private AssignCategoryDialog assignCategory
    private List<Integer> selectedModelRows

    OutputElementTable(OutputElementTableModel model) {
        // model and the like
        this.setModel model
        this.setRowSorter(new TableRowSorter(model))
        this.setSelectionMode(ULCListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        this.setRowSelectionAllowed(true)
        this.setAutoCreateColumnsFromModel(true)
        this.keyfigureSelection = model.keyFigureSelectionModel
        ClientContext.setModelUpdateMode(model, UlcEventConstants.SYNCHRONOUS_MODE)

        // view
        this.setShowHorizontalLines(true)
        this.setShowVerticalLines(true)
        // this.setAutoResizeMode(ULCTable.AUTO_RESIZE_ALL_COLUMNS)
        this.setVisible true

        // renderer
        setRenderer()

        // context menu
        createContextMenu()
    }

    /**
     * Set a filter to be applied to the table.
     * @param filter
     */
    public void setFilter(TableRowFilter filter) {
        this.getRowSorter().setRowFilter(filter)
    }

    /**
     * Set the renderer components that will e.g. depict tooltips
     */
    private void setRenderer() {
        for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
            String colName = getModel().getColumnName(i)                        
            ULCTableColumn column = getColumnModel().getColumn(i);
            if (colName == OutputElement.PATH) {
                column.setCellRenderer(new PathCellRenderer());
            } else {
                column.setCellRenderer(new CategoryCellRenderer(colName))
            }
        }
    }

    /**
     * Create the context menu shown on left clicks
     */
    private void createContextMenu() {

        // create the entries
        ULCPopupMenu menu = new ULCPopupMenu();
        ULCMenuItem selectAllItem = new ULCMenuItem("select all");
        ULCMenuItem deselectAllItem = new ULCMenuItem("de-select all");
        menu.add(selectAllItem);
        menu.add(deselectAllItem);
        this.setComponentPopupMenu(menu);

        // attach selection listeners so that associated actions will be called
        selectAllItem.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                OutputElementTable.this.getSelectionModel().clearSelection()
                OutputElementTable.this.getSelectionModel().setSelectionInterval(0, OutputElementTable.this.getRowCount()-1) //upperIndex is inclusive
            }
        });

        deselectAllItem.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                OutputElementTable.this.getSelectionModel().setSelectionInterval(0, 0)
            }
        });

        /*
        ULCMenuItem assignCategoryItem = new ULCMenuItem("assign category");
        menu.add(assignCategoryItem); // --> disabled since of little use
        OutputElementTableModel tableModel = (OutputElementTableModel) this.getModel()
        List<String> categories = tableModel.getCategories()
        assignCategory = new AssignCategoryDialog(UlcUtilities.getWindowAncestor(this), categories)
        assignCategoryItem.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                selectedModelRows = []
                int[] rows = OutputElementTable.this.getSelectionModel().getSelectedIndices()
                for (int i : rows) {
                    int modelRow = OutputElementTable.this.convertRowIndexToModel(i)
                    selectedModelRows.add( modelRow )
                }
                if (selectedModelRows && selectedModelRows.size()>0) {
                    assignCategory.setVisible true
                }
            }
        });
        assignCategory.addSaveActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                String[] assignment = assignCategory.getAssignedValue()
                for (int rowIndex : selectedModelRows) { 
                    OutputElement element = tableModel.getRowElement(rowIndex)
                    element.getCategoryMap()[assignment[0]]=assignment[1]
                }
                tableModel.fireTableDataChanged()
            }
        })*/

    }

    /*void addCategory(String newCategory) {
        OutputElementTableModel tableModel = (OutputElementTableModel) this.getModel()
        tableModel.addCategory(newCategory)
        // work around due to UBA-8496 --> fixed in RIA Suite Update 5.
        TableRowSorter sorter = this.getRowSorter()
        this.setRowSorter(null)
        tableModel.fireTableStructureChanged()
        this.setRowSorter(sorter)
    }*/

    private class PathCellRenderer extends DefaultTableCellRenderer {

        @Override
        public IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean isSelected, boolean hasFocus, int row) {
            DefaultTableCellRenderer rendererComponent = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row);
            OutputElement rowElement = ((OutputElementTableModel) OutputElementTable.this.model).getRowElement(row)
            String templatePath = new String(rowElement.templatePath)
            for (String wc : rowElement.wildCardPath.allWildCards) {
                templatePath = templatePath.replace("\${${wc}}", "<b style='color:green'>\${${wc}}</b>")
            }
            rendererComponent.setToolTipText("<html>Template based on categories ${rowElement.wildCardPath.allWildCards.toListString()}: <br>"
                                                                                    + " &nbsp; ${templatePath}</html>")
            return rendererComponent
        }
    }
    
    private class CategoryCellRenderer extends DefaultTableCellRenderer {
        
        String category
        
        CategoryCellRenderer(String category) {
            super()
            this.category=category
        }

        @Override
        public IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean isSelected, boolean hasFocus, int row) {
            DefaultTableCellRenderer rendererComponent = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row);
            OutputElement rowElement = ((OutputElementTableModel) OutputElementTable.this.model).getRowElement(row)
            String text = "<b>Possible values for the category <b style='color:green'>${category}:</b> <br> "
            List<String> values = rowElement.wildCardPath.getWildCardValues(category)
            if (values != null) {
                for (String val : values) {
                    text = text + "&nbsp; ${val} <br>"
                }
                rendererComponent.setToolTipText("<html>${text}</html>")
            }
            return rendererComponent
        }
    }
    
}

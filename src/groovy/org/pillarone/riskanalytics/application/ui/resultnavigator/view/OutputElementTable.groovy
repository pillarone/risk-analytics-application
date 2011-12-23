package org.pillarone.riskanalytics.application.ui.resultnavigator.view

import com.ulcjava.base.application.ULCTable
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElementTableModel
import com.ulcjava.base.application.table.TableRowSorter
import com.ulcjava.base.application.table.TableRowFilter

import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.shared.UlcEventConstants
import com.ulcjava.base.application.ULCListSelectionModel

import org.pillarone.riskanalytics.application.ui.resultnavigator.util.ITableRowFilterListener
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.UlcUtilities
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import com.ulcjava.base.application.table.DefaultTableCellRenderer
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.table.ULCTableColumn

/**
 * @author martin.melchior
 */
class OutputElementTable extends ULCTable implements ITableRowFilterListener {

    AssignCategoryDialog assignCategory
    List<Integer> selectedModelRows

    OutputElementTable(OutputElementTableModel model) {
        // model and the like
        this.setModel model
        this.setRowSorter(new TableRowSorter(model))
        // this.setSelectionBackground(Color.black)
        // this.setSelectionForeground(Color.white)
        this.setSelectionMode(ULCListSelectionModel.MULTIPLE_INTERVAL_SELECTION)
        this.setRowSelectionAllowed(true)
        this.setAutoCreateColumnsFromModel(true)

        // view
        this.setShowHorizontalLines(true)
        this.setShowVerticalLines(true)
        // this.setAutoResizeMode(ULCTable.AUTO_RESIZE_ALL_COLUMNS)
        this.setVisible true

        ClientContext.setModelUpdateMode(model, UlcEventConstants.SYNCHRONOUS_MODE)

        // renderer
        setRenderer()

        // context menu
        createContextMenu()


    }

    private void setRenderer() {
        DefaultTableCellRenderer defaultTableTreeCellRenderer = new DefaultTableCellRenderer()
        PathCellRenderer cellRendererWithTooltip = new PathCellRenderer()
        for (int i = 0; i < getColumnModel().getColumnCount(); i++) {
            ULCTableColumn column = getColumnModel().getColumn(i);
            if (i != ((OutputElementTableModel)this.model).getColumnIndex(OutputElement.PATH)) {
                column.setCellRenderer(defaultTableTreeCellRenderer);
            } else {
                column.setCellRenderer(cellRendererWithTooltip)
                // column.setMaxWidth(50)
            }
        }

    }

    void setFilter(TableRowFilter filter) {
        this.getRowSorter().setRowFilter(filter)
    }

    private void createContextMenu() {
        OutputElementTableModel tableModel = (OutputElementTableModel) this.getModel()
        List<String> categories = tableModel.getCategories()

        ULCPopupMenu menu = new ULCPopupMenu();

        ULCMenuItem selectAllItem = new ULCMenuItem("select all");
        selectAllItem.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                OutputElementTable.this.getSelectionModel().clearSelection()
                OutputElementTable.this.getSelectionModel().setSelectionInterval(0, OutputElementTable.this.getRowCount()-1) //upperIndex is inclusive
            }
        });
        menu.add(selectAllItem);

        ULCMenuItem deselectAllItem = new ULCMenuItem("de-select all");
        deselectAllItem.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                OutputElementTable.this.getSelectionModel().setSelectionInterval(0, 0)
            }
        });
        menu.add(deselectAllItem);

        assignCategory = new AssignCategoryDialog(UlcUtilities.getWindowAncestor(this), categories)
        ULCMenuItem assignCategoryItem = new ULCMenuItem("assign category");
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
        menu.add(assignCategoryItem);
        assignCategory.addSaveActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                String[] assignment = assignCategory.getAssignment()
                for (int rowIndex : selectedModelRows) { 
                    OutputElement element = tableModel.getRowElement(rowIndex)
                    element.getCategoryMap()[assignment[0]]=assignment[1]
                }
                tableModel.fireTableDataChanged()
            }
        })
        // addCategoryListChangeListener(assignCategory.getCategoryComboBox())

        this.setComponentPopupMenu(menu);
    }

    void addCategory(String newCategory) {
        OutputElementTableModel tableModel = (OutputElementTableModel) this.getModel()
        tableModel.addCategory(newCategory)
        // work around due to UBA-8496 --> fixed in RIA Suite Update 5.
        TableRowSorter sorter = this.getRowSorter()
        this.setRowSorter(null)
        tableModel.fireTableStructureChanged()
        this.setRowSorter(sorter)
        /*ListDataEvent e = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, 0, categories.size())
        for (IListDataListener listener : categoryListChangeListeners) {
            listener.contentsChanged e
        } */       
    }

    private class PathCellRenderer extends DefaultTableCellRenderer {

        @Override
        public IRendererComponent getTableCellRendererComponent(ULCTable table, Object value, boolean isSelected, boolean hasFocus, int row) {
            DefaultTableCellRenderer rendererComponent = (DefaultTableCellRenderer) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row);
            OutputElement rowElement = ((OutputElementTableModel) OutputElementTable.this.model).getRowElement(row)
            rendererComponent.setToolTipText(rowElement.getWildCardPath().templatePath)
            return rendererComponent
        }
    }
}

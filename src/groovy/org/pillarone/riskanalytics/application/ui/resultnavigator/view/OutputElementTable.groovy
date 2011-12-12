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

import com.ulcjava.base.application.event.ListDataEvent
import com.ulcjava.base.application.event.IListDataListener

/**
 * To change this template use File | Settings | File Templates.
 */
class OutputElementTable extends ULCTable implements ITableRowFilterListener {

    AssignCategoryDialog assignCategory
    AddCategoryDialog addCategory
    List<Integer> selectedModelRows
    List<IListDataListener> categoryListChangeListeners = []

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

        // context menu
        createContextMenu()
        
    }

    void setFilter(TableRowFilter filter) {
        this.getRowSorter().setRowFilter(filter)
    }

    void addCategoryListChangeListener(IListDataListener listener) {
        if (!categoryListChangeListeners.contains(listener)) {
            categoryListChangeListeners.add listener
        }
    }

    void removeCategoryListChangeListener(IListDataListener listener) {
        if (categoryListChangeListeners.contains(listener)) {
            categoryListChangeListeners.remove listener
        }
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

        addCategory = new AddCategoryDialog(UlcUtilities.getWindowAncestor(this))
        addCategory.setVisible false
        ULCMenuItem addCategoryItem = new ULCMenuItem("add category");
        addCategoryItem.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                addCategory.setVisible true
            }
        });
        menu.add(addCategoryItem);
        addCategory.addSaveActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                String newCategory = addCategory.getCategoryName()
                tableModel.addCategory(newCategory)
                // work around due to UBA-8496 --> fixed in RIA Suite Update 5.
                TableRowSorter sorter = OutputElementTable.this.getRowSorter()
                OutputElementTable.this.setRowSorter(null)
                tableModel.fireTableStructureChanged()
                OutputElementTable.this.setRowSorter(sorter)
                ListDataEvent e = new ListDataEvent(categoryList, ListDataEvent.CONTENTS_CHANGED, 0, categories.size())
                for (IListDataListener listener : categoryListChangeListeners) {
                    listener.contentsChanged e
                }
            }
        })

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
        addCategoryListChangeListener(assignCategory.getCategoryComboBox())

        this.setComponentPopupMenu(menu);
    }
}

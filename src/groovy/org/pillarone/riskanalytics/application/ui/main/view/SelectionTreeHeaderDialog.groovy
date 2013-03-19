package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.base.model.ITableTreeFilter
import org.pillarone.riskanalytics.application.ui.base.model.ModellingItemNodeFilter
import org.pillarone.riskanalytics.application.ui.base.model.MultiFilteringTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeRowSorterAction
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class SelectionTreeHeaderDialog {
    protected ULCWindow parent
    protected ULCDialog dialog
    protected ULCBoxPane content
    protected ULCButton ascOrder
    protected ULCButton descOrder
    protected ULCButton applyButton
    protected ULCButton clearButton
    protected ULCButton cancelButton
    protected ULCTableTree tableTree
    int columnIndex
    protected List filterValues
    MultiFilteringTableTreeModel model
    ModellingItemNodeFilter filter

    public SelectionTreeHeaderDialog(ULCTableTree tree, int columnIndex) {
        this.tableTree = tree
        model = tableTree.model
        this.parent = UlcUtilities.getWindowAncestor(tableTree)
        this.columnIndex = columnIndex
        this.filterValues = []
    }

    public void init() {
        initFilter()
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {
        dialog = new ULCDialog(parent, UIUtils.getText(SelectionTreeHeaderDialog, "title"), true)

        ascOrder = new ULCButton(new SelectionTreeRowSorterAction(tableTree.model, true, columnIndex))
        ascOrder.setContentAreaFilled false
        ascOrder.setBackground Color.white
        ascOrder.setOpaque false
        ascOrder.setPreferredSize(new Dimension(20, 20))
        descOrder = new ULCButton(new SelectionTreeRowSorterAction(tableTree.model, false, columnIndex))
        descOrder.setContentAreaFilled false
        descOrder.setBackground Color.white
        descOrder.setOpaque false
        descOrder.setPreferredSize(new Dimension(20, 20))
        if (columnIndex == model.orderByColumn) {
            if (model.ascOrder) {
                ascOrder.setBorder(BorderFactory.createLineBorder(Color.blue))
            } else {
                descOrder.setBorder(BorderFactory.createLineBorder(Color.blue))
            }
        }
        applyButton = new ULCButton(UIUtils.getText(SelectionTreeHeaderDialog.class, "apply"))
        cancelButton = new ULCButton(UIUtils.getText(SelectionTreeHeaderDialog.class, "cancel"))
        clearButton = new ULCButton(UIUtils.getText(SelectionTreeHeaderDialog.class, "clearAll"))
        content = new ULCBoxPane(1, 0)
        content.setPreferredSize new Dimension(200, 300)

    }

    private void layoutComponents() {
        ULCBoxPane buttonsPane = new ULCBoxPane(3, 0)
        buttonsPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(UIUtils.getText(SelectionTreeHeaderDialog.class, "sortedBy")))
        buttonsPane.add(ULCBoxPane.BOX_LEFT_TOP, ascOrder)
        buttonsPane.add(ULCBoxPane.BOX_LEFT_TOP, descOrder)
        content.add(ULCBoxPane.BOX_LEFT_TOP, buttonsPane)
        if (model.filters.size() > 0) {
            ULCBoxPane filtersPane = new ULCBoxPane(2, 0)
            filtersPane.setBorder BorderFactory.createTitledBorder(UIUtils.getText(SelectionTreeHeaderDialog.class, "alreadyUsed") + " : ");
            model.filters.each {ITableTreeFilter filter ->
                if (filter.column != columnIndex) {
                    ULCCheckBox checkBox = new ULCCheckBox(getFilterValues(filter), true)
                    checkBox.addValueChangedListener([valueChanged: { ValueChangedEvent event ->
                        if (checkBox.selected)
                            model.addFilter(filter)
                        else
                            model.removeFilter(filter)
                    }] as IValueChangedListener)
                    filtersPane.add(ULCBoxPane.BOX_LEFT_TOP, checkBox)
                    filtersPane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
                }
            }
            filtersPane.add(2, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
            ULCScrollPane filtersScrollPane = new ULCScrollPane(filtersPane)
            filtersScrollPane.setPreferredSize new Dimension(160, 200)
            content.add(ULCBoxPane.BOX_EXPAND_EXPAND, filtersScrollPane)
        }

        ULCScrollPane scrollPane = new ULCScrollPane(addChoiceButton())
        scrollPane.setPreferredSize new Dimension(160, 200)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, scrollPane)


        ULCBoxPane buttonPane = new ULCBoxPane(3, 1)
        buttonPane.add(ULCBoxPane.BOX_RIGHT_BOTTOM, UIUtils.spaceAround(applyButton, 10, 0, 0, 0))
        buttonPane.add(ULCBoxPane.BOX_RIGHT_BOTTOM, UIUtils.spaceAround(clearButton, 10, 0, 0, 0))
        buttonPane.add(ULCBoxPane.BOX_RIGHT_BOTTOM, UIUtils.spaceAround(cancelButton, 10, 0, 0, 0))
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, buttonPane)

        dialog.add(content)
        dialog.pack()
        dialog.resizable = false
    }

    abstract public ULCBoxPane addChoiceButton()

    abstract public void initFilter()

    abstract protected void selectValues()

    protected void attachListeners() {
        cancelButton.addActionListener([actionPerformed: { ActionEvent ->
            dialog.dispose()
        }] as IActionListener)

        clearButton.addActionListener([actionPerformed: { ActionEvent ->
            model.filters.clear()
            model.applyFilter()
            dialog.dispose()
        }] as IActionListener)

        ascOrder.addActionListener([actionPerformed: { ActionEvent ->
            dialog.dispose()
        }] as IActionListener)

        descOrder.addActionListener([actionPerformed: { ActionEvent ->
            dialog.dispose()
        }] as IActionListener)
    }



    public String getColumnName(int column) {
        return tableTree.model.getColumnFilterName(column)
    }

    String getFilterValues(ModellingItemNodeFilter filter) {
        StringBuilder sb = new StringBuilder("<html><b>" + getColumnName(filter.column) + "</b>:<br> ");
        if (filter.displayValue) {
            sb.append(filter.displayValue + "</html>")
        } else {
            if (filter.allSelected)
                sb.append(UIUtils.getText(SelectionTreeHeaderDialog.class, "all"))
            else
                filter.values.eachWithIndex {def it, int index ->
                    sb.append(it)
                    if (index < filter.values.size() - 1)
                        sb.append(", ")
                }
            sb.append("</html>")
        }
        return sb.toString()
    }

}





package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.base.model.ParameterizationNodeFilter
import org.pillarone.riskanalytics.application.ui.main.action.SelectionTreeRowSorterAction
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*
import static org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeModel.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTreeHeaderDialog {
    private ULCWindow parent
    ULCDialog dialog
    ULCBoxPane content
    private ULCToolBar toolBar
    private ULCToggleButton ascOrder
    private ULCToggleButton descOrder
    private List<ULCCheckBox> filterCheckBoxes
    private ULCButton applyButton
    private ULCButton cancelButton
    private ULCTableTree tableTree
    int columnIndex

    public SelectionTreeHeaderDialog(ULCTableTree tree, int columnIndex) {
        this.tableTree = tree
        this.parent = UlcUtilities.getWindowAncestor(tableTree)
        this.columnIndex = columnIndex
    }

    public void init() {
        initFilter()
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void initComponents() {
        dialog = new ULCDialog(parent, "SelectionTreeHeaderDialog", true)
        toolBar = new ULCToolBar()
        toolBar.setOrientation(ULCToolBar.VERTICAL)

        ascOrder = new ULCToggleButton(new SelectionTreeRowSorterAction(tableTree.model, true, columnIndex))
        descOrder = new ULCToggleButton(new SelectionTreeRowSorterAction(tableTree.model, false, columnIndex))
        applyButton = new ULCButton("apply")
        cancelButton = new ULCButton("cancel")
        content = new ULCBoxPane(1, 0)
        content.setPreferredSize new Dimension(200, 300)

    }

    private void layoutComponents() {
        if (isOrderVisible()) {
            toolBar.add(ascOrder)
            toolBar.add(descOrder)
            content.add(ULCBoxPane.BOX_EXPAND_TOP, ascOrder)
            content.add(ULCBoxPane.BOX_EXPAND_TOP, descOrder)
        }

        ULCBoxPane filterPane = new ULCBoxPane(2, 0)
        filterPane.setBorder BorderFactory.createTitledBorder(getFilterPaneTitle());
        filterCheckBoxes.each {ULCCheckBox checkBox ->
            filterPane.add(ULCBoxPane.BOX_LEFT_TOP, checkBox)
            filterPane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        }
        filterPane.add(2, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        ULCScrollPane scrollPane = new ULCScrollPane(filterPane)
        scrollPane.setPreferredSize new Dimension(160, 200)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, scrollPane)


        ULCBoxPane buttonPane = new ULCBoxPane(2, 1)
        buttonPane.add(ULCBoxPane.BOX_RIGHT_BOTTOM, UIUtils.spaceAround(applyButton, 10, 0, 0, 0))
        buttonPane.add(ULCBoxPane.BOX_RIGHT_BOTTOM, UIUtils.spaceAround(cancelButton, 10, 0, 0, 0))
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, buttonPane)

        dialog.add(content)
        dialog.setUndecorated(true)
        dialog.pack()
        dialog.resizable = false
    }

    private void attachListeners() {
        applyButton.addActionListener([actionPerformed: { ActionEvent ->
            List checkBoxValues = []
            filterCheckBoxes.each {ULCCheckBox checkBox ->
                if (checkBox.selected) {
                    checkBoxValues << checkBox.getText()
                }
            }
            ParameterizationNodeFilter filter = new ParameterizationNodeFilter(checkBoxValues, filterCheckBoxes[0].selected ? -1 : columnIndex)
            tableTree.model.setFilter(filter)
            dialog.dispose()
        }] as IActionListener)
        cancelButton.addActionListener([actionPerformed: { ActionEvent ->
            dialog.dispose()
        }] as IActionListener)
        ascOrder.addActionListener([actionPerformed: { ActionEvent ->
            dialog.dispose()
        }] as IActionListener)
        descOrder.addActionListener([actionPerformed: { ActionEvent ->
            dialog.dispose()
        }] as IActionListener)
    }

    public void initFilter() {
        filterCheckBoxes = []
        ULCCheckBox allCheckBox = new ULCCheckBox("All")
        allCheckBox.addValueChangedListener([valueChanged: { ValueChangedEvent event ->
            filterCheckBoxes.each {ULCCheckBox checkBox ->
                checkBox.setSelected(allCheckBox.selected)
            }
        }] as IValueChangedListener)

        filterCheckBoxes << allCheckBox
        List values = tableTree.model.getValues(columnIndex)
        values.each {
            filterCheckBoxes << new ULCCheckBox(it)
        }
    }

    private boolean isOrderVisible() {
        switch (columnIndex) {
            case STATE: return true;
            case OWNER: return true;
            case LAST_UPDATER: return true;
            case CREATION_DATE: return true;
            case CREATION_DATE: return true;
            case LAST_MODIFICATION_DATE: return true;
            default: return false
        }
    }

    public String getFilterPaneTitle() {
        return "filter by: " + tableTree.model.getColumnName(columnIndex)
    }

}

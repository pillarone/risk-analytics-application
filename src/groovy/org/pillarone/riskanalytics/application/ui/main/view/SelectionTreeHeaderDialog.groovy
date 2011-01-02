package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*

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

        ascOrder = new ULCToggleButton("ascending")
        descOrder = new ULCToggleButton("descending")
        applyButton = new ULCButton("apply")
        cancelButton = new ULCButton("cancel")
        content = new ULCBoxPane(1, 0)
        content.setPreferredSize new Dimension(200, 300)

    }

    private void layoutComponents() {
        toolBar.add(ascOrder)
        toolBar.add(descOrder)

        content.add(ULCBoxPane.BOX_EXPAND_TOP, ascOrder)
        content.add(ULCBoxPane.BOX_EXPAND_TOP, descOrder)

        ULCBoxPane filterPane = new ULCBoxPane(1, 0)
        filterPane.setBorder BorderFactory.createTitledBorder("filter");
        filterCheckBoxes.each {ULCCheckBox checkBox ->
            filterPane.add(ULCBoxPane.BOX_LEFT_TOP, checkBox)
        }
        ULCScrollPane scrollPane = new ULCScrollPane(filterPane)
        scrollPane.setPreferredSize new Dimension(160, 200)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, scrollPane)

        ULCBoxPane buttonPane = new ULCBoxPane(2, 1)
        buttonPane.add(ULCBoxPane.BOX_RIGHT_BOTTOM, UIUtils.spaceAround(applyButton, 10, 0, 0, 0))
        buttonPane.add(ULCBoxPane.BOX_RIGHT_BOTTOM, UIUtils.spaceAround(cancelButton, 10, 0, 0, 0))
        content.add(ULCBoxPane.BOX_RIGHT_BOTTOM, buttonPane)

        dialog.add(content)
//        dialog.setLocationRelativeTo(parent)
        dialog.setUndecorated(true)
        dialog.pack()
        dialog.resizable = false
    }

    private void attachListeners() {
        applyButton.addActionListener([actionPerformed: { ActionEvent ->
            dialog.dispose()
        }] as IActionListener)
    }

    public void initFilter() {
        filterCheckBoxes = []
        filterCheckBoxes << new ULCCheckBox("All")
        List values = tableTree.model.getValues(columnIndex)
        values.each {
            filterCheckBoxes << new ULCCheckBox(it)
        }
    }
}

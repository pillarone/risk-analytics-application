package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.result.model.SingleValueCollectorTableTreeModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SingleCollectorView {
    ULCBoxPane content
    ULCFixedColumnTableTree tree
    SingleValueCollectorTableTreeModel singleValueCollectorTableTreeModel
    ULCLabel fromLabel
    ULCLabel toLabel
    ULCButton applyButton
    ULCButton nextButton
    ULCButton previousButton
    ULCTextField fromTextField
    ULCTextField toTextField
    Dimension dimension = new Dimension(50, 20)
    Integer from = 1
    Integer to = 100

    public SingleCollectorView(SingleValueCollectorTableTreeModel singleValueCollectorTableTreeModel) {
        this.singleValueCollectorTableTreeModel = singleValueCollectorTableTreeModel
    }

    public void init() {
        singleValueCollectorTableTreeModel.init()
        initTree()
        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected void initTree() {

        def columnsWidths = 120

        tree = new ULCFixedColumnTableTree(singleValueCollectorTableTreeModel, 1, ([200] + [columnsWidths] * (singleValueCollectorTableTreeModel.columnCount - 1)) as int[])
        tree.name = "SingleCollectorTableTree"
        tree.viewPortTableTree.setRootVisible(false);
        tree.viewPortTableTree.showsRootHandles = true

        tree.rowHeaderTableTree.name = "SingleCollectorTreeRowHeader"
        tree.rowHeaderTableTree.setRootVisible(false);
        tree.rowHeaderTableTree.showsRootHandles = true
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = "Name"
        tree.cellSelectionEnabled = true
        SingleCollectorTableTreeNodeCellRenderer collectorTableTreeNodeCellRenderer = new SingleCollectorTableTreeNodeCellRenderer()
        tree.rowHeaderTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
            it.setCellRenderer(collectorTableTreeNodeCellRenderer)
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

        SingleCollectorValueTableTreeCellRenderer renderer = new SingleCollectorValueTableTreeCellRenderer()
        tree.viewPortTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
            it.setCellRenderer(renderer)
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

    }

    protected void initComponents() {
        content = new ULCBoxPane(1, 2)
        content.name = "SingleCollectorTreeViewContent"
        fromLabel = new ULCLabel(UIUtils.getText(this.class, "from"))
        toLabel = new ULCLabel(UIUtils.getText(this.class, "to"))
        applyButton = new ULCButton(UIUtils.getText(this.class, "apply"))
        applyButton.name = "SingleCollectorView.apply"
        nextButton = new ULCButton(UIUtils.getText(this.class, "next"))
        nextButton.name = "SingleCollectorView.nextButton"
        previousButton = new ULCButton(UIUtils.getText(this.class, "previous"))
        previousButton.name = "SingleCollectorView.previousButton"
        fromTextField = new ULCTextField()
        fromTextField.name = "SingleCollectorView.fromTextField"
        fromTextField.setMaximumSize(dimension)
        fromTextField.setPreferredSize(dimension)
        toTextField = new ULCTextField()
        toTextField.name = "SingleCollectorView.toTextField"
        toTextField.setMaximumSize(dimension)
        toTextField.setPreferredSize(dimension)
        initTextFields(fromTextField, toTextField)
    }

    private void layoutComponents() {
        ULCBoxPane pane = new ULCBoxPane()
        pane.add(ULCBoxPane.BOX_LEFT_CENTER, previousButton)
        pane.add(ULCBoxPane.BOX_LEFT_CENTER, fromTextField)
        pane.add(ULCBoxPane.BOX_LEFT_CENTER, toTextField)
        pane.add(ULCBoxPane.BOX_LEFT_CENTER, applyButton)
        pane.add(ULCBoxPane.BOX_LEFT_CENTER, nextButton)
        content.add(ULCBoxPane.BOX_LEFT_TOP, pane)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, tree)
    }

    private void attachListeners() {
        applyButton.addActionListener([actionPerformed: {event ->
            apply()
        }] as IActionListener)

        nextButton.addActionListener([actionPerformed: {event ->
            fromTextField.setText(String.valueOf(to))
            toTextField.setText(String.valueOf(to + 100))
            apply()
        }] as IActionListener)

        previousButton.addActionListener([actionPerformed: {event ->
            fromTextField.setText(String.valueOf(from - 100))
            toTextField.setText(String.valueOf(from))
            apply()
        }] as IActionListener)
    }

    private def apply() {
        try {
            from = Math.max(1, Integer.parseInt(fromTextField.getText()))
            to = Math.min(singleValueCollectorTableTreeModel.getMaxIteration(), Integer.parseInt(toTextField.getText()))
            if (validate(from, to)) {
                singleValueCollectorTableTreeModel.apply(from, to)
            } else {
                new I18NAlert("ShowSingleNumberNotAvailable").show()
            }
        } catch (Exception ex) {
            new I18NAlert("ShowSingleNumberNotAvailable").show()
            initTextFields(fromTextField, toTextField)
        }
    }

    private def initTextFields(ULCTextField fromTextField, ULCTextField toTextField) {
        from = 1
        to = 100
        fromTextField.setText(String.valueOf(from))
        toTextField.setText(String.valueOf(to))
        enable(from, to)
    }

    private boolean validate(int from, int to) {
        boolean status = to - from >= 0
        if (status) {
            fromTextField.setText(String.valueOf(from))
            toTextField.setText(String.valueOf(to))
            enable(from, to)
        }
        return status
    }

    private void enable(int from, int to) {
        previousButton.setEnabled(from > 1)
        nextButton.setEnabled(to < singleValueCollectorTableTreeModel.getMaxIteration())
    }


}

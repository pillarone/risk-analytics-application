package org.pillarone.riskanalytics.application.ui.base.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.util.Font
import com.ulcjava.base.application.util.HTMLUtilities
import com.ulcjava.base.application.util.Insets
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.base.action.*

abstract class AbstractModellingTreeView {

    AbstractModellingModel model

    ULCBoxPane content
    ULCFixedColumnTableTree tree
    ULCContainer viewComponent
    ULCLabel filterLabel
    ULCComboBox filterSelection
    ULCToolBar toolbar
    ULCToolBar selectionToolbar
    static double DIVIDER = 0.65
    static double NO_DIVIDER = 1.0

    IActionListener ctrlaction = [actionPerformed: {ActionEvent event -> new I18NAlert(UlcUtilities.getWindowAncestor(event.source), "CtrlA").show() }] as IActionListener


    public AbstractModellingTreeView(def model) {
        this.model = model
        content = new ULCBoxPane(1, 0)
        initView(model)
    }

    private def initView(def model) {
        if (model != null) {
            initComponents()
            layoutComponents()
            attachListeners()
        }
    }

    void setModel(def model) {
        this.model = model
        if (viewComponent) {
            content.remove viewComponent
            filterSelection.removeAllItems()
        }
        initView(model)
    }

    protected void initComponents() {
        initTree()
        ULCBoxPane filters = createSelectionPane()
        selectionToolbar = new ULCToolBar()
        selectionToolbar.margin = new Insets(2, 2, 2, 2)
        selectionToolbar.floatable = false
        selectionToolbar.add(filters)


        toolbar = new ULCToolBar()
        toolbar.margin = new Insets(2, 2, 2, 2)
        toolbar.floatable = false

        addToolBarElements(toolbar)
        changeToolbarFont(toolbar)
        toolbar.add(ULCFiller.createGlue())

    }

    ULCBoxPane createSelectionPane() {
        filterSelection = new ULCComboBox()
        filterSelection.name = "filter"
        filterSelection.addItem(getText("all"))
        model.nodeNames.each {
            filterSelection.addItem it
        }

        filterLabel = new ULCLabel(UIUtils.getIcon("filter-active.png"))

        ULCBoxPane filters = new ULCBoxPane(2, 1)
        filters.add(filterLabel)
        filters.add(filterSelection)
        return filters
    }

    protected changeToolbarFont(ULCContainer container) {
        container.getComponents().each {
            changeToolbarFont(it)
        }
    }

    protected changeToolbarFont(ULCComponent c) {
        Font oldFont = c.getFont()
        c.setFont(oldFont.deriveFont((oldFont.size * 1f).floatValue()))
    }

    protected changeToolbarFont(ULCAbstractButton button) {
        button.text = HTMLUtilities.convertToHtml(button.getAction().getValue(IAction.NAME)).replace(" ", "&nbsp;")
        Font oldFont = button.getFont()
        button.setFont(oldFont.deriveFont((oldFont.size * 1f).floatValue()))
    }

    protected void addToolBarElements(ULCToolBar toolbar) {

    }

    protected abstract void initTree()


    private void layoutComponents() {
        ULCBoxPane toolbarBox = new ULCBoxPane(1, 0, 5, 5)
        toolbarBox.add(new ULCFiller(0, 0))
        ULCBoxPane pane = new ULCBoxPane(2, 0)
        pane.add(selectionToolbar)
        pane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        toolbarBox.add(ULCBoxPane.BOX_EXPAND_TOP, pane)
        toolbarBox.add(ULCBoxPane.BOX_LEFT_TOP, toolbar)
        toolbarBox.add(ULCBoxPane.BOX_EXPAND_EXPAND, tree)
        viewComponent = layoutContent(toolbarBox)

        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, viewComponent)
    }

    protected ULCContainer layoutContent(ULCContainer content) {
        ULCBoxPane pane = new ULCBoxPane()
        pane.add(ULCBoxPane.BOX_EXPAND_EXPAND, content)
        return pane
    }

    private void attachListeners() {
        def rowHeaderTree = tree.getRowHeaderTableTree()
        def viewPortTree = tree.getViewPortTableTree()
        rowHeaderTree.registerKeyboardAction(new TreeNodeExpander(tree: rowHeaderTree), KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, true), ULCComponent.WHEN_FOCUSED);
        rowHeaderTree.registerKeyboardAction(new TreeNodeCollapser(tree: rowHeaderTree), KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, true), ULCComponent.WHEN_FOCUSED);
        rowHeaderTree.registerKeyboardAction(new TreeExpander(tree), KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        rowHeaderTree.registerKeyboardAction(new TreeCollapser(tree), KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        rowHeaderTree.registerKeyboardAction(new TreeNodeCopier(rowHeaderTree: rowHeaderTree, viewPortTree: viewPortTree, model: model.treeModel), KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        viewPortTree.registerKeyboardAction(new TreeNodePaster(tree: viewPortTree), KeyStroke.getKeyStroke(KeyEvent.VK_V, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        viewPortTree.registerKeyboardAction(new TreeSelectionFiller(tree: viewPortTree, model: viewPortTree.model), KeyStroke.getKeyStroke(KeyEvent.VK_R, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        viewPortTree.registerKeyboardAction(new TableTreeCopier(table: viewPortTree), KeyStroke.getKeyStroke(KeyEvent.VK_C, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        IActionListener saveAction = model.getSaveAction(content)
        if (saveAction) {
            content.registerKeyboardAction(saveAction, KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_IN_FOCUSED_WINDOW)
        }


        viewPortTree.tableTreeHeader.addActionListener([actionPerformed: {ActionEvent e ->
            //PMO-779
            //workaround: the tree will be scrolled to the root position
            tree.getVerticalScrollBar().setPosition(1)
            viewPortTree.requestFocus()
            viewPortTree.setRowSelectionInterval(0, viewPortTree.rowCount - 1)
            int selectedColumn = viewPortTree.columnModel.getColumnIndex(e.source)
            viewPortTree.setColumnSelectionInterval(selectedColumn, selectedColumn)
            tree.getVerticalScrollBar().setPosition(0)

        }] as IActionListener)


        filterSelection.addActionListener([actionPerformed: {e ->
            String filter = null
            if (filterSelection.getSelectedIndex()) {
                filter = filterSelection.getSelectedItem()
            }
            model?.updateNodeNameFilter(filter)

        }] as IActionListener)

    }



    protected void addColumns() {
    }

    protected void removeColumns() {
        List columns = []
        for (int i = 0; i < tree.viewPortTableTree.columnCount; i++) {
            columns << tree.viewPortTableTree.columnModel.getColumn(i)
        }
        for (ULCTableTreeColumn col in columns) {
            tree.viewPortTableTree.removeColumn(col)
        }
    }


    protected void nodeChanged() {
        List nodes = []
        findNodes(model.getTreeModel().getRoot(), nodes)
        for (ITableTreeNode node in nodes) {
            for (int i = 1; i < tree.viewPortTableTree.columnCount; i++) {
                model.treeModel.nodeChanged(new TreePath(DefaultTableTreeModel.getPathToRoot(node) as Object[]), i)
            }
        }
    }

    private void findNodes(ITableTreeNode node, List nodes) {
        if (node instanceof ResultTableTreeNode) {
            nodes << node
        }
        for (int i = 0; i < node.childCount; i++) {
            findNodes(node.getChildAt(i), nodes)
        }
    }

/**
 * Utility method to get resource bundle entries for this class
 *
 * @param key
 * @return the localized value corresponding to the key
 */
    protected String getText(String key) {
        return LocaleResources.getString("AbstractModellingTreeView." + key);
    }

}

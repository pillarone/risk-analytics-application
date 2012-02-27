package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.tree.ULCTreeSelectionModel
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.batch.action.OpenBatchAction
import org.pillarone.riskanalytics.application.ui.batch.action.TreeDoubleClickAction
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import static org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeModel.*
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.base.action.TreeExpander
import org.pillarone.riskanalytics.application.ui.base.action.Collapser

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTreeView {
    ULCFixedColumnTableTree tree
    ULCBoxPane content
    RiskAnalyticsMainModel mainModel
    AbstractTableTreeModel navigationTableTreeModel
    final static int TREE_FIRST_COLUMN_WIDTH = 240
    boolean ascOrder = true

    public SelectionTreeView(RiskAnalyticsMainModel mainModel) {
        this.mainModel = mainModel
        this.navigationTableTreeModel = mainModel.navigationTableTreeModel
        initTree()
        initComponents()
        layoutComponents()
        attachListeners()
    }


    protected void initComponents() {
        content = new ULCBoxPane()
        content.name = "treeViewContent"
    }

    private void layoutComponents() {
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, tree)
    }

    private void attachListeners() {
        ULCTableTree rowHeaderTableTree = tree.rowHeaderTableTree
        TreeDoubleClickAction treeDoubleClickAction = new TreeDoubleClickAction(rowHeaderTableTree, mainModel)
        rowHeaderTableTree.addActionListener(treeDoubleClickAction)
        rowHeaderTableTree.registerKeyboardAction(new DeleteAction(rowHeaderTableTree, mainModel), KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new RenameAction(rowHeaderTableTree, mainModel), KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new ImportAction(rowHeaderTableTree, mainModel), KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new ExportItemAction(rowHeaderTableTree, mainModel), KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new SimulationAction(rowHeaderTableTree, mainModel), KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new SaveAsAction(rowHeaderTableTree, mainModel), KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new OpenBatchAction(rowHeaderTableTree, mainModel), KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new TreeExpander(tree), KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, 0, false), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new Collapser(tree), KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, 0, false), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(treeDoubleClickAction, KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false), ULCComponent.WHEN_FOCUSED)
    }

    protected void initTree() {

        def columnsWidths = 120

        tree = new ULCFixedColumnTableTree(navigationTableTreeModel, 1, ([TREE_FIRST_COLUMN_WIDTH] + [columnsWidths] * (navigationTableTreeModel.columnCount - 1)) as int[])
        tree.name = "selectionTableTree"
        tree.viewPortTableTree.setRootVisible(false);
        tree.viewPortTableTree.showsRootHandles = true

        tree.viewPortTableTree.columnModel.getColumns().eachWithIndex {ULCTableTreeColumn it, int index ->
            it.setHeaderRenderer(new CenteredHeaderRenderer())
            //todo ART-206 add filter dialog
            //            it.setHeaderRenderer(new FilteredCenteredHeaderRenderer())
        }

        MainSelectionTableTreeCellRenderer renderer = getPopUpRenderer(tree)

        tree.rowHeaderTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
            it.setCellRenderer(renderer)
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

        tree.rowHeaderTableTree.name = "selectionTreeRowHeader"
        tree.rowHeaderTableTree.setRootVisible(false);
        tree.rowHeaderTableTree.showsRootHandles = true
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = "Name"
        tree.cellSelectionEnabled = true
        tree.rowHeaderTableTree.getSelectionModel().setSelectionMode(ULCTreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        tree.getRowHeaderTableTree().expandPaths([new TreePath([navigationTableTreeModel.root] as Object[])] as TreePath[], false);
        tree.getViewPortTableTree().getTableTreeHeader().addActionListener([actionPerformed: {ActionEvent event ->
            ULCTableTreeColumn column = (ULCTableTreeColumn) event.getSource()
            int columnIndex = navigationTableTreeModel.getColumnIndex(column.getModelIndex())
   // if (columnIndex == ASSIGNED_TO || columnIndex == VISIBILITY) return
            if (ActionEvent.META_MASK == event.getModifiers()) {
                SelectionTreeHeaderDialog dialog
                if (columnIndex == COMMENTS || columnIndex == REVIEW_COMMENT) {
                    dialog = new RadioButtonDialog(tree.viewPortTableTree, columnIndex)
                } else {
                    dialog = new CheckBoxDialog(tree.viewPortTableTree, columnIndex)
                }
                dialog.init()
                dialog.dialog.setLocationRelativeTo(tree)
                dialog.dialog.setAlignment(ULCBoxPane.BOX_CENTER_CENTER)
                dialog.dialog.setVisible true
            } else if (ActionEvent.BUTTON1_MASK == event.getModifiers()) {
                navigationTableTreeModel.order(columnIndex, ascOrder)
                ascOrder = !ascOrder
            }
        }] as IActionListener)
    }

    public MainSelectionTableTreeCellRenderer getPopUpRenderer(ULCFixedColumnTableTree tree) {
        MainSelectionTableTreeCellRenderer renderer = new MainSelectionTableTreeCellRenderer(tree.rowHeaderTableTree, mainModel)
        return renderer
    }

    ULCTableTree getSelectionTree() {
        return tree.rowHeaderTableTree
    }

    ITableTreeNode getRoot() {
        return navigationTableTreeModel.root
    }


}

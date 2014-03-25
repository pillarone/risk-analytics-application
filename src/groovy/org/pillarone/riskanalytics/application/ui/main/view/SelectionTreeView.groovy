package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCPollingTimer
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.tree.ULCTreeSelectionModel
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.server.ULCSession
import com.ulcjava.base.shared.IDefaults
import org.pillarone.riskanalytics.application.ui.base.action.Collapser
import org.pillarone.riskanalytics.application.ui.base.action.TreeExpander
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.FilterDefinition
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.batch.action.NewBatchAction
import org.pillarone.riskanalytics.application.ui.batch.action.OpenBatchAction
import org.pillarone.riskanalytics.application.ui.batch.action.TreeDoubleClickAction
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.core.search.CacheItemEventConsumer
import org.pillarone.riskanalytics.core.search.CacheItemEventQueueService

import javax.annotation.PostConstruct

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SelectionTreeView {
    ULCFixedColumnTableTree tree
    ULCBoxPane content
    ULCPollingTimer treeSyncTimer
    CacheItemEventConsumer eventConsumer
    RiskAnalyticsMainModel riskAnalyticsMainModel
    ModellingItemSelectionListener modellingItemSelectionListener
    ModellingInformationTableTreeModel navigationTableTreeModel
    final static int TREE_FIRST_COLUMN_WIDTH = 240
    boolean ascOrder = true

    @PostConstruct
    void initialize() {
        println(riskAnalyticsMainModel)
        initTree()
        initComponents()
        layoutComponents()
        attachListeners()
        treeSyncTimer.start()
    }

    protected void initComponents() {
        content = new ULCBoxPane()
        content.name = "treeViewContent"
        treeSyncTimer = new ULCPollingTimer(2000, [
                actionPerformed: { evt ->
                    modellingItemSelectionListener.rememberSelectionState()
                    navigationTableTreeModel.updateTreeStructure(eventConsumer)
                    modellingItemSelectionListener.flushSelectionState()

                }] as IActionListener)
        treeSyncTimer.syncClientState = false
        eventConsumer = new CacheItemEventConsumer(ULCSession.currentSession(), treeSyncTimer)
        CacheItemEventQueueService.instance.register(eventConsumer)
    }

    private void layoutComponents() {
        content.add(IDefaults.BOX_EXPAND_EXPAND, tree)
    }

    public void filterTree(FilterDefinition filterDefinition) {
        navigationTableTreeModel.filterTree(filterDefinition)
    }

    private void attachListeners() {
        ULCTableTree rowHeaderTableTree = tree.rowHeaderTableTree
        TreeDoubleClickAction treeDoubleClickAction = new TreeDoubleClickAction(rowHeaderTableTree, riskAnalyticsMainModel)
        rowHeaderTableTree.addActionListener(treeDoubleClickAction)
        rowHeaderTableTree.registerKeyboardAction(new DeleteAction(rowHeaderTableTree, riskAnalyticsMainModel), KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new RenameAction(rowHeaderTableTree, riskAnalyticsMainModel), KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new ImportAction(rowHeaderTableTree, riskAnalyticsMainModel), KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new ExportItemAction(rowHeaderTableTree, riskAnalyticsMainModel), KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new SimulationAction(rowHeaderTableTree, riskAnalyticsMainModel), KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new SaveAsAction(rowHeaderTableTree, riskAnalyticsMainModel), KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new OpenBatchAction(rowHeaderTableTree, riskAnalyticsMainModel), KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new NewBatchAction(rowHeaderTableTree, riskAnalyticsMainModel), KeyStroke.getKeyStroke(KeyEvent.VK_B, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK, true), ULCComponent.WHEN_FOCUSED)
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

        tree.viewPortTableTree.columnModel.getColumns().eachWithIndex { ULCTableTreeColumn it, int index ->
            it.setHeaderRenderer(new CenteredHeaderRenderer())
            //todo ART-206 add filter dialog
            //            it.setHeaderRenderer(new FilteredCenteredHeaderRenderer())
        }

        MainSelectionTableTreeCellRenderer renderer = getPopUpRenderer(tree)

        tree.rowHeaderTableTree.columnModel.getColumns().each { ULCTableTreeColumn it ->
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
        tree.getViewPortTableTree().getTableTreeHeader().addActionListener([actionPerformed: { ActionEvent event ->
            ULCTableTreeColumn column = (ULCTableTreeColumn) event.getSource()
            int columnIndex = navigationTableTreeModel.getColumnIndex(column.getModelIndex())

            if (ActionEvent.META_MASK == event.getModifiers()) {
                IColumnDescriptor descriptor = getDescriptor(columnIndex)
                if (descriptor != null) {
                    SelectionTreeHeaderDialog dialog =
                            new CheckBoxDialog(tree.viewPortTableTree, columnIndex, descriptor)
                    dialog.addFilterChangedListener([filterChanged: { FilterDefinition filter ->
                        filterTree(filter)
                    }] as IFilterChangedListener)
                    dialog.init()
                    dialog.dialog.setLocationRelativeTo(tree)
                    dialog.dialog.setAlignment(IDefaults.BOX_CENTER_CENTER)
                    dialog.dialog.setVisible true
                }
            } else if (ActionEvent.BUTTON1_MASK == event.getModifiers()) {
                navigationTableTreeModel.order(columnIndex, ascOrder)
                ascOrder = !ascOrder
            }
        }] as IActionListener)
        modellingItemSelectionListener = new ModellingItemSelectionListener(tree)
    }

    public MainSelectionTableTreeCellRenderer getPopUpRenderer(ULCFixedColumnTableTree tree) {
        MainSelectionTableTreeCellRenderer renderer = new MainSelectionTableTreeCellRenderer(tree.rowHeaderTableTree, riskAnalyticsMainModel)
        return renderer
    }

    ULCTableTree getSelectionTree() {
        return tree.rowHeaderTableTree
    }

    ITableTreeNode getRoot() {
        return navigationTableTreeModel.getRoot()
    }

    private IColumnDescriptor getDescriptor(int columnIndex) {
        switch (columnIndex) {
            case ModellingInformationTableTreeModel.TAGS:
                return new IColumnDescriptor.TagColumnDescriptor()
            case ModellingInformationTableTreeModel.STATE:
                return new IColumnDescriptor.StateColumnDescriptor()
        }

        return null
    }


}

package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.google.common.eventbus.Subscribe
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.tree.ULCTreeSelectionModel
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.shared.IDefaults
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.base.action.Collapser
import org.pillarone.riskanalytics.application.ui.base.action.TreeExpander
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.FilterDefinition
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.action.*
import org.pillarone.riskanalytics.application.ui.main.eventbus.RiskAnalyticsEventBus
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.ModellingItemEvent
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.PreDestroy
import javax.annotation.Resource

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class SelectionTreeView {

    private ModellingItemSelectionListener modellingItemSelectionListener
    static int TREE_FIRST_COLUMN_WIDTH = 390
    // Avoid building whole app just to tweak these settings
    //
    static {
        try{
            TREE_FIRST_COLUMN_WIDTH = Integer.parseInt( System.getProperty("GUI_TREE_FIRST_COLUMN_WIDTH", "390") )
        } catch( NumberFormatException e){
            TREE_FIRST_COLUMN_WIDTH = 390
        }

    }

    ULCFixedColumnTableTree tree
    ULCBoxPane content
    @Resource
    RiskAnalyticsEventBus riskAnalyticsEventBus
    @Resource
    NavigationTableTreeModel navigationTableTreeModel

    boolean ascOrder = true

    @PostConstruct
    void initialize() {
        initTree()
        initComponents()
        layoutComponents()
        attachListeners()
        riskAnalyticsEventBus.register(this)
    }

    @PreDestroy
    void unregister() {
        riskAnalyticsEventBus.unregister(this)
    }

    protected void initComponents() {
        content = new ULCBoxPane()
        content.name = "treeViewContent"
    }

    @Subscribe
    void onItemEvent(ModellingItemEvent event) {
        modellingItemSelectionListener.rememberSelectionState()
        navigationTableTreeModel.updateTreeStructure(event)
        modellingItemSelectionListener.flushSelectionState()
    }

    private void layoutComponents() {
        content.add(IDefaults.BOX_EXPAND_EXPAND, tree)
    }

    public void filterTree(FilterDefinition filterDefinition) {
        navigationTableTreeModel.filterTree(filterDefinition)
    }

    private void attachListeners() {
        ULCTableTree rowHeaderTableTree = tree.rowHeaderTableTree
        TreeDoubleClickAction treeDoubleClickAction = new TreeDoubleClickAction(rowHeaderTableTree)
        rowHeaderTableTree.addActionListener(treeDoubleClickAction)
        rowHeaderTableTree.registerKeyboardAction(new DeleteAction(rowHeaderTableTree), KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new RenameAction(rowHeaderTableTree), KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new ImportAction(rowHeaderTableTree), KeyStroke.getKeyStroke(KeyEvent.VK_I, KeyEvent.CTRL_DOWN_MASK, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new ExportItemAction(rowHeaderTableTree), KeyStroke.getKeyStroke(KeyEvent.VK_E, KeyEvent.CTRL_DOWN_MASK, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new SimulationAction(rowHeaderTableTree), KeyStroke.getKeyStroke(KeyEvent.VK_F9, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTableTree.registerKeyboardAction(new SaveAsAction(rowHeaderTableTree), KeyStroke.getKeyStroke(KeyEvent.VK_S, KeyEvent.CTRL_DOWN_MASK + KeyEvent.SHIFT_DOWN_MASK, true), ULCComponent.WHEN_FOCUSED)
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
        tree.rowSelectionAllowed = true
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
        MainSelectionTableTreeCellRenderer renderer = new MainSelectionTableTreeCellRenderer(tree.rowHeaderTableTree)
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
            case NavigationTableTreeModel.TAGS:
                return new IColumnDescriptor.TagColumnDescriptor()
            case NavigationTableTreeModel.STATE:
                return new IColumnDescriptor.StateColumnDescriptor()
        }

        return null
    }


}

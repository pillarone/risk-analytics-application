package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.DefaultCellEditor
import com.ulcjava.base.application.IRendererComponent
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCTabbedPane
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ITableTreeModelListener
import com.ulcjava.base.application.event.ITreeExpansionListener
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TableTreeModelEvent
import com.ulcjava.base.application.event.TreeExpansionEvent
import com.ulcjava.base.application.event.TreeSelectionEvent
import com.ulcjava.base.application.tabletree.DefaultTableTreeHeaderCellRenderer
import com.ulcjava.base.application.tabletree.ITableTreeCellEditor
import com.ulcjava.base.application.tabletree.ITableTreeCellRenderer
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.shared.UlcEventCategories
import com.ulcjava.base.shared.UlcEventConstants
import java.util.Map.Entry
import org.pillarone.riskanalytics.application.util.SimulationUtilities
import org.pillarone.riskanalytics.application.ui.base.action.TableTreeCopier
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodePaster
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingTreeView
import org.pillarone.riskanalytics.application.ui.base.view.ComponentNodeTableTreeNodeRenderer
import org.pillarone.riskanalytics.application.ui.base.view.DelegatingCellEditor
import org.pillarone.riskanalytics.application.ui.base.view.DelegatingCellRenderer
import org.pillarone.riskanalytics.application.ui.base.view.IModelItemChangeListener
import org.pillarone.riskanalytics.application.ui.base.view.PropertiesView
import org.pillarone.riskanalytics.application.ui.parameterization.model.ConstrainedStringParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.DateParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.DoubleTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.EnumParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.IntegerTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationClassifierTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.SimpleValueParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.view.BasicCellEditor
import org.pillarone.riskanalytics.application.ui.parameterization.view.BasicCellRenderer
import org.pillarone.riskanalytics.application.ui.parameterization.view.ComboBoxCellComponent
import org.pillarone.riskanalytics.application.ui.parameterization.view.MultiDimensionalCellRenderer
import org.pillarone.riskanalytics.application.ui.parameterization.view.MultiDimensionalParameterView
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.util.UIUtils

class ParameterView extends AbstractModellingTreeView implements IModelItemChangeListener {
    ULCTabbedPane tabbedPane
    PropertiesView propertiesView

    ParameterView(ParameterViewModel model) {
        super(model)
    }

    protected void initTree() {

        def treeModel = model.treeModel

        int treeWidth = UIUtils.calculateTreeWidth(treeModel.root)
        def columnsWidths = Math.max(UIUtils.calculateColumnWidth(treeModel.root, 1) + 10, 150)

        tree = new ULCFixedColumnTableTree(model.treeModel, 1, ([treeWidth] + [columnsWidths] * model.periodCount) as int[])


        tree.viewPortTableTree.name = "parameterTreeContent"
        tree.viewPortTableTree.columnModel.getColumns().eachWithIndex {ULCTableTreeColumn it, int index ->

            it.setCellEditor(new DelegatingCellEditor(createEditorConfiguration()))
            it.setCellRenderer(new DelegatingCellRenderer(createRendererConfiguration(index + 1, tree.viewPortTableTree)))
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }
        ComponentNodeTableTreeNodeRenderer renderer = new ComponentNodeTableTreeNodeRenderer(tree, model)


        tree.rowHeaderTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
            it.setCellRenderer(renderer)
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

        tree.rowHeaderTableTree.name = "parameterTreeRowHeader"
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = "Name"
        tree.cellSelectionEnabled = true
        // TODO (Mar 20, 2009, msh): Identified this as cause for PMO-240 (expand behaviour).

        tree.viewPortTableTree.addActionListener(new MultiDimensionalTabStarter(this))


        model.treeModel.root.childCount.times {
            tree.expandPath new TreePath([model.treeModel.root, model.treeModel.root.getChildAt(it)] as Object[])
        }

        new SelectionTracker(tree)
    }

    private Map createEditorConfiguration() {
        DefaultCellEditor defaultEditor = new DefaultCellEditor(new ULCTextField());
        DefaultCellEditor doubleEditor = new BasicCellEditor(DataTypeFactory.getDoubleDataTypeForEdit());
        DefaultCellEditor integerEditor = new BasicCellEditor(DataTypeFactory.getIntegerDataTypeForEdit());
        DefaultCellEditor dateEditor = new BasicCellEditor(DataTypeFactory.getDateDataType());

        ComboBoxCellComponent comboBoxEditor = new ComboBoxCellComponent();

        Map editors = new HashMap<Class, ITableTreeCellEditor>();
        editors.put(SimpleValueParameterizationTableTreeNode.class,
                defaultEditor);
        editors.put(DoubleTableTreeNode.class,
                doubleEditor);
        editors.put(IntegerTableTreeNode.class,
                integerEditor);
        editors.put(DateParameterizationTableTreeNode.class,
                dateEditor);
        editors.put(EnumParameterizationTableTreeNode.class,
                comboBoxEditor);
        editors.put(ParameterizationClassifierTableTreeNode.class,
                comboBoxEditor);
        editors.put(ConstrainedStringParameterizationTableTreeNode.class,
                comboBoxEditor);

        return editors
    }

    private Map createRendererConfiguration(int columnIndex, ULCTableTree tree) {
        BasicCellRenderer defaultRenderer = new BasicCellRenderer(columnIndex);
        MultiDimensionalCellRenderer mdpRenderer = new MultiDimensionalCellRenderer(columnIndex);
        BasicCellRenderer doubleRenderer = new BasicCellRenderer(columnIndex, DataTypeFactory.getDoubleDataTypeForNonEdit());
        BasicCellRenderer integerRenderer = new BasicCellRenderer(columnIndex, DataTypeFactory.getIntegerDataTypeForNonEdit());
        BasicCellRenderer dateRenderer = new BasicCellRenderer(columnIndex, DataTypeFactory.getDateDataType());
        ComboBoxCellComponent comboBoxRenderer = new ComboBoxCellComponent();

        ULCPopupMenu menu = new ULCPopupMenu();
        TableTreeCopier copier = new TableTreeCopier();
        copier.setTable(tree);
        menu.add(new ULCMenuItem(copier));
        TreeNodePaster paster = new TreeNodePaster();
        paster.setTree(tree);
        menu.add(new ULCMenuItem(paster));

        initRenderer(defaultRenderer, menu);
        initRenderer(doubleRenderer, menu);
        initRenderer(integerRenderer, menu);
        initRenderer(dateRenderer, menu);
        initComboBox(comboBoxRenderer, menu);
        initRenderer(mdpRenderer, menu);

        Map renderers = new HashMap<Class, ITableTreeCellRenderer>();
        renderers.put(SimpleValueParameterizationTableTreeNode.class,
                defaultRenderer);
        renderers.put(DoubleTableTreeNode.class,
                doubleRenderer);
        renderers.put(IntegerTableTreeNode.class,
                integerRenderer);
        renderers.put(DateParameterizationTableTreeNode.class,
                dateRenderer);
        renderers.put(EnumParameterizationTableTreeNode.class,
                comboBoxRenderer);
        renderers.put(ParameterizationClassifierTableTreeNode.class,
                comboBoxRenderer);
        renderers.put(ConstrainedStringParameterizationTableTreeNode.class,
                comboBoxRenderer);
        renderers.put(MultiDimensionalParameterizationTableTreeNode.class,
                mdpRenderer);

        return renderers
    }

    private void initRenderer(ULCLabel renderer, ULCPopupMenu menu) {
        renderer.setHorizontalAlignment(ULCLabel.RIGHT);
        renderer.setComponentPopupMenu(menu);
    }

    private void initComboBox(ULCComboBox renderer, ULCPopupMenu menu) {
        renderer.setComponentPopupMenu(menu);
    }

    protected void initComponents() {
        tabbedPane = new ULCCloseableTabbedPane(name: 'tabbedPane')
        tabbedPane.tabPlacement = ULCTabbedPane.TOP
        tabbedPane.addTabListener([tabClosing: {TabEvent event ->
            event.getClosableTabbedPane().closeCloseableTab(event.getTabClosingIndex())
            event.getClosableTabbedPane().selectedIndex = 0
        }] as ITabListener)

        super.initComponents();
    }



    protected ULCContainer layoutContent(ULCContainer content) {

        tabbedPane.removeAll()
        tabbedPane.addTab(model.treeModel.root.name, UIUtils.getIcon("treeview-active.png"), content)
        propertiesView = new PropertiesView(model.propertiesViewModel)
        tabbedPane.addTab(propertiesView.getText("properties"), UIUtils.getIcon("settings-active.png"), propertiesView.content)
        tabbedPane.setCloseableTab(0, false)
        tabbedPane.setCloseableTab(1, false)
        return tabbedPane
    }

    public void modelItemChanged() {
        propertiesView.updateGui()
    }

}

class MultiDimensionalTabStarter implements IActionListener {

    ParameterView parameterView
    Map openTabs = [:]

    public MultiDimensionalTabStarter(ParameterView parameterView) {
        this.@parameterView = parameterView
        parameterView.tabbedPane.addTabListener(
                [tabClosing: {
                    TabEvent event ->
                    int index = event.getTabClosingIndex()

                    for (Iterator it = openTabs.iterator(); it.hasNext();) {
                        Map.Entry entry = it.next();
                        if (entry.value > index) {
                            entry.value--
                        } else if (entry.value == index) {
                            it.remove()
                        }
                    }
                }
                ] as ITabListener)

    }

    public void actionPerformed(ActionEvent event) {
        ULCTableTree tree = event.source
        def lastComponent = tree.getSelectedPath().lastPathComponent

        if (lastComponent instanceof MultiDimensionalParameterizationTableTreeNode) {
            TabIdentifier identifier = new TabIdentifier(path: tree.getSelectedPath(), columnIndex: tree.selectedColumn)
            def index = openTabs.get(identifier)
            ULCTabbedPane tabbedPane = parameterView.tabbedPane

            if (index == null) {
                MultiDimensionalParameterModel model = new MultiDimensionalParameterModel(tree.model, lastComponent, tree.selectedColumn + 1)
                ClientContext.setModelUpdateMode(model.tableModel, UlcEventConstants.SYNCHRONOUS_MODE)
                model.tableModel.addListener([modelChanged: { parameterView.model.item.changed = true }] as IModelChangedListener)
                tabbedPane.addTab("${lastComponent.displayName} ${tree.getColumnModel().getColumn(tree.getSelectedColumn()).getHeaderValue()}", new MultiDimensionalParameterView(model).content)
                int currentTab = tabbedPane.tabCount - 1
                tabbedPane.selectedIndex = currentTab
                tabbedPane.setToolTipTextAt(currentTab, model.getPathAsString())
                openTabs.put(new TabIdentifier(path: tree.getSelectedPath(), columnIndex: tree.selectedColumn), currentTab)
            } else {
                tabbedPane.selectedIndex = index
            }
        } else {
            int selectedRow = tree.selectedRow
            if (selectedRow + 1 <= tree.rowCount) {
                tree.selectionModel.setSelectionPath(tree.getPathForRow(selectedRow + 1))
            }
        }
    }

}

class TabIdentifier {
    TreePath path
    Integer columnIndex

    public boolean equals(Object obj) {
        if (obj instanceof TabIdentifier) {
            return obj.path.equals(path) && obj.columnIndex.equals(columnIndex)
        } else {
            return false
        }
    }

    public int hashCode() {
        return path.hashCode() * columnIndex;
    }


}

class TreeExpansionForwarder implements ITreeExpansionListener {

    ITableTreeModel model

    public TreeExpansionForwarder(ITableTreeModel model) {
        this.@model = model
    }

    public void treeCollapsed(TreeExpansionEvent event) {
        forwardStateChange(event.path, false)
    }

    public void treeExpanded(TreeExpansionEvent event) {
        forwardStateChange(event.path, true)
    }

    private void forwardStateChange(TreePath path, boolean expanded) {
        model.expansionChanged(path, expanded)
    }

}

class CenteredHeaderRenderer extends DefaultTableTreeHeaderCellRenderer {
    int columnIndex = -1

    def CenteredHeaderRenderer() {
    }

    def CenteredHeaderRenderer(columnIndex) {
        this.columnIndex = columnIndex;
    }

    public IRendererComponent getTableTreeCellRendererComponent(ULCTableTree tableTree, Object value, boolean selected, boolean hasFocus, boolean expanded, boolean leaf, Object node) {
        setBackground(tableTree)
        IRendererComponent component = super.getTableTreeCellRendererComponent(tableTree, value, selected, hasFocus, expanded, leaf, node)
        component.horizontalAlignment = ULCLabel.CENTER
        component.setToolTipText String.valueOf(value)

        return component
    }

    private void setBackground(ULCTableTree tableTree) {
        if (columnIndex > -1) {
            int pIndex = columnIndex % tableTree.model.getParameterizationsSize()
            setBackground(SimulationUtilities.RESULT_VIEW_COLOR[pIndex])
        }
    }

}

class SelectionTracker implements ITableTreeModelListener, ITreeSelectionListener {

    ULCFixedColumnTableTree tableTree
    TreePath selectedPath
    int selectedColumn




    public SelectionTracker(ULCFixedColumnTableTree tableTree) {
        this.tableTree = tableTree
        tableTree.rowHeaderTableTree.model.addTableTreeModelListener this
        tableTree.rowHeaderTableTree.selectionModel.addTreeSelectionListener this
        tableTree.rowHeaderTableTree.selectionModel.setEventDeliveryMode(UlcEventCategories.SELECTION_CHANGED_EVENT_CATEGORY, UlcEventConstants.ASYNCHRONOUS_MODE)
        tableTree.rowHeaderTableTree.setEventDeliveryMode(UlcEventCategories.SELECTION_CHANGED_EVENT_CATEGORY, UlcEventConstants.ASYNCHRONOUS_MODE)
        tableTree.viewPortTableTree.selectionModel.addTreeSelectionListener this
        tableTree.viewPortTableTree.selectionModel.setEventDeliveryMode(UlcEventCategories.SELECTION_CHANGED_EVENT_CATEGORY, UlcEventConstants.ASYNCHRONOUS_MODE)
        tableTree.viewPortTableTree.setEventDeliveryMode(UlcEventCategories.SELECTION_CHANGED_EVENT_CATEGORY, UlcEventConstants.ASYNCHRONOUS_MODE)
    }

    public void tableTreeStructureChanged(TableTreeModelEvent tableTreeModelEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void tableTreeNodeStructureChanged(TableTreeModelEvent tableTreeModelEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void tableTreeNodesInserted(TableTreeModelEvent tableTreeModelEvent) {
        selectedAffectedNode(tableTreeModelEvent)
    }

    public void tableTreeNodesRemoved(TableTreeModelEvent tableTreeModelEvent) {
        selectedAffectedNode(tableTreeModelEvent)
    }

    private void selectedAffectedNode(TableTreeModelEvent tableTreeModelEvent) {
        TreePath parentPath = tableTreeModelEvent.treePath
        TreePath scrollingPath = parentPath.lastPathComponent.childCount > 0 ? parentPath.pathByAddingChild(parentPath.lastPathComponent.getChildAt(-1)) : parentPath
        TreePath selectionPath = parentPath.lastPathComponent.childCount > 0 ? parentPath.pathByAddingChild(parentPath.lastPathComponent.getChildAt(0)) : parentPath
        tableTree.expandPath parentPath
        def yPosition = tableTree.getVerticalScrollBar().getPosition()
        if (selectedColumn > 0) {
            tableTree.viewPortTableTree.scrollCellToVisible scrollingPath, selectedColumn - 1
            tableTree.viewPortTableTree.selectionModel.setSelectionPath(selectionPath)

        } else {
            tableTree.rowHeaderTableTree.scrollCellToVisible scrollingPath, 0
            tableTree.rowHeaderTableTree.selectionModel.setSelectionPath(selectionPath)
        }
        restoreColumnSelection()
        tableTree.getVerticalScrollBar().setPosition(yPosition)
    }

    private restoreColumnSelection() {
        if (selectedColumn == 0) {
            tableTree.rowHeaderTableTree.setColumnSelectionInterval(selectedColumn, selectedColumn)
        } else {
            tableTree.viewPortTableTree.setColumnSelectionInterval(selectedColumn - 1, selectedColumn - 1)
        }
    }

    public void tableTreeNodesChanged(TableTreeModelEvent tableTreeModelEvent) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void valueChanged(TreeSelectionEvent event) {

        def currentSelection

        event.paths.each {pathsElement ->
            if (event.isAddedPath(pathsElement)) {
                currentSelection = pathsElement
            }
        }
        if (currentSelection) {
            selectedPath = currentSelection

            int col = tableTree.rowHeaderTableTree.getSelectedColumn()

            if (col >= 0) {
                selectedColumn = col
            } else {
                selectedColumn = tableTree.viewPortTableTree.getSelectedColumn() + 1
            }
        }

    }


}

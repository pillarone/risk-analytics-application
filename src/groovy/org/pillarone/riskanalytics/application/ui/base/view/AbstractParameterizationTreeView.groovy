package org.pillarone.riskanalytics.application.ui.base.view

import com.ulcjava.base.application.DefaultCellEditor
import com.ulcjava.base.application.ULCTextField
import org.pillarone.riskanalytics.application.ui.parameterization.view.BasicCellEditor
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.parameterization.view.ComboBoxCellComponent
import org.pillarone.riskanalytics.application.ui.parameterization.view.CheckBoxCellComponent
import com.ulcjava.base.application.tabletree.ITableTreeCellEditor
import org.pillarone.riskanalytics.application.ui.parameterization.model.SimpleValueParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.DoubleTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.BooleanTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.IntegerTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.DateParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.EnumParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationClassifierTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ResourceParameterizationTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ConstrainedStringParameterizationTableTreeNode
import com.ulcjava.base.application.ULCTableTree
import org.pillarone.riskanalytics.application.ui.parameterization.view.BasicCellRenderer
import org.pillarone.riskanalytics.application.ui.parameterization.view.MultiDimensionalCellRenderer
import com.ulcjava.base.application.ULCPopupMenu
import com.ulcjava.base.application.ULCMenuItem
import org.pillarone.riskanalytics.application.ui.base.action.OpenMDPAction
import org.pillarone.riskanalytics.application.ui.base.action.TableTreeCopier
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodePaster
import org.pillarone.riskanalytics.application.ui.comment.action.InsertCommentAction
import org.pillarone.riskanalytics.application.ui.comment.action.ShowCommentsAction
import com.ulcjava.base.application.tabletree.ITableTreeCellRenderer
import org.pillarone.riskanalytics.application.ui.parameterization.model.MultiDimensionalParameterizationTableTreeNode
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCCheckBox
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.ULCTabbedPane
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.ulcjava.base.application.event.SelectionChangedEvent
import com.ulcjava.base.application.event.ISelectionChangedListener
import org.pillarone.riskanalytics.application.ui.main.action.RemoveDynamicSubComponent
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.ULCComponent
import org.pillarone.riskanalytics.application.ui.main.action.AddDynamicSubComponent
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodeRename
import org.pillarone.riskanalytics.application.ui.base.action.TreeExpander
import org.pillarone.riskanalytics.application.ui.base.action.TreeCollapser
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import com.ulcjava.base.application.ULCSplitPane
import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.ULCBoxPane
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.ui.parameterization.action.MultiDimensionalTabStarter
import org.pillarone.riskanalytics.application.ui.comment.model.CommentFilter
import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.parameterization.view.SelectionTracker
import org.pillarone.riskanalytics.application.ui.parameterization.model.AbstractParametrizedViewModel


abstract class AbstractParameterizationTreeView extends AbstractModellingTreeView {

    ULCCloseableTabbedPane tabbedPane
    ULCSplitPane splitPane
    CommentAndErrorView commentAndErrorView
    def commentFilters


    AbstractParameterizationTreeView(AbstractParametrizedViewModel model) {
        super(model)
        commentFilters = [:]
    }

    protected void initComponents() {
        commentAndErrorView = new CommentAndErrorView(model)
        tabbedPane = new ULCCloseableTabbedPane(name: 'tabbedPane')
        tabbedPane.tabPlacement = ULCTabbedPane.TOP
        tabbedPane.addTabListener([tabClosing: {TabEvent event ->
            event.getClosableTabbedPane().closeCloseableTab(event.getTabClosingIndex())
            event.getClosableTabbedPane().selectedIndex = 0
        }] as ITabListener)
        tabbedPane.addSelectionChangedListener([selectionChanged: { SelectionChangedEvent event ->
            if (commentFilters) {
                ULCTabbedPane tabbedPane = (ULCTabbedPane) event.getSource();
                int selection = tabbedPane.getSelectedIndex();
                model?.tabbedPaneChanged(commentFilters[selection])
                showHiddenCommentToolBar(selection)
            }
        }] as ISelectionChangedListener)


        super.initComponents();
        attachListeners()
//        updateErrorVisualization(model.item) TODO
    }

    protected void attachListeners() {
        def rowHeaderTree = tree.getRowHeaderTableTree()
        rowHeaderTree.registerKeyboardAction(new TreeExpander(tree), KeyStroke.getKeyStroke(KeyEvent.VK_RIGHT, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        rowHeaderTree.registerKeyboardAction(new TreeCollapser(tree), KeyStroke.getKeyStroke(KeyEvent.VK_LEFT, KeyEvent.CTRL_DOWN_MASK, false), ULCComponent.WHEN_FOCUSED)
        rowHeaderTree.unregisterKeyboardAction(KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK))
        rowHeaderTree.registerKeyboardAction(ctrlaction, KeyStroke.getKeyStroke(KeyEvent.VK_A, KeyEvent.CTRL_DOWN_MASK), ULCComponent.WHEN_FOCUSED)

        Closure closeSplitPane = {->
            int count = ((ULCCloseableTabbedPane) splitPane.getBottomComponent()).getTabCount()
            if (count == 1) {
                splitPane.setDividerLocation(NO_DIVIDER)
            }
        }
        commentAndErrorView.addPopupMenuListener(closeSplitPane)
    }

    protected void internalAttachListeners() {}

    protected void initTree() {

        def treeModel = model.treeModel

        int treeWidth = UIUtils.calculateTreeWidth(treeModel.root)
        def columnsWidths = Math.max(UIUtils.calculateColumnWidth(treeModel.root, 1) + 10, 150)

        tree = new ULCFixedColumnTableTree(model.treeModel, 1, ([treeWidth] + [columnsWidths] * model.periodCount) as int[])


        tree.viewPortTableTree.name = getViewPortTableTreeName()
        tree.viewPortTableTree.columnModel.getColumns().eachWithIndex {ULCTableTreeColumn it, int index ->

            it.setCellEditor(new DelegatingCellEditor(createEditorConfiguration()))
            it.setCellRenderer(new DelegatingCellRenderer(createRendererConfiguration(index + 1, tree.viewPortTableTree)))
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }
        ComponentNodeTableTreeNodeRenderer renderer = new ComponentNodeTableTreeNodeRenderer(tree, model, commentAndErrorView)


        tree.rowHeaderTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
            it.setCellRenderer(renderer)
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

        tree.rowHeaderTableTree.name = getRowHeaderTableTreeName()
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = "Name"
        tree.cellSelectionEnabled = true

        tree.viewPortTableTree.addActionListener(new MultiDimensionalTabStarter(this))


        tree.getRowHeaderTableTree().expandPaths([new TreePath([model.treeModel.root] as Object[])] as TreePath[], false);
        commentAndErrorView.tableTree = tree
        new SelectionTracker(tree)
    }

    protected abstract String getRowHeaderTableTreeName()
    protected abstract String getViewPortTableTreeName()

    protected ULCContainer layoutContent(ULCContainer content) {
        ULCBoxPane contentPane = new ULCBoxPane(1, 1)
        splitPane = new ULCSplitPane(ULCSplitPane.VERTICAL_SPLIT)
        splitPane.oneTouchExpandable = true
        splitPane.setResizeWeight(1)
        splitPane.setDividerSize(10)

        splitPane.setDividerLocation(DIVIDER)
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, splitPane)
        tabbedPane.removeAll()
        tabbedPane.addTab(model.treeModel.root.name, UIUtils.getIcon("treeview-active.png"), content)
        tabbedPane.setCloseableTab(0, false)
        splitPane.add(tabbedPane);
        splitPane.add(commentAndErrorView.tabbedPane)
        return splitPane
    }


    public void removeTabs() {
        int count = tabbedPane.getTabCount()
        for (int i = count - 1; i > 1; i--) {
            tabbedPane.closeCloseableTab(i)
        }
        tree.viewPortTableTree.getActionListeners().each {
            if (it instanceof MultiDimensionalTabStarter) {
                it.openTabs = [:]
            }
        }
    }

    public void showHiddenComments() {
        if ((NO_DIVIDER - splitPane.getDividerLocationRelative()) < 0.1)
            splitPane.setDividerLocation(DIVIDER)
        else
            splitPane.setDividerLocation(NO_DIVIDER)
    }

    public void showComments() {
        if ((NO_DIVIDER - splitPane.getDividerLocationRelative()) < 0.1) {
            splitPane.setDividerLocation(DIVIDER)
        }
    }

    public void selectTab(int index) {
        if (index < tabbedPane.getTabCount())
            tabbedPane.setSelectedIndex(index)
    }

    public void addCommentFilter(int tabbedPaneIndex, CommentFilter filter) {
        commentFilters[tabbedPaneIndex] = filter
        model?.tabbedPaneChanged(filter)
        showHiddenCommentToolBar(tabbedPaneIndex)
    }

    private void showHiddenCommentToolBar(int tabbedPaneIndex) {
        commentAndErrorView.commentSearchPane.setVisible(tabbedPaneIndex <= 1)
    }

    protected Map createEditorConfiguration() {
        DefaultCellEditor defaultEditor = new DefaultCellEditor(new ULCTextField());
        DefaultCellEditor doubleEditor = new BasicCellEditor(DataTypeFactory.getDoubleDataTypeForEdit());
        DefaultCellEditor integerEditor = new BasicCellEditor(DataTypeFactory.getIntegerDataTypeForEdit());
        DefaultCellEditor dateEditor = new BasicCellEditor(DataTypeFactory.getDateDataType());

        ComboBoxCellComponent comboBoxEditor = new ComboBoxCellComponent();
        CheckBoxCellComponent checkBoxEditor = new CheckBoxCellComponent();

        Map editors = new HashMap<Class, ITableTreeCellEditor>();
        editors.put(SimpleValueParameterizationTableTreeNode.class,
                defaultEditor);
        editors.put(DoubleTableTreeNode.class,
                doubleEditor);
        editors.put(BooleanTableTreeNode.class, checkBoxEditor);
        editors.put(IntegerTableTreeNode.class,
                integerEditor);
        editors.put(DateParameterizationTableTreeNode.class,
                dateEditor);
        editors.put(EnumParameterizationTableTreeNode.class,
                comboBoxEditor);
        editors.put(ParameterizationClassifierTableTreeNode.class,
                comboBoxEditor);
        editors.put(ResourceParameterizationTableTreeNode.class,
                comboBoxEditor);
        editors.put(ConstrainedStringParameterizationTableTreeNode.class,
                comboBoxEditor);

        return editors
    }

    protected Map createRendererConfiguration(int columnIndex, ULCTableTree tree) {
        BasicCellRenderer defaultRenderer = new BasicCellRenderer(columnIndex);
        MultiDimensionalCellRenderer mdpRenderer = new MultiDimensionalCellRenderer(columnIndex);
        BasicCellRenderer doubleRenderer = new BasicCellRenderer(columnIndex, DataTypeFactory.getDoubleDataTypeForNonEdit());
        BasicCellRenderer integerRenderer = new BasicCellRenderer(columnIndex, DataTypeFactory.getIntegerDataTypeForNonEdit());
        BasicCellRenderer dateRenderer = new BasicCellRenderer(columnIndex, DataTypeFactory.getDateDataType());
        ComboBoxCellComponent comboBoxRenderer = new ComboBoxCellComponent();
        CheckBoxCellComponent checkBoxRenderer = new CheckBoxCellComponent();

        ULCPopupMenu menu = new ULCPopupMenu();
        ULCPopupMenu mdpMenu = new ULCPopupMenu();
        mdpMenu.add(new ULCMenuItem(new OpenMDPAction(tree)))

        TableTreeCopier copier = new TableTreeCopier();
        copier.setTable(tree);
        menu.add(new ULCMenuItem(copier));
        mdpMenu.add(new ULCMenuItem(copier));
        TreeNodePaster paster = new TreeNodePaster();
        paster.setTree(tree);
        menu.add(new ULCMenuItem(paster));
        mdpMenu.add(new ULCMenuItem(paster));
        InsertCommentAction insertComment = new InsertCommentAction(tree, (columnIndex - 1) % model.periodCount)
        insertComment.addCommentListener commentAndErrorView
        ShowCommentsAction showCommentsAction = new ShowCommentsAction(tree, (columnIndex - 1) % model.periodCount, false)
        showCommentsAction.addCommentListener commentAndErrorView

        mdpMenu.addSeparator()
        mdpMenu.add(new ULCMenuItem(insertComment))
        mdpMenu.add(new ULCMenuItem(showCommentsAction))

        menu.addSeparator()
        menu.add(new ULCMenuItem(insertComment))
        menu.add(new ULCMenuItem(showCommentsAction))

        defaultRenderer.setMenu(menu)
        doubleRenderer.setMenu(menu)
        integerRenderer.setMenu(menu)
        dateRenderer.setMenu(menu)
        initComboBox(comboBoxRenderer, menu);
        initCheckBox(checkBoxRenderer, menu);
        mdpRenderer.setMenu(mdpMenu)

        Map renderers = new HashMap<Class, ITableTreeCellRenderer>();
        renderers.put(SimpleValueParameterizationTableTreeNode.class,
                defaultRenderer);
        renderers.put(DoubleTableTreeNode.class,
                doubleRenderer);
        renderers.put(BooleanTableTreeNode.class, checkBoxRenderer);
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
        renderers.put(ResourceParameterizationTableTreeNode.class,
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

    private void initCheckBox(ULCCheckBox renderer, ULCPopupMenu menu) {
        renderer.setComponentPopupMenu(menu);
    }
}

package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.tabletree.ITableTreeCellEditor
import com.ulcjava.base.application.tabletree.ITableTreeCellRenderer
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.base.action.TableTreeCopier
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodePaster
import org.pillarone.riskanalytics.application.ui.comment.action.InsertCommentAction
import org.pillarone.riskanalytics.application.ui.comment.action.ShowCommentsAction
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.comment.view.NavigationListener
import org.pillarone.riskanalytics.application.ui.main.action.AddDynamicSubComponent
import org.pillarone.riskanalytics.application.ui.main.action.RemoveDynamicSubComponent
import org.pillarone.riskanalytics.application.ui.parameterization.action.MultiDimensionalTabStarter
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.base.view.*
import org.pillarone.riskanalytics.application.ui.parameterization.model.*

class ParameterView extends AbstractModellingTreeView implements IModelItemChangeListener, NavigationListener {

    ULCTabbedPane tabbedPane
    PropertiesView propertiesView
    CommentAndErrorView commentAndErrorView
    ULCSplitPane splitPane
    boolean tabbedPaneVisible = true

    ParameterView(ParameterViewModel model) {
        super(model)
        model.addNavigationListener this
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
        ComponentNodeTableTreeNodeRenderer renderer = new ComponentNodeTableTreeNodeRenderer(tree, model, commentAndErrorView)


        tree.rowHeaderTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
            it.setCellRenderer(renderer)
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

        tree.rowHeaderTableTree.name = "parameterTreeRowHeader"
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = "Name"
        tree.cellSelectionEnabled = true
        // TODO (Mar 20, 2009, msh): Identified this as cause for PMO-240 (expand behaviour).

        tree.viewPortTableTree.addActionListener(new MultiDimensionalTabStarter(this))


        tree.getRowHeaderTableTree().expandPaths([new TreePath([model.treeModel.root] as Object[])] as TreePath[], false);
        new SelectionTracker(tree)
    }

    private Map createEditorConfiguration() {
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
        CheckBoxCellComponent checkBoxRenderer = new CheckBoxCellComponent();

        ULCPopupMenu menu = new ULCPopupMenu();
        TableTreeCopier copier = new TableTreeCopier();
        copier.setTable(tree);
        menu.add(new ULCMenuItem(copier));
        TreeNodePaster paster = new TreeNodePaster();
        paster.setTree(tree);
        menu.add(new ULCMenuItem(paster));
        InsertCommentAction insertComment = new InsertCommentAction(tree, (columnIndex - 1) % model.periodCount)
        insertComment.addCommentListener commentAndErrorView
        ShowCommentsAction showCommentsAction = new ShowCommentsAction(tree, (columnIndex - 1) % model.periodCount, false)
        showCommentsAction.addCommentListener commentAndErrorView

        menu.addSeparator()
        menu.add(new ULCMenuItem(insertComment))
        menu.add(new ULCMenuItem(showCommentsAction))
        menu.addSeparator()

        initRenderer(defaultRenderer, menu);
        initRenderer(doubleRenderer, menu);
        initRenderer(integerRenderer, menu);
        initRenderer(dateRenderer, menu);
        initComboBox(comboBoxRenderer, menu);
        initCheckBox(checkBoxRenderer, menu);
        initRenderer(mdpRenderer, menu);

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

    protected void initComponents() {
        commentAndErrorView = new CommentAndErrorView(model)
        tabbedPane = new ULCCloseableTabbedPane(name: 'tabbedPane')
        tabbedPane.tabPlacement = ULCTabbedPane.TOP
        tabbedPane.addTabListener([tabClosing: {TabEvent event ->
            event.getClosableTabbedPane().closeCloseableTab(event.getTabClosingIndex())
            event.getClosableTabbedPane().selectedIndex = 0
        }] as ITabListener)

        super.initComponents();
        attachListeners()
        updateErrorVisualization(model.item)
    }

    //TODO: remove listener after closing view

    protected void attachListeners() {
        def rowHeaderTree = tree.getRowHeaderTableTree()
        rowHeaderTree.registerKeyboardAction(new RemoveDynamicSubComponent(tree.rowHeaderTableTree, model), KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTree.registerKeyboardAction(new AddDynamicSubComponent(tree.rowHeaderTableTree, model), KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0, true), ULCComponent.WHEN_FOCUSED)

        def parameterization = model.getItem() as Parameterization
        parameterization.addModellingItemChangeListener([itemSaved: {item ->},
                itemChanged: {Parameterization item ->
                    updateErrorVisualization(item)
                }] as IModellingItemChangeListener)
    }

    protected void updateErrorVisualization(Parameterization item) {
        commentAndErrorView.updateErrorVisualization item
    }

    protected ULCContainer layoutContent(ULCContainer content) {
        ULCBoxPane contentPane = new ULCBoxPane(1, 1)

        splitPane = new ULCSplitPane(ULCSplitPane.VERTICAL_SPLIT)
        splitPane.oneTouchExpandable = true
        splitPane.setResizeWeight(1)
        splitPane.setDividerSize(10)
        splitPane.add(content); splitPane.add(commentAndErrorView.tabbedPane)
        splitPane.setDividerLocation(0.65)
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, splitPane)
        tabbedPane.removeAll()
        tabbedPane.addTab(model.treeModel.root.name, UIUtils.getIcon("treeview-active.png"), contentPane)
        propertiesView = new PropertiesView(model.propertiesViewModel)
        tabbedPane.addTab(propertiesView.getText("properties"), UIUtils.getIcon("settings-active.png"), propertiesView.content)
        tabbedPane.setCloseableTab(0, false)
        tabbedPane.setCloseableTab(1, false)

        return tabbedPane


    }

    public void modelItemChanged() {
        propertiesView.updateGui()
    }

    public void removeTabs() {
        int count = tabbedPane.getTabCount()
        for (int i = 2; i < count; i++)
            tabbedPane.closeCloseableTab(i)
        tree.viewPortTableTree.getActionListeners().each {
            if (it instanceof MultiDimensionalTabStarter)
                it.openTabs = [:]
        }
    }

    public void commentsSelected() {
        this.tabbedPaneVisible = !tabbedPaneVisible
        if (this.tabbedPaneVisible)
            splitPane.setDividerLocation(0.65)
        else
            splitPane.setDividerLocation(1.0)
    }


}






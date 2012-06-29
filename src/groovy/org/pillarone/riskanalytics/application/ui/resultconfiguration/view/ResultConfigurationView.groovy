package org.pillarone.riskanalytics.application.ui.resultconfiguration.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.tabletree.DefaultTableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeCellEditor
import com.ulcjava.base.application.tabletree.ITableTreeCellRenderer
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.parameterization.view.ComboBoxCellComponent
import org.pillarone.riskanalytics.application.ui.resultconfiguration.model.ResultConfigurationTableTreeNode
import org.pillarone.riskanalytics.application.ui.resultconfiguration.model.ResultConfigurationViewModel
import org.pillarone.riskanalytics.application.ui.resulttemplate.view.ResultConfigurationTableTreeNodeRenderer
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.base.view.*

class ResultConfigurationView extends AbstractModellingTreeView implements IModelItemChangeListener {

    private ULCLabel treeTitle
    ULCDetachableTabbedPane tabbedPane
    PropertiesView propertiesView

    public ResultConfigurationView(ResultConfigurationViewModel model) {
        super(model)
    }

    protected void initTree() {
        treeTitle = new ULCLabel(UIUtils.getText(ResultConfigurationView.class, "selectTheKeyFigures") + ":")

        int treeWidth = UIUtils.calculateTreeWidth(model.treeModel.root)
        int fixColumnCount = 1

        tree = new ULCFixedColumnTableTree(model.treeModel, fixColumnCount, ([treeWidth] + [300] * model.periodCount) as int[])
        tree.viewPortTableTree.name = "resultConfigurationTreeContent"
        tree.viewPortTableTree.columnModel.getColumns().eachWithIndex {ULCTableTreeColumn it, int index ->
            it.headerValue = UIUtils.getText(ResultConfigurationView.class, "CollectionMode")
            it.setCellRenderer(new DelegatingCellRenderer(createRendererConfiguration(index + fixColumnCount, tree.viewPortTableTree)))
            it.setCellEditor(new DelegatingCellEditor(createEditorConfiguration()))
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }
        tree.rowHeaderTableTree.columnModel.getColumns().eachWithIndex {ULCTableTreeColumn it, index ->
            it.setCellRenderer(new ResultConfigurationTableTreeNodeRenderer(tree))
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }
        tree.rowHeaderTableTree.name = "resultConfigurationTreeRowHeader"
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = "Name"
        tree.cellSelectionEnabled = true
        model.treeModel.getLeafsWithValue().each {
            TreePath path = new TreePath(DefaultTableTreeModel.getPathToRoot(it.parent) as Object[])
            tree.expandPath(path)
        }
    }

    private Map createEditorConfiguration() {
        ComboBoxCellComponent comboBoxRenderer = new ComboBoxCellComponent();

        Map renderers = new HashMap<Class, ITableTreeCellEditor>();
        renderers.put(ResultConfigurationTableTreeNode.class,
                comboBoxRenderer);

        return renderers
    }

    private Map createRendererConfiguration(int columnIndex, ULCTableTree tree) {
        ComboBoxCellComponent comboBoxRenderer = new ComboBoxCellComponent();

        Map renderers = new HashMap<Class, ITableTreeCellRenderer>();
        renderers.put(ResultConfigurationTableTreeNode.class,
                comboBoxRenderer);

        return renderers
    }

    protected void initComponents() {
        tabbedPane = new ULCDetachableTabbedPane()
        tabbedPane.tabPlacement = ULCTabbedPane.TOP
        tabbedPane.addTabListener([tabClosing: {TabEvent event ->
            event.getClosableTabbedPane().closeCloseableTab(event.getTabClosingIndex())
            event.getClosableTabbedPane().selectedIndex = 0
        }] as ITabListener)

        super.initComponents();
    }



    protected ULCContainer layoutContent(ULCContainer content) {
        propertiesView = new PropertiesView(model.propertiesViewModel)
        ULCBoxPane simulationPane = new ULCBoxPane(1, 1)

        tabbedPane.removeAll()
        tabbedPane.addTab(model.treeModel.root.name, UIUtils.getIcon("treeview-active.png"), content)
        tabbedPane.addTab('Properties', UIUtils.getIcon("settings-active.png"), propertiesView.content)
        tabbedPane.setCloseableTab(0, false)
        tabbedPane.setCloseableTab(1, false)
        simulationPane.add(ULCBoxPane.BOX_LEFT_TOP, treeTitle)
        simulationPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, tabbedPane)

        return simulationPane
    }

    public void modelItemChanged() {
        propertiesView.updateGui()
    }

    public void removeTabs() {
        int count = tabbedPane.getTabCount()
        for (int i = 2; i < count; i++)
            tabbedPane.closeCloseableTab(i)
    }

}
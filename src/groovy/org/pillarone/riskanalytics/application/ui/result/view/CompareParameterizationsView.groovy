package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.ULCTabbedPane
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.TreePath
import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingTreeView
import org.pillarone.riskanalytics.application.ui.base.view.CompareComponentNodeTableTreeNodeRenderer
import org.pillarone.riskanalytics.application.ui.base.view.CompareParameterizationRenderer
import org.pillarone.riskanalytics.application.ui.base.view.PropertiesView
import org.pillarone.riskanalytics.application.ui.parameterization.model.CompareParameterViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.parameterization.view.SelectionTracker
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.ULCBoxPane

/**
 * @author fouad jaada
 */

public class CompareParameterizationsView extends AbstractModellingTreeView {

    ULCCloseableTabbedPane tabbedPane
    PropertiesView propertiesView

    public CompareParameterizationsView(CompareParameterViewModel model) {
        super(model);
    }

    @Override
    protected void initComponents() {
        tabbedPane = new ULCCloseableTabbedPane(name: 'tabbedPane')
        tabbedPane.tabPlacement = ULCTabbedPane.TOP
        super.initComponents()
    }



    protected ULCContainer layoutContent(ULCContainer content) {
        ULCBoxPane contentPane = new ULCBoxPane(1, 1)

        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, tabbedPane)
        tabbedPane.removeAll()
        tabbedPane.addTab(model.treeModel.root.name, UIUtils.getIcon("treeview-active.png"), content)
        tabbedPane.setCloseableTab(0, false)
        return contentPane
    }

    protected void initTree() {
        def treeModel = model.treeModel

        int treeWidth = UIUtils.calculateTreeWidth(treeModel.root)
        tree = new ULCFixedColumnTableTree(model.treeModel, 1, ([treeWidth] + ([150] * (model.columnCount - 1))) as int[])

        CompareParameterizationRenderer cRenderer = new CompareParameterizationRenderer()
        tree.viewPortTableTree.name = "parameterTreeContent"
        def clonedColumns = []
        //remove columns for cloned parameterization
        tree.viewPortTableTree.columnModel.getColumns().eachWithIndex {ULCTableTreeColumn it, int index ->
            if (index % model.items.size() == 0) {
                clonedColumns.add(it)
            }
        }
        clonedColumns.each {
            tree.viewPortTableTree.columnModel.removeColumn it
        }


        tree.viewPortTableTree.columnModel.getColumns().eachWithIndex {ULCTableTreeColumn it, int index ->

            it.setCellRenderer(cRenderer)
            it.setHeaderRenderer(new CenteredHeaderRenderer(index))
        }
        CompareComponentNodeTableTreeNodeRenderer renderer = new CompareComponentNodeTableTreeNodeRenderer(tree, model)


        tree.rowHeaderTableTree.columnModel.getColumns().each {ULCTableTreeColumn it ->
            it.setCellRenderer(renderer)
            it.setHeaderRenderer(new CenteredHeaderRenderer())
        }

        tree.rowHeaderTableTree.name = "parameterTreeRowHeader"
        tree.rowHeaderTableTree.columnModel.getColumn(0).headerValue = "Name"
        tree.cellSelectionEnabled = true
        // TODO (Mar 20, 2009, msh): Identified this as cause for PMO-240 (expand behaviour).

         tree.viewPortTableTree.addActionListener(new MultiDimensionalCompareTabStarter(this))


        model.treeModel.root.childCount.times {
            tree.expandPath new TreePath([model.treeModel.root, model.treeModel.root.getChildAt(it)] as Object[])
        }

        new SelectionTracker(tree)
    }

}

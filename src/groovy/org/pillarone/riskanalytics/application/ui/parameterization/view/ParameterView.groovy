package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.canoo.ulc.detachabletabbedpane.server.ITabListener
import com.canoo.ulc.detachabletabbedpane.server.TabEvent
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import com.ulcjava.base.application.event.ISelectionChangedListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.event.SelectionChangedEvent
import com.ulcjava.base.application.tabletree.ITableTreeCellEditor
import com.ulcjava.base.application.tabletree.ITableTreeCellRenderer
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import com.ulcjava.base.application.tree.TreePath
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingTreeView
import org.pillarone.riskanalytics.application.ui.base.view.ComponentNodeTableTreeNodeRenderer
import org.pillarone.riskanalytics.application.ui.base.view.DelegatingCellEditor
import org.pillarone.riskanalytics.application.ui.base.view.DelegatingCellRenderer
import org.pillarone.riskanalytics.application.ui.comment.action.InsertCommentAction
import org.pillarone.riskanalytics.application.ui.comment.action.ShowCommentsAction
import org.pillarone.riskanalytics.application.ui.comment.model.CommentFilter
import org.pillarone.riskanalytics.application.ui.comment.view.CommentAndErrorView
import org.pillarone.riskanalytics.application.ui.comment.view.NavigationListener
import org.pillarone.riskanalytics.application.ui.main.action.AddDynamicSubComponent
import org.pillarone.riskanalytics.application.ui.main.action.RemoveDynamicSubComponent
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.parameterization.action.MultiDimensionalTabStarter
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.base.action.*
import org.pillarone.riskanalytics.application.ui.parameterization.model.*
import org.pillarone.riskanalytics.application.ui.base.view.AbstractParameterizationTreeView

class ParameterView extends AbstractParameterizationTreeView implements NavigationListener {

    ParameterView(ParameterViewModel model, RiskAnalyticsMainModel mainModel) {
        super(model, mainModel)
        model.addNavigationListener this
    }

    @Override
    protected void initComponents() {
        super.initComponents()    //To change body of overridden methods use File | Settings | File Templates.
        def parameterization = model.getItem() as Parameterization
        updateErrorVisualization(parameterization)
    }

    @Override
    protected void internalAttachListeners() {
        def rowHeaderTree = tree.getRowHeaderTableTree()
        rowHeaderTree.registerKeyboardAction(new RemoveDynamicSubComponent(tree.rowHeaderTableTree, model), KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTree.registerKeyboardAction(new AddDynamicSubComponent(tree.rowHeaderTableTree, model), KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTree.registerKeyboardAction(new TreeNodeRename(tree.rowHeaderTableTree, model), KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true), ULCComponent.WHEN_FOCUSED)

        def parameterization = model.getItem() as Parameterization
        parameterization.addModellingItemChangeListener([itemSaved: {item ->},
                itemChanged: {Parameterization item ->
                    updateErrorVisualization(item)
                }] as IModellingItemChangeListener)
    }

    @Override
    protected String getRowHeaderTableTreeName() {
        return "parameterTreeRowHeader"
    }

    @Override
    protected String getViewPortTableTreeName() {
        return "parameterTreeContent"
    }



    protected void updateErrorVisualization(Parameterization item) {
        commentAndErrorView.updateErrorVisualization item
    }

}






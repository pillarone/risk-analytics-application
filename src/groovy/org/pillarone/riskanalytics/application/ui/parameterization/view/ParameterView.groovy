package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.comment.view.NavigationListener
import org.pillarone.riskanalytics.application.ui.main.action.AddDynamicSubComponent
import org.pillarone.riskanalytics.application.ui.main.action.RemoveDynamicSubComponentAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
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
        rowHeaderTree.registerKeyboardAction(new RemoveDynamicSubComponentAction(tree.rowHeaderTableTree, model), KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true), ULCComponent.WHEN_FOCUSED)
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






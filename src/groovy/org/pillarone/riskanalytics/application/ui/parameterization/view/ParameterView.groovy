package org.pillarone.riskanalytics.application.ui.parameterization.view
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.util.KeyStroke
import org.pillarone.riskanalytics.application.ui.base.action.TreeNodeRename
import org.pillarone.riskanalytics.application.ui.base.view.AbstractParameterizationTreeView
import org.pillarone.riskanalytics.application.ui.comment.view.NavigationListener
import org.pillarone.riskanalytics.application.ui.main.action.AddDynamicSubComponent
import org.pillarone.riskanalytics.application.ui.main.action.RemoveDynamicSubComponentAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

class ParameterView extends AbstractParameterizationTreeView implements NavigationListener {

    private final IModellingItemChangeListener removeInvisibleCommentsListener = new RemoveInvisibleCommentsListener()

    ParameterView(ParameterViewModel model, RiskAnalyticsMainModel mainModel) {
        super(model, mainModel)
        model.addNavigationListener(this)
        model.item.addModellingItemChangeListener(removeInvisibleCommentsListener)
    }

    @Override
    ParameterViewModel getModel() {
        return super.getModel() as ParameterViewModel
    }

    @Override
    void close() {
        model.item.removeModellingItemChangeListener(removeInvisibleCommentsListener)
    }

    @Override
    protected void initComponents() {
        super.initComponents()
        Parameterization parameterization = model.item
        updateErrorVisualization(parameterization)
    }

    @Override
    protected void internalAttachListeners() {
        def rowHeaderTree = tree.rowHeaderTableTree
        rowHeaderTree.registerKeyboardAction(new RemoveDynamicSubComponentAction(tree.rowHeaderTableTree, model), KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTree.registerKeyboardAction(new AddDynamicSubComponent(tree.rowHeaderTableTree, model), KeyStroke.getKeyStroke(KeyEvent.VK_INSERT, 0, true), ULCComponent.WHEN_FOCUSED)
        rowHeaderTree.registerKeyboardAction(new TreeNodeRename(tree.rowHeaderTableTree, model), KeyStroke.getKeyStroke(KeyEvent.VK_F2, 0, true), ULCComponent.WHEN_FOCUSED)

        Parameterization parameterization = model.item
        parameterization.addModellingItemChangeListener([
                itemSaved  : { item -> },
                itemChanged: { Parameterization item ->
                    updateErrorVisualization(item)
                }
        ] as IModellingItemChangeListener)
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

    private class RemoveInvisibleCommentsListener implements IModellingItemChangeListener {
        @Override
        void itemChanged(ModellingItem item) {}

        @Override
        void itemSaved(ModellingItem item) {
            getModel().removeInvisibleComments()
        }
    }
}






package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.core.workflow.Status
import org.pillarone.riskanalytics.application.ui.main.view.NewVersionCommentDialog
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationTableTreeNode
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.hibernate.validator.InvalidStateException
import org.pillarone.riskanalytics.core.RiskAnalyticsInconsistencyException

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ParameterizationUIItem extends ModellingUIItem {

    private static Log LOG = LogFactory.getLog(ParameterizationUIItem)

    public ParameterizationUIItem(RiskAnalyticsMainModel model, Model simulationModel, Parameterization parameterization) {
        super(model, simulationModel, parameterization)
    }

    ULCContainer createDetailView() {
        ParameterView view = new ParameterView(getViewModel())
        return view.content
    }

    AbstractModellingModel getViewModel() {
        ParameterViewModel model = new ParameterViewModel(this.model, (Parameterization) item, ModelStructure.getStructureForModel(this.model.class))
        model.mainModel = mainModel
        mainModel.registerModel(this, model)
        return model
    }

    @Override
    void save() {
        ModellingUIItem modellingUIItem = mainModel.getAbstractUIItem(item)
        if (modellingUIItem) {
            AbstractModellingModel viewModel = mainModel.getViewModel(modellingUIItem)
            if (viewModel != null) {
                viewModel.removeInvisibleComments()

                //consistency check

                List<ParameterHolder> parameters = item.parameters.clone()
                parameters = parameters.findAll { !it.removed }
                List<ParameterHolder> uiParameters = []
                collectParameters(viewModel.treeModel.root, uiParameters)
                boolean error = false

                for (ParameterHolder holder in uiParameters) {
                    ParameterHolder parameterHolder = parameters.find { it.path == holder.path && it.periodIndex == holder.periodIndex}
                    if (parameterHolder == null) {
                        error = true
                        LOG.error("Parameter ${holder.path} P${holder.periodIndex} exists in the UI but not in the parameterization to be saved!")
                    } else if (!(holder.is(parameterHolder))) {
                        error = true
                        LOG.error("Parameter ${holder.path} P${holder.periodIndex} has different instances in the UI and parameterization to be saved!")
                    } else {
                        parameters.remove(parameterHolder)
                    }
                }

                if(!parameters.empty) {
                    for(ParameterHolder holder in parameters) {
                        error = true
                        LOG.error("Parameter ${holder.path} P${holder.periodIndex} exists in the parameterization to be saved, but not in the UI!")
                    }
                }

                if (error) {
                    throw new RiskAnalyticsInconsistencyException("Parameters in the UI and the parameterization are different.")
                }
            }
        }
        super.save()
    }

    private void collectParameters(ITableTreeNode node, List<ParameterHolder> list) {
        for (int i = 0; i < node.childCount; i++) {
            ITableTreeNode child = node.getChildAt(i)
            if (child instanceof ParameterizationTableTreeNode) {
                list.addAll(child.parameter)
            } else {
                collectParameters(child, list)
            }
        }
    }

    @Override
    public ModellingUIItem createNewVersion(Model selectedModel, boolean openNewVersion) {
        ModellingUIItem newItem = null
        Closure okAction = {String commentText ->
            if (!this.isLoaded()) {
                this.load()
            }
            createNewVersion(this.model, commentText, false)
        }

        NewVersionCommentDialog versionCommentDialog = new NewVersionCommentDialog(okAction)
        versionCommentDialog.show()

        return newItem
    }



    public ParameterizationUIItem createNewVersion(Model selectedModel, String commentText, boolean openNewVersion = true) {
        ParameterizationUIItem newItem = super.createNewVersion(selectedModel, false)
        addComment(selectedModel, newItem, commentText, openNewVersion)
        return newItem
    }

    private void addComment(Model selectedModel, ParameterizationUIItem uiItem, String commentText, boolean openNewVersion) {
        if (commentText) {
            Tag versionTag = Tag.findByName(NewCommentView.VERSION_COMMENT)
            ((Parameterization) uiItem.item).addTaggedComment(commentText, versionTag)
            uiItem.item.save()
        }
        if (openNewVersion)
            mainModel.openItem(selectedModel, uiItem)
    }

    @Override
    List<SimulationRun> getSimulations() {
        return item.getSimulations()
    }

    public boolean newVersionAllowed() {
        return ((Parameterization) item).newVersionAllowed()
    }

    @Override
    ULCIcon getIcon() {
        return UIUtils.getIcon("parametrization-active.png")
    }

    @Override
    boolean isVersionable() {
        return true
    }

    @Override
    boolean isChangeable() {
        return true
    }

    @Override
    boolean isDeletable() {
        Parameterization parameterization = item as Parameterization
        return parameterization.status == Status.NONE || parameterization.status == Status.DATA_ENTRY
    }

    @Override
    boolean isEditable() {
        return item.isEditable()
    }

    @Override
    String toString() {
        return item.toString()
    }

}

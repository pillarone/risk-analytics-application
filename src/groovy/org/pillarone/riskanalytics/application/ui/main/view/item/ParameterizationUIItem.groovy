package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.util.ULCIcon
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.application.ui.main.view.NewVersionCommentDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.workflow.Status

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ParameterizationUIItem extends ModellingUIItem {

    private static Log LOG = LogFactory.getLog(ParameterizationUIItem)

    public ParameterizationUIItem(RiskAnalyticsMainModel model, Model simulationModel, Parameterization parameterization) {
        super(model, simulationModel, parameterization)
    }

    @Override
    void close() {
        ParameterViewModel viewModel = mainModel.viewModelsInUse[this]
        Parameterization parameterization = item
        parameterization.removeListener(viewModel)
        super.close()
    }

    ULCContainer createDetailView() {
        ParameterView view = new ParameterView(getViewModel(), mainModel)
        return view.content
    }

    AbstractModellingModel getViewModel() {
        Parameterization parameterization = (Parameterization) item
        ParameterViewModel model = new ParameterViewModel(this.model, parameterization, ModelStructure.getStructureForModel(this.model.class))
        model.mainModel = mainModel
        mainModel.registerModel(this, model)

        parameterization.addListener(model)
        return model
    }

    @Override
    void save() {
        ModellingUIItem modellingUIItem = mainModel.getAbstractUIItem(item)
        if (modellingUIItem) {
            AbstractModellingModel viewModel = mainModel.getViewModel(modellingUIItem)
            if (viewModel != null) {
                viewModel.removeInvisibleComments()
            }
        }
        super.save()
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
        VersionNumber newVersion = newItem.item.versionNumber
        addComment(selectedModel, newItem, "v${newVersion}: ${commentText}", openNewVersion)
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

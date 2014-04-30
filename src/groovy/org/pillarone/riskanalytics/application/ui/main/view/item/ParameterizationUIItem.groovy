package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.util.ULCIcon
import grails.util.Holders
import groovy.transform.CompileStatic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.main.view.NewVersionCommentDialog
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

import static org.pillarone.riskanalytics.core.workflow.Status.DATA_ENTRY
import static org.pillarone.riskanalytics.core.workflow.Status.NONE
import static org.pillarone.riskanalytics.core.workflow.Status.REJECTED

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ParameterizationUIItem extends ModellingUiItemWithModel {

    protected static final Log LOG = LogFactory.getLog(ParameterizationUIItem.class)

    ParameterizationUIItem(Model model, Parameterization parameterization) {
        super(model, parameterization)
    }

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        Holders.grailsApplication.mainContext.getBean('navigationTableTreeModel', NavigationTableTreeModel)
    }

    @Override
    RiskAnalyticsMainModel getRiskAnalyticsMainModel() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsMainModel', RiskAnalyticsMainModel)
    }

    IDetailView createDetailView() {
        return new ParameterView(viewModel, riskAnalyticsMainModel)
    }

    private ParameterViewModel getViewModel() {
        Parameterization parameterization = (Parameterization) item
        Model simulationModel = this.model.class.newInstance() as Model//PMO-2471
        simulationModel.init()
        simulationModel.injectComponentNames()
        ParameterViewModel model = new ParameterViewModel(simulationModel, parameterization, ModelStructure.getStructureForModel(this.model.class))
        model.mainModel = riskAnalyticsMainModel
        return model
    }

    @Override
    ModellingUIItem createNewVersion(Model model, boolean openNewVersion) {
        Closure okAction = { String commentText ->
            if (!this.loaded) {
                this.load()
            }
            createNewVersion(this.model, commentText, false)
        }

        NewVersionCommentDialog versionCommentDialog = new NewVersionCommentDialog(okAction)
        versionCommentDialog.show()
        return null
    }


    ParameterizationUIItem createNewVersion(Model selectedModel, String commentText, boolean openNewVersion = true) {
        ParameterizationUIItem newItem = super.createNewVersion(selectedModel, false) as ParameterizationUIItem
        VersionNumber newVersion = newItem.item.versionNumber
        addComment(selectedModel, newItem, "v${newVersion}: ${commentText}", openNewVersion)
        return newItem
    }

    private void addComment(Model selectedModel, ParameterizationUIItem uiItem, String commentText, boolean openNewVersion) {
        if (commentText) {
            Tag versionTag = Tag.findByName(NewCommentView.VERSION_COMMENT)
            uiItem.item.addTaggedComment(commentText, versionTag)
            uiItem.item.save()
        }
        if (openNewVersion) {
            riskAnalyticsMainModel.openItem(selectedModel, uiItem)
        }
    }

    @Override
    List<Simulation> getSimulations() {
        return item.simulations
    }

    public boolean newVersionAllowed() {
        return item.newVersionAllowed()
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
    boolean isDeletable() {
        Parameterization p14n = item as Parameterization

        // We only allow deleting REJECTED items that are the latest version in their tree
        // So we don't leave a hole in the version sequence
        //
        if (p14n.status == REJECTED) {
            SortedSet allVersions = new TreeSet(VersionNumber.getExistingVersions(p14n))
            if (p14n.versionNumber != allVersions.last()) {
                LOG.info("NOT DELETING ${p14n.getNameAndVersion()} as later version exists: ${allVersions.last()}")
                return false
            }
        }
        return p14n.status == NONE ||
                p14n.status == DATA_ENTRY ||
                p14n.status == REJECTED
    }

    @Override
    boolean isEditable() {
        return item.editable
    }

    @Override
    String toString() {
        return item.toString()
    }

    @Override
    @CompileStatic
    Parameterization getItem() {
        super.getItem() as Parameterization
    }
}

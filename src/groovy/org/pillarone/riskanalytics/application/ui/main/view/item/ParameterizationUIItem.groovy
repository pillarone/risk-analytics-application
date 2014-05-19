package org.pillarone.riskanalytics.application.ui.main.view.item
import com.ulcjava.base.application.util.ULCIcon
import grails.util.Holders
import groovy.transform.CompileStatic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.comment.view.NewCommentView
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.view.NewVersionCommentDialog
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.ParameterView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameter.comment.Tag
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

import static org.pillarone.riskanalytics.core.workflow.Status.*
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ParameterizationUIItem extends ModellingUiItemWithModel<ParameterView> {

    protected static final Log LOG = LogFactory.getLog(ParameterizationUIItem.class)

    ParameterizationUIItem(Parameterization parameterization) {
        super(parameterization)
    }

    @Override
    NavigationTableTreeModel getNavigationTableTreeModel() {
        Holders.grailsApplication.mainContext.getBean('navigationTableTreeModel', NavigationTableTreeModel)
    }

    ParameterView createDetailView() {
        return new ParameterView(viewModel)
    }

    private ParameterViewModel getViewModel() {
        Parameterization parameterization = (Parameterization) item
        Model simulationModel = this.model.class.newInstance() as Model//PMO-2471
        simulationModel.init()
        simulationModel.injectComponentNames()
        ParameterViewModel model = new ParameterViewModel(simulationModel, parameterization, ModelStructure.getStructureForModel(this.model.class))
        return model
    }

    @Override
    ModellingUIItem createNewVersion(boolean openNewVersion = true) {
        Closure okAction = { String commentText ->
            if (!this.loaded) {
                this.load()
            }
            createNewVersion(commentText, false)
        }

        NewVersionCommentDialog versionCommentDialog = new NewVersionCommentDialog(okAction)
        versionCommentDialog.show()
        return null
    }


    ParameterizationUIItem createNewVersion(String commentText, boolean openNewVersion = true) {
        ParameterizationUIItem newItem = super.createNewVersion(false) as ParameterizationUIItem
        VersionNumber newVersion = newItem.item.versionNumber
        addComment(newItem, "v${newVersion}: ${commentText}", openNewVersion)
        return newItem
    }

    private void addComment(ParameterizationUIItem uiItem, String commentText, boolean openNewVersion) {
        if (commentText) {
            Tag versionTag = Tag.findByName(NewCommentView.VERSION_COMMENT)
            uiItem.item.addTaggedComment(commentText, versionTag)
            uiItem.item.save()
        }
        if (openNewVersion) {
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(uiItem))
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

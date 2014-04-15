package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.util.ULCIcon
import grails.util.Holders
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.NavigationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
abstract class AbstractUIItem implements IUIItem {

    Model model
    RiskAnalyticsMainModel mainModel

    AbstractUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel) {
        this.mainModel = mainModel
        this.model = simulationModel
    }

    void rename(String newName) {}

    void save() {}

    boolean remove() {
        return false
    }

    ModellingUIItem createNewVersion(Model selectedModel, boolean openNewVersion = true) {
        return null
    }

    void close() {
        mainModel.closeItem(model, this)
    }

    boolean isEditable() {
        return false
    }

    boolean isLoaded() {
        return false
    }

    void load(boolean completeLoad = true) {}

    void unload() {}

    List<SimulationRun> getSimulations() {
        return []
    }

    abstract public ULCContainer createDetailView()

    abstract public Object getViewModel()

    abstract public String createTitle()

    String getToolTip() {
        return ""
    }

    ULCIcon getIcon() {
        return UIUtils.getIcon("clear.png")
    }

    String getName() {
        ""
    }

    String getNameAndVersion() {
        name
    }

    boolean isVersionable() {
        return false
    }

    boolean isChangeable() {
        return false
    }

    boolean isChanged() {
        return false
    }

    boolean isDeletable() {
        return true
    }

    NavigationTableTreeModel getNavigationTableTreeModel() {
        Holders.grailsApplication.mainContext.getBean('navigationTableTreeModel') as NavigationTableTreeModel
    }

    String getWindowTitle() {
        String windowTitle = model ? model.name : ""
        windowTitle += " " + createTitle()
        return windowTitle
    }
}

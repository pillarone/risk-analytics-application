package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.IModellingItemChangeListener
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.main.model.IContentModel
import org.pillarone.riskanalytics.application.ui.base.model.AsynchronTableTreeModel
import com.ulcjava.base.application.tabletree.AbstractTableTreeModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractUIItem implements IUIItem {

    public RiskAnalyticsMainModel mainModel
    public Model model

    public AbstractUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel) {
        this.mainModel = mainModel
        this.model = simulationModel
    }


    public void rename(String newName) {
    }

    public void save() {
    }


    public boolean remove() {
        return false
    }

    public ModellingItem createNewVersion(Model selectedModel, boolean openNewVersion = true) {
        return null
    }


    public void close() {
        mainModel.closeItem(model, this)
    }

    public boolean isEditable() {
        return false
    }


    public boolean isLoaded() {
        return false
    }

    public void load(boolean completeLoad = true) {
    }

    List<SimulationRun> getSimulations() {
        return []
    }

    abstract public ULCContainer createDetailView()

    abstract public IContentModel getViewModel()

    abstract public String createTitle()

    public String getToolTip() {
        return ""
    }

    public ULCIcon getIcon() {
        return UIUtils.getIcon("clear.png")
    }

    public String getName() {
        ""
    }

    public boolean isVersionable() {
        return false
    }

    public boolean isChangeable() {
        return false
    }

    public boolean isChanged() {
        return false
    }

    public void removeAllModellingItemChangeListener() {

    }

    public addModellingItemChangeListener(IModellingItemChangeListener listener) {

    }

    Object getItem() {
        return null
    }

    public AbstractTableTreeModel getNavigationTableTreeModel() {
        return mainModel.navigationTableTreeModel
    }



}

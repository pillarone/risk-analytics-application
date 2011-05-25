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

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ParameterizationUIItem extends ModellingUIItem {

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
            mainModel.getViewModel(modellingUIItem)?.removeInvisibleComments()
        }
        super.save()
        //todo fja
        //        refresh(item)
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
    boolean isEditable() {
        return item.isEditable()
    }


}

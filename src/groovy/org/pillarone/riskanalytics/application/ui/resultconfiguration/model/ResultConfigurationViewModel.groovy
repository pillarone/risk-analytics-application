package org.pillarone.riskanalytics.application.ui.resultconfiguration.model

import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.PropertiesViewModel
import org.pillarone.riskanalytics.application.ui.main.action.SaveAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.TableTreeValueChangedListener

class ResultConfigurationViewModel extends AbstractModellingModel {

    private ResultConfigurationTableTreeModel tableTreeModel
    PropertiesViewModel propertiesViewModel
    P1RATModel p1RATModel

    public ResultConfigurationViewModel(Model model, ResultConfiguration resultConfiguration, ModelStructure structure) {
        super(model, resultConfiguration, structure);
        propertiesViewModel = new PropertiesViewModel(resultConfiguration)
    }

    protected ITableTreeModel buildTree() {
        builder = new ResultConfigurationTreeBuilder(model, structure, item as ResultConfiguration)

        def localTreeRoot = builder.root
        periodCount = 1
        tableTreeModel = new ResultConfigurationTableTreeModel(localTreeRoot, 1 + periodCount)
        tableTreeModel.addValueChangedListener([valueChanged: {Object node, int column -> item.changed = true}] as TableTreeValueChangedListener)
        tableTreeModel.readOnly = !item.isEditable()

        return tableTreeModel
    }


    @Override
    IActionListener getSaveAction(ULCComponent parent) {
        return new SaveAction(parent, p1RATModel, item)
    }

    void setReadOnly(boolean value) {
        tableTreeModel.readOnly = value
    }


}
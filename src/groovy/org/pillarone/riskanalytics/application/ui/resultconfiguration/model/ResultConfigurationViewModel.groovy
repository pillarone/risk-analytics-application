package org.pillarone.riskanalytics.application.ui.resultconfiguration.model

import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.PropertiesViewModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.TableTreeValueChangedListener
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

class ResultConfigurationViewModel extends AbstractModellingModel {

    private ResultConfigurationTableTreeModel tableTreeModel
    PropertiesViewModel propertiesViewModel

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

    void setReadOnly(boolean value) {
        tableTreeModel.readOnly = value
    }


}
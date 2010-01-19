package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.core.parameterization.ParameterWriter
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.PropertiesViewModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ParameterInjector
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.application.ui.parameterization.model.*

class ParameterViewModel extends AbstractModellingModel {

    private ParameterizationTableTreeModel paramterTableTreeModel
    PropertiesViewModel propertiesViewModel

    public ParameterViewModel(Model model, Parameterization parameterization, ModelStructure structure) {
        super(model, parameterization, structure);
        propertiesViewModel = new PropertiesViewModel(parameterization)
    }

    protected ITableTreeModel buildTree() {
        builder = new ParameterizationTreeBuilder(model, structure, item)
        treeRoot = builder.root
        periodCount = builder.periodCount
        paramterTableTreeModel = new ParameterizationTableTreeModel(builder)
        paramterTableTreeModel.simulationModel = model
        paramterTableTreeModel.addValueChangedListener(
                [valueChanged: {Object node, int column -> item.changed = true}] as TableTreeValueChangedListener)
        paramterTableTreeModel.readOnly = item.isUsedInSimulation()
        return paramterTableTreeModel
    }

    void save() {
        ParameterWriter writer = new ParameterWriter()
        File file = new File("${modellingFileName}.groovy")

        File tmpFile = File.createTempFile("tmpParam", ".groovy")
        writer.write(builder.parameterConfigObject, tmpFile.newWriter())
        try {
            new ParameterInjector(tmpFile.absolutePath - ".groovy")
        } catch (Exception e) {
            throw new IllegalArgumentException("Error in parameter. Can not write parameter file:${file.name}. original message: ${e.message}")
        }


        writer.write(builder.parameterConfigObject, file.newWriter())
    }

    void setReadOnly(boolean value) {
        paramterTableTreeModel.readOnly = value
    }
}

class CompareParameterViewModel extends AbstractModellingModel {
    private CompareParameterizationTableTreeModel paramterTableTreeModel

    public CompareParameterViewModel(Model model, List<Parameterization> parameterizations, ModelStructure structure) {
        super(model, parameterizations, structure);
    }

    protected ITableTreeModel buildTree() {
        builder = new CompareParameterizationTreeBuilder(model, structure, getFirstObject(), getItems())
        treeRoot = builder.root
        periodCount = builder.minPeriod
        paramterTableTreeModel = new CompareParameterizationTableTreeModel(builder, getItems())
        paramterTableTreeModel.simulationModel = model
        paramterTableTreeModel.readOnly = false
        return paramterTableTreeModel

    }

    public int getColumnCount() {
        return paramterTableTreeModel.getColumnCount()
    }

    private List getItems() {
        return (item.get(0) instanceof Parameterization) ? item : item*.item
    }

    private Object getFirstObject() {
        return (item.get(0) instanceof Parameterization) ? item.get(0) : item.get(0).item
    }


}

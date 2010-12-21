package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.core.example.parameter.ExampleParameterObjectClassifier
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolderFactory
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.StringParameterHolder

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareParameterViewModel extends AbstractModellingModel {
    private CompareParameterizationTableTreeModel paramterTableTreeModel

    public CompareParameterViewModel(Model model, List<Parameterization> parameterizations, ModelStructure structure) {
        super(model, parameterizations, structure);
    }

    protected ITableTreeModel buildTree() {
        Parameterization firstParameterization = getFirstObject()
        aggregateParameters(firstParameterization)
        builder = new CompareParameterizationTreeBuilder(model, structure, firstParameterization, getItems())
        periodCount = builder.minPeriod
        paramterTableTreeModel = new CompareParameterizationTableTreeModel(builder, getItems())
        paramterTableTreeModel.simulationModel = model
        paramterTableTreeModel.readOnly = false

        return paramterTableTreeModel

    }

    private void aggregateParameters(Parameterization firstParameterization) {
        getItems().eachWithIndex {Parameterization parameterization, int index ->
            if (index > 0) {
                parameterization.getParameterHolders().each {ParameterHolder parameterHolder ->
                    def list = firstParameterization.getParameters().findAll {ParameterHolder parameter ->
                        parameter.path == parameterHolder.path
                    }
                    if (!list || list.size() == 0) {
                        if (parameterHolder instanceof ParameterObjectParameterHolder) {
                            ParameterObjectParameterHolder cloned = ParameterHolderFactory.createParamaterObjectHolder(parameterHolder.createEmptyParameter())
                            ExampleParameterObjectClassifier classifier = new ExampleParameterObjectClassifier(parameterHolder.classifier.typeName, parameterHolder.classifier.parameters)
                            classifier.displayName = ""
                            cloned.classifier = classifier
                            firstParameterization.addParameter cloned
                        } else
                            firstParameterization.addParameter new StringParameterHolder(parameterHolder.path, parameterHolder.periodIndex, "")
                    }
                }
            }
        }
    }

    public int getColumnCount() {
        return paramterTableTreeModel.getColumnCount()
    }

    private List getItems() {
        return (item.get(0) instanceof Parameterization) ? item : item*.item
    }

    private Object getFirstObject() {
        if (item.get(0) instanceof Parameterization) {
            Parameterization source = item.get(0)
            Parameterization cloned = new Parameterization(source.name)
            cloned.versionNumber = source.versionNumber.clone()
            source.getParameters().each {ParameterHolder parameter ->
                cloned.addParameter parameter.clone()
            }
            return cloned
        }
        return item.get(0).item
    }


}

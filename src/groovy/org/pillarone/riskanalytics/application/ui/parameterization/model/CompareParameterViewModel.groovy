package org.pillarone.riskanalytics.application.ui.parameterization.model

import com.ulcjava.base.application.tabletree.ITableTreeModel
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CompareParameterViewModel extends AbstractModellingModel {
    private CompareParameterizationTableTreeModel paramterTableTreeModel
    List items

    public CompareParameterViewModel(Model model, List<Parameterization> parameterizations, ModelStructure structure) {
        super(model, parameterizations, structure);
    }

    protected ITableTreeModel buildTree() {
        Parameterization aggregatedParameterization = getAggregatedParameterization()
        this.items = getItems(aggregatedParameterization)
        builder = new CompareParameterizationTreeBuilder(model, structure, aggregatedParameterization, items)
        periodCount = builder.minPeriod
        paramterTableTreeModel = new CompareParameterizationTableTreeModel(builder, items)
        paramterTableTreeModel.simulationModel = model
        paramterTableTreeModel.readOnly = false

        return paramterTableTreeModel

    }

    public int getColumnCount() {
        return paramterTableTreeModel.getColumnCount()
    }

    private List getItems(Parameterization aggregatedParameterization) {
        List items = [aggregatedParameterization]
        def list = (item.get(0) instanceof Parameterization) ? item : item*.item
        items.addAll(list)
        return items
    }

    /**
     * aggregate all parameterizationHolders in cloned parameteriazation
     * to show all nodes for the compare
     * @return
     */
    private Object getAggregatedParameterization() {
        Parameterization source = item.get(0)
        Parameterization cloned = new Parameterization(source.name)
        cloned.versionNumber = source.versionNumber.clone()
        cloned.periodCount = source.periodCount
        cloned.periodLabels = source.periodLabels
        cloned.modelClass = source.modelClass
        source.getParameters().each {ParameterHolder parameter ->
            cloned.addParameter parameter.clone()
        }
        item.eachWithIndex {Parameterization parameterization, int index ->
            if (index > 0) {
                parameterization.getParameters().each {ParameterHolder parameterHolder ->
                    def list = cloned.getParameters().findAll {ParameterHolder parameter ->
                        parameter.path == parameterHolder.path
                    }
                    if (!list || list.size() == 0) {
                        cloned.addParameter parameterHolder.clone()
                    }
                }
            }
        }
        return cloned
    }


}

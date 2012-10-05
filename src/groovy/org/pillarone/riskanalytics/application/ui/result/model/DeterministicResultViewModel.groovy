package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.output.*

class DeterministicResultViewModel extends AbstractResultViewModel {

    DeterministicResultViewModel(Model model, ModelStructure structure, Simulation simulation) {
        super(model, simulation, structure)
    }

    protected ITableTreeModel getResultTreeTableModel(Model model, Parameterization parameterization, simulationRun, ITableTreeNode treeRoot, ConfigObject results) {
        return new DeterministicResultTableTreeModel(treeRoot, simulationRun, parameterization, results)
    }

    @Override
    protected Map<String, ICollectingModeStrategy> obtainsCollectors(SimulationRun simulationRun, List allPaths) {
        Map<String, ICollectingModeStrategy> result = [:]
        List<Object[]> calculations = SingleValueResult.executeQuery("SELECT path.pathName, field.fieldName, collector.collectorName FROM org.pillarone.riskanalytics.core.output.SingleValueResult " +
                " WHERE simulationRun.id = ?", [simulationRun.id])
        for (Object[] psc in calculations) {
            String path = "${psc[0]}:${psc[1]}"
            String collector = psc[2]
            if (allPaths.contains(path)) {
                result.put(path, CollectingModeFactory.getStrategy(collector))
            }
        }

        return result
    }

    @Override
    protected ConfigObject initPostSimulationCalculations(SimulationRun simulationRun) {
        ConfigObject results = new ConfigObject()

        List<Object[]> calculations = SingleValueResult.executeQuery("SELECT period, path.pathName, field.fieldName, value FROM org.pillarone.riskanalytics.core.output.SingleValueResult " +
                " WHERE simulationRun.id = ?", [simulationRun.id])
        for (Object[] psc in calculations) {
            Map periodMap = results[psc[0].toString()]
            Map pathMap = periodMap[psc[1]]
            Map fieldMap = pathMap[psc[2]]
            fieldMap[PostSimulationCalculation.MEAN] = psc[3]
        }

        return results
    }

    @Override
    protected void recreateAllColumns() {
        //TODO
    }
}

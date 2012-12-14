package org.pillarone.riskanalytics.application.ui.result.model

import org.pillarone.riskanalytics.application.ui.base.model.AbstractCommentableItemModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.application.ui.result.view.ItemsComboBoxModel
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.application.output.structure.ResultStructureTreeBuilder
import org.pillarone.riskanalytics.application.ui.base.model.FilteringTableTreeModel
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy
import com.ulcjava.base.application.tabletree.ITableTreeModel
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.Simulation


abstract class AbstractResultViewModel extends AbstractCommentableItemModel {

    List resultStructures
    ItemsComboBoxModel selectionViewModel
    ConfigObject allResults = null

    AbstractResultViewModel(Model model, Simulation item, ModelStructure modelStructure) {
        super(model, item, modelStructure)

        model.init()
        resultStructures = ModellingItemFactory.getResultStructuresForModel(model.class)
        selectionViewModel = new ItemsComboBoxModel(resultStructures, "DEFAULT_VIEW" + model.name)
        item.load()
        buildTreeStructure(selectionViewModel.getSelectedObject())
    }

    abstract protected ConfigObject initPostSimulationCalculations(SimulationRun simulationRun)

    abstract protected Map<String, ICollectingModeStrategy> obtainsCollectors(SimulationRun simulationRun, List allPaths)

    abstract protected ITableTreeModel getResultTreeTableModel(Model model, Parameterization parameterization, simulationRun, ITableTreeNode treeRoot, ConfigObject results)

    @Override
    protected ITableTreeModel buildTree() {
        return null  //TODO?
    }

    protected List<String> obtainAllPaths(ConfigObject paths) {
        ResultViewUtils.obtainAllPaths(paths)
    }

    protected void buildTreeStructure(ResultStructure resultStructure) {
        ParameterizationDAO.withTransaction {status ->
            //parameterization is required for certain models to obtain period labels
            Parameterization parameterization = item.parameterization
            if (!parameterization.isLoaded())
                parameterization.load(false)

            if (!allResults) {
                //All pre-calculated results, used in the RTTM. We already create it here because this is the fastest way to obtain
                //all result paths for this simulation run
                allResults = initPostSimulationCalculations(item.simulationRun)

            }
            Set paths = new HashSet()
            //look through all periods, not all paths may have a result in the first period
            for (Map<String, Map> periodResults in allResults.values()) {
                paths.addAll(obtainAllPaths(periodResults))
            }

            def simulationRun = item.simulationRun

            resultStructure.load()
            builder = new ResultStructureTreeBuilder(obtainsCollectors(simulationRun, paths.toList()), model, resultStructure, item)

            def localTreeRoot = builder.buildTree()
            periodCount = simulationRun.periodCount

            // todo (msh): This is normally done in super ctor but here the simulationRun is required for the treeModel
            treeModel = new FilteringTableTreeModel(getResultTreeTableModel(model, parameterization, simulationRun, localTreeRoot, allResults), filter)
            nodeNames = extractNodeNames(treeModel)
        }

    }

    public void resultStructureChanged() {
        buildTreeStructure(selectionViewModel.getSelectedObject())
    }

    void adjust(int adjustment) {
        treeModel.numberDataType.maxFractionDigits = treeModel.numberDataType.maxFractionDigits + adjustment
        treeModel.numberDataType.minFractionDigits = treeModel.numberDataType.minFractionDigits + adjustment
        recreateAllColumns() //TODO: is necessary because the data type of the renderer is not updated on a nodeChanged
    }

    abstract protected void recreateAllColumns()
}

package org.pillarone.riskanalytics.application.ui.result.model

import com.ulcjava.base.application.tabletree.ITableTreeNode
import models.application.ApplicationModel
import models.core.CoreModel
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.output.structure.item.ResultStructure
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.view.ItemsComboBoxModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class MockResultViewModel extends ResultViewModel {

    public MockResultViewModel() {
        super(new CoreModel(), null, null);
        Simulation simulation1 = new Simulation("item1")
        simulation1.setDao(new SimulationRun(name: "item1", periodCount: 0))
        item = simulation1
        selectionViewModel = new ItemsComboBoxModel([])
        buildTreeStructure()
    }

    protected void buildTreeStructure(ResultStructure resultStructure = null) {
        SimpleTableTreeNode rootNode = new SimpleTableTreeNode("root")
        ResultStructureTableTreeNode child = new ResultStructureTableTreeNode("child", ApplicationModel)
        child.metaClass.getCellValue = {i ->
            i
        }
        rootNode.add(child)
        resultStructures = []
        String[] strings = ["name", "one", "two"].toArray()
        MockResultTableTreeModel model = new MockResultTableTreeModel(rootNode, new SimulationRun(name: "item1", periodCount: 0), null, null, null)
        treeModel = model
    }


}


class MockResultTableTreeModel extends ResultTableTreeModel {

    protected MockResultTableTreeModel(ITableTreeNode rootNode, SimulationRun simulationRun, Parameterization parameterization, IFunction mean, Model model) {
        this.rootNode = rootNode
        this.simulationRun = simulationRun
    }

    def getAsynchronValue(Object node, int column) {
        return column
    }

    public String getColumnName(int i) {
        return "column" + i
    }

    public int getColumnCount() {
        return 2
    }


}

package org.pillarone.riskanalytics.application.ui.chart.model

import groovy.mock.interceptor.MockFor
import models.core.CoreModel
import org.jfree.chart.JFreeChart
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ParameterizationDAO
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.*

class ChartViewModelTests extends GroovyTestCase {

    void setUp() {
        LocaleResources.setTestMode(true)
    }

    void tearDown() {
        LocaleResources.setTestMode(false)
    }

    void testGetSimulationSettings() {

        MockFor itemFactory = new MockFor(ModellingItemFactory)
        itemFactory.demand.getParameterization { ParameterizationDAO parameterization -> return new TestParam() }
        itemFactory.demand.getSimulation { run -> return new TestSimulation() }

        ChartViewModel model
        SimulationRun simulationRun = new SimulationRun()
        simulationRun.periodCount = 0
        simulationRun.model = "testModel"
        simulationRun.parameterization = new ParameterizationDAO()

        ResultTableTreeNode node = new ResultTableTreeNode("node1")
        node.collector = "testCollector"
        ResultTableTreeNode node1 = new ResultTableTreeNode("node2")
        node.collector = "testCollector"

        model = new TestChartViewModel("testChart", simulationRun, [node, node1])

        List result
        itemFactory.use {
            result = model.getSimulationSettings()
        }
        assertEquals(11, result.size())
        assertEquals(["", "", "Version"], result[0])
        assertEquals(["Chart Type:", "testChart"], result[1])
        assertEquals(["Simulation Name:", "testSimulation"], result[2])
        assertEquals(["Comment:", "comment"], result[3])
        assertEquals(["Model:", "java.lang.String", "1.3"], result[4])
        assertEquals(["Parameterization:", "testParam", "1"], result[5])
        assertEquals(["Template:", "testSimulationTemplate", "1.2"], result[6])
        assertEquals(["Structure:", "testModelStructure", "1.4"], result[7])
        assertEquals(["Number of Periods:", 0], result[8])
        assertEquals(["Number of Iterations:", 0], result[9])
        assertEquals(["Simulation end Date:", ""], result[10])
    }
}

class TestChartViewModel extends ChartViewModel {
    public TestChartViewModel(String title, SimulationRun simulationRun, List<SimpleTableTreeNode> nodes) {
        super(title, simulationRun, nodes, 0.0)
    }

    public JFreeChart getChart() {
        return null;
    }
}

class TestParam extends Parameterization {
    public TestParam() {
        super("testParam")
    }

    public void load() {
        return
    }

    Class modelClass = Integer
    VersionNumber modelVersionNumber = new VersionNumber("1.1")
}

class TestSimulation extends Simulation {
    public TestSimulation() {
        super("testSimulation")
        template.versionNumber = new VersionNumber("1.2")
        structure.versionNumber = new VersionNumber("1.4")
        periodCount = 0
    }

    public void load() {
        return
    }

    Class modelClass = String
    VersionNumber modelVersionNumber = new VersionNumber("1.3")
    Parameterization parameterization = new TestParam()
    ResultConfiguration template = new ResultConfiguration("testSimulationTemplate", CoreModel)
    ModelStructure structure = new ModelStructure("testModelStructure")
    String comment = "comment"
}
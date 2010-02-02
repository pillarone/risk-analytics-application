package org.pillarone.riskanalytics.application.ui.result.model

import groovy.mock.interceptor.MockFor
import groovy.mock.interceptor.StubFor
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.dataaccess.function.Max
import org.pillarone.riskanalytics.application.dataaccess.function.Min
import org.pillarone.riskanalytics.core.output.SimulationRun

import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import models.core.CoreModel

class ResultViewModelTests extends GroovyTestCase {

    void testAddFunction() {
        LocaleResources.setTestMode()
        SimulationRun simulationRun = new SimulationRun(name: "testRun", periodCount: 2)
        MockFor resultAccessor = new MockFor(ResultAccessor)
        resultAccessor.demand.getPaths(2..2) {SimulationRun run -> []}

        StubFor structure = new StubFor(ModelStructure)
        structure.demand.load {->}
        structure.demand.getData {-> new ConfigObject()}
        structure.demand.getSimpleName {-> "ModelStructure"}
        structure.demand.newInstance {args -> new ModelStructure(args[0])}

        StubFor simulationStub = new StubFor(Simulation)
        simulationStub.demand.getName {-> "testRun"}
        simulationStub.demand.getSimulationRun(3..3) {-> simulationRun}
        simulationStub.demand.getParameterization {-> return new Parameterization("name") }
        simulationStub.demand.getSimpleName {-> "Simulation"}
        simulationStub.demand.newInstance {args -> new Simulation(args[0])}

        Model model = new CoreModel()
        model.init()
        StubFor parameterization = new StubFor(Parameterization)
        parameterization.demand.load {->}
        parameterization.demand.setName {a ->}
        parameterization.demand.getModelClass(2..2) {index -> return model.class}
        parameterization.demand.getPeriodLabels {-> return []}

        resultAccessor.use {
            structure.use {
                simulationStub.use {
                    parameterization.use {
                        ModelStructure modelStructure = new ModelStructure("")
                        Simulation simulation = ModellingItemFactory.getSimulation("testRun", model.class)
                        ResultViewModel resultViewModel = new ResultViewModel(model, modelStructure, simulation)
                        Min min = new Min()
                        int initialFunctionsSize = resultViewModel.treeModel.functions.size
                        resultViewModel.addFunction(min)
                        assertEquals initialFunctionsSize + 2, resultViewModel.treeModel.functions.size
                        assertEquals("function already exists -> no re-add expected.", initialFunctionsSize + 2, resultViewModel.treeModel.functions.size)

                        Max max = new Max()
                        resultViewModel.addFunction(max)
                        assertEquals initialFunctionsSize + 4, resultViewModel.treeModel.functions.size
                        assertEquals("function already exists -> no re-add expected.", initialFunctionsSize + 4, resultViewModel.treeModel.functions.size)
                    }
                }
            }
        }
        LocaleResources.clearTestMode()
    }

}
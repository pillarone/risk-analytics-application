package org.pillarone.riskanalytics.functional.main

import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.testframework.operator.ULCTableTreeOperator
import com.ulcjava.testframework.operator.ULCTextFieldOperator
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationActionsPane
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.simulation.engine.ISimulationQueueListener
import org.pillarone.riskanalytics.core.simulation.engine.QueueEntry
import org.pillarone.riskanalytics.core.simulation.engine.SimulationQueueService
import org.pillarone.riskanalytics.functional.AbstractFunctionalTestCase

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RunSimulationTests extends AbstractFunctionalTestCase {

    SimulationQueueService simulationQueueService

    TestListener listener = new TestListener()

    void setUp() {
        simulationQueueService.addSimulationQueueListener(listener)
        new ParameterizationImportService().compareFilesAndWriteToDB(['Core'])
        super.setUp();
    }

    void tearDown() {
        super.tearDown()
        simulationQueueService.removeSimulationQueueListener(listener)
        listener.offered.clear()
    }

    void testRunSimulation() {
        ULCTableTreeOperator tableTree = selectionTableTreeRowHeader
        pushKeyOnPath(tableTree, tableTree.findPath(["Core", "Parameterization"] as String[]), KeyEvent.VK_F9, 0)
        ULCTextFieldOperator iterations = getTextFieldOperator("iterations")
        iterations.typeText("11")
        getButtonOperator("${SimulationActionsPane.simpleName}.run").clickMouse()

        //TODO find out why this fails on jenkins. Remove the println afterwards.
        println(listener.offered)
        listener.offered.each {
            println(it.simulationConfiguration.simulation.numberOfIterations)
        }
        assert listener.offered.any {
            it.simulationConfiguration.simulation.numberOfIterations == 11
        }
    }

    class TestListener implements ISimulationQueueListener {

        List<QueueEntry> offered = []

        @Override
        void starting(QueueEntry entry) {

        }

        @Override
        void finished(UUID id) {

        }

        @Override
        void removed(UUID id) {

        }

        @Override
        void offered(QueueEntry entry) {
            offered.add(entry)
        }
    }
}

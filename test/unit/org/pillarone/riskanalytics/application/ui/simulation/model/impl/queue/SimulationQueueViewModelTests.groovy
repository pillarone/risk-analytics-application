package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.pillarone.riskanalytics.core.simulation.engine.*

import static java.util.UUID.randomUUID

@TestMixin(GrailsUnitTestMixin)
class SimulationQueueViewModelTests {


    void testUpdate() {
        def tableModelControl = mockFor(SimulationQueueTableModel)

        SimulationRuntimeInfo addInfo = new SimulationRuntimeInfo(new QueueEntry(randomUUID()))
        SimulationRuntimeInfo deleteInfo = new SimulationRuntimeInfo(new QueueEntry(randomUUID()))
        SimulationRuntimeInfo changeInfo = new SimulationRuntimeInfo(new QueueEntry(randomUUID()))

        def addEvent = new AddSimulationRuntimeInfoEvent(index: 1, info: addInfo)
        def deleteEvent = new DeleteSimulationRuntimeInfoEvent(info: deleteInfo)
        def changeEvent = new ChangeSimulationRuntimeInfoEvent(info: changeInfo)

        tableModelControl.demand.itemAdded { SimulationRuntimeInfo info, int index ->
            assert addInfo.is(info)
            assert 1 == index
        }
        tableModelControl.demand.itemRemoved { UUID id ->
            assert deleteInfo.id == id
        }
        tableModelControl.demand.itemChanged { SimulationRuntimeInfo info ->
            assert changeInfo.is(info)
        }
        SimulationQueueViewModel subject = new SimulationQueueViewModel(
                simulationQueueTableModel: tableModelControl.createMock()
        )

        subject.infoListener.onEvent(addEvent)
        subject.infoListener.onEvent(deleteEvent)
        subject.infoListener.onEvent(changeEvent)
    }

    void testInitialize() {
        def runtimeServiceControl = mockFor(SimulationRuntimeService)
        def tableModelControl = mockFor(SimulationQueueTableModel)
        UlcSimulationRuntimeService ulcSimulationRuntimeService = new UlcSimulationRuntimeService()
        List<SimulationRuntimeInfo> simulationRuntimeInfos = [
                new SimulationRuntimeInfo(new QueueEntry(randomUUID())),
                new SimulationRuntimeInfo(new QueueEntry(randomUUID()))
        ]
        runtimeServiceControl.demand.getQueued { ->
            simulationRuntimeInfos
        }
        tableModelControl.demand.setQueueItems { List<SimulationRuntimeInfo> infos ->
            assert simulationRuntimeInfos == infos
        }

        SimulationQueueViewModel subject = new SimulationQueueViewModel(
                simulationRuntimeService: runtimeServiceControl.createMock(),
                simulationQueueTableModel: tableModelControl.createMock(),
                ulcSimulationRuntimeService: ulcSimulationRuntimeService
        )
        subject.initialize()
        assert ulcSimulationRuntimeService.eventSupport.infoListeners.contains(subject.infoListener)

        subject.unregister()
        assert ulcSimulationRuntimeService.eventSupport.infoListeners.isEmpty()
    }
}

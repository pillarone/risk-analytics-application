package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue
import grails.test.mixin.TestMixin
import grails.test.mixin.support.GrailsUnitTestMixin
import org.pillarone.riskanalytics.application.ui.util.IResourceBundleResolver
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeService

import static java.util.UUID.randomUUID

@TestMixin(GrailsUnitTestMixin)
class SimulationQueueViewModelTests {


    void testUpdate() {
        def tableModelControl = mockFor(SimulationQueueTableModel)

        SimulationRuntimeInfo addInfo = new SimulationRuntimeInfo(randomUUID())
        SimulationRuntimeInfo deleteInfo = new SimulationRuntimeInfo(randomUUID())
        SimulationRuntimeInfo changeInfo = new SimulationRuntimeInfo(randomUUID())

        tableModelControl.demand.itemChanged(2..2) { SimulationRuntimeInfo info ->
            assert changeInfo.is(info)
        }
        tableModelControl.demand.itemAdded { SimulationRuntimeInfo info, int index ->
            assert addInfo.is(info)
            assert 1 == index
        }
        tableModelControl.demand.itemRemoved(2..2) { UUID id ->
            assert deleteInfo.id == id
        }

        SimulationQueueViewModel subject = new SimulationQueueViewModel(
                simulationQueueTableModel: tableModelControl.createMock()
        )

        subject.infoListener.starting(changeInfo)
        subject.infoListener.changed(changeInfo)

        subject.infoListener.offered(addInfo)

        subject.infoListener.finished(deleteInfo)
        subject.infoListener.removed(deleteInfo)
    }

    void testInitialize() {
        defineBeans {
            resourceBundleResolver(TestResolver)
        }
        def runtimeServiceControl = mockFor(SimulationRuntimeService)
        def tableModelControl = mockFor(SimulationQueueTableModel)
        UlcSimulationRuntimeService ulcSimulationRuntimeService = new UlcSimulationRuntimeService()
        List<SimulationRuntimeInfo> simulationRuntimeInfos = [
                new SimulationRuntimeInfo(randomUUID()),
                new SimulationRuntimeInfo(randomUUID())
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

    static class TestResolver implements IResourceBundleResolver {
        @Override
        String getText(Class objClass, String key) {
            return ''
        }

        @Override
        String getText(Class objClass, String key, List argsValue) {
            return ''
        }
    }

}

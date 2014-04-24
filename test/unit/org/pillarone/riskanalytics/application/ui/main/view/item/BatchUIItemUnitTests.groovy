package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.testframework.standalone.AbstractSimpleStandaloneTestCase
import grails.test.mixin.TestMixin
import org.pillarone.riskanalytics.application.GrailsUnitTestMixinWithAnnotationSupport
import org.pillarone.riskanalytics.application.ui.P1UnitTestMixin
import org.pillarone.riskanalytics.application.ui.PollingSupport
import org.pillarone.riskanalytics.application.ui.batch.model.BatchViewModel
import org.pillarone.riskanalytics.application.ui.batch.model.SimulationParameterizationTableModel
import org.pillarone.riskanalytics.application.ui.batch.view.BatchView
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.UlcSimulationRuntimeService
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.engine.ISimulationRuntimeInfoListener
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeService
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile
import org.springframework.beans.factory.FactoryBean

@TestMixin(GrailsUnitTestMixinWithAnnotationSupport)
@Mixin(P1UnitTestMixin)
class BatchUIItemUnitTests extends AbstractSimpleStandaloneTestCase {

    void testView() {
//       Thread.sleep 5000
    }

    @Override
    void start() {
        LocaleResources.testMode = true
        initGrailsApplication()
        defineBeans {
            batchView(BatchView) {
                it.scope = 'prototype'
            }
            batchViewModel(BatchViewModel) {
                it.scope = 'prototype'
            }
            batchRunService(TestBatchRunService)
            simulationParameterizationTableModel(SimulationParameterizationTableModel) {
                it.scope = 'prototype'
            }
            ulcSimulationRuntimeService(TestRunTimeService)
            simulationRuntimeService(NullFactoryBean) {
                type = SimulationRuntimeService
            }
            pollingSupport2000(NullFactoryBean){
                type = PollingSupport
            }
        }
        inTestFrame(createContentPane())
    }

    ULCComponent createContentPane() {
        BatchUIItem batchUIItem = new BatchUIItem(new Batch('heidiho'))
        return batchUIItem.createDetailView()
    }

    static class TestBatchRunService extends BatchRunService {
        @Override
        Map<Class, SimulationProfile> getSimulationProfilesGroupedByModelClass(String simulationProfileName) {
            [:]
        }

        @Override
        List<String> getSimulationProfileNames() {
            return ['testProfile']
        }
    }

    static class NullFactoryBean implements FactoryBean {
        Class type

        @Override
        Object getObject() throws Exception {
            return null
        }

        @Override
        Class<?> getObjectType() {
            return type
        }

        @Override
        boolean isSingleton() {
            return false
        }
    }

    static class TestRunTimeService extends UlcSimulationRuntimeService {
        SimulationRuntimeService simulationRuntimeService

        @Override
        void register() {
        }

        @Override
        void addSimulationRuntimeInfoListener(ISimulationRuntimeInfoListener listener) {
        }

        @Override
        void removeSimulationRuntimeInfoListener(ISimulationRuntimeInfoListener listener) {
        }
    }
}

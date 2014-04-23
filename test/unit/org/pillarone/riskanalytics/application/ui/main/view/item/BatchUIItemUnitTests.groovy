package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.testframework.standalone.AbstractSimpleStandaloneTestCase
import grails.test.mixin.TestMixin
import org.pillarone.riskanalytics.application.GrailsUnitTestMixinWithAnnotationSupport
import org.pillarone.riskanalytics.application.ui.P1UnitTestMixin
import org.pillarone.riskanalytics.application.ui.batch.model.BatchViewModel
import org.pillarone.riskanalytics.application.ui.batch.view.BatchesView
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.batch.BatchRunService
import org.pillarone.riskanalytics.core.simulation.item.Batch
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile

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
            batchesView(BatchesView) {
                it.scope = 'prototype'
                it.autowire = 'byName'
            }
            batchViewModel(TestBatchViewModel) {
                it.scope = 'prototype'
                it.autowire = 'byName'
            }
            batchRunService(TestBatchRunService)
        }
        inTestFrame(createContentPane())
    }

    ULCComponent createContentPane() {
        BatchUIItem batchUIItem = new BatchUIItem(new Batch('heidiho'))
        return batchUIItem.createDetailView()
    }

    static class TestBatchViewModel extends BatchViewModel {
        TestBatchViewModel(Batch batch) {
            super(batch)
        }

        @Override
        List<String> getSimulationProfileNames() {
            return ['testProfile']
        }
    }

    static class TestBatchRunService extends BatchRunService {
        @Override
        Map<Class, SimulationProfile> getSimulationProfilesGroupedByModelClass(String simulationProfileName) {
            [:]
        }
    }


}

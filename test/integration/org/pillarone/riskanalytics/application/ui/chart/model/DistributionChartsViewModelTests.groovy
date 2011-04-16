package org.pillarone.riskanalytics.application.ui.chart.model

import groovy.mock.interceptor.StubFor
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.dataaccess.ResultAccessor
import org.pillarone.riskanalytics.core.output.QuantilePerspective
import org.pillarone.riskanalytics.core.output.SimulationRun

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class DistributionChartsViewModelTests extends GroovyTestCase {
    DistributionChartsViewModel model
    StubFor resultAccessor

    void setUp() {
        LocaleResources.setTestMode()
        resultAccessor = new StubFor(ResultAccessor)
        resultAccessor.demand.hasDifferentValues(1..100) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> true}
        resultAccessor.demand.getValues(1..100) {simulationRun, periodIndex, path, String s, String s2 -> [1d, 2d, 3d, 4d, 5d]}
        resultAccessor.demand.getMin(1..100) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> 1}
        resultAccessor.demand.getMax(1..100) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> 5}
        resultAccessor.demand.getMean(1..100) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> 3}
        resultAccessor.demand.getStdDev(1..100) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2 -> 2}
        resultAccessor.demand.getPercentile(2..200) {SimulationRun simulationRun, int periodIndex, String path, String s, String s2, percentile, QuantilePerspective perspective -> 2}

        ResultTableTreeNode node = new ResultTableTreeNode("node1")
        node.collector = "testCollector"

        resultAccessor.use {
            model = new DistributionChartsViewModel("TEST", new SimulationRun(name: "testRun", periodCount: 1), [node])
        }
    }

    protected void tearDown() {
        LocaleResources.clearTestMode()
        super.tearDown();
    }

    void testChangeStrategy() {
        resultAccessor.use {
            assertTrue model.strategyModel instanceof PDFRawChartViewModel
            model.typeComboBoxModel.selectedEnum = DistributionTyp.CDF
            model.methodComboBoxModel.selectedEnum = DistributionMethod.GAUSS_ADAPTIVE
            model.changeStrategy()
            assertTrue model.strategyModel instanceof CDFAdaptiveKernelBandwidthEstimatorChartViewModel
            def strategy = model.strategyModel
            model.changeStrategy()
            assertSame strategy, model.strategyModel
        }
    }


    void testFireModelChanged() {
        boolean receivedEvent = false
        model.addListener([modelChanged: {
            receivedEvent = true
        }] as IModelChangedListener)

        model.strategyModel.fireModelChanged()
        assertTrue receivedEvent

        receivedEvent = false
        model.methodComboBoxModel.selectedEnum = DistributionMethod.GAUSS_ADAPTIVE
        resultAccessor.use {
            model.changeStrategy()
        }
        model.strategyModel.fireModelChanged()
        assertTrue receivedEvent

    }

}
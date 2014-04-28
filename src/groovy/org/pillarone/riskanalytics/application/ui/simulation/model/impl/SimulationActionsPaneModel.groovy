package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import com.ulcjava.base.application.ULCSpinnerNumberModel
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import grails.util.Holders
import groovy.beans.Bindable
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.action.RunSimulationAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.ISimulationProvider
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy
import org.pillarone.riskanalytics.core.simulation.engine.SimulationConfiguration
import org.pillarone.riskanalytics.core.simulation.engine.SimulationQueueService
import org.pillarone.riskanalytics.core.simulation.item.Simulation
/**
 * The view model for the SimulationActionsPane.
 * It controls the simulation provided by the ISimulationProvider (run, stop, cancel)
 * and provides information about the current simulation state.
 */
class SimulationActionsPaneModel {

    protected final static Log LOG = LogFactory.getLog(SimulationActionsPaneModel)

    final ULCSpinnerNumberModel priorityModel
    Simulation simulation
    ICollectorOutputStrategy outputStrategy

    RunSimulationAction runSimulationAction

    ISimulationProvider simulationProvider
    RiskAnalyticsMainModel mainModel

    @Bindable
    String batchMessage

    SimulationActionsPaneModel(ISimulationProvider provider) {
        simulationProvider = provider
        runSimulationAction = new RunSimulationAction(this)
        priorityModel = new ULCSpinnerNumberModel(5, 0, 10, 1)
    }

    String getText(String key) {
        return UIUtils.getText(SimulationActionsPaneModel, key)
    }

    void runSimulation() {
        simulation.save()
        if (Holders.config.iterationCountThresholdForWarningWhenUsingSingleCollector) {
            possiblyWarnUserIfHugeResultSetExpected()
        } else {
            startSimulation()
        }
    }

    private void possiblyWarnUserIfHugeResultSetExpected() {
        try {
            int iterationThreshold = Holders.config.iterationCountThresholdForWarningWhenUsingSingleCollector

            if (simulation.numberOfIterations > iterationThreshold &&
                    simulation.template.collectors.find { collector -> collector.mode instanceof SingleValueCollectingModeStrategy }) {
                // TODO send this from view not this model so a parent will be available for the dialog, then it will display in
                // better location
                I18NAlert alert = new I18NAlert("WarnPotentiallyLargeResultSet")
                alert.addWindowListener([windowClosing: { WindowEvent e -> handleEvent(alert) }] as IWindowListener)
                alert.show()
            } else {
                startSimulation()
            }
        } catch (Exception e) {
            LOG.error("Failure in possiblyWarnUserIfHugeResultSetExpected(), running simulation without checking" + e.message)
            startSimulation()
        }
    }

    void handleEvent(I18NAlert alert) {
        if (alert.value.equals(alert.firstButtonLabel)) {
            startSimulation()
        } else if (!(alert.value.equals(alert.secondButtonLabel) || alert.value.equals("windowClosing"))) {
            throw new RuntimeException("Unknown button pressed: " + alert.value)
        }
    }

    private void startSimulation() {
        SimulationConfiguration configuration = new SimulationConfiguration(simulation, outputStrategy)
        int priority = priorityModel.value as int
        simulationQueueService.offer(configuration, priority)
    }

    private SimulationQueueService getSimulationQueueService() {
        Holders.grailsApplication.mainContext.getBean('simulationQueueService', SimulationQueueService)
    }

    void notifySimulationToBatchAdded(String message) {
        setBatchMessage(message)
    }
}

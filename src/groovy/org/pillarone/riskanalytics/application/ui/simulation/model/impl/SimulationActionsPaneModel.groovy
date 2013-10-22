package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.event.serializable.IWindowListener
import groovy.time.TimeCategory
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.result.view.ItemsComboBoxModel
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.ISimulationProvider
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationActionsPane
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.BatchRunSimulationRun
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.engine.RunSimulationService
import org.pillarone.riskanalytics.core.simulation.engine.SimulationConfiguration
import org.pillarone.riskanalytics.core.simulation.engine.grid.SimulationHandler
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.action.*
import org.codehaus.groovy.grails.commons.ConfigurationHolder
import org.pillarone.riskanalytics.core.output.SingleValueCollectingModeStrategy
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.event.IWindowListener
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener

/**
 * The view model for the SimulationActionsPane.
 * It controls the simulation provided by the ISimulationProvider (run, stop, cancel)
 * and provides information about the current simulation state.
 */
class SimulationActionsPaneModel implements IModelChangedListener {

    protected Log LOG = LogFactory.getLog(SimulationActionsPaneModel)

    protected SimulationHandler runner
    private DateTimeFormatter dateFormat = DateFormatUtils.getDateFormat("HH:mm")
    private List listeners = []

    volatile Simulation simulation
    ICollectorOutputStrategy outputStrategy

    RunSimulationAction runSimulationAction
    CancelSimulationAction cancelSimulationAction
    OpenResultsAction openResultsAction

    ItemsComboBoxModel<BatchRun> batchRunComboBoxModel
    AddToBatchAction addToBatchAction

    ISimulationProvider simulationProvider
    RiskAnalyticsMainModel mainModel

    String batchMessage

    public SimulationActionsPaneModel(ISimulationProvider provider, RiskAnalyticsMainModel mainModel) {
        this.mainModel = mainModel
        simulationProvider = provider
        runSimulationAction = new RunSimulationAction(this)
        cancelSimulationAction = new CancelSimulationAction(this)
        openResultsAction = new OpenResultsAction(this)

        batchRunComboBoxModel = new ItemsComboBoxModel<BatchRun>(BatchRun.list())
        addToBatchAction = new AddToBatchAction(this)
    }

    String getText(String key) {
        return UIUtils.getText(SimulationActionsPaneModel, key)
    }

    void runSimulation() {
        simulation.save()

        if (ConfigurationHolder.config.iterationCountThresholdForWarningWhenUsingSingleCollector) {
            possiblyWarnUserIfHugeResultSetExpected()
        } else {
            startSimulation()
        }
    }

    private void possiblyWarnUserIfHugeResultSetExpected() {
        try {
            int iterationThreshold = ConfigurationHolder.config.iterationCountThresholdForWarningWhenUsingSingleCollector

            if (simulation.numberOfIterations > iterationThreshold &&
                    simulation.template.collectors.find { collector -> collector.mode instanceof SingleValueCollectingModeStrategy }) {
                I18NAlert alert = new I18NAlert("WarnPotentiallyLargeResultSet")
                alert.addWindowListener([windowClosing: {WindowEvent e -> handleEvent(alert)}] as IWindowListener)
                alert.show()
            } else {
                startSimulation()
            }
        } catch (Exception e) {
            LOG.error("Failure in possiblyWarnUserIfHugeResultSetExpected(), running simulation without checking" + e.getMessage())
            startSimulation()
        }
    }

    void handleEvent(I18NAlert alert) {
        if (alert.value.equals(alert.firstButtonLabel)) {
            startSimulation()
        } else if (alert.value.equals(alert.secondButtonLabel) || alert.value.equals("windowClosing")) {
            //User has clicked "Cancel" or simply closed the alert window... either way, just return
            return
        } else {
            throw new RuntimeException("Unknown button pressed: " + alert.value)
        }
    }

    private void startSimulation() {
        runner = RunSimulationService.getService().runSimulationOnGrid(
                new SimulationConfiguration(simulation: simulation, outputStrategy: outputStrategy),
                simulation.template)
        notifySimulationStart()
    }

    void cancelSimulation() {
        runner.cancel()
    }

    int getProgress() {
        runner.getProgress()
    }

    int getIterationsDone() {
        runner.simulation.numberOfIterations
    }

    SimulationState getSimulationState() {
        runner.getSimulationState()
    }

    String getEstimatedEndTime() {
        DateTime estimatedSimulationEnd = runner.getEstimatedSimulationEnd()
        if (estimatedSimulationEnd != null) {
            return dateFormat.print(estimatedSimulationEnd)
        }
        return "-"
    }

    String getSimulationStartTime() {
        DateTime simulationStartTime = runner.simulation.start
        if (simulationStartTime != null) {
            return dateFormat.print(simulationStartTime)
        }
        return "-"
    }

    String getSimulationEndTime() {
        DateTime estimatedSimulationEnd = runner.simulation.end
        if (estimatedSimulationEnd != null) {
            return dateFormat.print(estimatedSimulationEnd)
        }
        return "-"
    }

    String getRemainingTime() {
        String result = "-"
        DateTime end = runner.getEstimatedSimulationEnd()
        if (end != null) {
            use(TimeCategory) {
                def duration = end.toDate() - new Date()
                result = "$duration.hours h $duration.minutes m $duration.seconds s"
            }
        }
        return result
    }

    void addSimulationListener(ISimulationListener listener) {
        listeners.add(listener)
    }

    void removeSimulationListener(ISimulationListener listener) {
        listeners.remove(listener)
    }

    void notifySimulationStart() {
        listeners*.simulationStart(simulation)
    }

    void notifySimulationStop() {
        this.simulation = runner.simulation
        listeners*.simulationEnd(simulation, simulation.modelClass.newInstance())
    }

    void notifySimulationToBatchAdded(String message, BatchRunSimulationRun batchRun) {
        batchMessage = message
        ISimulationListener pane = listeners.find {it.class.name == SimulationActionsPane.class.name}
        pane?.simulationToBatchAdded()
        mainModel.fireRowAdded(batchRun)
    }

    String getErrorMessage() {
        HashSet<String> messages = new HashSet<String>();
        for (Throwable simulationException: runner.getSimulationErrors()) {
            String exceptionMessage = simulationException.message
            if (exceptionMessage == null) {
                exceptionMessage = simulationException.class.name
            }
            messages.add(exceptionMessage);
        }

        StringBuffer text = new StringBuffer();
        for (String exceptionMessage: messages) {
            List words = exceptionMessage.split(" ") as List
            int lineLength = 0
            for (String s in words) {
                if (lineLength + s.length() > 70) {
                    text << "\n"
                    lineLength = 0
                }
                text << s + " "
                lineLength += (s.length() + 1)
            }
            text << "\n";
        }

        return text.toString()
    }

    void newBatchAdded(BatchRun batchRun) {
        batchRunComboBoxModel.addItem(batchRun)
    }

    void modelChanged() {

        final BatchRun selected = batchRunComboBoxModel.getSelectedObject()
        batchRunComboBoxModel.removeAllElements()

        for (BatchRun run in BatchRun.list()) {
            batchRunComboBoxModel.addItem(run)
        }

        if (selected != null) {
            batchRunComboBoxModel.setSelectedItem(selected.name)
        }
    }


}

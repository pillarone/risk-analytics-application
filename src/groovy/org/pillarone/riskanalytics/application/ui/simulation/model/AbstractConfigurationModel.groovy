package org.pillarone.riskanalytics.application.ui.simulation.model

import com.ulcjava.base.application.AbstractAction
import javax.sql.DataSource
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.base.model.ModelListModel
import org.pillarone.riskanalytics.application.ui.batch.action.AddToBatchAction
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNameListModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationVersionsListModel
import org.pillarone.riskanalytics.application.ui.result.view.ItemsComboBoxModel
import org.pillarone.riskanalytics.application.util.UserPreferences
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.output.FileOutput
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy
import org.pillarone.riskanalytics.core.output.OutputStrategy
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.engine.RunSimulationService
import org.pillarone.riskanalytics.core.simulation.engine.SimulationConfiguration
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRunner
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.util.MathUtils
import org.pillarone.riskanalytics.application.ui.simulation.action.*

abstract class AbstractConfigurationModel implements IModelChangedListener {

    ModelListModel availableModels
    ParameterizationNameListModel availableParameterizationNamesForModel
    ParameterizationVersionsListModel availableParameterizationVersionsForModel
    ResultConfigurationNameListModel availableResultConfigurationNamesForModel
    ResultConfigurationVersionsListModel availableResultConfigurationVersionsForModel
    OutputStrategyComboBoxModel outputStrategyComboBoxModel
    ItemsComboBoxModel<BatchRun> itemsComboBoxModel
    String simulationName
    String comment
    String resultLocation
    private String resultFileLocation
    boolean useUserDefinedSeed
    int randomSeed = MathUtils.DEFAULT_RANDOM_SEED

    protected DateTime beginOfFirstPeriod

    Integer iterationCount
    int currentIteration

    boolean modelChangeable

    private List simulationListeners
    private List simulationConfigurationListeners

    Simulation currentSimulation
    protected SimulationRunner runner = SimulationRunner.createRunner()

    P1RATModel mainModel

    final AbstractAction runAction
    final AbstractAction stopAction
    final AbstractAction cancelAction
    final AbstractAction openResultAction
    final AbstractAction changeResultLocationAction
    final AbstractAction addToBatchAction

    Exception simulationException

    public AbstractConfigurationModel(P1RATModel mainModel, Class modelClass, Parameterization parameterization, ResultConfiguration template) {
        this.mainModel = mainModel
        simulationListeners = []
        simulationConfigurationListeners = []
        modelChangeable = modelClass == null
        availableModels = new ModelListModel()
        availableModels.load()
        availableParameterizationNamesForModel = new ParameterizationNameListModel()
        availableParameterizationVersionsForModel = new ParameterizationVersionsListModel()
        availableResultConfigurationNamesForModel = new ResultConfigurationNameListModel()
        availableResultConfigurationVersionsForModel = new ResultConfigurationVersionsListModel()
        outputStrategyComboBoxModel = new OutputStrategyComboBoxModel()
        Collection batches = BatchRun.findAll()
        itemsComboBoxModel = new ItemsComboBoxModel<BatchRun>(batches?.toList())

        runAction = new RunSimulationAction(this)
        stopAction = new StopSimulationAction(this)
        cancelAction = new CancelSimulationAction(this)
        openResultAction = new OpenResultAction(this)
        changeResultLocationAction = new ChangeResultLocationAction(this)
        addToBatchAction = new AddToBatchAction(this, mainModel)



        if (modelClass != null) {
            setSelectedModel(modelClass)
            availableParameterizationNamesForModel.load(selectedModel)
            availableParameterizationVersionsForModel.load(selectedModel, availableParameterizationNamesForModel.selectedItem)
            availableResultConfigurationNamesForModel.load(selectedModel)
            availableResultConfigurationVersionsForModel.load(selectedModel, availableResultConfigurationNamesForModel.selectedItem)

        }

        selectedParameterization = parameterization
        selectedResultTemplate = template

        connectListModels()
    }

    private void connectListModels() {
        availableParameterizationNamesForModel.contentsChanged = {e ->
            def selectedParameterization = e.source.selectedItem
            availableParameterizationVersionsForModel.load(selectedModel, selectedParameterization)
            availableParameterizationVersionsForModel.selectedItem = availableParameterizationVersionsForModel.getElementAt(0)
            notifySimulationConfigurationChanged()
        }


        availableResultConfigurationNamesForModel.contentsChanged = {e ->
            def selectedTemplate = e.source.selectedItem
            availableResultConfigurationVersionsForModel.load(selectedModel, selectedTemplate)
            availableResultConfigurationVersionsForModel.selectedItem = availableResultConfigurationVersionsForModel.getElementAt(0)
            notifySimulationConfigurationChanged()
        }

        availableModels.contentsChanged = {e ->
//            selectedModel = e.source.selectedObject
            availableParameterizationNamesForModel.load(selectedModel)
            availableParameterizationVersionsForModel.load(selectedModel, availableParameterizationNamesForModel.selectedItem)
            availableResultConfigurationNamesForModel.load(selectedModel)
            availableResultConfigurationVersionsForModel.load(selectedModel, availableResultConfigurationNamesForModel.selectedItem)
            notifySimulationConfigurationChanged()
        }
    }

    public void setIterationCount(Integer count) {
        iterationCount = count
        notifySimulationConfigurationChanged()
    }

    /**
     * This method sets a new first period date in the model and notifies all listeners that the model has changed.
     */
    void setBeginOfFirstPeriod(Date begin) {
        beginOfFirstPeriod = new DateTime(begin)
        notifySimulationConfigurationChanged()
    }

    void setSimulationName(String name) {
        this.simulationName = name
        notifySimulationConfigurationChanged()
    }

    public void setUseUserDefinedSeed(boolean b) {
        useUserDefinedSeed = b
        notifySimulationConfigurationChanged()
    }

    public void setRandomSeed(double value) {
        randomSeed = value
        notifySimulationConfigurationChanged()
    }

    String getSimulationName() {
        this.simulationName != null ? this.simulationName : ""
    }

    void setComment(String newComment) {
        this.comment = newComment
        notifySimulationConfigurationChanged()
    }

    Class getSelectedModel() {
        return availableModels.selectedObject
    }

    void setSelectedModel(Class modelClass) {
        availableModels.selectedObject = modelClass
    }

    void setSelectedParameterization(Parameterization parameterization) {
        if (parameterization == null) {
            return
        }
        availableParameterizationNamesForModel.selectedItem = parameterization.name
        availableParameterizationVersionsForModel.load(selectedModel, availableParameterizationNamesForModel.selectedItem)
        availableParameterizationVersionsForModel.selectedItem = "v${parameterization.versionNumber.toString()}".toString()
    }

    void setSelectedResultTemplate(ResultConfiguration resultTemplate) {
        if (resultTemplate == null) {
            return
        }
        availableResultConfigurationNamesForModel.selectedItem = resultTemplate.name
        availableResultConfigurationVersionsForModel.load(selectedModel, availableResultConfigurationNamesForModel.selectedItem)
        availableResultConfigurationVersionsForModel.selectedItem = "v${resultTemplate.versionNumber.toString()}".toString()
    }

    public isResultLocationChangeable() {
        ICollectorOutputStrategy selectedStrategy = outputStrategyComboBoxModel.getStrategy()
        return selectedStrategy instanceof FileOutput
    }

    public String getResultLocation() {
        if (isResultLocationChangeable()) {
            if (resultLocation == null) {
                resultLocation = UserPreferences.getUserDirectory()
            }
            outputStrategyComboBoxModel.getStrategy().resultLocation = resultLocation
            return resultLocation
        } else {
            return getDatabaseUrl()
        }
    }

    private String getDatabaseUrl() {
        DataSource dataSource = ApplicationHolder.application.mainContext.getBean('dataSource') as DataSource
        return dataSource.url
    }

    abstract Simulation getSimulation()

    public void runSimulation() {
        simulationException = null

        currentSimulation = getSimulation()

        if (!currentSimulation.save()) {
            throw new RuntimeException("Error saving Simulation")
        }

        ICollectorOutputStrategy strategy = outputStrategyComboBoxModel.getStrategy()

        runner = SimulationRunner.createRunner()
        SimulationConfiguration configuration = new SimulationConfiguration(simulation: currentSimulation, outputStrategy: strategy)

        RunSimulationService.getService().runSimulation(runner, configuration)
        notifySimulationStart()
    }

    public void addToBatch(BatchRun batchRun, OutputStrategy strategy) {
        batchRun.batchRunService.addSimulationRun(batchRun, getSimulation(), strategy)
    }

    void stopSimulation() {
        runner.stop()
    }

    void cancelSimulation() {
        runner.cancel()
    }


    void syncIterationCount() {
        if (simulationRunning()) {
            currentIteration = runner.currentScope.iterationsDone
        } else {
            simulationException = runner.error?.error
            notifySimulationEnd()
        }
    }

    boolean simulationRunning() {
        return runner.simulationState != SimulationState.NOT_RUNNING &&
                runner.simulationState != SimulationState.STOPPED &&
                runner.simulationState != SimulationState.FINISHED &&
                runner.simulationState != SimulationState.CANCELED &&
                runner.simulationState != SimulationState.ERROR

    }

    boolean postSimulationCalculationsRunning() {
        return runner.simulationState == SimulationState.POST_SIMULATION_CALCULATIONS
    }

    void addSimulationListener(ISimulationListener listener) {
        simulationListeners << listener
    }

    void removeSimulationListener(ISimulationListener listener) {
        simulationListeners.remove(listener)
    }

    void notifySimulationStart() {
        notifySimulationConfigurationChanged()

        simulationListeners.each {ISimulationListener it ->
            it.simulationStart(currentSimulation)
        }
    }


    void notifySimulationEnd() {
        this.simulationName = null
        currentSimulation.load()
        notifySimulationConfigurationChanged()

        simulationListeners.each {ISimulationListener it ->
            it.simulationEnd(currentSimulation, selectedModel.newInstance())
        }
    }

    void reloadListModels() {
        def currentParamName = availableParameterizationNamesForModel.selectedItem
        availableParameterizationNamesForModel.reload()
        availableParameterizationNamesForModel.selectedItem = currentParamName
        availableParameterizationVersionsForModel.reload(currentParamName)

        def currentTemplateName = availableResultConfigurationNamesForModel.selectedItem
        availableResultConfigurationNamesForModel.reload()
        availableResultConfigurationNamesForModel.selectedItem = currentTemplateName
        availableResultConfigurationVersionsForModel.reload(currentTemplateName)
    }

    void addSimulationConfigurationListener(ISimulationConfigurationListener listener) {
        simulationConfigurationListeners << listener
    }

    void removeSimulationConfigurationListener(ISimulationConfigurationListener listener) {
        simulationConfigurationListeners.remove(listener)
    }

    void notifySimulationConfigurationChanged() {
        updateActionEnabling()
        simulationConfigurationListeners.each {
            it.simulationConfigurationChanged()
        }
    }

    void batchAdded(String message, boolean error) {
        setSimulationName("")
        simulationConfigurationListeners.each {
            it.batchAdded(message, error)
        }
    }

    abstract boolean isSimulationStartEnabled()

    boolean isSimulationStopEnabled() {
        simulationRunning()
    }

    boolean isOpenResultEnabled() {
        return currentSimulation != null && currentSimulation.end != null
    }

    boolean isConfigurationChangeable() {
        return !simulationRunning()
    }

    private void updateActionEnabling() {
        changeResultLocationAction.enabled = isResultLocationChangeable()
    }

    int getSimulationProgress() {
        int progress = 0
        if (simulationRunning()) {
            progress = runner.progress
        }
        return progress
    }

    boolean isCurrentTaskEndIndeterminate() {
        if (simulationRunning()) {
            switch (runner.simulationState) {
                case SimulationState.INITIALIZING:
                    return true
                case SimulationState.SAVING_RESULTS:
                    return true
                default:
                    return false
            }
        }
        return false
    }

    String getSimulationMessage() {
        switch (runner.simulationState) {
            case SimulationState.NOT_RUNNING:
                return "SimulationNotRunningMessage"
            case SimulationState.INITIALIZING:
                return "StartSimulationMessage"
            case SimulationState.SAVING_RESULTS:
                return "SavingResultsMessage"
            case SimulationState.FINISHED:
                return "Done"
            case SimulationState.STOPPED:
                return "Stopped"
            case SimulationState.CANCELED:
                return "Canceled"
            case SimulationState.ERROR:
                return "Error"
            default:
                return ""
        }
    }

    Date getEstimatedSimulationEnd() {
        runner.getEstimatedSimulationEnd()
    }

    Date getSimulationStart() {
        runner.currentScope.simulation.start
    }

    Date getSimulationEnd() {
        currentSimulation?.end
    }

    public void modelChanged() {
        reloadListModels()
    }

    public SimulationRunner getSimulationRunner() {
        return runner
    }


}

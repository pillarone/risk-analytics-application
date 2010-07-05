package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import com.ulcjava.base.application.ULCSpinnerDateModel
import java.text.SimpleDateFormat
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.model.ModelListModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNameListModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationVersionsListModel
import org.pillarone.riskanalytics.application.ui.simulation.model.OutputStrategyComboBoxModel
import org.pillarone.riskanalytics.application.ui.simulation.model.ResultConfigurationNameListModel
import org.pillarone.riskanalytics.application.ui.simulation.model.ResultConfigurationVersionsListModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.action.ChangeOutputStrategyAction
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.action.ChangeResultLocationAction
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.action.RandomSeedAction
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.action.ReloadListModelAction
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.ISimulationProvider
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.ISimulationValidationListener
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.DBOutput
import org.pillarone.riskanalytics.core.output.FileOutput
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener

/**
 * The view model of the SimulationSettingsPane.
 * It is possible to retrieve a Simulation and ICollectorOutputStrategy object from the model (created from the current values).
 *
 * It is also possible to register a ISimulationValidationListener, which will be notified when the simulation configuration changes from
 * an invalid (incomplete) to a valid state.
 */
class SimulationSettingsPaneModel implements ISimulationProvider, IModelChangedListener {

    private static Log LOG = LogFactory.getLog(SimulationSettingsPaneModel)

    String simulationName
    String comment

    ModelListModel models

    ParameterizationNameListModel parameterizationNames
    ParameterizationVersionsListModel parameterizationVersions

    ResultConfigurationNameListModel resultConfigurationNames
    ResultConfigurationVersionsListModel resultConfigurationVersions

    OutputStrategyComboBoxModel outputStrategies

    DateTime beginOfFirstPeriod
    Integer randomSeed
    Integer numberOfIterations

    RandomSeedAction randomSeedAction
    ReloadListModelAction reloadListModelAction
    private ChangeOutputStrategyAction changeOutputStrategyAction
    private ChangeResultLocationAction changeResultLocationAction

    private File resultLocation
    private Class modelClass
    private Model modelInstance

    private List<ISimulationValidationListener> listeners = []

    public SimulationSettingsPaneModel(Class modelClass) {
        this.modelClass = modelClass
        simulationName = new SimpleDateFormat("yyyy.MM.dd kk:mm:ss").format(new Date())
        comment = ""

        models = new ModelListModel()
        parameterizationNames = new ParameterizationNameListModel()
        parameterizationVersions = new ParameterizationVersionsListModel()
        resultConfigurationNames = new ResultConfigurationNameListModel()
        resultConfigurationVersions = new ResultConfigurationVersionsListModel()

        models.load()
        models.selectedObject = modelClass
        parameterizationNames.load(modelClass)
        updateParameterizationVersions()
        resultConfigurationNames.load(modelClass)
        updateResultConfigurationVersions()

        outputStrategies = new OutputStrategyComboBoxModel()

        randomSeedAction = new RandomSeedAction(this)
        reloadListModelAction = new ReloadListModelAction(this)
    }

    /**
     * @return an instance of the currently selected model class
     */
    protected Model getModelInstance() {
        if (modelInstance == null) {
            modelInstance = modelClass.newInstance()
        }
        return modelInstance
    }

    /**
     * @return a ChangeOutputStrategyAction which will also run the given closure when executed
     */
    ChangeOutputStrategyAction getChangeOutputStrategyAction(Closure action) {
        if (changeOutputStrategyAction == null) {
            changeOutputStrategyAction = new ChangeOutputStrategyAction(this, action)
        }
        return changeOutputStrategyAction
    }

    /**
     * @return a ChangeResultLocationAction which will also run the given closure when executed
     */
    ChangeResultLocationAction getChangeResultLocationAction(Closure action) {
        if (changeResultLocationAction == null) {
            changeResultLocationAction = new ChangeResultLocationAction(this, action)
        }
        return changeResultLocationAction
    }

    void updateParameterizationVersions() {
        String selected = parameterizationNames.selectedItem
        parameterizationVersions.load(modelClass, selected)
    }

    void updateResultConfigurationVersions() {
        String selected = resultConfigurationNames.selectedItem
        resultConfigurationVersions.load(modelClass, selected)
    }

    String getText(String key) {
        return UIUtils.getText(SimulationSettingsPaneModel, key)
    }

    /**
     * @return a string representation of the currently selected output strategy (jdbc url, file path, etc)
     */
    String getResultLocation() {
        ICollectorOutputStrategy outputStrategy = outputStrategies.getStrategy()
        if (outputStrategy instanceof DBOutput) {
            return getDatabaseUrl()
        } else if (outputStrategy instanceof FileOutput) {
            return outputStrategy.resultLocation
        }
        return ""
    }

    /**
     * Initializes a ULCSpinnerDateModel with the first day of the current year and also sets beginOfFirstPeriod to this value.
     */
    ULCSpinnerDateModel getBeginOfFirstPeriodSpinnerModel() {
        DateTime firstInYear = new DateTime().withDayOfYear(1)
        firstInYear = new DateTime(firstInYear.getYear(), firstInYear.getMonthOfYear(), firstInYear.getDayOfMonth(), 0, 0, 0, 0)
        beginOfFirstPeriod = firstInYear
        return new ULCSpinnerDateModel(firstInYear.toDate(), null, null, Calendar.DAY_OF_MONTH)
    }

    boolean requiresStartDate() {
        return getModelInstance().requiresStartDate()
    }

    void setResultLocation(String location) {
        resultLocation = new File(location)
    }

    private String getDatabaseUrl() {
        return ApplicationHolder.application.getMainContext().getBean("dataSource").url
    }

    /**
     * Creates a new Simulation based on the values saved in the model.
     * If no simulation name is set the current date & time is used as simulation name.
     * If no user defined seed is set, a random one will be calculated and used.
     */
    Simulation getSimulation() {
        Simulation simulation = new Simulation(simulationName)
        simulation.modelClass = modelClass
        simulation.comment = comment
        Parameterization parameterization = parameterizationVersions.selectedObject as Parameterization
        parameterization.load()
        simulation.parameterization = parameterization
        ResultConfiguration configuration = resultConfigurationVersions.selectedObject as ResultConfiguration
        configuration.load()
        simulation.template = configuration
        simulation.numberOfIterations = numberOfIterations
        simulation.periodCount = parameterization.periodCount
        simulation.beginOfFirstPeriod = beginOfFirstPeriod
        if (randomSeed != null) {
            simulation.randomSeed = randomSeed
        } else {
            long millis = System.currentTimeMillis()
            long millisE5 = millis / 1E5
            simulation.randomSeed = millis - millisE5 * 1E5
        }
        simulation.modelVersionNumber = ModellingItemFactory.getNewestModelItem(modelClass.simpleName).versionNumber // ???

        return simulation
    }

    /**
     * Setter for numberOfIterations, which also fires a validation event (because the iteration number must be set)
     */
    void setNumberOfIterations(Integer i) {
        numberOfIterations = i
        notifyConfigurationChanged()
    }

    void setSimulationName(String name) {
        simulationName = name
        notifyConfigurationChanged()
    }

    void addSimulationValidationListener(ISimulationValidationListener listener) {
        listeners.add(listener)
    }

    void removeSimulationValidationListener(ISimulationValidationListener listener) {
        listeners.remove(listener)
    }

    protected void notifyConfigurationChanged() {
        listeners*.simulationPropertyChanged(validate())
    }

    /**
     *
     * @return true if a valid simulation can be created from the current model values
     */
    protected boolean validate() {
        return numberOfIterations != null && (simulationName?.trim()?.length() > 0)
    }

    ICollectorOutputStrategy getOutputStrategy() {
        return outputStrategies.getStrategy()
    }

    void setSelectedParameterization(Parameterization parameterization) {
        if (parameterization != null) {
            parameterizationNames.selectedItem = parameterization.name
            parameterizationVersions.reload(parameterizationNames.selectedItem.toString())
            parameterizationVersions.selectedItem = parameterization.versionNumber.toString()
        }
    }

    void setSelectedResultConfiguration(ResultConfiguration resultConfiguration) {
        if (resultConfiguration != null) {
            resultConfigurationNames.selectedItem = resultConfiguration.name
            resultConfigurationVersions.reload(resultConfigurationNames.selectedItem.toString())
            resultConfigurationVersions.selectedItem = resultConfiguration.versionNumber.toString()
        }
    }

    void modelChanged() {
        String currentSelection = parameterizationNames.selectedItem
        parameterizationNames.reload()
        parameterizationNames.selectedItem = currentSelection

        parameterizationVersions.reload(currentSelection)

        currentSelection = resultConfigurationNames.selectedItem
        resultConfigurationNames.reload()
        resultConfigurationNames.selectedItem = currentSelection

        resultConfigurationVersions.reload(currentSelection)
    }


}

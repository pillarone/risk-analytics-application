package org.pillarone.riskanalytics.application.ui.simulation.model.impl

import com.ulcjava.base.application.DefaultComboBoxModel
import com.ulcjava.base.application.ULCSpinnerDateModel
import grails.util.Holders
import groovy.beans.Bindable
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.base.model.ModelListModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationNameListModel
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationVersionsListModel
import org.pillarone.riskanalytics.application.ui.simulation.model.OutputStrategyComboBoxModel
import org.pillarone.riskanalytics.application.ui.simulation.model.ResultConfigurationNameListModel
import org.pillarone.riskanalytics.application.ui.simulation.model.ResultConfigurationVersionsListModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.action.*
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.*
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.DBOutput
import org.pillarone.riskanalytics.core.output.FileOutput
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy
import org.pillarone.riskanalytics.core.simulation.item.*
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

/**
 * The view model of the SimulationSettingsPane.
 * It is possible to retrieve a Simulation and ICollectorOutputStrategy object from the model (created from the current values).
 *
 * It is also possible to register a ISimulationValidationListener, which will be notified when the simulation configuration changes from
 * an invalid (incomplete) to a valid state.
 */
class SimulationSettingsPaneModel implements ISimulationProvider, IModelChangedListener, ISimulationProfileApplicable {

    private static final Log LOG = LogFactory.getLog(SimulationSettingsPaneModel)

    String simulationName
    String comment

    ModelListModel models

    ParameterizationNameListModel parameterizationNames
    ParameterizationVersionsListModel parameterizationVersions

    ResultConfigurationNameListModel resultConfigurationNames
    ResultConfigurationVersionsListModel resultConfigurationVersions

    OutputStrategyComboBoxModel outputStrategies

    DateTime beginOfFirstPeriod
    @Bindable
    Integer randomSeed
    @Bindable
    Integer numberOfIterations

    RandomSeedAction randomSeedAction
    ReloadParameterizationListModelAction reloadParameterizationListModelAction
    ReloadResultConfigurationListModelAction reloadResultConfigurationListModelAction
    ChangeOutputStrategyAction changeOutputStrategyAction
    ChangeResultLocationAction changeResultLocationAction

    File resultLocation
    Class modelClass
    Model modelInstance

    RuntimeParameterPaneModel parameterPaneModel
    PostSimulationCalculationPaneModel postSimulationCalculationPaneModel

    private List<ISimulationValidationListener> listeners = []

    public SimulationSettingsPaneModel(Class modelClass) {
        this.modelClass = modelClass
        simulationName = ""
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
        reloadParameterizationListModelAction = new ReloadParameterizationListModelAction(this)
        reloadResultConfigurationListModelAction = new ReloadResultConfigurationListModelAction(this)

        parameterPaneModel = new RuntimeParameterPaneModel(getModelInstance())
        postSimulationCalculationPaneModel = new PostSimulationCalculationPaneModel()
    }

    /**
     * @return an instance of the currently selected model class
     */
    protected Model getModelInstance() {
        if (modelInstance == null) {
            modelInstance = modelClass.newInstance() as Model
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
        ICollectorOutputStrategy outputStrategy = outputStrategies.strategy
        if (outputStrategy instanceof DBOutput) {
            return Holders.config.dataSource.url
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
        firstInYear = new DateTime(firstInYear.year, firstInYear.monthOfYear, firstInYear.dayOfMonth, 0, 0, 0, 0)
        beginOfFirstPeriod = firstInYear
        return new ULCSpinnerDateModel(firstInYear.toDate(), null, null, Calendar.DAY_OF_MONTH)
    }

    boolean requiresStartDate() {
        return getModelInstance().requiresStartDate()
    }

    void setResultLocation(String location) {
        resultLocation = new File(location)
    }

/**
 * Creates a new Simulation based on the values saved in the model.
 * If no simulation name is set the current date & time is used as simulation name.
 * If no user defined seed is set, a random one will be calculated and used.
 */
    Simulation getSimulation() {
        Parameterization parameterization = parameterizationVersions.selectedObject as Parameterization
        //do not always load, because params could be open and modified ("save and run")
        if (!parameterization.loaded) {
            parameterization.load()
        }

        String name = simulationName
        if (name == null || name.trim().length() == 0) {
            name = parameterization.name + " " + DateFormatUtils.getDateFormat("yyyy.MM.dd HH:mm:ss").print(new DateTime())
        }
        Simulation simulation = new Simulation(name)
        simulation.modelClass = modelClass //does also set model version number
        simulation.comment = comment

        simulation.parameterization = parameterization
        ResultConfiguration configuration = resultConfigurationVersions.selectedObject as ResultConfiguration
        if (!configuration.loaded) {
            configuration.load()
        }
        simulation.template = configuration
        simulation.beginOfFirstPeriod = beginOfFirstPeriod
        simulation.structure = ModelStructure.getStructureForModel(modelClass)

        initConfigParameters(simulation, parameterization.periodCount)

        for (ParameterHolder holder in parameterPaneModel.parameters) {
            simulation.addParameter(holder)
        }
        simulation.keyFiguresToPreCalculate = postSimulationCalculationPaneModel.keyFigureMap
        return simulation
    }

    public void initConfigParameters(Simulation simulation, int periodCount) {
        simulation.numberOfIterations = numberOfIterations
        simulation.periodCount = periodCount
        if (randomSeed != null) {
            simulation.randomSeed = randomSeed
        } else {
            long millis = System.currentTimeMillis()
            long millisE5 = millis / 1E5
            simulation.randomSeed = millis - millisE5 * 1E5
        }
    }

    /**
     * Setter for numberOfIterations, which also fires a validation event (because the iteration number must be set)
     */
    void setNumberOfIterations(Integer i) {
        boolean changed = numberOfIterations != i
        numberOfIterations = i
        if (changed) {
            notifyConfigurationChanged()
        }
    }

    void addSimulationValidationListener(ISimulationValidationListener listener) {
        listeners.add(listener)
    }

    void removeSimulationValidationListener(ISimulationValidationListener listener) {
        listeners.remove(listener)
    }

    void notifyConfigurationChanged() {
        listeners*.simulationPropertyChanged(validate())
    }

    /**
     *
     * @return true if a valid simulation can be created from the current model values
     */
    protected boolean validate() {
        Parameterization parameterization = parameterizationVersions.selectedObject as Parameterization
        if (parameterization == null) {
            return false
        }
        return numberOfIterations != null && parameterization.valid
    }

    ICollectorOutputStrategy getOutputStrategy() {
        ICollectorOutputStrategy outputStrategy = outputStrategies.strategy
        if (outputStrategy instanceof DBOutput) {
            outputStrategy.batchInsert.reset()
        }
        return outputStrategy
    }

    void setSelectedParameterization(Parameterization parameterization) {
        if (parameterization != null) {
            parameterizationNames.selectedItem = parameterization.name
            parameterizationVersions.reload(parameterizationNames.selectedItem.toString())
            parameterizationVersions.selectedItem = "v" + parameterization.versionNumber.toString()
        }
    }

    void setSelectedResultConfiguration(ResultConfiguration resultConfiguration) {
        if (resultConfiguration != null) {
            resultConfigurationNames.selectedItem = resultConfiguration.name
            resultConfigurationVersions.reload(resultConfigurationNames.selectedItem.toString())
            resultConfigurationVersions.selectedItem = "v" + resultConfiguration.versionNumber.toString()
        }
    }

    void modelChanged() {
        doWithoutListening(parameterizationNames) {
            parameterizationNames.reload()
        }
        doWithoutListening(parameterizationVersions) {
            parameterizationVersions.reload(parameterizationNames.selectedItem as String)
        }
        doWithoutListening(resultConfigurationNames) {
            resultConfigurationNames.reload()
        }
        doWithoutListening(resultConfigurationVersions) {
            resultConfigurationVersions.reload(resultConfigurationNames.selectedItem as String)
        }
    }

    private void doWithoutListening(DefaultComboBoxModel comboBoxModel, Closure c) {
        doWithRestoredSelection(comboBoxModel) {
            def listeners = comboBoxModel.listDataListeners
            listeners.each {
                comboBoxModel.removeListDataListener(it)
            }
            c.call()
            listeners.each {
                comboBoxModel.addListDataListener(it)
            }
        }
    }

    private doWithRestoredSelection(DefaultComboBoxModel comboBoxModel, Closure c) {
        def current = comboBoxModel.selectedItem
        c.call()
        if (current) {
            comboBoxModel.selectedItem = current
        }
    }

    @Override
    SimulationProfile createProfile(String name) {
        def profile = new SimulationProfile(name, modelClass)
        profile.load()
        profile.randomSeed = randomSeed
        profile.template = resultConfigurationVersions.selectedObject as ResultConfiguration
        profile.numberOfIterations = numberOfIterations
        profile.notDeletedParameterHolders.each {
            profile.removeParameter(it)
        }
        for (ParameterHolder holder in parameterPaneModel.parameters) {
            profile.addParameter(holder)
        }
        profile
    }

    @Override
    void applyProfile(SimulationProfile profile) {
        setNumberOfIterations(profile.numberOfIterations)
        setRandomSeed(profile.randomSeed)
        resultConfigurationNames.selectedItem = profile.template.name
        resultConfigurationVersions.selectedObject = profile.template

        sanityCheck(parameterPaneModel.runtimeParameters, profile)

        parameterPaneModel.runtimeParameters.each { RuntimeParameterCollector.RuntimeParameterDescriptor descriptor ->
            def holder = profile.getParameterHolder(descriptor.propertyName, 0)
            descriptor.value = holder.businessObject
        }
    }

    void sanityCheck(List<RuntimeParameterCollector.RuntimeParameterDescriptor> descriptors, SimulationProfile profile) {
        Set holderNames = profile.runtimeParameters*.path as Set
        Set descriptorNames = descriptors.propertyName as Set
        if (holderNames != descriptorNames) {
            throw new IllegalStateException("The simulationProfile $profile does not match the given runtimeDescriptors $descriptors")
        }
        descriptors.each {
            def holder = profile.getParameterHolder(it.propertyName, 0)
            if (!holder) {
                throw new IllegalStateException("could not find holder for descriptor $it in profile $profile")
            }
        }
    }
}

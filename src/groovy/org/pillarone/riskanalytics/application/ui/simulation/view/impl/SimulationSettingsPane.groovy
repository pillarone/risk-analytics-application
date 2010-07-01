package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCSpinner.ULCDateEditor
import com.ulcjava.base.application.event.IKeyListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.util.Dimension
import org.apache.commons.lang.time.FastDateFormat
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.FileOutput
import com.ulcjava.base.application.*
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.boxLayout
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.spaceAround

/**
 * A view class which can be used to collect all information necessary for a simulation run (Simulation & output strategy)
 */
class SimulationSettingsPane {

    public static final String SIMULATION_SETTINGS_KEY = "SimulationSettings"
    public static final String SIMULATION_NAME_KEY = "Name"
    public static final String SIMULATION_COMMENT_KEY = "Comment"
    public static final String PARAMETERIZATION_KEY = "Parameter"
    public static final String MODEL_KEY = "Model"
    public static final String RESULT_CONFIGURATION_KEY = "ResultTemplate"
    public static final String OUTPUT_STRATEGY_KEY = "OutputStrategy"
    public static final String RESULT_LOCATION_KEY = "ResultLocation"
    public static final String BEGIN_OF_FIRST_PERIOD_KEY = "BeginOfPeriods"
    public static final String USER_DEFINED_RANDOM_SEED_KEY = "IsRandom"
    public static final String RANDOM_SEED_KEY = "InitSeed"
    public static final String ITERATIONS_KEY = "NumberOfIterations"

    ULCBoxPane content

    private ULCTextField simulationName
    private ULCTextArea comment
    private ULCComboBox modelComboBox
    private ULCComboBox parametrizationNamesComboBox
    private ULCComboBox parameterizationVersionsComboBox
    private ULCComboBox resultConfigurationNamesComboBox
    private ULCComboBox resultConfigurationVersionsComboBox
    private ULCComboBox outputStrategy
    private ULCTextField resultLocation
    private ULCButton changeLocationButton
    private ULCSpinner beginOfFirstPeriod
    private ULCCheckBox userDefinedRandomSeed
    private ULCTextField randomSeed
    private ULCTextField numberOfIterations

    SimulationSettingsPaneModel model

    public SimulationSettingsPane(SimulationSettingsPaneModel model) {
        this.model = model
        initComponents()
        layoutComponents()
        attachListeners()
    }

    private void attachListeners() {
        randomSeed.addValueChangedListener(model.randomSeedAction)
        userDefinedRandomSeed.addValueChangedListener(model.randomSeedAction)
        parametrizationNamesComboBox.addActionListener(model.reloadListModelAction)
        resultConfigurationNamesComboBox.addActionListener(model.reloadListModelAction)

        Closure outputStrategyAction = { boolean resultLocationRequired ->
            resultLocation.enabled = resultLocationRequired
            changeLocationButton.enabled = resultLocationRequired
            resultLocation.text = model.getResultLocation()
        }
        outputStrategy.addActionListener(model.getChangeOutputStrategyAction(outputStrategyAction))

        simulationName.addKeyListener([keyTyped: { e -> model.simulationName = simulationName.text }] as IKeyListener)
        comment.addValueChangedListener([valueChanged: { e -> model.comment = comment.text }] as IValueChangedListener)
        numberOfIterations.addKeyListener([keyTyped: { e ->
            model.numberOfIterations = numberOfIterations.value
        }] as IKeyListener)
    }

    private void initComponents() {
        simulationName = new ULCTextField(model.simulationName)
        simulationName.preferredSize = new Dimension(150, 20)
        simulationName.name = "simulationName"

        comment = new ULCTextArea(model.comment, 4, 20)
        comment.lineWrap = true
        comment.wrapStyleWord = true
        comment.name = "comment"

        modelComboBox = new ULCComboBox(model.models)
        modelComboBox.enabled = false
        parametrizationNamesComboBox = new ULCComboBox(model.parameterizationNames)
        parametrizationNamesComboBox.name = "parameterizationNames"
        parameterizationVersionsComboBox = new ULCComboBox(model.parameterizationVersions)
        parameterizationVersionsComboBox.name = "parameterizationVersions"
        resultConfigurationNamesComboBox = new ULCComboBox(model.resultConfigurationNames)
        resultConfigurationVersionsComboBox = new ULCComboBox(model.resultConfigurationVersions)

        outputStrategy = new ULCComboBox(model.outputStrategies)
        outputStrategy.name = "outputStrategy"
        resultLocation = new ULCTextField(model.resultLocation)
        resultLocation.name = "resultLocation"
        resultLocation.preferredSize = new Dimension(150, 20)
        resultLocation.enabled = false
        Closure resultLocationAction = {
            resultLocation.text = model.getResultLocation()
        }
        changeLocationButton = new ULCButton(model.getChangeResultLocationAction(resultLocationAction))
        changeLocationButton.name = "changeLocation"

        userDefinedRandomSeed = new ULCCheckBox(model.getText(USER_DEFINED_RANDOM_SEED_KEY), false)
        userDefinedRandomSeed.name = "userDefinedRandomSeed"
        randomSeed = new ULCTextField()
        randomSeed.preferredSize = new Dimension(50, 20)
        randomSeed.enabler = userDefinedRandomSeed
        randomSeed.name = "randomSeed"
        randomSeed.dataType = DataTypeFactory.getIntegerDataTypeForEdit()

        numberOfIterations = new ULCTextField()
        numberOfIterations.preferredSize = new Dimension(150, 20)
        numberOfIterations.name = "iterations"
        numberOfIterations.dataType = DataTypeFactory.getIntegerDataTypeForEdit()

        if (model.requiresStartDate()) {
            beginOfFirstPeriod = new ULCSpinner(model.getBeginOfFirstPeriodSpinnerModel())
            beginOfFirstPeriod.preferredSize = new Dimension(150, 20)
            beginOfFirstPeriod.setEditor(new ULCDateEditor(beginOfFirstPeriod, FastDateFormat.getDateInstance(FastDateFormat.SHORT, LocaleResources.getLocale()).pattern))
        }
    }

    private void layoutComponents() {
        content = boxLayout(model.getText(SIMULATION_SETTINGS_KEY)) { ULCBoxPane pane ->
            ULCBoxPane innerPane = new ULCBoxPane(3, 0)

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(SIMULATION_NAME_KEY) + ":"))
            innerPane.add(2, ULCBoxPane.BOX_LEFT_CENTER, spaceAround(simulationName, 5, 10, 0, 0))

            innerPane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel(model.getText(SIMULATION_COMMENT_KEY) + ":"))
            innerPane.add(2, ULCBoxPane.BOX_LEFT_EXPAND, spaceAround(comment, 5, 10, 0, 0))

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(MODEL_KEY) + ":"))
            innerPane.add(2, ULCBoxPane.BOX_LEFT_CENTER, spaceAround(modelComboBox, 5, 10, 0, 0))

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(PARAMETERIZATION_KEY) + ":"))
            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(parametrizationNamesComboBox, 5, 10, 0, 0))
            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(parameterizationVersionsComboBox, 5, 10, 0, 0))

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(RESULT_CONFIGURATION_KEY) + ":"))
            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(resultConfigurationNamesComboBox, 5, 10, 0, 0))
            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(resultConfigurationVersionsComboBox, 5, 10, 0, 0))

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(OUTPUT_STRATEGY_KEY) + ":"))
            innerPane.add(2, ULCBoxPane.BOX_LEFT_CENTER, spaceAround(outputStrategy, 5, 10, 0, 0))

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(RESULT_LOCATION_KEY) + ":"))
            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(resultLocation, 5, 10, 0, 0))
            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(changeLocationButton, 5, 10, 0, 0))

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(RANDOM_SEED_KEY) + ":"))
            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(userDefinedRandomSeed, 5, 10, 0, 0))
            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(randomSeed, 5, 10, 0, 0))

            if (model.requiresStartDate()) {
                innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(BEGIN_OF_FIRST_PERIOD_KEY) + ":"))
                innerPane.add(2, ULCBoxPane.BOX_LEFT_CENTER, spaceAround(beginOfFirstPeriod, 5, 10, 0, 0))
            }

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(ITERATIONS_KEY) + ":"))
            innerPane.add(2, ULCBoxPane.BOX_LEFT_CENTER, spaceAround(numberOfIterations, 5, 10, 0, 0))

            pane.add(ULCBoxPane.BOX_EXPAND_EXPAND, innerPane)
        }
    }

    /**
     * disables the entire UI (for example during a simulation)
     */
    void disable() {
        beginOfFirstPeriod?.enabled = false
        changeLocationButton.enabled = false
        comment.enabled = false
        modelComboBox.enabled = false
        numberOfIterations.enabled = false
        outputStrategy.enabled = false
        parametrizationNamesComboBox.enabled = false
        parameterizationVersionsComboBox.enabled = false
        resultConfigurationNamesComboBox.enabled = false
        resultConfigurationVersionsComboBox.enabled = false
        randomSeed.enabled = false
        resultLocation.enabled = false
        simulationName.enabled = false
        userDefinedRandomSeed.enabled = false
    }

    /**
     * enables the UI again
     */
    void enable() {
        boolean fileMode = model.getOutputStrategy() instanceof FileOutput
        beginOfFirstPeriod?.enabled = true
        changeLocationButton.enabled = fileMode
        comment.enabled = true
        modelComboBox.enabled = false
        numberOfIterations.enabled = true
        outputStrategy.enabled = true
        parametrizationNamesComboBox.enabled = true
        parameterizationVersionsComboBox.enabled = true
        resultConfigurationNamesComboBox.enabled = true
        resultConfigurationVersionsComboBox.enabled = true
        randomSeed.enabled = userDefinedRandomSeed.isSelected()
        resultLocation.enabled = fileMode
        simulationName.enabled = true
        userDefinedRandomSeed.enabled = true
    }

}

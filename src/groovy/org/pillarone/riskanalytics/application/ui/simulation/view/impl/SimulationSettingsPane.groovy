package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCSpinner.ULCDateEditor
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IKeyListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import org.apache.commons.lang.time.FastDateFormat
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationVersionsListModel
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.FileOutput
import com.ulcjava.base.application.*
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.boxLayout
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.spaceAround
import com.ulcjava.base.application.util.Font
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences

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

    protected ULCTextField simulationName
    protected ULCTextArea comment
    protected ULCComboBox modelComboBox
    protected ULCComboBox parametrizationNamesComboBox
    protected ULCComboBox parameterizationVersionsComboBox
    protected ULCComboBox resultConfigurationNamesComboBox
    protected ULCComboBox resultConfigurationVersionsComboBox
    protected ULCComboBox outputStrategy
    protected ULCTextField resultLocation
    protected ULCButton changeLocationButton
    protected ULCSpinner beginOfFirstPeriod
    protected ULCCheckBox userDefinedRandomSeed
    protected ULCTextField randomSeed
    protected ULCTextField numberOfIterations

    protected RuntimeParameterPane runtimeParameterPane
    protected PostSimulationCalculationPane postSimulationCalculationPane

    private final userPreferences = UserPreferencesFactory.getUserPreferences()

    SimulationSettingsPaneModel model
    final private Dimension dimension = new Dimension(100, 20)

    public SimulationSettingsPane() {
    }

    public SimulationSettingsPane(SimulationSettingsPaneModel model) {
        this.model = model
        initComponents()
        layoutComponents()
        attachListeners()
    }

    protected void attachListeners() {
        parametrizationNamesComboBox.addActionListener(model.reloadParameterizationListModelAction)
        parameterizationVersionsComboBox.addActionListener([actionPerformed: {e ->
            ParameterizationVersionsListModel listModel = parameterizationVersionsComboBox.model
            if (listModel.isValid(listModel.selectedItem)) {
                parameterizationVersionsComboBox.foreground = Color.black
            } else {
                parameterizationVersionsComboBox.foreground = Color.orange
            }
            model.notifyConfigurationChanged()
        }] as IActionListener)
        resultConfigurationNamesComboBox.addActionListener(model.reloadResultConfigurationListModelAction)

        resultConfigurationVersionsComboBox.addActionListener([actionPerformed: {e ->
            model.notifyConfigurationChanged()
        }] as IActionListener)

        Closure outputStrategyAction = {boolean resultLocationRequired ->
            resultLocation.enabled = resultLocationRequired
            changeLocationButton.enabled = resultLocationRequired
            resultLocation.text = model.getResultLocation()
            model.notifyConfigurationChanged()
        }
        outputStrategy.addActionListener(model.getChangeOutputStrategyAction(outputStrategyAction))

        simulationName.addValueChangedListener([valueChanged: {e -> model.simulationName = simulationName.text }] as IValueChangedListener)
        comment.addValueChangedListener([valueChanged: {e -> model.comment = comment.text }] as IValueChangedListener)
    }

    protected void initComponents() {
        simulationName = new ULCTextField(model.simulationName)
        simulationName.setPreferredSize(new Dimension(150, 20))
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

        parameterizationVersionsComboBox.setMinimumSize(dimension)
        resultConfigurationNamesComboBox = new ULCComboBox(model.resultConfigurationNames)
        resultConfigurationVersionsComboBox = new ULCComboBox(model.resultConfigurationVersions)
        resultConfigurationVersionsComboBox.setMinimumSize(dimension)

        outputStrategy = new ULCComboBox(model.outputStrategies)
        outputStrategy.name = "outputStrategy"

        resultLocation = new ULCTextField(model.resultLocation)
        resultLocation.name = "resultLocation"
        resultLocation.setPreferredSize(new Dimension(150, 20))
        resultLocation.enabled = false
        Closure resultLocationAction = {
            resultLocation.text = model.getResultLocation()
        }
        changeLocationButton = new ULCButton(model.getChangeResultLocationAction(resultLocationAction))
        changeLocationButton.name = "changeLocation"
        changeLocationButton.setPreferredSize(dimension)

        if (model.requiresStartDate()) {
            beginOfFirstPeriod = new ULCSpinner(model.getBeginOfFirstPeriodSpinnerModel())
            beginOfFirstPeriod.setEditor(new ULCDateEditor(beginOfFirstPeriod, FastDateFormat.getDateInstance(FastDateFormat.SHORT, LocaleResources.getLocale()).pattern))
        }

        runtimeParameterPane = new RuntimeParameterPane(model)
        postSimulationCalculationPane = new PostSimulationCalculationPane(model.postSimulationCalculationPaneModel)

    }

    protected void layoutComponents() {
        content = boxLayout(model.getText(SIMULATION_SETTINGS_KEY)) {ULCBoxPane pane ->
            ULCBoxPane innerPane = new ULCBoxPane(3, 0)

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(SIMULATION_NAME_KEY) + ":"))
            innerPane.add(2, ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(simulationName, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))

            innerPane.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel(model.getText(SIMULATION_COMMENT_KEY) + ":"))
            innerPane.add(2, ULCBoxPane.BOX_EXPAND_EXPAND, spaceAround(comment, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(MODEL_KEY) + ":"))
            innerPane.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(modelComboBox, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))
            innerPane.add(new ULCFiller())

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(PARAMETERIZATION_KEY) + ":"))
            innerPane.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(parametrizationNamesComboBox, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))
            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(parameterizationVersionsComboBox, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(RESULT_CONFIGURATION_KEY) + ":"))
            innerPane.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(resultConfigurationNamesComboBox, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))
            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(resultConfigurationVersionsComboBox, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(OUTPUT_STRATEGY_KEY) + ":"))
            innerPane.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(outputStrategy, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))
            innerPane.add(new ULCFiller())

            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(RESULT_LOCATION_KEY) + ":"))
            innerPane.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(resultLocation, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))
            innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(changeLocationButton, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))

            initConfigProperties(innerPane)

            pane.add(ULCBoxPane.BOX_EXPAND_EXPAND, innerPane)

            if (model.requiresStartDate()) {
                innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(BEGIN_OF_FIRST_PERIOD_KEY) + ":"))
                innerPane.add(2, ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(beginOfFirstPeriod, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))
            }
        }
    }

    protected initConfigProperties(ULCBoxPane innerPane) {
        userDefinedRandomSeed = new ULCCheckBox(model.getText(USER_DEFINED_RANDOM_SEED_KEY), false)
        userDefinedRandomSeed.name = "userDefinedRandomSeed"
        userDefinedRandomSeed.setSelected(Boolean.parseBoolean(userPreferences.getDefaultValue(UserPreferences.RANDOM_SEED_USE_USER_DEFINED, "" + Boolean.FALSE)))
        userDefinedRandomSeed.addValueChangedListener([valueChanged: { ValueChangedEvent e ->
            userPreferences.putPropertyValue(UserPreferences.RANDOM_SEED_USE_USER_DEFINED, "" + userDefinedRandomSeed.isSelected())
            model.notifyConfigurationChanged()
        }] as IValueChangedListener)

        randomSeed = new ULCTextField()
        randomSeed.setPreferredSize(dimension)
        randomSeed.enabler = userDefinedRandomSeed
        randomSeed.name = "randomSeed"
        randomSeed.dataType = DataTypeFactory.getIntegerDataTypeForEdit()

        numberOfIterations = new ULCTextField()
        numberOfIterations.name = "iterations"
        numberOfIterations.dataType = DataTypeFactory.getIntegerDataTypeForEdit()

        model.randomSeedAction.randomSeed = randomSeed
        randomSeed.addValueChangedListener(model.randomSeedAction)
        model.randomSeedAction.setUserDefinedRandomSeedCheckBox(userDefinedRandomSeed)

        numberOfIterations.addKeyListener([keyTyped: {e ->
            def value = numberOfIterations.value
            if (value && (value instanceof Number) && value < Integer.MAX_VALUE)
                model.numberOfIterations = value
            else if (value) {
                new I18NAlert("IterationNumberNotValid").show()
                numberOfIterations.setValue(model.numberOfIterations)
            } else {
                model.numberOfIterations = null
            }
        }] as IKeyListener)

        randomSeed.addKeyListener([keyTyped: {e ->
            model.notifyConfigurationChanged()
        }] as IKeyListener)

        simulationName.addKeyListener([keyTyped: {e ->
            model.notifyConfigurationChanged()
        }] as IKeyListener)

        innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(RANDOM_SEED_KEY) + ":"))
        innerPane.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(userDefinedRandomSeed, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))
        innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(randomSeed, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))

        innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(model.getText(ITERATIONS_KEY) + ":"))
        innerPane.add(2, ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(numberOfIterations, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))
    }

    /**
     * disables the entire UI (for example during a simulation)
     */
    void disable() {
        changeLocationButton.enabled = false
        comment.enabled = false
        modelComboBox.enabled = false
        outputStrategy.enabled = false
        parametrizationNamesComboBox.enabled = false
        parameterizationVersionsComboBox.enabled = false
        resultConfigurationNamesComboBox.enabled = false
        resultConfigurationVersionsComboBox.enabled = false
        resultLocation.enabled = false
        simulationName.enabled = false
        disableConfigProperties()
    }

    protected void disableConfigProperties() {
        beginOfFirstPeriod?.enabled = false
        numberOfIterations.enabled = false
        randomSeed.enabled = false
        userDefinedRandomSeed.enabled = false
    }

    /**
     * enables the UI again
     */
    void enable() {
        boolean fileMode = model.getOutputStrategy() instanceof FileOutput
        changeLocationButton.enabled = fileMode
        comment.enabled = true
        modelComboBox.enabled = false
        outputStrategy.enabled = true
        parametrizationNamesComboBox.enabled = true
        parameterizationVersionsComboBox.enabled = true
        resultConfigurationNamesComboBox.enabled = true
        resultConfigurationVersionsComboBox.enabled = true
        resultLocation.enabled = fileMode
        simulationName.enabled = true
        enableConfigProperties()
    }

    protected void enableConfigProperties() {
        beginOfFirstPeriod?.enabled = true
        numberOfIterations.enabled = true
        randomSeed.enabled = userDefinedRandomSeed.isSelected()
        userDefinedRandomSeed.enabled = true
    }

    ULCContainer getContent() {
        ULCTabbedPane tabbedPane = new ULCTabbedPane()
        tabbedPane.addTab("Settings", content)
        if (runtimeParameterPane.model.hasRuntimeParameters()) {
            tabbedPane.addTab("Runtime parameters", runtimeParameterPane.content)
        }
        tabbedPane.addTab("Post simulation calculations", new ULCScrollPane(postSimulationCalculationPane.content))
        return tabbedPane
    }

}

class VersionComboBoxRenderer extends DefaultComboBoxCellRenderer {

    IRendererComponent getComboBoxCellRendererComponent(ULCComboBox comboBox, Object value, boolean isSelected, int row) {
        ULCLabel component = super.getComboBoxCellRendererComponent(comboBox, value, isSelected, row)
        ParameterizationVersionsListModel model = comboBox.model
        if (value != null) {
            if (!model.isValid(value)) {
                component.font = component.font.deriveFont(Font.ITALIC + Font.BOLD)
                component.foreground = Color.orange
            } else {
                component.font = component.font.deriveFont(Font.PLAIN + Font.BOLD)
                component.foreground = Color.black
            }
        }
        return component;
    }

}


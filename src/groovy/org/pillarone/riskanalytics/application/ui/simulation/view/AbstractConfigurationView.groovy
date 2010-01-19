package org.pillarone.riskanalytics.application.ui.simulation.view

import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCCheckBox
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCFiller
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCPollingTimer
import com.ulcjava.base.application.ULCProgressBar
import com.ulcjava.base.application.ULCSpinner
import com.ulcjava.base.application.ULCSpinner.ULCDateEditor
import com.ulcjava.base.application.ULCSpinnerDateModel
import com.ulcjava.base.application.ULCTabbedPane
import com.ulcjava.base.application.ULCTextArea
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.datatype.IDataType
import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.shared.UlcEventConstants
import java.text.SimpleDateFormat
import org.apache.commons.lang.time.FastDateFormat
import org.codehaus.groovy.runtime.TimeCategory
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.application.ui.result.view.ItemsComboBoxModel
import org.pillarone.riskanalytics.application.ui.simulation.model.AbstractConfigurationModel
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationConfigurationListener
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

abstract class AbstractConfigurationView implements ISimulationListener, ISimulationConfigurationListener {

    ULCBoxPane content
    ULCTextField simulationName
    ULCTextArea comment
    ULCComboBox modelComboBox
    ULCComboBox parametrizationNamesComboBox
    ULCComboBox parameterizationVersionsComboBox
    ULCComboBox templateNamesComboBox
    ULCComboBox templateVersionsComboBox
    ULCCheckBox useUserDefinedSeed
    ULCTextField randomSeed
    ULCSpinner startDate

    ULCProgressBar progressBar
    ULCPollingTimer timer
    ULCLabel startTimeLabel
    ULCLabel startTimeInfo
    ULCLabel estimatedEndTimeLabel
    ULCLabel estimatedEndTimeInfo
    ULCLabel remainingTimeLabel
    ULCLabel remainingTimeInfo

    ULCButton run, stop, openResults, changeLocation
    ULCComboBox outputStrategy
    ULCTextField resultLocation
    ULCButton addToBatchButton
    ULCComboBox batchesComboBox
    ULCLabel actionMessage
    ULCTabbedPane buttonsTabbedPane


    int pollRepeatCount = 0
    String selectedDirectory

    AbstractConfigurationModel model

    ULCAlert alert

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm")
    protected Model modelInstance

    public AbstractConfigurationView(AbstractConfigurationModel model) {
        this.model = model
        modelInstance = (Model) model.selectedModel.newInstance()
        initComponents()
        layoutComponents()
        attachListeners()
        model.notifySimulationConfigurationChanged()
    }

    /**
     * Creates all view components which are shared by all subclasses of AbstractConfigurationView.
     * This method also calls an abstract method which allows subclasses to create additional components.
     *
     * Once a simulation has been startet a PolllingTimer is used to regularly update the UI.
     */
    void initComponents() {
        content = new ULCBoxPane(1, 2)

        simulationName = new ULCTextField(model.simulationName)
        simulationName.setPreferredSize(new Dimension(150, 20))
        simulationName.name = "simulationName"

        comment = new ULCTextArea(4, 20)
        comment.lineWrap = true
        comment.wrapStyleWord = true


        modelComboBox = new ULCComboBox(model.availableModels)
        modelComboBox.name = "modelComboBox"
        ClientContext.setModelUpdateMode(modelComboBox.model, UlcEventConstants.ASYNCHRONOUS_MODE)

        parametrizationNamesComboBox = new ULCComboBox(model.availableParameterizationNamesForModel)
        parametrizationNamesComboBox.name = "parametrizationNamesComboBox"
        ClientContext.setModelUpdateMode(parametrizationNamesComboBox.model, UlcEventConstants.ASYNCHRONOUS_MODE)

        parameterizationVersionsComboBox = new ULCComboBox(model.availableParameterizationVersionsForModel)
        parameterizationVersionsComboBox.name = "parameterizationVersionsComboBox"

        useUserDefinedSeed = new ULCCheckBox()
        randomSeed = new ULCTextField("")
        randomSeed.enabled = false
        IDataType dataType = new ULCNumberDataType(ClientContext.locale)
        dataType.integer = true
        randomSeed.dataType = dataType

        if (modelInstance.requiresStartDate()) {
            DateTime firstInYear = new DateTime().withDayOfYear(1)
            firstInYear = new DateTime(firstInYear.getYear(), firstInYear.getMonthOfYear(), firstInYear.getDayOfMonth(), 0, 0, 0, 0)
            ULCSpinnerDateModel dateSpinnerModel = new ULCSpinnerDateModel(firstInYear.toDate(), null, null, Calendar.DAY_OF_MONTH)
            startDate = new ULCSpinner(dateSpinnerModel)
            startDate.setEditor(new ULCDateEditor(startDate, FastDateFormat.getDateInstance(FastDateFormat.SHORT, UIUtils.getClientLocale()).pattern))
            model.setBeginOfFirstPeriod(startDate.getValue())
        }


        templateNamesComboBox = new ULCComboBox(model.availableResultConfigurationNamesForModel)
        templateNamesComboBox.name = 'templateNamesComboBox'
        ClientContext.setModelUpdateMode(templateNamesComboBox.model, UlcEventConstants.ASYNCHRONOUS_MODE)

        templateVersionsComboBox = new ULCComboBox(model.availableResultConfigurationVersionsForModel)
        templateVersionsComboBox.name = "templateVersionsComboBox"

        outputStrategy = new ULCComboBox(model.outputStrategyComboBoxModel)
        outputStrategy.setSelectedItem(model.outputStrategyComboBoxModel.getElementAt(0))
        outputStrategy.name = "outputStrategyComboBox"

        resultLocation = new ULCTextField(model.resultLocation)
        resultLocation.setPreferredSize(new Dimension(150, 20))
        changeLocation = new ULCButton(model.changeResultLocationAction)


        startTimeLabel = new ULCLabel(getText("StartTime") + ":")
        startTimeInfo = new ULCLabel("-")
        remainingTimeLabel = new ULCLabel(getText("RemainingTime") + ":")
        remainingTimeInfo = new ULCLabel("-")
        estimatedEndTimeLabel = new ULCLabel(getText("EstimatedEndTime") + ":")
        estimatedEndTimeInfo = new ULCLabel("-")
//        applicationUserLabel = new ULCLabel(getText("User") + ":")
//        applicationUserInfo = new ULCLabel(UserManagement.getCurrentUser()?.getUsername())

        initCustomComponents()

        progressBar = new ULCProgressBar(ULCProgressBar.HORIZONTAL, 0, 100)
        progressBar.name = 'progressBar'
        progressBar.setStringPainted(true)

        timer = new ULCPollingTimer(1000, null)
        timer.repeats = true

        Closure performAction = {event ->
            pollRepeatCount++
            model.syncIterationCount()
            updateUIState()

            if (!model.simulationRunning()) {
                timer.stop()
            }
        }
        timer.addActionListener([actionPerformed: performAction] as IActionListener)

        actionMessage = new ULCLabel()
    }

    abstract protected void initCustomComponents()

    private ULCBoxPane boxLayout(String title, Closure body) {
        ULCBoxPane result = new ULCBoxPane()
        result.border = BorderFactory.createTitledBorder(" $title ")
        ULCBoxPane inner = new ULCBoxPane()
        body(inner)
        result.add ULCBoxPane.BOX_EXPAND_EXPAND, spaceAround(inner, 0, 5, 5, 5)
        return result
    }

    protected ULCBoxPane spaceAround(ULCComponent comp, int top, int left, int bottom, int right) {
        ULCBoxPane deco = new ULCBoxPane()
        deco.border = BorderFactory.createEmptyBorder(top, left, bottom, right)
        deco.add(ULCBoxPane.BOX_EXPAND_EXPAND, comp)
        return deco
    }

    /**
     * Layouts all view components which are shared by all subclasses of AbstractConfigurationView.
     * This method also calls an abstract method which allows subclasses to layout their additional components.
     */
    void layoutComponents() {
        ULCBoxPane parameterSection = boxLayout(getText("SimulationSettings") + ":") {ULCBoxPane box ->
            ULCBoxPane content = new ULCBoxPane(3, 7)

            content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("Name") + ":"))
            content.add(2, ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(simulationName, 5, 10, 0, 0))

            content.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel(getText("Comment") + ":"))
            content.add(2, ULCBoxPane.BOX_EXPAND_EXPAND, spaceAround(comment, 5, 10, 0, 0))

            content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("Model") + ":"))
            content.add(2, ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(modelComboBox, 5, 10, 0, 0))

            content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("Parameter") + ":"))
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(parametrizationNamesComboBox, 5, 10, 0, 0))
            content.add(ULCBoxPane.BOX_RIGHT_CENTER, spaceAround(parameterizationVersionsComboBox, 5, 10, 0, 0))

            content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("ResultTemplate") + ":"))
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(templateNamesComboBox, 5, 10, 0, 0))
            content.add(ULCBoxPane.BOX_RIGHT_CENTER, spaceAround(templateVersionsComboBox, 5, 10, 0, 0))

            content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("OutputStrategy") + ":"))
            content.add(2, ULCBoxPane.BOX_LEFT_CENTER, spaceAround(outputStrategy, 5, 10, 0, 0))

            content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("ResultLocation") + ":"))
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(resultLocation, 5, 10, 0, 0))
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(changeLocation, 5, 10, 0, 0))



            layoutCustomComponents(content)

            if (modelInstance.requiresStartDate()) {
                ULCLabel beginLabel = new ULCLabel(getText("BeginOfPeriods") + ":")
                beginLabel.setLabelFor(startDate)
                beginLabel.minimumSize = new Dimension(150, 30)
                content.add(ULCBoxPane.BOX_LEFT_CENTER, beginLabel)
                content.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(startDate, 5, 10, 0, 0))
                content.add(ULCFiller.createHorizontalGlue())
            }


            box.add ULCBoxPane.BOX_EXPAND_EXPAND, content
        }


        ULCBoxPane holder = new ULCBoxPane(columns: 1, rows: 3)
        holder.maximumSize = new Dimension(500, 600)

        holder.add(ULCBoxPane.BOX_EXPAND_EXPAND, parameterSection)
        holder.add(ULCBoxPane.BOX_EXPAND_EXPAND, spaceAround(getButtonsTabbedPane(), 5, 2, 5, 0))
        content.add(ULCBoxPane.BOX_LEFT_TOP, holder)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }

    abstract protected void layoutCustomComponents(ULCBoxPane content)

    ULCBoxPane getSimulationBottomPane() {
        run = new ULCButton(model.runAction)
        stop = new ULCButton(model.stopAction)
        openResults = new ULCButton(model.openResultAction)

        run.name = "run"
        stop.name = "stop"
        openResults.name = "open"
        run.repaint()
        Dimension dimension = new Dimension(140, 20)
        run.setPreferredSize(dimension)
        stop.setPreferredSize(dimension)
        openResults.setPreferredSize(dimension)

        ULCBoxPane buttonPane = new ULCBoxPane(columns: 3, rows: 1)
        buttonPane.add(spaceAround(run, 10, 8, 10, 8))
        buttonPane.add(spaceAround(stop, 10, 8, 10, 8))
        buttonPane.add(spaceAround(openResults, 10, 8, 10, 8))
        ULCBoxPane simulationInfoSection = getSimulationInfoSectionPane()
        if (simulationInfoSection != null)
            buttonPane.add(3, ULCBoxPane.BOX_EXPAND_EXPAND, getSimulationInfoSectionPane())


        return buttonPane
    }

    public ULCBoxPane getBatchBottomPane() {
        Collection batches = BatchRun.findAll()
        ItemsComboBoxModel<BatchRun> batchesComboBoxModel = new ItemsComboBoxModel<BatchRun>(batches?.toList())
        batchesComboBox = new ULCComboBox(batchesComboBoxModel)
        batchesComboBox.setEditable(true)
        model.itemsComboBoxModel = batchesComboBoxModel

        addToBatchButton = new ULCButton(model.addToBatchAction)
        addToBatchButton.name = "addToBatch"
        Dimension dimension = new Dimension(140, 20)
        addToBatchButton.setPreferredSize(dimension)

        ULCBoxPane buttonPane = new ULCBoxPane(columns: 3, rows: 1)
        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(batchesComboBox, 10, 10, 10, 8))
        buttonPane.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(addToBatchButton, 10, 8, 10, 8))
        buttonPane.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
        buttonPane.add(3, ULCBoxPane.BOX_EXPAND_EXPAND, spaceAround(actionMessage, 15, 8, 10, 8))
        buttonPane.add(3, ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
        return buttonPane
    }


    ULCTabbedPane getButtonsTabbedPane() {
        buttonsTabbedPane = new ULCTabbedPane(ULCTabbedPane.TOP)
        buttonsTabbedPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0))
        buttonsTabbedPane.addTab("Simulation", getSimulationBottomPane());
        buttonsTabbedPane.addTab("Batch", getBatchBottomPane());
        return buttonsTabbedPane
    }

    ULCBoxPane getSimulationInfoSectionPane() {
        ULCBoxPane simulationInfoSection = boxLayout(getText("SimulationProgress") + ":") {ULCBoxPane box ->
            ULCBoxPane content = new ULCBoxPane(4, 3, 5, 5)
            content.add(4, ULCBoxPane.BOX_EXPAND_CENTER, progressBar)
            content.add(ULCBoxPane.BOX_LEFT_CENTER, startTimeLabel)
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, startTimeInfo)
            content.add(ULCBoxPane.BOX_LEFT_CENTER, estimatedEndTimeLabel)
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, estimatedEndTimeInfo)
            content.add(ULCBoxPane.BOX_LEFT_CENTER, remainingTimeLabel)
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, remainingTimeInfo)
            content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCFiller())
            content.add(ULCBoxPane.BOX_EXPAND_CENTER, new ULCFiller())
            box.add(ULCBoxPane.BOX_EXPAND_EXPAND, content)
        }
        return simulationInfoSection
    }

    void attachListeners() {

        model.addSimulationListener this
        model.addSimulationConfigurationListener this

        addCustomListeners()

        simulationName.addValueChangedListener([valueChanged: {event -> model.simulationName = event.source.text}] as IValueChangedListener)
        comment.addValueChangedListener([valueChanged: {event -> model.comment = event.source.text}] as IValueChangedListener)
        useUserDefinedSeed.addValueChangedListener([valueChanged: {event -> model.useUserDefinedSeed = useUserDefinedSeed.selected}] as IValueChangedListener)
        randomSeed.addValueChangedListener([valueChanged: {event ->
            int seed = randomSeed.value
            if (seed != null) {
                model.randomSeed = seed
            } else {
                new I18NAlert("NoInteger").show()
            }
        }] as IValueChangedListener)

        outputStrategy.addActionListener([actionPerformed: {event -> model.notifySimulationConfigurationChanged(); updateUIState(); }] as IActionListener)

        if (modelInstance.requiresStartDate()) {
            startDate.valueChanged = {event ->
                model.setBeginOfFirstPeriod(startDate.getValue())
            }
        }
    }

    abstract protected void addCustomListeners()

    public void simulationStart(Simulation simulation) {
        alert = null
        displaySimulationStart(simulation)
        timer.start()
    }

    public void simulationEnd(Simulation simulation, Model model) {
    }

    private void displaySimulationStart(Simulation simulation) {
        remainingTimeLabel.text = getText("RemainingTime") + ":"
        remainingTimeInfo.text = ""
        updateUIState()
    }

    public void simulationConfigurationChanged() {
        updateUIState()
    }

    protected def updateUIState() {

        resultLocation.text = model.resultLocation
        simulationName.text = model.getSimulationName()

        stop.enabled = model.isSimulationStopEnabled()
        resultLocation.enabled = model.isConfigurationChangeable() && model.isResultLocationChangeable()
        modelComboBox.enabled = model.isConfigurationChangeable() && model.modelChangeable
        simulationName.enabled = model.isConfigurationChangeable()
        comment.enabled = model.isConfigurationChangeable()
        parametrizationNamesComboBox.enabled = model.isConfigurationChangeable()
        parameterizationVersionsComboBox.enabled = model.isConfigurationChangeable()
        templateNamesComboBox.enabled = model.isConfigurationChangeable()
        templateVersionsComboBox.enabled = model.isConfigurationChangeable()
        outputStrategy.enabled = model.isConfigurationChangeable()
        if (modelInstance.requiresStartDate()) {
            startDate.enabled = model.isConfigurationChangeable()
        }
        randomSeed.enabled = model.useUserDefinedSeed

        progressBar.enabled = model.isSimulationStopEnabled()
        progressBar.setValue(model.simulationProgress)
        String simulationMessage = ""
        String simulationMessageKey = model.simulationMessage
        if (simulationMessageKey) {
            simulationMessage = getText(simulationMessageKey)
        } else if (model.postSimulationCalculationsRunning()) {
            simulationMessage = "Calculations ${model.simulationProgress} % complete"
        } else if (model.simulationRunning()) {
            simulationMessage = "Simulation ${model.simulationProgress} % complete"
        }
        progressBar.indeterminate = model.isCurrentTaskEndIndeterminate()
        progressBar.setString(simulationMessage)

        if (model.currentSimulation != null) {
            if (model.simulationRunning()) {
                Date start = model.getSimulationStart()
                Date endTime = model.getEstimatedSimulationEnd()
                if (start != null)
                    startTimeInfo.text = dateFormat.format(start)
                if (start != null && endTime != null) {
                    use(TimeCategory) {
                        def duration = endTime - new Date()
                        remainingTimeInfo.text = "$duration.hours h $duration.minutes m $duration.seconds s"
                    }
                    estimatedEndTimeInfo.text = dateFormat.format(endTime)
                }
            } else {
                if (model.getSimulationEnd()) {
                    remainingTimeLabel.text = getText("SimulatedIterations") + ":"
                    remainingTimeInfo.text = "${model.currentIteration}"
                    estimatedEndTimeLabel.text = getText("EndTime") + ":"
                    estimatedEndTimeInfo.text = dateFormat.format(model.getSimulationEnd())
                }

            }
        }
        if (model.simulationException != null) {
            if (alert == null) {
                String exceptionMessage = model.simulationException.message
                if (exceptionMessage == null) {
                    exceptionMessage = model.simulationException.class.name
                }
                List words = exceptionMessage.split(" ") as List
                StringBuffer text = new StringBuffer()
                int lineLength = 0
                for (String s in words) {
                    if (lineLength + s.length() > 70) {
                        text << "\n"
                        lineLength = 0
                    }
                    text << s + " "
                    lineLength += (s.length() + 1)
                }
                alert = new ULCAlert(UlcUtilities.getWindowAncestor(content), "Error occured during simulation", text.toString(), "Ok")
                alert.show()
            }

        }
        updateCustomUIState()
    }

    abstract protected void updateCustomUIState()

    public void batchAdded(String message, boolean error) {
        actionMessage.setForeground(error ? Color.red : Color.blue)
        actionMessage.setText(message)
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("SimulationConfigurationView." + key);
    }

}

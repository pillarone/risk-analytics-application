package org.pillarone.riskanalytics.application.ui.simulation.view

import com.ulcjava.base.application.ULCSpinner.ULCDateEditor
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.shared.UlcEventConstants
import org.apache.commons.lang.time.FastDateFormat
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.simulation.model.AbstractConfigurationModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import com.ulcjava.base.application.*
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.getText

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ConfigurationSettingView {

    ULCBoxPane content
    ULCTextField simulationName
    ULCTextArea comment
    ULCComboBox modelComboBox
    ULCComboBox parametrizationNamesComboBox
    ULCComboBox parameterizationVersionsComboBox
    ULCComboBox templateNamesComboBox
    ULCComboBox templateVersionsComboBox
    ULCComboBox outputStrategy
    ULCTextField resultLocation
    ULCButton changeLocation

    ULCSpinner startDate

    protected Model modelInstance

    AbstractConfigurationModel model


    public ConfigurationSettingView(AbstractConfigurationModel configurationModel) {
        this.model = configurationModel;
        modelInstance = (Model) model.selectedModel.newInstance()
        initComponents()
    }

    private void initComponents() {
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

        outputStrategy = new ULCComboBox(model.outputStrategyComboBoxModel)
        outputStrategy.setSelectedItem(model.outputStrategyComboBoxModel.getElementAt(0))
        outputStrategy.name = "outputStrategyComboBox"

        resultLocation = new ULCTextField(model.resultLocation)
        resultLocation.setPreferredSize(new Dimension(150, 20))
        changeLocation = new ULCButton(model.changeResultLocationAction)

        templateNamesComboBox = new ULCComboBox(model.availableResultConfigurationNamesForModel)
        templateNamesComboBox.name = 'templateNamesComboBox'
        ClientContext.setModelUpdateMode(templateNamesComboBox.model, UlcEventConstants.ASYNCHRONOUS_MODE)

        templateVersionsComboBox = new ULCComboBox(model.availableResultConfigurationVersionsForModel)
        templateVersionsComboBox.name = "templateVersionsComboBox"


        if (modelInstance.requiresStartDate()) {
            DateTime firstInYear = new DateTime().withDayOfYear(1)
            firstInYear = new DateTime(firstInYear.getYear(), firstInYear.getMonthOfYear(), firstInYear.getDayOfMonth(), 0, 0, 0, 0)
            ULCSpinnerDateModel dateSpinnerModel = new ULCSpinnerDateModel(firstInYear.toDate(), null, null, Calendar.DAY_OF_MONTH)
            startDate = new ULCSpinner(dateSpinnerModel)
            startDate.setEditor(new ULCDateEditor(startDate, FastDateFormat.getDateInstance(FastDateFormat.SHORT, UIUtils.getClientLocale()).pattern))
            model.setBeginOfFirstPeriod(startDate.getValue())
        }


    }


    protected void attachListeners() {

        simulationName.addValueChangedListener([valueChanged: {event -> model.simulationName = event.source.text}] as IValueChangedListener)
        comment.addValueChangedListener([valueChanged: {event -> model.comment = event.source.text}] as IValueChangedListener)

        outputStrategy.addActionListener([actionPerformed: {event -> model.notifySimulationConfigurationChanged(); updateUIState(); }] as IActionListener)

        if (modelInstance.requiresStartDate()) {
            startDate.valueChanged = {event ->
                model.setBeginOfFirstPeriod(startDate.getValue())
            }
        }


    }

    protected void addParameterSection(ULCBoxPane parameterSection) {
        parameterSection.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText(this.class, "Name") + ":"))
        parameterSection.add(2, ULCBoxPane.BOX_EXPAND_CENTER, AbstractConfigurationView.spaceAround(simulationName, 5, 10, 0, 0))

        parameterSection.add(ULCBoxPane.BOX_LEFT_TOP, new ULCLabel(getText(this.class, "Comment") + ":"))
        parameterSection.add(2, ULCBoxPane.BOX_EXPAND_EXPAND, AbstractConfigurationView.spaceAround(comment, 5, 10, 0, 0))

        parameterSection.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText(this.class, "Model") + ":"))
        parameterSection.add(2, ULCBoxPane.BOX_EXPAND_CENTER, AbstractConfigurationView.spaceAround(modelComboBox, 5, 10, 0, 0))

        parameterSection.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText(this.class, "Parameter") + ":"))
        parameterSection.add(ULCBoxPane.BOX_EXPAND_CENTER, AbstractConfigurationView.spaceAround(parametrizationNamesComboBox, 5, 10, 0, 0))
        parameterSection.add(ULCBoxPane.BOX_EXPAND_CENTER, AbstractConfigurationView.spaceAround(parameterizationVersionsComboBox, 5, 10, 0, 0))

        parameterSection.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText(this.class, "ResultTemplate") + ":"))
        parameterSection.add(ULCBoxPane.BOX_EXPAND_CENTER, AbstractConfigurationView.spaceAround(templateNamesComboBox, 5, 10, 0, 0))
        parameterSection.add(ULCBoxPane.BOX_EXPAND_CENTER, AbstractConfigurationView.spaceAround(templateVersionsComboBox, 5, 10, 0, 0))

        parameterSection.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText(this.class, "OutputStrategy") + ":"))
        parameterSection.add(2, ULCBoxPane.BOX_LEFT_CENTER, AbstractConfigurationView.spaceAround(outputStrategy, 5, 10, 0, 0))

        parameterSection.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText(this.class, "ResultLocation") + ":"))
        parameterSection.add(ULCBoxPane.BOX_EXPAND_CENTER, AbstractConfigurationView.spaceAround(resultLocation, 5, 10, 0, 0))
        parameterSection.add(ULCBoxPane.BOX_EXPAND_CENTER, AbstractConfigurationView.spaceAround(changeLocation, 5, 10, 0, 0))
    }

    protected void addPeriodSection(ULCBoxPane boxPanne) {
        if (modelInstance.requiresStartDate()) {
            ULCLabel beginLabel = new ULCLabel(UIUtils.getText(this.class, "BeginOfPeriods") + ":")
            beginLabel.setLabelFor(startDate)
            beginLabel.minimumSize = new Dimension(150, 30)
            boxPanne.add(ULCBoxPane.BOX_LEFT_CENTER, beginLabel)
            boxPanne.add(ULCBoxPane.BOX_EXPAND_CENTER, AbstractConfigurationView.spaceAround(startDate, 5, 10, 0, 0))
            boxPanne.add(ULCFiller.createHorizontalGlue())
        }

    }

    protected def updateUIState() {
        resultLocation.text = model.resultLocation
        simulationName.text = model.getSimulationName()
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


    }
}

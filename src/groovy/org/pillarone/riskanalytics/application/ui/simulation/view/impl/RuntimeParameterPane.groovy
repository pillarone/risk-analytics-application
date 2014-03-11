package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCSpinner.ULCDateEditor
import com.ulcjava.base.application.event.IValueChangedListener
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.RuntimeParameterCollector.RuntimeParameterDescriptor
import com.ulcjava.base.application.*

import java.beans.PropertyChangeEvent
import java.beans.PropertyChangeListener

import static com.ulcjava.base.shared.IDefaults.BOX_EXPAND_CENTER
import static com.ulcjava.base.shared.IDefaults.BOX_EXPAND_EXPAND
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.spaceAround
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.base.model.EnumComboBoxModel
import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory
import org.pillarone.riskanalytics.core.components.ResourceHolder
import org.pillarone.riskanalytics.core.ResourceDAO
import org.pillarone.riskanalytics.core.simulation.item.parameter.ResourceParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber

class RuntimeParameterPane {

    ULCBoxPane content

    RuntimeParameterPaneModel model
    SimulationSettingsPaneModel simulationSettingsPaneModel
    UserPreferences userPreferences

    RuntimeParameterPane(SimulationSettingsPaneModel simulationSettingsPaneModel) {
        this.model = simulationSettingsPaneModel.parameterPaneModel
        this.simulationSettingsPaneModel = simulationSettingsPaneModel
        userPreferences = UserPreferencesFactory.userPreferences
        initComponents()
    }

    protected void initComponents() {
        content = new ULCBoxPane(2, 0)
        content.border = BorderFactory.createTitledBorder("Runtime parameters")
        for (RuntimeParameterDescriptor descriptor in model.runtimeParameters) {
            addParameter(descriptor, descriptor.value)
        }
        content.add(BOX_EXPAND_EXPAND, ULCFiller.createGlue())
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, String defaultValue) {
        addLabel(descriptor.displayName)

        ULCTextField textField = new ULCTextField((String) getValueFromPrefs(defaultValue, descriptor, { it }))
        textField.name = descriptor.propertyName
        textField.addValueChangedListener([valueChanged: { evt ->
            descriptor.value = textField.text
            putValueInPrefs(descriptor, descriptor.value as String)
            simulationSettingsPaneModel.notifyConfigurationChanged()
        }] as IValueChangedListener)
        descriptor.addPropertyChangeListener({ PropertyChangeEvent event ->
            textField.value = event.newValue
        } as PropertyChangeListener)
        content.add(BOX_EXPAND_CENTER, textField)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, Integer defaultValue) {
        addLabel(descriptor.displayName)

        ULCTextField textField = new ULCTextField()
        textField.name = descriptor.propertyName
        textField.dataType = DataTypeFactory.integerDataType
        textField.value = (Integer) getValueFromPrefs(defaultValue, descriptor, { Integer.parseInt(it) })
        textField.addValueChangedListener([valueChanged: { evt ->
            descriptor.value = textField.value
            putValueInPrefs(descriptor, "" + descriptor.value)
            simulationSettingsPaneModel.notifyConfigurationChanged()
        }] as IValueChangedListener)
        descriptor.addPropertyChangeListener({ PropertyChangeEvent event ->
            textField.value = event.newValue
        } as PropertyChangeListener)
        content.add(BOX_EXPAND_CENTER, textField)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, Double defaultValue) {
        addLabel(descriptor.displayName)

        ULCTextField textField = new ULCTextField()
        textField.name = descriptor.propertyName
        textField.dataType = DataTypeFactory.doubleDataType
        textField.value = (Double) getValueFromPrefs(defaultValue, descriptor, { Double.parseDouble(it) })
        textField.addValueChangedListener([valueChanged: { evt ->
            descriptor.value = textField.value
            putValueInPrefs(descriptor, "" + descriptor.value)
            simulationSettingsPaneModel.notifyConfigurationChanged()
        }] as IValueChangedListener)
        descriptor.addPropertyChangeListener({ PropertyChangeEvent event ->
            textField.value = event.newValue
        } as PropertyChangeListener)
        content.add(BOX_EXPAND_CENTER, textField)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, Boolean defaultValue) {
        addLabel(descriptor.displayName)

        ULCCheckBox checkBox = new ULCCheckBox()
        checkBox.name = descriptor.propertyName
        checkBox.selected = (Boolean) getValueFromPrefs(defaultValue, descriptor, { Boolean.parseBoolean(it) })
        checkBox.addValueChangedListener([valueChanged: { evt ->
            descriptor.value = checkBox.selected
            putValueInPrefs(descriptor, checkBox.selected.toString())
            simulationSettingsPaneModel.notifyConfigurationChanged()
        }] as IValueChangedListener)
        descriptor.addPropertyChangeListener({ PropertyChangeEvent event ->
            checkBox.selected = event.newValue
        } as PropertyChangeListener)
        content.add(BOX_EXPAND_CENTER, checkBox)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, Enum defaultValue) {
        addLabel(descriptor.displayName)

        Object[] values = defaultValue.values()
        EnumComboBoxModel comboBoxModel = new EnumComboBoxModel(values, getValueFromPrefs(defaultValue, descriptor, {
            defaultValue.valueOf(it)
        }), true)
        ULCComboBox comboBox = new ULCComboBox(comboBoxModel)
        comboBox.name = descriptor.propertyName
        comboBox.addActionListener([actionPerformed: { evt ->
            descriptor.value = comboBoxModel.selectedEnum
            putValueInPrefs(descriptor, comboBoxModel.selectedEnum.name())
            simulationSettingsPaneModel.notifyConfigurationChanged()
        }] as IActionListener)

        descriptor.addPropertyChangeListener({ PropertyChangeEvent event ->
            comboBoxModel.selectedEnum = event.newValue
        } as PropertyChangeListener)

        content.add(BOX_EXPAND_CENTER, comboBox)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, ResourceHolder defaultValue) {
        addLabel(descriptor.displayName)

        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel(ResourceDAO.findAllByResourceClassName(defaultValue.resourceClass.name).collect {
            new ResourceParameterHolder.NameVersionPair(it.name, it.itemVersion).toString()
        })
        ULCComboBox comboBox = new ULCComboBox(comboBoxModel)
        comboBox.name = descriptor.propertyName

        comboBoxModel.selectedItem = getValueFromPrefs(comboBoxModel.selectedItem?.toString(), descriptor, { it })
        if (comboBoxModel.selectedItem != null) {
            def pair = ResourceParameterHolder.NameVersionPair.parse(comboBoxModel.selectedItem.toString())
            descriptor.value = new ResourceHolder(defaultValue.resourceClass, pair.name, new VersionNumber(pair.version))
        }
        comboBox.addActionListener([actionPerformed: { evt ->
            def selectedItem = comboBox.selectedItem.toString()
            descriptor.value = new ResourceHolder(defaultValue.resourceClass, selectedItem.substring(0, selectedItem.lastIndexOf(' ')), new VersionNumber(selectedItem.substring(selectedItem.lastIndexOf(' ') + 2)))
            putValueInPrefs(descriptor, comboBoxModel.selectedItem.toString())
            simulationSettingsPaneModel.notifyConfigurationChanged()
        }] as IActionListener)
        descriptor.addPropertyChangeListener({ PropertyChangeEvent event ->
            ResourceHolder resourceHolder = event.newValue as ResourceHolder
            comboBoxModel.selectedItem = resourceHolder ? new ResourceParameterHolder.NameVersionPair(resourceHolder.name, resourceHolder.version.toString()) : null
        } as PropertyChangeListener)

        content.add(BOX_EXPAND_CENTER, comboBox)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, DateTime defaultValue) {
        String format = DateFormatUtils.inputDateFormats[0]
        addLabel("${descriptor.displayName} ($format)")

        ULCSpinnerDateModel dateModel = new ULCSpinnerDateModel(getValueFromPrefs(defaultValue, descriptor, { str -> new DateTime(Long.parseLong(str)) }).toDate(), null, null, Calendar.DAY_OF_MONTH)

        ULCSpinner spinner = new ULCSpinner(dateModel)
        spinner.name = descriptor.propertyName
        spinner.editor = new ULCDateEditor(spinner, format)
        spinner.addValueChangedListener([valueChanged: { evt ->
            DateTime time = new DateTime(spinner.value)
            descriptor.value = time
            putValueInPrefs(descriptor, "" + time.millis)
            simulationSettingsPaneModel.notifyConfigurationChanged()
        }] as IValueChangedListener)
        descriptor.addPropertyChangeListener({ PropertyChangeEvent event ->
            DateTime value = event.newValue as DateTime
            spinner.value = value?.toDate()
        } as PropertyChangeListener)
        content.add(BOX_EXPAND_CENTER, spinner)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, def defaultValue) {
        throw new IllegalArgumentException("Unsupported runtime parameter type: ${defaultValue?.class?.name}")
    }

    def putValueInPrefs(RuntimeParameterDescriptor descriptor, String valueAsString) {
        def propertyName = getUserPrefsPropertyName(descriptor)
        userPreferences.putPropertyValue(propertyName, valueAsString)
    }

    def getValueFromPrefs(def defaultValue, RuntimeParameterDescriptor descriptor, Closure stringToPrefsValue) {
        def propertyName = getUserPrefsPropertyName(descriptor)
        String userPrefValueAsString = userPreferences.getPropertyValue(propertyName)
        def value = userPrefValueAsString != null ? stringToPrefsValue.call(userPrefValueAsString) : defaultValue
        if (value != defaultValue) {
            descriptor.value = value
        }
        return value
    }

    private String getUserPrefsPropertyName(RuntimeParameterDescriptor descriptor) {
        return "runtimeParameter_" + descriptor.typeClass.simpleName + "_" + descriptor.propertyName
    }

    protected void addLabel(String label) {
        content.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(new ULCLabel(label), 0, 10, 0, 10))
    }
}

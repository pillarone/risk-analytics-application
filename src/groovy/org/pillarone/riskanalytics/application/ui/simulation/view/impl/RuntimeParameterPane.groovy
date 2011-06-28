package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCSpinner.ULCDateEditor
import com.ulcjava.base.application.event.IValueChangedListener
import org.apache.commons.lang.time.FastDateFormat
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.RuntimeParameterCollector.RuntimeParameterDescriptor
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.base.application.*
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.spaceAround
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.base.model.EnumComboBoxModel
import com.ulcjava.base.application.event.IActionListener

class RuntimeParameterPane {

    ULCBoxPane content

    RuntimeParameterPaneModel model

    RuntimeParameterPane(RuntimeParameterPaneModel model) {
        this.model = model
        initComponents()
    }

    protected void initComponents() {
        content = new ULCBoxPane(2, 0)
        content.border = BorderFactory.createTitledBorder("Runtime parameters")
        for (RuntimeParameterDescriptor descriptor in model.runtimeParameters) {
            addParameter(descriptor, descriptor.value)
        }
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, ULCFiller.createGlue())
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, String value) {
        addLabel(descriptor.displayName)

        ULCTextField textField = new ULCTextField(value)
        textField.name = descriptor.propertyName
        textField.addValueChangedListener([valueChanged: { evt ->
            descriptor.value = textField.text
        }] as IValueChangedListener)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, textField)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, Integer value) {
        addLabel(descriptor.displayName)

        ULCTextField textField = new ULCTextField()
        textField.name = descriptor.propertyName
        textField.dataType = DataTypeFactory.getIntegerDataTypeForEdit()
        textField.value = value
        textField.addValueChangedListener([valueChanged: { evt ->
            descriptor.value = textField.value
        }] as IValueChangedListener)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, textField)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, Double value) {
        addLabel(descriptor.displayName)

        ULCTextField textField = new ULCTextField()
        textField.name = descriptor.propertyName
        textField.dataType = DataTypeFactory.getDoubleDataTypeForEdit()
        textField.value = value
        textField.addValueChangedListener([valueChanged: { evt ->
            descriptor.value = textField.value
        }] as IValueChangedListener)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, textField)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, Boolean value) {
        addLabel(descriptor.displayName)

        ULCCheckBox checkBox = new ULCCheckBox()
        checkBox.name = descriptor.propertyName
        checkBox.selected = value
        checkBox.addValueChangedListener([valueChanged: { evt ->
            descriptor.value = checkBox.selected
        }] as IValueChangedListener)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, checkBox)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, Enum value) {
        addLabel(descriptor.displayName)

        EnumComboBoxModel comboBoxModel = new EnumComboBoxModel(value.values(), value, true)
        ULCComboBox comboBox = new ULCComboBox(comboBoxModel)
        comboBox.name = descriptor.propertyName
        comboBox.addActionListener([actionPerformed: { evt ->
            descriptor.value = comboBoxModel.selectedEnum
        }] as IActionListener)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, comboBox)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, DateTime value) {
        addLabel(descriptor.displayName)

        ULCSpinnerDateModel dateModel = new ULCSpinnerDateModel(value.toDate(), null, null, Calendar.DAY_OF_MONTH)

        ULCSpinner spinner = new ULCSpinner(dateModel)
        spinner.name = descriptor.propertyName
        spinner.setEditor(new ULCDateEditor(spinner, FastDateFormat.getDateInstance(FastDateFormat.MEDIUM, LocaleResources.getLocale()).pattern))
        spinner.addValueChangedListener([valueChanged: { evt ->
            descriptor.value = new DateTime(spinner.value)
        }] as IValueChangedListener)
        content.add(ULCBoxPane.BOX_EXPAND_CENTER, spinner)
    }

    protected void addParameter(RuntimeParameterDescriptor descriptor, def value) {
        throw new IllegalArgumentException("Unsupported runtime parameter type: ${value?.class?.name}")
    }

    protected void addLabel(String label) {
        content.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(new ULCLabel(label), 0, 10, 0, 10))
    }
}

package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.chart.model.CriteriaViewModel
import org.pillarone.riskanalytics.application.ui.chart.model.ValueIntepretationType
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.LocaleResources
import com.ulcjava.base.application.*

class CriteriaView {
    CriteriaViewModel model
    ULCBoxPane content
    ULCComboBox keyFigureTypeComboBox
    ULCComboBox comparatorComboBox
    ULCTextField valueField
    ULCComboBox valueIntepretationComboBox
    ULCComboBox periodComboBox

    ULCButton removeButton

    public CriteriaView(CriteriaViewModel model) {
        this.@model = model
        initComponents()
        layoutComponents()
        attachListeners()
    }

    void attachListeners() {
        removeButton.addActionListener([actionPerformed: { model.remove() }] as IActionListener)
        valueField.addValueChangedListener([valueChanged: {e ->
            double value = valueField.value
            if (value != null) {
                model.value = value
            } else {
                ULCAlert alert = new I18NAlert("InvalidNumberFormat")
                alert.show()
            }
        }] as IValueChangedListener)
        valueField.addActionListener([actionPerformed: { model.queryModel.query() }] as IActionListener)
        periodComboBox.addActionListener([actionPerformed: {
            if (model.selectedPeriod == null) {
                valueIntepretationComboBox.model.selectedEnum = ValueIntepretationType.ABSOLUTE
            }
            valueIntepretationComboBox.enabled = (model.selectedPeriod != null)
        }] as IActionListener)
    }

    void layoutComponents() {
        comparatorComboBox.setPreferredSize(new Dimension(50, 20))
        valueField.setPreferredSize(new Dimension(150, 20))
        valueIntepretationComboBox.setPreferredSize(new Dimension(150, 20))
        periodComboBox.setPreferredSize(new Dimension(100, 20))
        periodComboBox.enabled = model.isEnablePeriodComboBox()

        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, keyFigureTypeComboBox)
        content.add(ULCBoxPane.BOX_RIGHT_EXPAND, comparatorComboBox)
        content.add(ULCBoxPane.BOX_RIGHT_EXPAND, valueField)
        content.add(ULCBoxPane.BOX_RIGHT_EXPAND, valueIntepretationComboBox)
        content.add(ULCBoxPane.BOX_RIGHT_EXPAND, periodComboBox)
        content.add(ULCBoxPane.BOX_RIGHT_EXPAND, removeButton)
    }

    void initComponents() {
        content = new ULCBoxPane(6, 1)
        keyFigureTypeComboBox = new ULCComboBox(model.keyFigureTypeModel)
        comparatorComboBox = new ULCComboBox(model.comparatorModel)

        valueField = new ULCTextField()
        valueField.value = model.value
        ULCNumberDataType dataType = new ULCNumberDataType(ClientContext.locale)
        dataType.integer = false
        dataType.minFractionDigits = 1
        dataType.maxFractionDigits = 2
        valueField.dataType = dataType

        valueIntepretationComboBox = new ULCComboBox(model.valueIntepretationModel)
        valueIntepretationComboBox.enabled = (model.selectedPeriod != null)
        periodComboBox = new ULCComboBox(model.periodModel)
        removeButton = new ULCButton(getText("remove"))

        keyFigureTypeComboBox.toolTipText = getText("path")
        comparatorComboBox.toolTipText = getText("comperator")
        valueField.toolTipText = getText("value")
        valueIntepretationComboBox.toolTipText = getText("interpreteAs")
        periodComboBox.toolTipText = getText("period")
        removeButton.toolTipText = getText("removeTootltip")

        periodComboBox.name = "${content.name}/period"
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("CriteriaView." + key);
    }
}
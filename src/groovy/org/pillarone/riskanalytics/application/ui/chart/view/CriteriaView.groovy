package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.chart.model.CriteriaViewModel
import org.pillarone.riskanalytics.application.ui.chart.model.ValueInterpretationType
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
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
            def oldValue = model.value
            try {
                model.value = valueField.value
            } catch (Exception ex) {
                model.value = oldValue
            }
        }] as IValueChangedListener)
        valueField.addActionListener([actionPerformed: {
            if (model.validate())
                model.queryModel.query()
            else {
                new I18NAlert(UlcUtilities.getWindowAncestor(content), getErrorMessage(valueIntepretationComboBox.model.selectedEnum)).show()
            }
        }] as IActionListener)

        periodComboBox.addActionListener([actionPerformed: {
            if (model.selectedPeriod == null) {
                valueIntepretationComboBox.model.selectedEnum = ValueInterpretationType.ABSOLUTE
            }
            valueIntepretationComboBox.enabled = (model.selectedPeriod != null)
            model.notifyCriteriaChanged()
        }] as IActionListener)

        keyFigureTypeComboBox.addActionListener([actionPerformed: {
            model.notifyCriteriaChanged()
        }] as IActionListener)

        comparatorComboBox.addActionListener([actionPerformed: {
            model.notifyCriteriaChanged()
        }] as IActionListener)

        valueIntepretationComboBox.addActionListener([actionPerformed: {
            model.notifyCriteriaChanged()
        }] as IActionListener)
    }

    void layoutComponents() {
        comparatorComboBox.setPreferredSize(new Dimension(50, 20))
        valueField.setPreferredSize(new Dimension(90, 20))
        periodComboBox.enabled = model.isEnablePeriodComboBox()

        content.add(ULCBoxPane.BOX_RIGHT_EXPAND, keyFigureTypeComboBox)
        content.add(ULCBoxPane.BOX_RIGHT_EXPAND, comparatorComboBox)
        content.add(ULCBoxPane.BOX_RIGHT_EXPAND, valueField)
        content.add(ULCBoxPane.BOX_RIGHT_EXPAND, valueIntepretationComboBox)
        content.add(ULCBoxPane.BOX_RIGHT_EXPAND, periodComboBox)
        content.add(ULCBoxPane.BOX_RIGHT_EXPAND, removeButton)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }

    void initComponents() {
        content = new ULCBoxPane(7, 1)
        keyFigureTypeComboBox = new ULCComboBox(model.keyFigureTypeModel)
        comparatorComboBox = new ULCComboBox(model.comparatorModel)

        valueField = new ULCTextField()
        valueField.value = model.value
        valueField.dataType = DataTypeFactory.getDoubleDataType()

        valueIntepretationComboBox = new ULCComboBox(model.valueInterpretationModel)
        valueIntepretationComboBox.enabled = (model.selectedPeriod != null)
        periodComboBox = new ULCComboBox(model.periodModel)
        removeButton = new ULCButton(UIUtils.getText(CriteriaView.class, "remove"))

        keyFigureTypeComboBox.toolTipText = UIUtils.getText(CriteriaView.class, "path")
        comparatorComboBox.toolTipText = UIUtils.getText(CriteriaView.class, "comperator")
        valueField.toolTipText = UIUtils.getText(CriteriaView.class, "value")
        valueIntepretationComboBox.toolTipText = UIUtils.getText(CriteriaView.class, "interpreteAs")
        periodComboBox.toolTipText = UIUtils.getText(CriteriaView.class, "period")
        removeButton.toolTipText = UIUtils.getText(CriteriaView.class, "removeTootltip")

        periodComboBox.name = "${content.name}/period"
    }


    static String getErrorMessage(ValueInterpretationType selectedType) {
        switch (selectedType) {
            case ValueInterpretationType.PERCENTILE: return "PercentileNumberNotValid"
            case ValueInterpretationType.ORDER_STATISTIC: return "ObservationNumberNotValid"
            default: return "InvalidNumberFormat"
        }
    }
}
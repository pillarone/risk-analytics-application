package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.event.IKeyListener
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.CalculationSettingsPaneModel
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class CalculationSettingsPane extends SimulationSettingsPane {

    public static final String PERIOD_KEY = "PeriodCount"

    private ULCTextField periodCount

    public CalculationSettingsPane(CalculationSettingsPaneModel model) {
        this.model = model
        initComponents()
        layoutComponents()
        attachListeners()
    }


    @Override
    protected initConfigProperties(ULCBoxPane innerPane) {
        periodCount = new ULCTextField(name: "periodCount")
        periodCount.name = "CalculationSettingsPane.periodCount"
        periodCount.dataType = DataTypeFactory.getIntegerDataTypeForEdit()

        periodCount.addKeyListener([keyTyped: {e ->
            def value = periodCount.value
            if (value && (value instanceof Number) && value < Integer.MAX_VALUE)
                ((CalculationSettingsPaneModel) model).periodCount = value
            else if (value) {
                new I18NAlert("IterationNumberNotValid").show()
                periodCount.setValue(((CalculationSettingsPaneModel) model).periodCount)
            } else {
                ((CalculationSettingsPaneModel) model).periodCount = null
            }
        }] as IKeyListener)

        innerPane.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(UIUtils.getText(CalculationSettingsPane.class, PERIOD_KEY) + ":"))
        innerPane.add(2, ULCBoxPane.BOX_EXPAND_CENTER, UIUtils.spaceAround(periodCount, 5, 10, 0, 0, ULCBoxPane.BOX_EXPAND_EXPAND))
    }

    @Override
    protected void disableConfigProperties() {
        periodCount.enabled = true
    }

    @Override
    protected void enableConfigProperties() {
        periodCount.enabled = false
    }


}

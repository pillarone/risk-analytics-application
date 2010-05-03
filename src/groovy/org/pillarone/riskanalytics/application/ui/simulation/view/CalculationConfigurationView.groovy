package org.pillarone.riskanalytics.application.ui.simulation.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.simulation.model.CalculationConfigurationModel
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.getText

class CalculationConfigurationView extends AbstractConfigurationView {

    ULCTextField periodCount

    public CalculationConfigurationView(CalculationConfigurationModel model) {
        super(model)
    }

    /**
     * Initializes DeterministicModel specific components:
     * <ul>
     * <li>Period count</li>
     * </ul>
     */
    protected void initCustomComponents() {
        ULCNumberDataType numberDataType = new ULCNumberDataType()
        numberDataType.integer = true
        numberDataType.min = 1

        periodCount = new ULCTextField(name: "periodCount")
        periodCount.setPreferredSize(new Dimension(100, 20))
        periodCount.dataType = numberDataType

    }

    protected void layoutCustomComponents(ULCBoxPane content) {
        ULCLabel numberOfPeriodLabel = new ULCLabel(getText(this.class, "NumberOfPeriods") + ":")
        numberOfPeriodLabel.minimumSize = new Dimension(150, 30)
        numberOfPeriodLabel.setLabelFor(periodCount)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, numberOfPeriodLabel)
        content.add(2, ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(periodCount, 5, 10, 0, 0))
    }

    protected void addCustomListeners() {
        periodCount.keyTyped = {
            Object value = periodCount.value
            if (value != null) {
                model.periodCount = value
            }
        }
    }

    protected void updateCustomUIState() {
        periodCount.enabled = model.isConfigurationChangeable()
    }


}

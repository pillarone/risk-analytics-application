package org.pillarone.riskanalytics.application.ui.simulation.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.ui.simulation.model.SimulationConfigurationModel

class SimulationConfigurationView extends AbstractConfigurationView {

    ULCTextField iterationCount

    public SimulationConfigurationView(SimulationConfigurationModel model) {
        super(model)
    }

    protected void initCustomComponents() {
        ULCNumberDataType numberDataType = new ULCNumberDataType()
        numberDataType.integer = true
        numberDataType.min = 1

        iterationCount = new ULCTextField(name: "iterationCount")
        iterationCount.setPreferredSize(new Dimension(100, 20))
        iterationCount.dataType = numberDataType
    }

    protected void layoutCustomComponents(ULCBoxPane content) {
        content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(getText("Random") + ":"))
        ULCBoxPane randomPane = new ULCBoxPane(0, 2)
        randomPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(new ULCLabel(getText("IsRandom") + ":"), 5, 10, 0, 0))
        randomPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(useUserDefinedSeed, 5, 10, 0, 0))
        randomPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(new ULCLabel(getText("initSeed") + ":"), 5, 10, 0, 0))
        randomPane.add(ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(randomSeed, 5, 10, 0, 0))
        content.add(2, ULCBoxPane.BOX_EXPAND_EXPAND, randomPane)

        ULCLabel label = new ULCLabel(getText("NumberOfIterations") + ":")
        label.minimumSize = new Dimension(150, 30)
        content.add(ULCBoxPane.BOX_LEFT_CENTER, label)
        content.add(2, ULCBoxPane.BOX_EXPAND_CENTER, spaceAround(iterationCount, 5, 10, 0, 0))

    }

    protected void addCustomListeners() {
        iterationCount.keyTyped = {
            Object value = iterationCount.value
            model.iterationCount = value
        }
    }

    protected void updateCustomUIState() {
        iterationCount.enabled = model.isConfigurationChangeable()
    }

}
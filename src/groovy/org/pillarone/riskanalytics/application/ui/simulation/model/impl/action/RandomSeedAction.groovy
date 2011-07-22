package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action

import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel
import com.ulcjava.base.application.ULCCheckBox
import com.ulcjava.base.application.ULCTextField
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.UserPreferences

/**
 * A IValueChangedListener which is used for the random seed check box and text field.
 * If the event is fired from the TextField the new value is written to the model.
 * If it is fired from the checkbox, the model value is set to null (if disabled) or to the last entered value (if any) if enabled.
 */
class RandomSeedAction implements IValueChangedListener {

    private static Log LOG = LogFactory.getLog(RandomSeedAction)

    private SimulationSettingsPaneModel model;
    private Integer oldRandomSeed
    private UserPreferences userPreferences = new UserPreferences()
    ULCTextField randomSeed

    public RandomSeedAction(SimulationSettingsPaneModel model) {
        this.model = model;
    }

    void valueChanged(ValueChangedEvent event) {
        handleEvent(event.getSource())
    }

    private handleEvent(ULCCheckBox checkBox) {
        if (!checkBox.isSelected()) {
            LOG.info("User defined random seed disabled, setting to null.")
            oldRandomSeed = model.randomSeed
            model.randomSeed = null
        } else {
            model.randomSeed = getRandomSeed()
            randomSeed?.setValue model.randomSeed
        }
    }

    private handleEvent(ULCTextField textField) {
        Integer randomSeed = textField.getValue()
        if (randomSeed != null && randomSeed > 0) {
            model.randomSeed = randomSeed
            userPreferences.putPropertyValue(UserPreferences.RANDOM_SEED_USER_VALUE, "" + randomSeed)
        } else {
            randomSeed = 1
            new I18NAlert("NoInteger").show()
            textField.value = randomSeed
            model.randomSeed = randomSeed
        }
        LOG.info("User defined random seed changed to ${randomSeed}.")
    }

    private Integer getRandomSeed() {
        if (!oldRandomSeed) {
            String value = userPreferences.getPropertyValue(UserPreferences.RANDOM_SEED)
            if (value) return Integer.valueOf(value)
            return null
        }
        return oldRandomSeed
    }
}

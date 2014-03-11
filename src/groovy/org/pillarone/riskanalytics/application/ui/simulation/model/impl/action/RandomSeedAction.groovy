package org.pillarone.riskanalytics.application.ui.simulation.model.impl.action
import com.ulcjava.base.application.ULCCheckBox
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.SimulationSettingsPaneModel
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory
/**
 * A IValueChangedListener which is used for the random seed check box and text field.
 * If the event is fired from the TextField the new value is written to the model.
 * If it is fired from the checkbox, the model value is set to null (if disabled) or to the last entered value (if any) if enabled.
 */
class RandomSeedAction implements IValueChangedListener {

    private static Log LOG = LogFactory.getLog(RandomSeedAction)

    private SimulationSettingsPaneModel model;
    private Integer oldRandomSeed
    private UserPreferences userPreferences = UserPreferencesFactory.userPreferences
    ULCTextField randomSeed

    public RandomSeedAction(SimulationSettingsPaneModel model) {
        this.model = model;
    }

    void setUserDefinedRandomSeedCheckBox(ULCCheckBox ulcCheckBox) {
        ulcCheckBox.addValueChangedListener(this)
        // set the initial value
        handleEvent(ulcCheckBox)
    }

    void valueChanged(ValueChangedEvent event) {
        handleEvent(event.source)
    }

    private handleEvent(ULCCheckBox checkBox) {
        if (checkBox.selected) {
            model.randomSeed = getRandomSeed()
            randomSeed.value = model.randomSeed
        } else {
            LOG.info("User defined random seed disabled, setting to null.")
            oldRandomSeed = model.randomSeed
            model.randomSeed = null
        }
    }

    private handleEvent(ULCTextField textField) {
        Integer randomSeed = textField.value as Integer
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
            String value = userPreferences.getPropertyValue(UserPreferences.RANDOM_SEED_USER_VALUE)
            if (value) return Integer.valueOf(value)
            return null
        }
        return oldRandomSeed
    }
}

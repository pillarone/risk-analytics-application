package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.main.view.IDetailView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractUIItem<T extends IDetailView> {

    ModellingUIItem createNewVersion(boolean openNewVersion = true) {
        return null
    }

    boolean isEditable() {
        return false
    }

    boolean isLoaded() {
        return false
    }

    void load(boolean completeLoad = true) {}

    void unload() {}

    List<Simulation> getSimulations() {
        return []
    }

    abstract T createDetailView()

    abstract String createTitle()

    abstract String getWindowTitle()

    String getToolTip() {
        return ""
    }

    ULCIcon getIcon() {
        return UIUtils.getIcon("clear.png")
    }

    String getName() {
        ""
    }

    String getNameAndVersion() {
        name
    }

    boolean isVersionable() {
        return false
    }

    boolean isDeletable() {
        return true
    }

    abstract Model getModel()
}

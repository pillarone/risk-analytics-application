package org.pillarone.riskanalytics.application.ui.main.view.item
import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun
/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
abstract class AbstractUIItem implements IUIItem {

    void save() {}

    boolean remove() {
        return false
    }

    ModellingUIItem createNewVersion(Model selectedModel, boolean openNewVersion = true) {
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

    List<SimulationRun> getSimulations() {
        return []
    }

    abstract ULCContainer createDetailView()

    abstract Object getViewModel()

    abstract String createTitle()

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

    boolean isChanged() {
        return false
    }

    boolean isDeletable() {
        return true
    }
}

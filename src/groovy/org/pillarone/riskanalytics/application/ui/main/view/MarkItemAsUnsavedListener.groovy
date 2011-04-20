package org.pillarone.riskanalytics.application.ui.main.view

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.*

class MarkItemAsUnsavedListener implements IModellingItemChangeListener {
    ULCCloseableTabbedPane tabbedPane
    int paneIndex
    static String UNSAVED_MARK = " *"

    public MarkItemAsUnsavedListener(ULCCloseableTabbedPane tabbedPane, int paneIndex) {
        this.@tabbedPane = tabbedPane
        this.@paneIndex = paneIndex
    }

    public void itemChanged(ResultConfiguration item) {
        updateUnsavedTabbedPaneTitle(item)
    }


    public void itemChanged(Parameterization item) {
        updateUnsavedTabbedPaneTitle(item)
    }

    public void itemChanged(ModellingItem item) {
    }

    private void updateUnsavedTabbedPaneTitle(ModellingItem item) {
        String title = "$item.name v${item.versionNumber.toString()}"
        String newTitle = getTabTitle(item)
        TabbedPaneGuiHelper.updateTabbedPaneTitle(tabbedPane, title, newTitle)
    }

    public void itemSaved(ModellingItem item) {
        String title = "$item.name v${item.versionNumber.toString()}"
        TabbedPaneGuiHelper.updateTabbedPaneTitle(tabbedPane, title + UNSAVED_MARK, title)
    }

    public static String getTabTitle(ModellingItem item) {
        String title = "$item.name v${item.versionNumber.toString()}"
        if (isChanged(item)) {
            title += UNSAVED_MARK
        }
        return title
    }

    public static String getTabTitle(ResultConfiguration item, Model selectedModel) {
        return getTabTitle(item)
    }

    public static String getTabTitle(Parameterization item, Model selectedModel) {
        return getTabTitle(item)
    }

    public static String getTabTitle(ConfigObjectBasedModellingItem item, Model selectedModel) {
        return getTabTitle(item)
    }

    public static String getTabTitle(ModellingItem item, Model selectedModel) {
        return "$item.name".toString()
    }

    public static String getTabTitle(Simulation item, DeterministicModel selectedModel) {
        return item.start == null ? "Calculation" : item.name
    }

    private static boolean isChanged(ModellingItem item) {
        return item.isChanged()
    }

    private static boolean isChanged(Parameterization item) {
        return item.isChanged() || item.commentHasChanged()
    }

}


package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.chart.model.ChartViewModel

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ChartRenameListener implements IModelChangedListener {
    ULCCloseableTabbedPane tabbedPane
    int panelIndex
    ChartViewModel model

    public ChartRenameListener(ULCCloseableTabbedPane tabbedPane, int panelIndex, ChartViewModel model) {
        this.@tabbedPane = tabbedPane
        this.@panelIndex = panelIndex
        this.@model = model
    }

    public void modelChanged() {
        try {
            tabbedPane.setTitleAt(panelIndex, format(model.chartProperties.title))
        } catch (Exception ex) {
            //ignore exception due an undocked pane
        }
    }

    private String format(String title) {
        return (title.length() > 12) ? (title.substring(0, 12) + "...") : title
    }
}

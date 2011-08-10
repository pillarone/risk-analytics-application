package org.pillarone.riskanalytics.application.ui.main.model

import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
public interface IRiskAnalyticsModelListener {

    void openDetailView(Model model, AbstractUIItem item)

    void openDetailView(Model model, ModellingItem item)

    void closeDetailView(Model model, AbstractUIItem item)

    void changedDetailView(Model model, AbstractUIItem item)

    void setWindowTitle(AbstractUIItem windowTitle)

}
package org.pillarone.riskanalytics.application.ui.main.model

import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
interface IRiskAnalyticsModelListener {

    void openDetailView(AbstractUIItem item)

    void closeDetailView(AbstractUIItem item)

    void changedDetailView(AbstractUIItem item)

}
package org.pillarone.riskanalytics.application.logging.view

import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCList
import com.ulcjava.base.application.ULCScrollPane
import org.pillarone.riskanalytics.application.logging.model.RealTimeLoggingModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RealTimeLoggingView {

    RealTimeLoggingModel model
    ULCList loggingList
    ULCButton clearButton
    ULCBoxPane content

    def RealTimeLoggingView() {
        model = new RealTimeLoggingModel();
        initComponents()
        layoutComponents()
        attachListeners()
    }

    void attachListeners() {
        clearButton.actionPerformed = {
            model.clear()
        }
    }

    void layoutComponents() {
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(loggingList))
        content.add(ULCBoxPane.BOX_CENTER_BOTTOM, clearButton)
    }

    void initComponents() {
        content = new ULCBoxPane(false)
        loggingList = new ULCList(model.getListModel())
        clearButton = new ULCButton(UIUtils.getText(this.getClass(), "clearButton"))
    }
}

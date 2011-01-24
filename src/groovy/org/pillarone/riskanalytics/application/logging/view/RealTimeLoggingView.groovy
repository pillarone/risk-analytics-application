package org.pillarone.riskanalytics.application.logging.view

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.logging.model.RealTimeLoggingModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class RealTimeLoggingView {

    RealTimeLoggingModel model
    ULCList loggingList
    ULCButton clearButton
    ULCButton copyContentButton
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

        copyContentButton.actionPerformed = {
            ULCClipboard.getClipboard().content = model.getContent()
        }
    }

    void layoutComponents() {
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCScrollPane(loggingList))
        ULCBoxPane pane = new ULCBoxPane(1, 3)
        pane.add(ULCBoxPane.BOX_EXPAND_TOP, copyContentButton)
        pane.add(ULCBoxPane.BOX_CENTER_EXPAND, new ULCFiller())
        pane.add(ULCBoxPane.BOX_EXPAND_BOTTOM, clearButton)
        content.add(ULCBoxPane.BOX_RIGHT_EXPAND, pane)
    }

    void initComponents() {
        content = new ULCBoxPane(false)
        loggingList = new ULCList(model.getListModel())
        clearButton = new ULCButton(UIUtils.getText(this.class, "clear"))
        clearButton.setPreferredSize(new Dimension(120, 20))
        copyContentButton = new ULCButton(UIUtils.getText(this.class, "copyContent"))
        copyContentButton.setPreferredSize(new Dimension(120, 20))
    }
}
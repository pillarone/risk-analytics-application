package org.pillarone.riskanalytics.application.ui.comment.model

import com.ulcjava.base.application.event.IPopupMenuListener
import com.ulcjava.base.application.event.PopupMenuEvent

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class UndockedPaneListener implements IPopupMenuListener {
    Closure closeSplitPane

    void popupMenuHasBecomeVisible(PopupMenuEvent popupMenuEvent) { }

    void popupMenuHasBecomeInvisible(PopupMenuEvent popupMenuEvent) {
        closeSplitPane.call()
    }

    void popupMenuCanceled(PopupMenuEvent popupMenuEvent) {}
}

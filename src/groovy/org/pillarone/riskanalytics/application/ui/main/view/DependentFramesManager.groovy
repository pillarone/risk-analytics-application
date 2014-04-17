package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCTabbedPane
import org.pillarone.riskanalytics.application.ui.main.view.item.AbstractUIItem
import com.canoo.ulc.detachabletabbedpane.server.ULCDetachableTabbedPane
import com.ulcjava.base.application.ULCFrame
import com.canoo.ulc.detachabletabbedpane.server.ULCCloseableTabbedPane
import static org.pillarone.riskanalytics.application.ui.main.view.MarkItemAsUnsavedListener.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class DependentFramesManager {

    ULCDetachableTabbedPane tabbedPane

    public DependentFramesManager(ULCTabbedPane tabbedPane) {
        this.tabbedPane = tabbedPane
    }

    void selectTab(AbstractUIItem abstractUIItem) {
        tabbedPane.dependentFrames.each { ULCFrame frame ->
            if (removeUnsavedMark(frame.title).equals(removeUnsavedMark(abstractUIItem.createTitle()))) {
                ULCCloseableTabbedPane tp = (ULCCloseableTabbedPane) frame.contentPane.components[0]
                tp.selectedIndex = 0
                frame.toFront()
            }

        }
    }

    void closeTab(AbstractUIItem abstractUIItem) {
        tabbedPane.dependentFrames.each { ULCFrame frame ->
            if (removeUnsavedMark(frame.title).equals(removeUnsavedMark(abstractUIItem.createTitle()))) {
                ULCCloseableTabbedPane tp = (ULCCloseableTabbedPane) frame.contentPane.components[0]
                tp.closeCloseableTab(0)
                frame.dispose()
            }
        }
    }

    void updateTabbedPaneTitle(AbstractUIItem abstractUIItem) {
        tabbedPane.dependentFrames.each { ULCFrame frame ->
            String newTitle = abstractUIItem.createTitle()
            if (removeUnsavedMark(frame.title).equals(removeUnsavedMark(newTitle))) {
                frame.title = newTitle
                ULCCloseableTabbedPane tp = (ULCCloseableTabbedPane) frame.contentPane.components[0]
                tp.setTitleAt(0, newTitle)
            }
        }
    }

}

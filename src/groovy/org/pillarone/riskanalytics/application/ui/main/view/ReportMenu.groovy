package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.ULCMenu
import com.ulcjava.base.application.ULCMenuItem
import com.ulcjava.base.application.event.ITreeSelectionListener
import com.ulcjava.base.application.event.TreeSelectionEvent
import org.pillarone.riskanalytics.application.ui.base.action.GenerateReportAction

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ReportMenu extends ULCMenu implements ITreeSelectionListener {
    List actions = []

    public ReportMenu(String name) {
        super(name)
    }

    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        boolean enable = false
        actions.each {GenerateReportAction action ->
            enable = enable || action.isEnabled()
        }
        setEnabled(enable)
    }

    public ULCMenuItem add(ULCMenuItem ulcMenuItem) {
        ULCMenuItem retValue = super.add(ulcMenuItem)
        actions << ulcMenuItem.action
        return retValue
    }

}

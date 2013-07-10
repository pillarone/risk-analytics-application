package org.pillarone.riskanalytics.application.ui.main.view

import com.ulcjava.base.application.*
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.shared.IDefaults
import grails.util.Holders
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.core.log.TraceLogManager

@CompileStatic
class UserTraceDialog {

    ULCDialog dialog
    TraceLogManager traceLogManager = Holders.grailsApplication.mainContext.getBean(TraceLogManager)

    UserTraceDialog(ULCWindow root) {
        dialog = new ULCDialog(root, "Log", true)
        dialog.preferredSize = new Dimension(800, 600)

        ULCBoxPane boxPane = new ULCBoxPane()

        ULCTextArea textArea = new ULCTextArea(traceLogManager.trace.join(""))
        boxPane.add(IDefaults.BOX_EXPAND_EXPAND, textArea)

        dialog.contentPane = new ULCScrollPane(boxPane)
    }
}

package org.pillarone.riskanalytics.application.ui.simulation.view.impl.queue

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.RealTimeLoggingModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

import static com.ulcjava.base.shared.IDefaults.*

@CompileStatic
@Scope(UlcSessionScope.ULC_SESSION_SCOPE)
@Component
class RealTimeLoggingView {

    @Resource
    RealTimeLoggingModel realTimeLoggingModel
    private ULCList loggingList
    private ULCButton clearButton
    private ULCButton copyContentButton
    private ULCBoxPane content

    @PostConstruct
    private void initialize() {
        initComponents()
        layoutComponents()
        attachListeners()
    }

    ULCBoxPane getContent() {
        return content
    }

    private void attachListeners() {
        clearButton.addActionListener([actionPerformed: {
            realTimeLoggingModel.clear()
        }] as IActionListener)

        copyContentButton.addActionListener([actionPerformed: {
            ULCClipboard.clipboard.content = realTimeLoggingModel.content
        }] as IActionListener)
    }

    private void layoutComponents() {
        content.add(BOX_EXPAND_EXPAND, new ULCScrollPane(loggingList))
        ULCBoxPane pane = new ULCBoxPane(1, 3)
        pane.add(BOX_EXPAND_TOP, copyContentButton)
        pane.add(BOX_CENTER_EXPAND, new ULCFiller())
        pane.add(BOX_EXPAND_BOTTOM, clearButton)
        content.add(BOX_RIGHT_EXPAND, pane)
    }

    private void initComponents() {
        content = new ULCBoxPane(false)
        loggingList = new ULCList(realTimeLoggingModel.listModel)
        clearButton = new ULCButton(UIUtils.getText(this.class, 'clear'))
        clearButton.preferredSize = new Dimension(120, 20)
        copyContentButton = new ULCButton(UIUtils.getText(this.class, 'copyContent'))
        copyContentButton.preferredSize = new Dimension(120, 20)
    }
}
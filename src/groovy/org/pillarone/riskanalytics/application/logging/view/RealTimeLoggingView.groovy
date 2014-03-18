package org.pillarone.riskanalytics.application.logging.view

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.logging.model.RealTimeLoggingModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils

import static com.ulcjava.base.shared.IDefaults.*

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
@CompileStatic
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
        clearButton.addActionListener([actionPerformed: {
            model.clear()
        }] as IActionListener)

        copyContentButton.addActionListener([actionPerformed: {
            ULCClipboard.clipboard.content = model.content
        }] as IActionListener)
    }

    void layoutComponents() {
        content.add(BOX_EXPAND_EXPAND, new ULCScrollPane(loggingList))
        ULCBoxPane pane = new ULCBoxPane(1, 3)
        pane.add(BOX_EXPAND_TOP, copyContentButton)
        pane.add(BOX_CENTER_EXPAND, new ULCFiller())
        pane.add(BOX_EXPAND_BOTTOM, clearButton)
        content.add(BOX_RIGHT_EXPAND, pane)
    }

    void initComponents() {
        content = new ULCBoxPane(false)
        loggingList = new ULCList(model.listModel)
        clearButton = new ULCButton(UIUtils.getText(this.class, 'clear'))
        clearButton.preferredSize = new Dimension(120, 20)
        copyContentButton = new ULCButton(UIUtils.getText(this.class, 'copyContent'))
        copyContentButton.preferredSize = new Dimension(120, 20)
    }
}
package org.pillarone.riskanalytics.application.environment.jnlp

import com.ulcjava.base.application.ULCRootPane
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.environment.P1RATViewFactory
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.ulc.server.ULCMinimalSizeFrame

import static com.ulcjava.base.application.ULCFrame.TERMINATE_ON_CLOSE

class P1RATFrameViewFactory extends P1RATViewFactory {

    ULCRootPane createRootPane() {
        ULCMinimalSizeFrame frame = new ULCMinimalSizeFrame()
        frame.iconImage = UIUtils.getIcon("application.png")
        frame.title = "Risk Analytics"
        frame.defaultCloseOperation = TERMINATE_ON_CLOSE
        frame.setSize(1000, 800)
        frame.setLocation(100, 100)
        frame.minimumSize = new Dimension(800, 600)
        return frame
    }
}
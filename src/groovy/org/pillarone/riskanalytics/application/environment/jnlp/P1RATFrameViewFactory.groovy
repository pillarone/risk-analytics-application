package org.pillarone.riskanalytics.application.environment.jnlp

import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.ULCRootPane
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.environment.P1RATViewFactory
import org.pillarone.riskanalytics.application.ui.util.server.ULCMinimalSizeFrame

class P1RATFrameViewFactory extends P1RATViewFactory {


    ULCRootPane createRootPane() {
        ULCMinimalSizeFrame frame = new ULCMinimalSizeFrame()
        URL resource = getClass().getResource("/application.png")
        if (resource) {
            frame.setIconImage(new ULCIcon(resource))
        }
        frame.title = "Risk Analytics"

        frame.setDefaultCloseOperation(ULCFrame.TERMINATE_ON_CLOSE)
        frame.setSize(1000, 800)
        frame.setLocation(100, 100)
        frame.minimumSize = new Dimension(800, 600)
        return frame
    }

}
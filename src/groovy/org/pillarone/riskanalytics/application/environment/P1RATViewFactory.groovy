package org.pillarone.riskanalytics.application.environment

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCRootPane
import com.ulcjava.base.application.util.BorderedComponentUtilities
import com.ulcjava.container.grails.UlcViewFactory
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.main.view.P1RATMainView
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe

abstract class P1RATViewFactory implements UlcViewFactory {

    public ULCRootPane create() {

        ULCClipboard.install()
        ULCRootPane frame = createRootPane()

        P1RATMainView mainView = new P1RATMainView(new P1RATModel())
        frame.setMenuBar(mainView.getMenuBar())
        frame.add(BorderedComponentUtilities.createBorderedComponent(mainView.getContent(), ULCBoxPane.BOX_EXPAND_EXPAND, BorderFactory.createEmptyBorder(5, 5, 5, 5)))
        ExceptionSafe.rootPane = frame
        return frame
    }

    abstract protected ULCRootPane createRootPane()

}
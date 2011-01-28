package org.pillarone.riskanalytics.application.environment

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCRootPane
import com.ulcjava.base.application.util.BorderedComponentUtilities
import com.ulcjava.container.grails.UlcViewFactory
import org.pillarone.riskanalytics.application.ui.main.model.P1RATModel
import org.pillarone.riskanalytics.application.ui.main.view.P1RATMainView
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.core.user.UserManagement
import org.apache.log4j.MDC
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.user.Person

abstract class P1RATViewFactory implements UlcViewFactory {

    private Log LOG = LogFactory.getLog(P1RATViewFactory)

    public ULCRootPane create() {

        Person user = UserManagement.currentUser
        MDC.put("username", user ? user.username : "")
        LOG.info "Started session for user '${UserManagement.currentUser?.username}'"

        ULCClipboard.install()
        ULCRootPane frame = createRootPane()

        P1RATMainView mainView = new P1RATMainView(new P1RATModel())
        frame.setMenuBar(mainView.getMenuBar())
        frame.add(BorderedComponentUtilities.createBorderedComponent(mainView.getContent(), ULCBoxPane.BOX_EXPAND_EXPAND, BorderFactory.createEmptyBorder(5, 5, 5, 5)))
        UIUtils.setRootPane(frame)
        return frame
    }

    abstract protected ULCRootPane createRootPane()

}
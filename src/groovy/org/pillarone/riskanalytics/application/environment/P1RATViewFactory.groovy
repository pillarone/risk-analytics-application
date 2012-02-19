package org.pillarone.riskanalytics.application.environment

import com.canoo.ulc.community.locale.server.ULCClientTimeZoneSetter
import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCRootPane
import com.ulcjava.base.application.util.BorderedComponentUtilities
import com.ulcjava.container.grails.UlcViewFactory
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.log4j.MDC
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.application.search.ModellingItemSearchService
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ulcjava.base.server.ULCSession

//used for Applet & JNLP (but not standalone)
abstract class P1RATViewFactory implements UlcViewFactory {

    private Log LOG = LogFactory.getLog(P1RATViewFactory)

    ModellingItemSearchService searchService

    public ULCRootPane create() {
        LOG.info "Started session for user '${UserContext.currentUser?.username}'"
        try {
            MDC.put("username", UserContext.currentUser?.username)
        } catch (Exception ex) {
            // put a user in MDC causes an exception in integration Test
        }

        searchService = ApplicationHolder.application.mainContext.getBean(ModellingItemSearchService)

        UserContext.setUserTimeZone(ClientContext.timeZone)
        ULCClientTimeZoneSetter.setDefaultTimeZone(TimeZone.getTimeZone("UTC"))

        ULCClipboard.install()
        ULCRootPane frame = createRootPane()

        RiskAnalyticsMainView mainView = new RiskAnalyticsMainView(new RiskAnalyticsMainModel())
        mainView.init()

        searchService.registerSession(ULCSession.currentSession())

        frame.setMenuBar(mainView.getMenuBar())
        frame.add(BorderedComponentUtilities.createBorderedComponent(mainView.getContent(), ULCBoxPane.BOX_EXPAND_EXPAND, BorderFactory.createEmptyBorder(5, 5, 5, 5)))
        UIUtils.setRootPane(frame)
        return frame
    }

    abstract protected ULCRootPane createRootPane()

    void stop() {
        searchService.unregisterSession(ULCSession.currentSession())
    }

}
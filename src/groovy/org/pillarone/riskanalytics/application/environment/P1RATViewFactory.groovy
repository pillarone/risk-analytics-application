package org.pillarone.riskanalytics.application.environment

import com.canoo.ulc.community.locale.server.ULCClientTimeZoneSetter
import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.applicationframework.application.ApplicationContext
import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCRootPane
import com.ulcjava.base.application.event.IRoundTripListener
import com.ulcjava.base.application.event.RoundTripEvent
import com.ulcjava.base.application.util.BorderedComponentUtilities
import com.ulcjava.container.grails.UlcViewFactory
import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import groovy.transform.CompileStatic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.log4j.MDC
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.UlcSessionScope
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainView
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.log.TraceLogManager

import static com.ulcjava.base.shared.IDefaults.BOX_EXPAND_EXPAND
import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_BY_NAME

//used for Applet & JNLP (but not standalone)
@CompileStatic
abstract class P1RATViewFactory implements UlcViewFactory {

    private static final Log LOG = LogFactory.getLog(P1RATViewFactory)

    TraceLogManager traceLogManager
    RiskAnalyticsMainView riskAnalyticsMainView
    SpringSecurityService springSecurityService

    public ULCRootPane create(ApplicationContext applicationContext) {
        initializeInjection()
        addTerminationListener()
        String username = UserContext.currentUser?.username;
        LOG.info "Started session for user '${username}'"
        try {
            MDC.put("username", username)
        } catch (Exception ignored) {
            // put a user in MDC causes an exception in integration Test
            LOG.warn("Exception trying to put username into Mapped Diagnostic Context (MDC): " + username)
        }
        traceLogManager.activateLogging()

        UserContext.userTimeZone = ClientContext.timeZone
        ULCClientTimeZoneSetter.defaultTimeZone = TimeZone.getTimeZone("UTC")

        ULCClipboard.install()
        ULCRootPane frame = createRootPane()

        frame.menuBar = riskAnalyticsMainView.menuBar
        frame.add(BorderedComponentUtilities.createBorderedComponent(riskAnalyticsMainView.content, BOX_EXPAND_EXPAND, BorderFactory.createEmptyBorder(5, 5, 5, 5)))
        UIUtils.rootPane = frame
        return frame
    }

    private void initializeInjection() {
        Holders.grailsApplication.mainContext.autowireCapableBeanFactory.autowireBeanProperties(this, AUTOWIRE_BY_NAME, false)
    }

    private void addTerminationListener() {
        ApplicationContext.addRoundTripListener([roundTripDidStart: { def event -> },
                roundTripWillEnd: { RoundTripEvent event ->
                    if (!springSecurityService.loggedIn) {
                        terminate()
                    }
                }
        ] as IRoundTripListener)
    }

    private void terminate() {
        com.ulcjava.base.application.ApplicationContext.terminate()
    }

    abstract protected ULCRootPane createRootPane()

    void stop() {
        UlcSessionScope.destroy()
        traceLogManager.deactivateLogging()
    }

    @Override
    void stop(Throwable reason) {
        ExceptionSafe.saveError(reason)
        stop()
    }
}

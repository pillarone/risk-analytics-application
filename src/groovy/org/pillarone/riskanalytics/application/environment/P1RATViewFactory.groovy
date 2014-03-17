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
import com.ulcjava.base.server.ULCSession
import com.ulcjava.base.shared.IDefaults
import com.ulcjava.container.grails.UlcViewFactory
import grails.plugin.springsecurity.SpringSecurityService
import grails.util.Holders
import groovy.transform.CompileStatic
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.log4j.MDC
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainView
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.log.TraceLogManager
import org.pillarone.riskanalytics.core.search.CacheItemEventQueueService
//used for Applet & JNLP (but not standalone)
@CompileStatic
abstract class P1RATViewFactory implements UlcViewFactory {

    private Log LOG = LogFactory.getLog(P1RATViewFactory)

    TraceLogManager traceLogManager

    public ULCRootPane create(ApplicationContext applicationContext) {
        addTerminationListener()

        // 20140107 in Chrome, on production, login as frahman and see the following kind of output in logfile:
        //
        //.. (jrichardson)- INFO  P1RATViewFactory Started session for user 'null'
        //
        // (But no such problem on test instance - Is it to do with test DB being a newer SQL Server ?)
        String username = UserContext.currentUser?.username;
        LOG.info "Started session for user '${username}'"
        try {
            MDC.put("username", username)
        } catch (Exception ex) {
            // put a user in MDC causes an exception in integration Test
            LOG.warn("Exception trying to put username into Mapped Diagnostic Context (MDC): " + username)
        }
        traceLogManager = Holders.grailsApplication.mainContext.getBean(TraceLogManager)
        traceLogManager.activateLogging()

        UserContext.setUserTimeZone(ClientContext.timeZone)
        ULCClientTimeZoneSetter.setDefaultTimeZone(TimeZone.getTimeZone("UTC"))

        ULCClipboard.install()
        ULCRootPane frame = createRootPane()

        RiskAnalyticsMainView mainView = new RiskAnalyticsMainView(new RiskAnalyticsMainModel(applicationContext: applicationContext))
        mainView.init()

        frame.setMenuBar(mainView.getMenuBar())
        frame.add(BorderedComponentUtilities.createBorderedComponent(mainView.content, IDefaults.BOX_EXPAND_EXPAND, BorderFactory.createEmptyBorder(5, 5, 5, 5)))
        UIUtils.setRootPane(frame)
        return frame
    }

    private void addTerminationListener() {
        ApplicationContext.addRoundTripListener([roundTripDidStart: { def event -> },
                roundTripWillEnd: { RoundTripEvent event ->
                    if (!isLoggedIn()) {
                        terminate()
                    }
                }
        ] as IRoundTripListener)
    }

    private void terminate() {
        com.ulcjava.base.application.ApplicationContext.terminate()
    }

    private boolean isLoggedIn() {
        Holders.grailsApplication.mainContext.getBean("springSecurityService", SpringSecurityService).isLoggedIn()
    }

    abstract protected ULCRootPane createRootPane()

    void stop() {
        CacheItemEventQueueService.getInstance().unregisterAllConsumersForSession(ULCSession.currentSession())
        traceLogManager.deactivateLogging()
    }

    @Override
    void stop(Throwable reason) {
        ExceptionSafe.saveError(reason)
        stop()
    }
}

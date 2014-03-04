package org.pillarone.riskanalytics.application.ui
import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.applicationframework.application.Application
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.server.ULCSession
import com.ulcjava.base.shared.IWindowConstants
import grails.util.Holders
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.main.action.ExitAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainView
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.log.TraceLogManager
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry
import org.pillarone.riskanalytics.core.search.CacheItemEventQueueService
import org.pillarone.ulc.server.ULCMinimalSizeFrame
//used for standalone
class P1RATApplication extends Application {

    private static Log LOG = LogFactory.getLog(P1RATApplication)

    ULCMinimalSizeFrame mainFrame = new ULCMinimalSizeFrame("Risk Analytics")
    RiskAnalyticsMainModel mainModel
    public static boolean CLOSE_WINDOW = false

    TraceLogManager traceLogManager

    protected void startup() {
        ClientContext.sendMessage("hideSplash");
        if (UserContext.isStandAlone()) {
            try {
                UserContext.setUserTimeZone(TimeZone.getTimeZone(System.getProperty("user.timezone")))
            } catch (Exception e) {
                LOG.error("Unable to determine user time zone - using UTC", e)
                UserContext.setUserTimeZone(TimeZone.getTimeZone("UTC"))
            }
        }
        traceLogManager = Holders.grailsApplication.mainContext.getBean(TraceLogManager)
        initMainView()
        traceLogManager.activateLogging()
    }

    @Override
    void stop(Throwable reason) {
        ExceptionSafe.saveError(reason)
        super.stop(reason)
    }

    public void initMainView() {
        //init RiskAnalyticsMainModel after login
        mainModel = new RiskAnalyticsMainModel(applicationContext: getContext())
        mainFrame.defaultCloseOperation = IWindowConstants.DO_NOTHING_ON_CLOSE
        mainFrame.size = new Dimension(1000, 750)
        mainFrame.minimumSize = new Dimension(800, 600)

        //If argument is null, the window is centered on the screen.
        mainFrame.locationRelativeTo = null
        mainFrame.setIconImage(UIUtils.getIcon("application.png"))
        ULCClipboard.install()
        RiskAnalyticsMainView mainView = new RiskAnalyticsMainView(mainModel)
        mainView.init()
        mainFrame.contentPane.add(mainView.content)
        mainFrame.menuBar = mainView.menuBar
        UIUtils.setRootPane(mainFrame)
        mainFrame.visible = true
        mainFrame.toFront()
        mainFrame.addWindowListener([windowClosing: { WindowEvent e -> mainFrame.visible = false; handleEvent(e) }] as IWindowListener)

        ModelRegistry.instance.addListener(mainModel)
    }

    private void handleEvent(WindowEvent e) {
        def session = ULCSession.currentSession()
        CacheItemEventQueueService.getInstance().unregisterAllConsumersForSession(session)
        ModelRegistry.instance.removeListener(mainModel)
        traceLogManager.deactivateLogging()
        ExitAction.terminate()
    }


}

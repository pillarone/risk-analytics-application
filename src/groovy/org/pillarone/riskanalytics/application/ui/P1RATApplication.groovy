package org.pillarone.riskanalytics.application.ui

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.base.application.AbstractApplication
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.ULCFrame
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.util.Dimension
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.main.action.ExitAction
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.ulc.server.ULCMinimalSizeFrame
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.search.ModellingItemSearchService
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ulcjava.base.server.ULCSession

//used for standalone
class P1RATApplication extends AbstractApplication {

    private static Log LOG = LogFactory.getLog(P1RATApplication)

    ULCMinimalSizeFrame mainFrame = new ULCMinimalSizeFrame("Risk Analytics")
    RiskAnalyticsMainModel mainModel
    public static boolean CLOSE_WINDOW = false

    ModellingItemSearchService searchService

    public void start() {
        ClientContext.sendMessage("hideSplash");
        if (UserContext.isStandAlone()) {
            try {
                UserContext.setUserTimeZone(TimeZone.getTimeZone(System.getProperty("user.timezone")))
            } catch (Exception e) {
                LOG.error("Unable to determine user time zone - using UTC", e)
                UserContext.setUserTimeZone(TimeZone.getTimeZone("UTC"))
            }
        }
        searchService = ApplicationHolder.application.mainContext.getBean(ModellingItemSearchService)
        initMainView()
        searchService.registerSession(ULCSession.currentSession())
    }

    public void initMainView() {
        //init RiskAnalyticsMainModel after login
        mainModel = new RiskAnalyticsMainModel()
        mainFrame.defaultCloseOperation = ULCFrame.DO_NOTHING_ON_CLOSE
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
        mainFrame.addWindowListener([windowClosing: {WindowEvent e -> mainFrame.visible = false; handleEvent(e)}] as IWindowListener)
    }

    private void handleEvent(WindowEvent e) {
        searchService.unregisterSession(ULCSession.currentSession())
        ExitAction.terminate()
    }


}

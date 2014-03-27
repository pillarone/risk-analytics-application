package org.pillarone.riskanalytics.application.ui

import com.canoo.ulc.community.ulcclipboard.server.ULCClipboard
import com.ulcjava.applicationframework.application.Application
import com.ulcjava.base.application.ApplicationContext
import com.ulcjava.base.application.ClientContext
import com.ulcjava.base.application.event.IWindowListener
import com.ulcjava.base.application.event.WindowEvent
import com.ulcjava.base.application.util.Dimension
import grails.util.Holders
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.ui.base.model.modellingitem.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.log.TraceLogManager
import org.pillarone.riskanalytics.core.model.registry.ModelRegistry
import org.pillarone.ulc.server.ULCMinimalSizeFrame

import static com.ulcjava.base.shared.IWindowConstants.DO_NOTHING_ON_CLOSE
import static org.springframework.beans.factory.config.AutowireCapableBeanFactory.AUTOWIRE_BY_NAME

//used for standalone
class P1RATApplication extends Application {

    private static final Log LOG = LogFactory.getLog(P1RATApplication)

    ULCMinimalSizeFrame mainFrame = new ULCMinimalSizeFrame("Risk Analytics")
    RiskAnalyticsMainModel riskAnalyticsMainModel
    RiskAnalyticsMainView riskAnalyticsMainView
    ModellingInformationTableTreeModel navigationTableTreeModel
    TraceLogManager traceLogManager

    protected void startup() {
        initializeInjection()
        ClientContext.sendMessage("hideSplash");
        if (UserContext.standAlone) {
            try {
                UserContext.userTimeZone = TimeZone.getTimeZone(System.getProperty("user.timezone"))
            } catch (Exception e) {
                LOG.error("Unable to determine user time zone - using UTC", e)
                UserContext.userTimeZone = TimeZone.getTimeZone("UTC")
            }
        }
        initMainView()
        traceLogManager.activateLogging()
    }

    @Override
    void stop() {
        UlcSessionScope.destroy()
        super.stop()
    }

    @Override
    void stop(Throwable reason) {
        UlcSessionScope.destroy()
        super.stop(reason)
    }

    private void initializeInjection() {
        Holders.grailsApplication.mainContext.autowireCapableBeanFactory.autowireBeanProperties(this, AUTOWIRE_BY_NAME, false)
    }

    private void initMainView() {
        //init RiskAnalyticsMainModel after login
        mainFrame.defaultCloseOperation = DO_NOTHING_ON_CLOSE
        mainFrame.size = new Dimension(1000, 750)
        mainFrame.minimumSize = new Dimension(800, 600)

        //If argument is null, the window is centered on the screen.
        mainFrame.locationRelativeTo = null
        mainFrame.iconImage = UIUtils.getIcon("application.png")
        ULCClipboard.install()
        mainFrame.contentPane.add(riskAnalyticsMainView.content)
        mainFrame.menuBar = riskAnalyticsMainView.menuBar
        UIUtils.rootPane = mainFrame
        mainFrame.visible = true
        mainFrame.toFront()
        mainFrame.addWindowListener([windowClosing: { WindowEvent e -> mainFrame.visible = false; windowClosing() }] as IWindowListener)
        ModelRegistry.instance.addListener(navigationTableTreeModel)
    }

    private void windowClosing() {
        ModelRegistry.instance.removeListener(navigationTableTreeModel)
        traceLogManager.deactivateLogging()
        ApplicationContext.terminate()
    }
}

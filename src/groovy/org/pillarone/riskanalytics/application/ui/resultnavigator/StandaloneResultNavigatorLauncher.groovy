package org.pillarone.riskanalytics.application.ui.resultnavigator

import com.canoo.common.logging.LogManager
import com.canoo.common.logging.SimpleLogManager
import com.ulcjava.base.client.ISessionStateListener
import com.ulcjava.base.client.UISession
import com.ulcjava.container.local.server.LocalContainerAdapter
import org.pillarone.riskanalytics.application.environment.shared.UIManagerHelper
import org.pillarone.riskanalytics.application.ui.resultnavigator.view.ResultNavigator

class StandaloneResultNavigatorLauncher extends LocalContainerAdapter {

    @Override
    protected Class getApplicationClass() {
        StandaloneResultNavigator
    }

    public static void launch() {
        // UIManagerHelper.setLookAndFeel()
        UIManagerHelper.setTooltipDismissDelay()
        UIManagerHelper.setTextFieldUI()
        UIManagerHelper.setParserDelegator()
        LogManager.setLogManager(new SimpleLogManager())
        StandaloneResultNavigatorLauncher launcher = new StandaloneResultNavigatorLauncher()
        launcher.start()
        StandaloneSessionStateListener listener = new StandaloneSessionStateListener()
        launcher.clientSession.addSessionStateListener(listener)
        synchronized (listener) {
            listener.wait()
        }
    }
        
}

class StandaloneSessionStateListener implements ISessionStateListener {

    void sessionEnded(UISession session) throws Exception {
        println("PillarOne ResultNavigator application shutdown ... cleaning up")
        synchronized (this) {
            notifyAll()
        }
    }

    void sessionError(UISession session, Throwable reason) {
        println("PillarOne ResultNavigator application error..." + reason.getMessage())

    }

    void sessionStarted(UISession session) throws Exception {
        println("PillarOne ResultNavigator application started...")

    }
}

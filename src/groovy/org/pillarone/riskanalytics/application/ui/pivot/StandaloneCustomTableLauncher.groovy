package org.pillarone.riskanalytics.application.ui.pivot

import com.ulcjava.base.client.ISessionStateListener
import com.ulcjava.base.client.UISession
import com.ulcjava.base.shared.logging.LogManager
import com.ulcjava.base.shared.logging.SimpleLogManager
import com.ulcjava.container.local.server.LocalContainerAdapter
import org.pillarone.riskanalytics.application.environment.shared.UIManagerHelper
import org.pillarone.riskanalytics.application.ui.resultnavigator.view.ResultNavigator
import org.pillarone.riskanalytics.application.ui.resultnavigator.StandaloneResultNavigator

class StandaloneCustomTableLauncher extends LocalContainerAdapter {

    @Override
    protected Class getApplicationClass() {
        StandaloneCustomTable
    }

    public static void launch() {
        // UIManagerHelper.setLookAndFeel()
        UIManagerHelper.setTooltipDismissDelay()
        UIManagerHelper.setTextFieldUI()
        UIManagerHelper.setParserDelegator()
        LogManager.setLogManager(new SimpleLogManager())
        StandaloneCustomTableLauncher launcher = new StandaloneCustomTableLauncher()
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
        println("PillarOne CustomTable application shutdown ... cleaning up")
        synchronized (this) {
            notifyAll()
        }
    }

    void sessionError(UISession session, Throwable reason) {
        println("PillarOne CustomTable application error..." + reason.getMessage())

    }

    void sessionStarted(UISession session) throws Exception {
        println("PillarOne CustomTable application started...")

    }
}

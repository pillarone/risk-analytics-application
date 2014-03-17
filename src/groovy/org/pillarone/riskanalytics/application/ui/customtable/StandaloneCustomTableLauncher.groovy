package org.pillarone.riskanalytics.application.ui.customtable

import com.canoo.common.logging.LogManager
import com.canoo.common.logging.SimpleLogManager
import com.ulcjava.base.client.ISessionStateListener
import com.ulcjava.base.client.UISession
import com.ulcjava.container.local.server.LocalContainerAdapter
import org.pillarone.riskanalytics.application.environment.shared.UIManagerHelper

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
        synchronized (this) {
            notifyAll()
        }
    }

    void sessionError(UISession session, Throwable reason) {
    }

    void sessionStarted(UISession session) throws Exception {
    }
}

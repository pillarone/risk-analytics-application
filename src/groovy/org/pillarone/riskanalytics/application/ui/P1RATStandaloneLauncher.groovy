package org.pillarone.riskanalytics.application.ui

import com.ulcjava.base.client.ISessionStateListener
import com.ulcjava.base.client.UISession
import com.ulcjava.base.shared.logging.LogManager
import com.ulcjava.base.shared.logging.SimpleLogManager
import org.pillarone.riskanalytics.application.environment.shared.UIManagerHelper

class P1RATStandaloneLauncher {

    static void main(String[] args) {
        runApp()
    }

    static void start() {
        P1RATStandaloneRunner runner = runApp()

        StandaloneSessionStateListener listener = new StandaloneSessionStateListener()
        runner.getClientSession().addSessionStateListener(listener)
        synchronized (listener) {
            listener.wait()
        }
    }

    private static P1RATStandaloneRunner runApp() {
        LogManager logManager = new SimpleLogManager()
        LogManager.setLogManager(logManager)

        UIManagerHelper.setLookAndFeel()
        P1RATStandaloneRunner runner = new P1RATStandaloneRunner()
        runner.start()
        return runner
    }
}

class StandaloneSessionStateListener implements ISessionStateListener {

    void sessionEnded(UISession session) throws Exception {
        println("PillarOne application shutdown ... cleaning up")
        synchronized (this) {
            notifyAll()
        }
    }

    void sessionError(UISession session, Throwable reason) {
        println("PillarOne application error..." + reason.getMessage())

    }

    void sessionStarted(UISession session) throws Exception {
        println("PillarOne application started...")

    }
}

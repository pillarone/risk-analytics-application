package org.pillarone.riskanalytics.application.ui

import com.canoo.common.logging.LogManager
import com.canoo.common.logging.SimpleLogManager
import com.ulcjava.base.client.ISessionStateListener
import com.ulcjava.base.client.UISession
import groovy.transform.CompileStatic
import org.pillarone.riskanalytics.application.environment.shared.UIManagerHelper

@CompileStatic
class P1RATStandaloneLauncher {

    static void main(String[] args) {
        runApp()
    }

    static void start() {
        start(null)
    }

    static void start(ISessionStateListener customSessionStateListener) {
        P1RATStandaloneRunner runner = runApp()
        UISession clientSession = runner.getClientSession()

        if (customSessionStateListener != null) {
            clientSession.addSessionStateListener(customSessionStateListener)
        }

        StandaloneSessionStateListener listener = new StandaloneSessionStateListener()
        clientSession.addSessionStateListener(listener)
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

@CompileStatic
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

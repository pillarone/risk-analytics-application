package org.pillarone.riskanalytics.application.initialization;

import com.ulcjava.base.client.ISessionStateListener;
import com.ulcjava.base.client.UISession;

/**
 * An ULC session listener which shuts down external databases when the application is
 * terminated.
 */
public class DatabaseManagingSessionStateListener implements ISessionStateListener {

    private IExternalDatabaseSupport databaseSupport;

    public DatabaseManagingSessionStateListener(IExternalDatabaseSupport databaseSupport) {
        this.databaseSupport = databaseSupport;
    }

    /**
     * Does NOT start the database, because grails is already running at this point.
     * @param uiSession
     * @throws Exception
     */
    public void sessionStarted(UISession uiSession) throws Exception {
    }

    public void sessionEnded(UISession uiSession) throws Exception {
        databaseSupport.stopDatabase();
    }

    public void sessionError(UISession uiSession, Throwable throwable) {
    }
}

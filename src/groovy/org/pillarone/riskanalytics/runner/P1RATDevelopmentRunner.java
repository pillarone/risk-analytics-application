package org.pillarone.riskanalytics.runner;

import com.ulcjava.base.application.event.IRoundTripListener;
import com.ulcjava.base.application.event.RoundTripEvent;
import com.ulcjava.base.development.DevelopmentRunner;
import com.ulcjava.base.development.DevelopmentRunnerSessionProvider;
import com.ulcjava.base.server.ULCSession;
import org.codehaus.groovy.grails.commons.ApplicationHolder;
import org.codehaus.groovy.grails.support.PersistenceContextInterceptor;
import org.pillarone.riskanalytics.application.ui.P1RATApplication;
import org.springframework.context.ApplicationContext;

import java.util.Map;

public class P1RATDevelopmentRunner {

    public static void run() {
        DevelopmentRunner.setApplicationClass(P1RATApplication.class);
        DevelopmentRunner.setUseGui(true);
        DevelopmentRunner.setSessionProvider(new DevelopmentGrailsSessionProvider());
        DevelopmentRunner.run();
    }
}

class DevelopmentGrailsSessionProvider extends DevelopmentRunnerSessionProvider {

    @Override
    public ULCSession createSession() {
        ULCSession session = super.createSession();
        session.addRoundTripListener(new GrailsInitializerForLocalUlcApp(ApplicationHolder.getApplication().getMainContext()));
        return session;
    }
}

class GrailsInitializerForLocalUlcApp implements IRoundTripListener {

    GrailsInitializerForLocalUlcApp(ApplicationContext appCtx) {
        this.appCtx = appCtx;
    }

    ApplicationContext appCtx;

    public void roundTripDidStart(RoundTripEvent event) {
        final Map<String, PersistenceContextInterceptor> contextInterceptorMap = appCtx.getBeansOfType(PersistenceContextInterceptor.class);
        for (PersistenceContextInterceptor interceptort : contextInterceptorMap.values()) {
            interceptort.init();
        }
    }

    public void roundTripWillEnd(RoundTripEvent event) {
        final Map<String, PersistenceContextInterceptor> contextInterceptorMap = appCtx.getBeansOfType(PersistenceContextInterceptor.class);
        for (PersistenceContextInterceptor interceptort : contextInterceptorMap.values()) {
            interceptort.flush();
            interceptort.destroy();
        }
    }
}

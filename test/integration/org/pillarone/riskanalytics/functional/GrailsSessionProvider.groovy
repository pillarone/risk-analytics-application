package org.pillarone.riskanalytics.functional;

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */

import com.ulcjava.base.application.event.IRoundTripListener
import com.ulcjava.base.application.event.RoundTripEvent
import com.ulcjava.base.server.DefaultSessionProvider
import com.ulcjava.base.server.ULCSession
import grails.util.Holders
import org.codehaus.groovy.grails.support.PersistenceContextInterceptor
import org.springframework.context.ApplicationContext

public class GrailsSessionProvider extends DefaultSessionProvider {
    ULCSession createSession() {
        ULCSession session = super.createSession();
        session.addRoundTripListener(new GrailsInitializerForLocalUlcApp(Holders.grailsApplication.getMainContext()))
        return session
    }

}
class GrailsInitializerForLocalUlcApp implements IRoundTripListener {

    GrailsInitializerForLocalUlcApp(ApplicationContext appCtx) {
        this.appCtx = appCtx;
    }

    ApplicationContext appCtx;

    public void roundTripDidStart(RoundTripEvent event) {
        final Map<String, PersistenceContextInterceptor> contextInterceptorMap = appCtx.getBeansOfType(PersistenceContextInterceptor.class);
        for (PersistenceContextInterceptor interceptort: contextInterceptorMap.values()) {
            interceptort.init();
        }
    }

    public void roundTripWillEnd(RoundTripEvent event) {
        final Map<String, PersistenceContextInterceptor> contextInterceptorMap = appCtx.getBeansOfType(PersistenceContextInterceptor.class);
        for (PersistenceContextInterceptor interceptort: contextInterceptorMap.values()) {
            interceptort.flush();
            interceptort.destroy();
        }
    }
}

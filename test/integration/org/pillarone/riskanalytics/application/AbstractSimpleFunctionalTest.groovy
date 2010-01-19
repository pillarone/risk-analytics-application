package org.pillarone.riskanalytics.application

import com.ulcjava.testframework.standalone.AbstractSimpleStandaloneTestCase
import org.pillarone.riskanalytics.application.user.ApplicationUser

abstract class AbstractSimpleFunctionalTest extends AbstractSimpleStandaloneTestCase {

    private Throwable throwable


    final void start() {
        try {
            ApplicationUser.withTransaction {
                doStart()
            }
        } catch (Throwable t) {
            throwable = t
        }
    }

    abstract protected void doStart()

    void testInitialization() {
        assertNull "Error during doStart(): ${throwable?.message}", throwable
    }

}
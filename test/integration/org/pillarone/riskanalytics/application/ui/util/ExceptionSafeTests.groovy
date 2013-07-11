package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.core.log.TraceLogManager

class ExceptionSafeTests extends GroovyTestCase {

    TraceLogManager traceLogManager

    @Override
    protected void setUp() {
        super.setUp()
        traceLogManager.activateLogging()
    }

    @Override
    protected void tearDown() {
        traceLogManager.deactivateLogging()
        super.tearDown()
    }

    void testSimple() {
        ExceptionSafe.protect {
            throw new NullPointerException()
        }
    }

    void testReturnValuesAreRelayed() {
        def result = ExceptionSafe.protect {
            1
        }
        assertEquals 1, result
    }

}
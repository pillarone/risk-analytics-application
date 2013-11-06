package org.pillarone.riskanalytics.application.ui.util

import org.junit.After
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.core.log.TraceLogManager

import static org.junit.Assert.assertEquals

class ExceptionSafeTests {

    TraceLogManager traceLogManager

    @Before
    void setUp() {
        traceLogManager.activateLogging()
    }

    @After
    void tearDown() {
        traceLogManager.deactivateLogging()
    }

    @Test
    void testSimple() {
        ExceptionSafe.protect {
            throw new NullPointerException()
        }
    }

    @Test
    void testReturnValuesAreRelayed() {
        def result = ExceptionSafe.protect {
            1
        }
        assertEquals 1, result
    }

}
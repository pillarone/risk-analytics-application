package org.pillarone.riskanalytics.application.ui.util

class ExceptionSafeTests extends GroovyTestCase {

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
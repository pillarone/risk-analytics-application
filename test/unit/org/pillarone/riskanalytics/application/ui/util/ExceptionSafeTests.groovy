package org.pillarone.riskanalytics.application.ui.util

class ExceptionSafeTests extends GroovyTestCase {

    void testSimple() {
        ExceptionSafe.protect {
            throw new NullPointerException()
        }
    }

    void testSimpleWithOutput() {
        String trace
        def oldWriter = ExceptionSafe.out
        try {
            ExceptionSafe.out = new StringBuffer()
            ExceptionSafe.protect {
                throw new NullPointerException()
            }
            trace = ExceptionSafe.out.toString()
        } finally {
            ExceptionSafe.out = oldWriter
        }
        assert trace =~ /org.pillarone.riskanalytics.application.ui.util.ExceptionSafeTests.*ExceptionSafeTests.groovy:\d+/
        println "Just for the curious, here is the sanitized trace:"
        println trace
    }

    void testReturnValuesAreRelayed() {
        def result = ExceptionSafe.protect {
            1
        }
        assertEquals 1, result
    }

}
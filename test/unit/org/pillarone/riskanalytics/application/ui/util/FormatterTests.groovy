package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.core.parameterization.SimpleMultiDimensionalParameter
import org.pillarone.riskanalytics.application.ui.util.Formatter
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter

class FormatterTests extends GroovyTestCase {

    void testFormatList() {
        List values = [1.2d, 3.5d, 4]
        assertEquals "GERMAN List format", "[1,2; 3,5; 4]", Formatter.format(values, Locale.GERMAN)
        assertEquals "ENGLISH List format", "[1.2; 3.5; 4]", Formatter.format(values, Locale.ENGLISH)
        assertEquals "list containing text", "[1,2; foo; 2,2]", Formatter.format([1.2d, "foo", 2.2d], Locale.GERMAN)
    }

    void testFormatListOfList() {
        List values = [[1.2d, 3.5d, 4], [1.2d, 3.5d, 4]]
        String result = Formatter.format(values, Locale.GERMAN)
        println result
        assertEquals "GERMAN List format", "[[1,2; 3,5; 4]; [1,2; 3,5; 4]]", result
        assertEquals "ENGLISH List format", "[[1.2; 3.5; 4]; [1.2; 3.5; 4]]", Formatter.format(values, Locale.ENGLISH)
    }

    void testFormatMultiDimensionalParameter() {
        AbstractMultiDimensionalParameter mdp = new SimpleMultiDimensionalParameter([[1.2d, 3.5d, 4], [1.2d, 3.5d, 4]])
        assertEquals "GERMAN List format", "[[1,2; 3,5; 4]; [1,2; 3,5; 4]]", Formatter.format(mdp, Locale.GERMAN)
        assertEquals "ENGLISH List format", "[[1.2; 3.5; 4]; [1.2; 3.5; 4]]", Formatter.format(mdp, Locale.ENGLISH)
    }
}

package org.pillarone.riskanalytics.application.ui.util

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class UIUtilsTests extends GroovyTestCase {

    public void testAddBreakLines() {
        String names = "2011.09.27 11:59:24, 2011.09.27 11:59:40, 2011.09.27 11:59:44, 2011.09.27 11:59:48, 2011.09.27 11:59:51"
        names += ", 2011.09.27 11:59:54, 2011.09.27 11:59:57, 2011.09.27 12:00:00"
        String lines = UIUtils.addBreakLines(names, 60, ", ")
        assertTrue lines.indexOf("2011.09.27 11:59:24") != -1
        assertTrue lines.indexOf("2011.09.27 12:00:00") != -1
        assertEquals 8,lines.split(', ').size()

        names="test"
        lines = UIUtils.addBreakLines(names, 60, ", ")
        assertEquals "test", lines
    }
}

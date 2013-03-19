package org.pillarone.riskanalytics.application.ui.util

import java.text.NumberFormat
import java.text.DecimalFormatSymbols

class NumberParserTests extends GroovyTestCase {

    void testReturnTypes() {
        NumberParser parser = new NumberParser(Locale.defaultLocale)
        assertSame "parsing integer value", Integer, parser.parse("2").class
        assertSame "parsing double value", Double, parser.parse("2.5").class
        assertSame "parsing double value", Double, parser.parse("2.0").class
        assertSame "parsing string value", String, parser.parse("foo").class
    }

    void testParse_DefaultLocale() {
        parseValuesWithLocale([2, 20000, 2.0, 20000.20, 1E3, -1.23E4, "foo", "", null], Locale.defaultLocale)
    }

    void testParse_Locale_EN() {
        parseValuesWithLocale([2, 20000, 2.0, 20000.20, 1E3, -1.23E4, "foo", "", null], Locale.ENGLISH)
    }

    void testParse_Locale_DE() {
        parseValuesWithLocale([2, 20000, 2.0, 20000.20, 1E3, -1.23E4, "foo", "", null], Locale.GERMANY)
    }

    void testParse_Locale_DE_CH() {
        parseValuesWithLocale([2, 20000, 2.0, 20000.20, 1E3, -1.23E4, "foo", ""], new Locale("de", "ch"))
    }

    void testIsString() {
        NumberParser parser = new NumberParser(Locale.defaultLocale)
        def groupingSeparator = new DecimalFormatSymbols(Locale.default).groupingSeparator
        assertTrue '3A-56739', parser.isString('3A-56739')
        assertTrue '2008-01-01', parser.isString('2008-01-01')
        assertFalse '200.25', parser.isString('200.25')
        assertFalse "2${groupingSeparator}008.01", parser.isString("2${groupingSeparator}008.01")
        assertTrue "foo bar", parser.isString("foo bar")
        assertFalse parser.isString("-1")
        assertFalse parser.isString("-1.23")
        assertFalse parser.isString("-1E3")
        assertFalse parser.isString("1E3")
        assertFalse parser.isString("-1.23E3")
        assertFalse parser.isString("1.23E3")
    }

    private def parseValuesWithLocale(List values, Locale locale) {
        NumberFormat numberFormat = NumberFormat.getInstance(locale)
        NumberParser parser = new NumberParser(locale)
        values.each {
            def value = it
            if (it instanceof Number) {
                value = numberFormat.format(it)
            }
            assertEquals "parsing error with <$value>", it, parser.parse(value)
        }
    }
}
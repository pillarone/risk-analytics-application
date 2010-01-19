package org.pillarone.riskanalytics.application.ui.util

import java.text.NumberFormat
import org.springframework.util.NumberUtils

/**
 * This NumberParser converts the given String into a:
 * <ul>
 * <li> Double
 * <li> Integer
 * <li> String
 * </ul>
 *
 */

public class NumberParser {

    private Locale locale
    NumberFormat format

    public NumberParser(Locale locale) {
        this.locale = locale
        this.format = NumberFormat.getInstance(locale)
    }

    public def parse(String value) {
        def result = value

        if (value == null || value == "") {
            return result
        }

        if (!isString(value)) {
            //DecimalFormatSymbols decimalFormatSymbols = new DecimalFormatSymbols(locale)
            boolean containsDecimalSeparator = value =~ /[\\.,]/
            if (containsDecimalSeparator) {
                result = parseValue(value, Double)
            }
            else {
                result = parseValue(value, Integer)
            }
        }
        return result
    }

    private def parseValue(String value, Class target) {
        def result = null
        if (target == String) {
            result = value
        }
        try {
            result = NumberUtils.parseNumber(value, target, format)
        } catch (Exception e) {
            e.printStackTrace()
        }
        return result
    }

    public boolean isString(String value) {
        return value =~ /d*[a-zA-Z\\-]+/
    }
}
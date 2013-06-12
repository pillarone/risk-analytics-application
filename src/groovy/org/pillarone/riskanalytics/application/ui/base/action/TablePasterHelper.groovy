package org.pillarone.riskanalytics.application.ui.base.action

import groovy.transform.CompileStatic
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.NumberParser
import org.springframework.util.NumberUtils
import java.text.NumberFormat

@CompileStatic
class TablePasterHelper {

    private NumberParser numberParser
    private Locale locale

    TablePasterHelper(Locale locale) {
        this.locale = locale
        this.numberParser = new NumberParser(locale)
    }

    def fromString(String s, Class targetType) {
        switch (targetType) {
            case Integer:
                return toInteger(s)
            case Double:
                return toDouble(s)
            case Boolean:
                return toBoolean(s)
            case Date:
                return toDateTime(s)
            case DateTime:
                return toDateTime(s)
            case String:
                return s
            default:
                return numberParser.parse(s)
        }
    }

    private Integer toInteger(String s) {
        try {
            return NumberUtils.parseNumber(s, Integer, NumberFormat.getInstance(locale))
        } catch (IllegalArgumentException e) {
            throw new CopyPasteException(s, Integer)
        }
    }

    private Double toDouble(String s) {
        try {
            return NumberUtils.parseNumber(s, Double, NumberFormat.getInstance(locale))
        } catch (IllegalArgumentException e) {
            throw new CopyPasteException(s, Double)
        }
    }

    private Boolean toBoolean(String s) {
        if ("true".equalsIgnoreCase(s)) {
            return Boolean.TRUE
        } else if ("false".equalsIgnoreCase(s)) {
            return Boolean.FALSE
        } else {
            throw new CopyPasteException(s, Boolean)
        }
    }

    private DateTime toDateTime(String s) {
        List<String> formats = DateFormatUtils.getInputDateFormats()
        for (String format in formats) {
            try {
                DateTimeFormatter dateFormat = DateTimeFormat.forPattern(format)
                return dateFormat.parseDateTime(s);
            } catch (Exception ex) {
                //try the next format
            }
        }
        throw new CopyPasteException(s, DateTime, "The following formats are supported: ${formats}")
    }
}

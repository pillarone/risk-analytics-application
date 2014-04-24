package org.pillarone.riskanalytics.application.ui.util

import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.joda.time.DateTime

abstract class DateFormatUtils {

    //DateTimeFormatter is thread safe
    public static final String PARAMETER_DISPLAY_FORMAT = System.getProperty("DateFormatUtils.PARAMETER_DISPLAY_FORMAT","MMM dd, yyyy")
    private static DateTimeFormatter detailedFormatter
    private static DateTimeFormatter simpleFormatter
    private static List<String> inputDateFormats = ["yyyy-MM-dd", "dd.MM.yyyy", "yyyy/MM/dd", "dd/MM/yyyy"]
    //PMO-2746 was "dd.MM.yyyy, HH:mm z" which broke ordering of date columns
    private static final String DETAILED_FORMATTER_STRING = System.getProperty("DateFormatUtils.DETAILED_FORMATTER_STRING","yyyy.MM.dd, HH:mm:ss z")
    private static final String SIMPLE_FORMATTER_STRING   = System.getProperty("DateFormatUtils.SIMPLE_FORMATTER_STRING","dd.MM.yyyy")

    public static DateTimeFormatter getDetailedDateFormat() {
        if (detailedFormatter == null) {
            detailedFormatter = getDateFormat(DETAILED_FORMATTER_STRING)
        }
        return detailedFormatter
    }

    public static DateTimeFormatter getSimpleDateFormat() {
        if (simpleFormatter == null) {
            simpleFormatter = getDateFormat(SIMPLE_FORMATTER_STRING)
        }
        return simpleFormatter
    }

    public static DateTimeFormatter getDateFormat(String formatString) {
        TimeZone timeZone = UserContext.userTimeZone
        if (timeZone == null) {
            timeZone == TimeZone.default
        }
        return DateTimeFormat.forPattern(formatString).withLocale(LocaleResources.locale).withZone(DateTimeZone.forTimeZone(timeZone))
    }

    public static String formatDetailed(DateTime date) {
        if (date == null) {
            return ""
        }
        return detailedDateFormat.print(date)
    }

    public static String formatSimple(DateTime date) {
        if (date == null) {
            return ""
        }
        return simpleDateFormat.print(date)
    }

    public static List<String> getInputDateFormats() {
        return (List<String>) inputDateFormats.clone()
    }
}

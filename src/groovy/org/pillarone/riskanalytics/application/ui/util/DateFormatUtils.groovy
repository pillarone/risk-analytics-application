package org.pillarone.riskanalytics.application.ui.util

import org.joda.time.DateTimeZone
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.pillarone.riskanalytics.application.UserContext
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.joda.time.DateTime

abstract class DateFormatUtils {

    //DateTimeFormatter is thread safe
    private static DateTimeFormatter detailedFormatter
    private static List<String> inputDateFormats = ["yyyy-MM-dd", "dd.MM.yyyy", "yyyy/MM/dd", "dd/MM/yyyy"]
    public static final String PARAMETER_DISPLAY_FORMAT = "MMM dd, yyyy"

    public static DateTimeFormatter getDetailedDateFormat() {
        if (detailedFormatter == null) {
            detailedFormatter = getDateFormat("dd.MM.yyyy, HH:mm z")
        }
        return detailedFormatter
    }

    public static DateTimeFormatter getDateFormat(String formatString) {
        TimeZone timeZone = UserContext.userTimeZone
        if (timeZone == null) {
            timeZone == TimeZone.getDefault()
        }
        return DateTimeFormat.forPattern(formatString).withLocale(LocaleResources.locale).withZone(DateTimeZone.forTimeZone(timeZone))
    }

    public static String formatDetailed(DateTime date) {
        if (date == null) {
            return ""
        }
        return getDetailedDateFormat().print(date)
    }

    public static List<String> getInputDateFormats() {
        return (List<String>) inputDateFormats.clone()
    }
}

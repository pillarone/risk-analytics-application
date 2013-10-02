package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.core.parameterization.ConstrainedMultiDimensionalParameter

import java.text.NumberFormat
import java.text.SimpleDateFormat
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.parameterization.view.MultiDimensionalCellRenderer
import org.pillarone.riskanalytics.core.parameterization.AbstractMultiDimensionalParameter
import org.pillarone.riskanalytics.core.components.ComponentUtils

/**
 Format a MultiDimensionalParam for the parameter view, where only an indication of the
 backing data should be shown.
 These methods are used for the cell value itself. The tooltip content is defined in{@link MultiDimensionalCellRenderer}
 */
public class Formatter {

    public static final int MAX_DISPLAY_COLS = 6
    public static final int MAX_DISPLAY_ROWS = 4

    static String format(AbstractMultiDimensionalParameter mdp, Locale locale) {
        List values = mdp.values
        if (!values) return ''
        def cols = mdp.columnCount - mdp.titleColumnCount
        def rows = mdp.rowCount - mdp.titleRowCount
        if (rows == 0 || rows > MAX_DISPLAY_ROWS || cols > MAX_DISPLAY_COLS) return "<$cols/$rows>"
        StringBuilder result = new StringBuilder()
        result << format(values, locale)
        return result.toString()
    }

    static String format(ConstrainedMultiDimensionalParameter mdp, Locale locale) {
        List values = mdp.values
        if (!values) return ''
        def cols = mdp.columnCount - mdp.titleColumnCount
        def rows = mdp.rowCount - mdp.titleRowCount
        if (rows == 0 || rows > MAX_DISPLAY_ROWS || cols > MAX_DISPLAY_COLS) return "<$cols/$rows>"
        StringBuilder result = new StringBuilder()
        mdp.columnCount.times {

        }
        result << format(values, locale, mdp)
        return result.toString()
    }

    static String format(List list, Locale locale, ConstrainedMultiDimensionalParameter mdp = null, int index = 0) {
        if (list.any { it instanceof List }) {
            StringBuilder result = new StringBuilder()
            String results = ""
            for (int i = 0; i < list.size(); i++) {
                results += format(list[i], locale, mdp, i) + "; "
            }
            result << "["
            result << results
            result << "]"
            return result.toString()
        }

        List values = list
        NumberFormat format = NumberFormat.getInstance(locale)
        values = list.collect {
            if (it instanceof Number) {
                return format.format(it)
            } else if (it instanceof DateTime) {
                return new SimpleDateFormat(DateFormatUtils.PARAMETER_DISPLAY_FORMAT).format(it.toDate())
            } else {
                String displayName = ComponentUtils.getNormalizedName(String.valueOf(it))
                if (mdp && mdp.constraints.getColumnType(index)?.isEnum()) {
                    String enumDisplayName = I18NUtils.findEnumDisplayName(mdp.constraints.getColumnType(index), it)
                    if (enumDisplayName) {
                        displayName = enumDisplayName
                    }
                }
                return displayName
            }
        }
        String joinValue = values.join("; ")
        if (values.size() == 0 || joinValue == "") {
            return ""
        }

        return "[${joinValue}]".toString()
    }
}

package org.pillarone.riskanalytics.application.ui.util

import org.pillarone.riskanalytics.application.ui.util.NumberParser
import org.pillarone.riskanalytics.application.ui.util.UIUtils

public class TableDataParser {

    Locale locale
    String columnSeparator = '\t'
    String lineSeparator = '\n'


    List parseTableData(String stringData) {

        NumberParser numberParser = locale != null ? new NumberParser(locale) : UIUtils.getNumberParser()

        String[] lineStrings = stringData.split(lineSeparator)
        List lines = new ArrayList(lineStrings.length)
        lineStrings.each {String line ->
            lines << line.split(columnSeparator).collect {numberParser.parse(it)}
        }

        return lines
    }
}
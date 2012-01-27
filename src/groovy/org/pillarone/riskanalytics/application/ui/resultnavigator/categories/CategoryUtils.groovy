package org.pillarone.riskanalytics.application.ui.resultnavigator.categories


/**
 * @author martin.melchior
 */
class CategoryUtils {

    /**
     * Utility method to parse from a String that contains 'words' separated by comma's a List of String.
     * @param str
     * @return
     */

    public static List<String> parseList(String str) {
        String[] array = str.split(",")
        List<String> values = []
        for (String x : array) {
            x = x.trim()
            values.add(x)
        }
        return values
    }

    /**
     * Utility method to write a List of String to a single String with the elements of the list separated by comma's.
     * @param list
     * @return
     */
    static String writeList(List<String> list) {
        StringBuilder builder = new StringBuilder()
        for (String value : list) {
            builder.append( value + " , ")
        }
        String value = builder.toString()
        value = value.trim()
        if (value.endsWith(",")) {
            value = value[0..-2]
            value.trim()
        }
        return value
    }
}

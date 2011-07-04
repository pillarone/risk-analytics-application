package org.pillarone.riskanalytics.application.util

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ReportUtils {

    public static void addList(Map target, Object key, List values) {
        if (values && values.size() > 0) {
            target.put(key, values)
        }
    }
}

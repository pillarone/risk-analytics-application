package org.pillarone.riskanalytics.application.reports.gira.model

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class SuffixPathFilter implements IPathFilter {
    List<String> suffixes

    public SuffixPathFilter(List<String> suffixes) {
        this.suffixes = suffixes
    }

    boolean accept(String path) {
        for (String suffix: suffixes) {
            if (path.endsWith(":" + suffix)) {
                return true
            }

        }
        return false
    }
}

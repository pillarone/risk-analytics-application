package org.pillarone.riskanalytics.application.reports.bean

class ReportWaterfallDataBean implements Comparable  {
    double value
    String line

    public int compareTo(Object o) {
        value - o.value
    }
}
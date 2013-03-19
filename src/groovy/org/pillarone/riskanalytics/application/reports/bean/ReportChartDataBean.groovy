package org.pillarone.riskanalytics.application.reports.bean

public class ReportChartDataBean {
    double x
    double y
    String line
    int period

    public String toString(){
        "new ReportChartDataBean(x: ${x},y: ${y}, line: \"${line}\", period: ${period})"
    }

    public boolean equals(Object obj) {
        return x == obj.x && y == obj.y && line == obj.line && period == obj.period
    }
}
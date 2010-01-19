package org.pillarone.riskanalytics.application.reports


public interface ReportModel {

    public Collection prepareData()

    public Map getParameters()

    public String getReportFileName()
}
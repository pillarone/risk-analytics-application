package org.pillarone.riskanalytics.application.ui.chart.model

import com.ulcjava.base.application.ULCSpinnerNumberModel

interface PDFChartViewModel {
    public ULCSpinnerNumberModel getSpinnerModel()

    public void setBandwith(double value)

}
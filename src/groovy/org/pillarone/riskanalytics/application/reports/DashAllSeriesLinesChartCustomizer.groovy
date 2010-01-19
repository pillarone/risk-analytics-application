package org.pillarone.riskanalytics.application.reports

import net.sf.jasperreports.engine.JRChartCustomizer
import org.jfree.chart.JFreeChart
import net.sf.jasperreports.engine.JRChart
import org.jfree.chart.plot.XYPlot
import java.awt.Stroke
import java.awt.BasicStroke

public class DashAllSeriesLinesChartCustomizer implements JRChartCustomizer {

    public void customize(JFreeChart jFreeChart, JRChart jrChart) {
        XYPlot plot = jFreeChart.plot
        plot.renderer.setDrawSeriesLineAsPath(true)
        plot.getSeriesCount().times {int seriesIndex ->
            Stroke s = new BasicStroke(1.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, [5.0f, 3.0f] as float[], 0.0f);
            plot.renderer.setSeriesStroke(seriesIndex, s)
        }
    }

}
package org.pillarone.modelling.reports

import groovy.transform.CompileStatic
import net.sf.jasperreports.engine.JRChartCustomizer
import org.jfree.chart.JFreeChart
import net.sf.jasperreports.engine.JRChart
import org.jfree.chart.plot.XYPlot

import java.awt.Stroke
import java.awt.BasicStroke

//TODO (db) is this class still required ?
public class DashNetSeriesLinesChartCustomizer implements JRChartCustomizer {

    public void customize(JFreeChart jFreeChart, JRChart jrChart) {
        XYPlot plot = jFreeChart.getXYPlot()
        plot.renderer.setDrawSeriesLineAsPath(true)
        plot.renderer.setBaseItemLabelsVisible(false)
        plot.getSeriesCount().times {int seriesIndex ->
            if (plot.getDataset().getSeriesKey(seriesIndex) == "net") {
                Stroke s = new BasicStroke(2.0f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1.0f, [5.0f, 3.0f] as float[], 0.0f);
                plot.renderer.setSeriesStroke(seriesIndex, s)
            }
        }
        new RemoveLegendBorderCustomizer().customize(jFreeChart, jrChart)
    }

}
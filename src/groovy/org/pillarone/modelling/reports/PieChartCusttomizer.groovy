package org.pillarone.modelling.reports

import net.sf.jasperreports.engine.JRChartCustomizer
import org.jfree.chart.JFreeChart
import net.sf.jasperreports.engine.JRChart
import org.jfree.chart.plot.PiePlot
import java.awt.Color
import org.jfree.chart.labels.PieSectionLabelGenerator
import org.jfree.data.general.PieDataset
import java.text.AttributedString


public class PieChartCusttomizer implements JRChartCustomizer {

    public void customize(JFreeChart jFreeChart, JRChart jrChart) {
        PiePlot plot = jFreeChart.plot
        plot.labelOutlinePaint = null
        plot.labelShadowPaint = null
        plot.labelBackgroundPaint = null
        plot.setLabelGenerator new PieChartLabelGenerator()
        plot.shadowPaint = null
        plot.setSectionOutlinePaint(Color.white)
        new RemoveLegendBorderCustomizer().customize(jFreeChart, jrChart)
    }

}

class PieChartLabelGenerator implements PieSectionLabelGenerator {

    public String generateSectionLabel(PieDataset pieDataset, Comparable comparable) {
        double sum = 0
        pieDataset.keys.each {
            sum += pieDataset.getValue(it)
        }
        double percent = pieDataset.getValue(comparable) * 100 / sum
        return "" + Math.round(percent) + " %"
    }

    public AttributedString generateAttributedSectionLabel(PieDataset pieDataset, Comparable comparable) {
        new AttributedString(generateSectionLabel(pieDataset, comparable))
    }

}
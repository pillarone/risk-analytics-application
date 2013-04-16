package org.pillarone.modelling.reports

import groovy.transform.CompileStatic
import net.sf.jasperreports.engine.JRChartCustomizer
import org.jfree.chart.JFreeChart
import net.sf.jasperreports.engine.JRChart
import org.jfree.chart.block.BlockBorder

@CompileStatic
public class RemoveLegendBorderCustomizer implements JRChartCustomizer{

    public void customize(JFreeChart jFreeChart, JRChart jrChart) {
        jFreeChart?.getLegend()?.frame = BlockBorder.NONE
    }

}
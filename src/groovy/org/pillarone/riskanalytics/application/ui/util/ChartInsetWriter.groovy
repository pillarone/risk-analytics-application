package org.pillarone.riskanalytics.application.ui.util

import com.ulcjava.base.application.ULCAlert
import java.awt.geom.Rectangle2D
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import org.jfree.chart.JFreeChart
import org.jfree.chart.annotations.XYShapeAnnotation
import org.jfree.chart.annotations.XYTextAnnotation
import org.jfree.ui.TextAnchor
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.dataaccess.function.Percentile
import org.pillarone.riskanalytics.core.output.SimulationRun

class ChartInsetWriter {
    protected double insetYPosition
    protected double insetHeight
    private int insetLineCount

    private static double lineOffset = 0.03
    private static java.awt.Font insetFont = new java.awt.Font("Verdana", java.awt.Font.PLAIN, 12)
    private DecimalFormat decimalFormat

    JFreeChart chart
    double offset
    double x

    public ChartInsetWriter(double insetHeight) {
        Locale locale = LocaleResources.getLocale()
        decimalFormat = new DecimalFormat("###,###.00", new DecimalFormatSymbols(locale))
        this.insetHeight = insetHeight
    }

    public void addInset(JFreeChart chart, Closure write) {
        this.chart = chart
        insetYPosition = insetHeight * (chart.XYPlot.rangeAxis.upperBound - chart.XYPlot.rangeAxis.lowerBound)
        lineOffset = (chart.XYPlot.rangeAxis.upperBound - chart.XYPlot.rangeAxis.lowerBound) / 25

        double upperBoundXAxis = chart.getXYPlot().getDomainAxis().getUpperBound()
        double lowerBoundXAxis = chart.getXYPlot().getDomainAxis().getLowerBound()
        x = (upperBoundXAxis - lowerBoundXAxis) * 0.6 + lowerBoundXAxis
        offset = upperBoundXAxis * 0.96 - x
        double borderYOffset = lineOffset / 3
        double topY = insetYPosition + borderYOffset
        double leftX = x * 0.99
        double rightX = upperBoundXAxis * 0.98
        insetLineCount = 0

        write.call([this])

        if (insetLineCount != 0) {
            Rectangle2D rectangle = new Rectangle2D.Double(leftX, topY - (insetLineCount * lineOffset), (rightX - leftX), insetLineCount * lineOffset + 2 * borderYOffset)
            chart.getXYPlot().addAnnotation(new XYShapeAnnotation(rectangle))
        }
    }

    public void writeInset(title, value) {
        XYTextAnnotation annotation = new XYTextAnnotation("$title:", x, insetYPosition)
        annotation.setTextAnchor(TextAnchor.BASELINE_LEFT)
        annotation.setFont(insetFont)
        chart.getXYPlot().addAnnotation(annotation)

        annotation = new XYTextAnnotation(decimalFormat.format(value), x + offset, insetYPosition)
        annotation.setTextAnchor(TextAnchor.BASELINE_RIGHT)
        annotation.setFont(insetFont)
        chart.getXYPlot().addAnnotation(annotation)
        this.insetYPosition -= lineOffset
        insetLineCount++
    }

    public void writePercentiles(List list, SimulationRun simulationRun, int period, def node) {
        list.each {
            Percentile percentile = new Percentile(percentile: it)
            double value = percentile.evaluate(simulationRun, 0, node)
            writeInset("$it% percentile", value)
        }
    }

    public void writeEmptyLine() {
        this.insetLineCount++; this.insetYPosition -= lineOffset
    }

    public JFreeChart createErrorMessageChart(String errorMessage) {
//        JFreeChart chart = ChartFactory.createScatterPlot(title, null, null, null, PlotOrientation.VERTICAL, true, false, false)
//        chart.getXYPlot().setNoDataMessage errorMessage
        new ULCAlert("Chart Error", errorMessage, "ok").show()
        return null
    }
}
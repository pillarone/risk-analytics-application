package org.pillarone.riskanalytics.application.reports

import java.awt.Color
import java.text.SimpleDateFormat
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import net.sf.jasperreports.renderers.JCommonDrawableRenderer
import org.jfree.chart.ChartFactory
import org.jfree.chart.JFreeChart
import org.jfree.chart.axis.CategoryAxis
import org.jfree.chart.axis.CategoryLabelPositions
import org.jfree.chart.plot.PlotOrientation
import org.jfree.chart.renderer.category.BarRenderer
import org.jfree.chart.renderer.category.WaterfallBarRenderer
import org.jfree.data.category.DefaultCategoryDataset
import org.pillarone.riskanalytics.application.reports.bean.PropertyValuePairBean
import org.pillarone.riskanalytics.application.reports.bean.ReportWaterfallDataBean
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.jfree.data.xy.XYSeries
import org.jfree.data.xy.XYSeriesCollection
import java.awt.BasicStroke
import org.pillarone.riskanalytics.application.ui.chart.model.ChartViewModel
import org.jfree.ui.RectangleEdge
import org.jfree.ui.HorizontalAlignment
import org.jfree.chart.title.LegendTitle
import org.jfree.chart.title.TextTitle
import java.awt.Font
import org.jfree.data.Range
import org.pillarone.riskanalytics.application.util.ReportUtils

class JasperChartUtils {

    public static List<Color> seriesColor = [new Color(247, 139, 0), new Color(247, 139, 0), new Color(247, 139, 0), new Color(247, 139, 0), new Color(247, 139, 0)]

    public static JCommonDrawableRenderer generateWaterfallChart(List<ReportWaterfallDataBean> data) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        data.eachWithIndex {ReportWaterfallDataBean bean, int index ->
            dataset.addValue(bean.value, "", bean.line);
        }
        JFreeChart chart = ChartFactory.createWaterfallChart(
                "",
                "",
                "[in currency units]",
                dataset,
                PlotOrientation.VERTICAL,
                false,
                true,
                false
        )
        chart.setBackgroundPaint(Color.WHITE)
        WaterfallBarRenderer renderer = (BarRenderer) chart.getPlot().getRenderer();
        renderer.firstBarPaint = seriesColor[0]
        renderer.positiveBarPaint = seriesColor[0]
        renderer.negativeBarPaint = new Color(173, 228, 142)
        renderer.lastBarPaint = seriesColor[1]

        CategoryAxis domainAxis = chart.getPlot().getDomainAxis();
        domainAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        return new JCommonDrawableRenderer(chart)
    }

    public static JCommonDrawableRenderer generatePDFChart(Map seriesMap, Map colorsMap) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        JFreeChart chart = ChartFactory.createXYLineChart("", "[in currency units]", "", dataset, PlotOrientation.VERTICAL, true, true, false);
        int seriesIndex = 0
        List maxYs = []
        seriesMap.each {String title, List xyPairs ->
            maxYs << ReportUtils.maxYValue(xyPairs)
            XYSeries series = new XYSeries(title);
            xyPairs.each {List xyPair ->
                series.add(xyPair[0], xyPair[1])

            }
            dataset.addSeries(series)

            BasicStroke thickLine = new BasicStroke(ChartViewModel.chartLineThickness)
            chart.getPlot().getRenderer(0).setSeriesStroke(seriesIndex, thickLine)
            chart.getPlot().getRenderer(0).setSeriesPaint seriesIndex, colorsMap.get(title)
            seriesIndex++
        }

        double yMaxValue = ReportUtils.getMaxValue(maxYs)
        chart.getXYPlot().getRangeAxis().setRange(0.0, yMaxValue + (0.1 * yMaxValue))
        chart.setBackgroundPaint(Color.WHITE)
        chart.setTitle(new TextTitle("Probabillity Density (Adaptive Gauss Kernel Estimate)", new Font("Verdana", Font.PLAIN, 10)))
        LegendTitle legend = chart.getLegend();
        legend.setPosition(RectangleEdge.BOTTOM);
        legend.setHorizontalAlignment(HorizontalAlignment.LEFT);
        legend.setMargin 5, 50, 5, 5
        legend.setBorder(0, 0, 0, 0)
        return new JCommonDrawableRenderer(chart)
    }

    public static JRBeanCollectionDataSource createSimulationSettingsDataSource(Simulation simulation, boolean small = false) {
        Collection currentValues = new ArrayList<PropertyValuePairBean>()

        if (small) {
            currentValues << new PropertyValuePairBean(property: "Model", value: "$simulation.modelClass.simpleName v${simulation.modelVersionNumber.toString()}")
            currentValues << new PropertyValuePairBean(property: "Parameterization", value: "$simulation.parameterization.name v${simulation.parameterization.versionNumber.toString()}")
            currentValues << new PropertyValuePairBean(property: "End Date", value: DateFormatUtils.formatDetailed(simulation.end))
        } else {
            currentValues << new PropertyValuePairBean(property: "Name", value: simulation.name)
            currentValues << new PropertyValuePairBean(property: "End Date", value: DateFormatUtils.formatDetailed(simulation.end))
            currentValues << new PropertyValuePairBean(property: "Comment", value: simulation.comment ? simulation.comment : "")
            currentValues << new PropertyValuePairBean(property: "Model", value: "$simulation.modelClass.simpleName v${simulation.modelVersionNumber.toString()}")
            currentValues << new PropertyValuePairBean(property: "Structure", value: "$simulation.structure.name v${simulation.structure.versionNumber.toString()}")
            currentValues << new PropertyValuePairBean(property: "Parameterization", value: "$simulation.parameterization.name v${simulation.parameterization.versionNumber.toString()}")
            currentValues << new PropertyValuePairBean(property: "Result Template", value: "$simulation.template.name v${simulation.template.versionNumber.toString()}")
            currentValues << new PropertyValuePairBean(property: "Periods", value: simulation.periodCount.toString())
            int simulationDuration = (simulation.end.millis - simulation.start.millis) / 1000
            currentValues << new PropertyValuePairBean(property: "Completed Iterations", value: "${simulation.numberOfIterations.toString()} in ${simulationDuration} secs")
        }

        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource

    }
}
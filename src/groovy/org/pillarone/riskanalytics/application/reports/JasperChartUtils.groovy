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
                "[in 1000 EUR]",
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
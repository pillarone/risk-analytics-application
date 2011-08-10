package org.pillarone.riskanalytics.application.ui.chart.model

import com.ulcjava.base.application.ULCRootPane
import org.jfree.chart.JFreeChart
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.base.model.EnumComboBoxModel
import org.pillarone.riskanalytics.application.ui.base.model.IModelChangedListener
import org.pillarone.riskanalytics.application.ui.chart.view.PDFPropertiesViewDialog
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeNode
import org.pillarone.riskanalytics.application.ui.chart.model.*

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class DistributionChartsViewModel extends ChartViewModel implements IModelChangedListener {


    ChartViewModel strategyModel
    EnumComboBoxModel typeComboBoxModel = new EnumComboBoxModel(DistributionTyp.values() as Object[], DistributionTyp.PDF, true)
    EnumComboBoxModel methodComboBoxModel = new EnumComboBoxModel(DistributionMethod.values() as Object[], DistributionMethod.NONE, false)


    public DistributionChartsViewModel(String title, SimulationRun simulationRun, List<ResultTableTreeNode> nodes) {
        super(title, simulationRun, nodes, 0.8)
        strategyModel = new PDFRawChartViewModel("Probability Density", simulationRun, nodes)
        strategyModel.addListener(this)
    }

    public JFreeChart getChart() {
        strategyModel.getChart()
    }

    Map getDataTable() {
        strategyModel.getDataTable()
    }

    public ChartProperties getChartProperties() {
        strategyModel.chartProperties
    }

    public void setChartProperties(ChartProperties chartProperties) {
        strategyModel.chartProperties = chartProperties
    }

    public String getTitle() {
        strategyModel.title
    }

    public void changeStrategy() {
        if (typeComboBoxModel.selectedEnum == DistributionTyp.PDF) {
            switch (methodComboBoxModel.selectedEnum) {
                case DistributionMethod.GAUSS:
                    if (strategyModel instanceof PDFGaussKernelEstimateChartViewModel) return;
                    strategyModel = new PDFGaussKernelEstimateChartViewModel("Probability Density (Gauss Kernel Estimate)", simulationRun, nodes)
                    break
                case DistributionMethod.GAUSS_ADAPTIVE:
                    if (strategyModel instanceof PDFAdaptiveKernelBandwidthEstimatorChartViewModel) return;
                    strategyModel = new PDFAdaptiveKernelBandwidthEstimatorChartViewModel("Probability Density (Adaptive Gauss Kernel Estimate)", simulationRun, nodes)
                    break
                case DistributionMethod.NONE:
                    if (strategyModel instanceof PDFRawChartViewModel) return;
                    strategyModel = new PDFRawChartViewModel("Probability Density", simulationRun, nodes)
                    break

            }
        } else if (typeComboBoxModel.selectedEnum == DistributionTyp.CDF) {
            switch (methodComboBoxModel.selectedEnum) {
                case DistributionMethod.GAUSS:
                    if (strategyModel instanceof CDFGaussKernelEstimateChartViewModel) return;
                    strategyModel = new CDFGaussKernelEstimateChartViewModel("Cumulative Distribution (Gauss Kernel Estimate)", simulationRun, nodes)
                    break
                case DistributionMethod.GAUSS_ADAPTIVE:
                    if (strategyModel instanceof CDFAdaptiveKernelBandwidthEstimatorChartViewModel) return;
                    strategyModel = new CDFAdaptiveKernelBandwidthEstimatorChartViewModel("Cumulative Distribution (Adaptive Gauss Kernel Estimate)", simulationRun, nodes)
                    break
                case DistributionMethod.NONE:
                    if (strategyModel instanceof CDFRawChartViewModel) return;
                    strategyModel = new CDFRawChartViewModel("Cumulative Distribution", simulationRun, nodes)
                    break

            }
        }
        strategyModel.addListener(this)
        fireModelChanged()
    }

    public boolean isSettingsEnabled() {
        if (typeComboBoxModel.selectedEnum == DistributionTyp.PDF) {
            switch (methodComboBoxModel.selectedEnum) {
                case DistributionMethod.GAUSS:
                    return nodes.size() > 0
                case DistributionMethod.GAUSS_ADAPTIVE:
                    return nodes.size() > 0
            }
        } else if (typeComboBoxModel.selectedEnum == DistributionTyp.CDF) {
            switch (methodComboBoxModel.selectedEnum) {
                case DistributionMethod.GAUSS:
                    return false
                case DistributionMethod.GAUSS_ADAPTIVE:
                    return false
            }
        }
        return false
    }

    void fireModelChanged() {
        strategyModel.showLine = showLine
        super.fireModelChanged()
    }

    public void modelChanged() {
        fireModelChanged()
    }

    public def showProperties(ULCRootPane rootPane) {
        new PDFPropertiesViewDialog(strategyModel, rootPane)
    }

}

enum DistributionTyp {
    CDF, PDF
}
enum DistributionMethod {
    GAUSS_ADAPTIVE("Adaptive Gauss"),
    GAUSS("Gauss"),
    NONE("None")


    private String displayName

    private DistributionMethod(String displayName) {
        this.@displayName = displayName
    }

    public String toString() {
        return displayName
    }
}
package org.pillarone.riskanalytics.application.util

import net.sf.jasperreports.engine.data.JRMapCollectionDataSource
import net.sf.jasperreports.renderers.JCommonDrawableRenderer
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.simulation.view.impl.SimulationConfigurationView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.reports.bean.PropertyValuePairBean
import org.pillarone.riskanalytics.application.ui.base.model.ModellingInformationTableTreeModel
import org.pillarone.riskanalytics.application.ui.result.view.ResultSettingsView
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.Parameterization

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ReportUtils {

    public static void addList(Map target, Object key, List values) {
        if (values && values.size() > 0) {
            target.put(key, values)
        }
    }

    public static JRMapCollectionDataSource getRendererDataSource(String key, JCommonDrawableRenderer renderer) {
        Map simpleMasterMap = new HashMap();
        simpleMasterMap.put(key, renderer);
        List simpleMasterList = new ArrayList();
        simpleMasterList.add(simpleMasterMap);
        return new JRMapCollectionDataSource(simpleMasterList)
    }

    public static JRMapCollectionDataSource getPDFChartAndCommentsInfoDataSource(JRBeanCollectionDataSource comments, JRBeanCollectionDataSource values, String pageTitle) {
        Map simpleMasterMap = new HashMap();
        simpleMasterMap.put("comments", comments);
        simpleMasterMap.put("fieldFunctionValues", values);
        simpleMasterMap.put("pageTitle", pageTitle)
        List simpleMasterList = new ArrayList();
        simpleMasterList.add(simpleMasterMap);
        return new JRMapCollectionDataSource(simpleMasterList)
    }

    static JRBeanCollectionDataSource getItemInfo(Parameterization parameterization) {
        Collection currentValues = new ArrayList<PropertyValuePairBean>()
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "Name"), value: parameterization.name + " v" + parameterization.versionNumber.toString())
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "Model"), value: parameterization.modelClass.simpleName)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "State"), value: parameterization.status)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "Tags"), value: parameterization?.tags?.join(","))
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "Owner"), value: (parameterization.creator ? parameterization.creator.username : ""))
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "Created"), value: DateFormatUtils.formatDetailed(parameterization.creationDate))
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "LastUpdateBy"), value: (parameterization.lastUpdater ? parameterization.lastUpdater.username : ""))
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "LastModification"), value: parameterization.modificationDate ? DateFormatUtils.formatDetailed(parameterization.modificationDate) : "")
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    static JRBeanCollectionDataSource getItemInfo(Simulation simulation) {
        Collection currentValues = new ArrayList<PropertyValuePairBean>()

        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "Name"), value: simulation.name)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "Model"), value: simulation.modelClass.simpleName)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "Parameter"), value: simulation.parameterization.name + " v" + simulation.parameterization.versionNumber.toString())
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "ResultTemplate"), value: simulation.template.name + " v" + simulation.template.versionNumber.toString())
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "NumberOfPeriods"), value: simulation.periodCount)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "NumberOfIterations"), value: simulation.numberOfIterations)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ResultSettingsView.class, "randomSeed"), value: simulation.randomSeed)
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(ModellingInformationTableTreeModel.class, "Owner"), value: simulation.creator ? simulation.creator.username : "")
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "StartTime"), value: DateFormatUtils.formatDetailed(simulation.start))
        currentValues << new PropertyValuePairBean(property: UIUtils.getText(SimulationConfigurationView.class, "EndTime"), value: DateFormatUtils.formatDetailed(simulation.end))
        JRBeanCollectionDataSource jrBeanCollectionDataSource = new JRBeanCollectionDataSource(currentValues);
        return jrBeanCollectionDataSource
    }

    static String getItemName(Parameterization parameterization) {
        return parameterization.name + " v" + parameterization.versionNumber.toString()
    }

    static String getItemName(Simulation simulation) {
        return simulation.name
    }

    public static double maxYValue(List xyPairs) {
        if (!xyPairs || xyPairs.size() == 0) return 1.0
        double max = xyPairs[0][1]
        for (List<Double> list: xyPairs) {
            if (max < list[1])
                max = list[1]
        }
        return max
    }

    public static double getMaxValue(List values) {
        if (values.size() == 0) return 1.0
        values.sort()
        if (values.last() > 1) {
            values.remove(values.last())
        } else
            return values.last()
        getMaxValue(values)
    }
}

package org.pillarone.riskanalytics.application.ui.main.view

import org.pillarone.riskanalytics.application.ui.util.UIUtils

/**
 * @author fouad jaada
 */

public class TabbedPaneManagerHelper {
    private Map simulationTabTitlesMap = [:]
    private Map parameterizationTabTitlesMap = [:]
    static int PARAMETERIZATION = 0
    static int SIMULATION = 1
    final static String COMPARE_RESULTS_KEY = "compareResults"
    final static String COMPARE_PARAMETERIZATIONS_KEY = "compareParameterizations"
    private int compareParametersTabIndex = 1
    private int compareSimulationsTabIndex = 1



    protected String getTabTitle(int type, List items) {
        switch (type) {
            case 0:
                int tabIndex = tabIndex(parameterizationTabTitlesMap, getItemNames(items))
                String title = ""
                if (tabIndex == -1) {
                    title = UIUtils.getText(this.class, COMPARE_PARAMETERIZATIONS_KEY) + ": " + compareParametersTabIndex
                    parameterizationTabTitlesMap.put(compareParametersTabIndex, getItemNames(items))
                    compareParametersTabIndex++
                    return title
                } else {
                    title = UIUtils.getText(this.class, COMPARE_PARAMETERIZATIONS_KEY) + ": " + tabIndex
                }
                return title
            case 1:
                int tabIndex = tabIndex(simulationTabTitlesMap, getItemNames(items))
                String title = ""
                if (tabIndex == -1) {
                    title = UIUtils.getText(this.class, COMPARE_RESULTS_KEY) + ": " + compareSimulationsTabIndex
                    simulationTabTitlesMap.put(compareSimulationsTabIndex, getItemNames(items))
                    compareSimulationsTabIndex++
                    return title
                } else {
                    title = UIUtils.getText(this.class, COMPARE_RESULTS_KEY) + ": " + tabIndex
                }
                return title
        }
    }

    protected String getToolTip(List items) {
        List<String> names = getItemNames(items)
        StringBuilder builder = new StringBuilder("<html>")
        names.eachWithIndex {it, int index ->
            if (index > 0)
                builder.append("<br>")
            builder.append(it)
        }
        builder.append("</html>")
        return builder.toString()
    }

    private int tabIndex(Map map, List itemsName) {
        int index = -1
        map.each {k, List v ->
            boolean tabExist = true
            itemsName.each {
                if (v.indexOf(it) < 0)
                    tabExist = false
            }
            if (tabExist)
                index = k
        }
        return index
    }

    private List<String> getItemNames(List items) {
        try {
            return items*.item.name
        } catch (Exception ex) {
            return items*.name
        }
    }


}

package org.pillarone.riskanalytics.application.ui.main.view

import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.Simulation

/**
 * @author fouad jaada
 */

public class TabbedPaneManagerHelper {
    final static String COMPARE_RESULTS_KEY = "compareResults"
    final static String COMPARE_PARAMETERIZATIONS_KEY = "compareParameterizations"



    public static String getTabTitle(Class type) {
        switch (type) {
            case Parameterization: return UIUtils.getText(TabbedPaneManagerHelper.class, COMPARE_PARAMETERIZATIONS_KEY)
            case Simulation: return UIUtils.getText(TabbedPaneManagerHelper.class, COMPARE_RESULTS_KEY)
            default: return ""
        }
    }

    public static String getToolTip(List items) {
        List<String> names = items*.name
        StringBuilder builder = new StringBuilder("<html>")
        names.eachWithIndex { it, int index ->
            if (index > 0)
                builder.append("<br>")
            builder.append(it)
        }
        builder.append("</html>")
        return builder.toString()
    }


}

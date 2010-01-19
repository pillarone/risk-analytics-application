package org.pillarone.riskanalytics.application.util

import com.ulcjava.base.application.util.Color
import org.pillarone.riskanalytics.core.output.SimulationRun

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class SimulationUtilities {

    public static List RESULT_CHAR_PREFIXES = ["A", "B", "C", "D", "E", "F", "G", "H", "I", "J"]
    public static List RESULT_VIEW_COLOR = [new Color(255, 204, 204), new Color(204, 204, 255), new Color(204, 255, 153), new Color(191, 90, 60), new Color(255, 223, 191),  new Color(255, 255, 102),new Color(255, 255, 80), new Color(170, 170, 39), new Color(99, 191, 60), new Color(223, 223, 167)]

    /**
     *  get a max periodCount in the simulations list
     */
    static int getMinSimulationsPeriod(List<SimulationRun> runs) {
        int result = Integer.MAX_VALUE
        for (SimulationRun run in runs) {
            if (run.periodCount < result) {
                result = run.periodCount
            }
        }
        return result
    }


    static Color getColor(Number value, double minValue, double maxValue) {
        if (maxValue == minValue) {
            return Color.white
        }

        int r
        if (Math.abs(value) < minValue) {
            r = 0
        } else if (Math.abs(value) > maxValue) {
            r = 255
        } else {
            r = ((int) (((Math.abs(value) - minValue) / (maxValue - minValue)) * 255))
            if (r < 10)
                r = 10
        }
        return (value > 0) ? new Color(0, 255, 0, r) : new Color(255, 0, 0, r)

    }

}


package org.pillarone.riskanalytics.application.ui.customtable

import org.pillarone.riskanalytics.core.output.SimulationRun

/**
*   author simon.parten @ art-allianz . com
 */
public class GroovyHelperMethods {


    /* SCP 13.03.2013 - Used when java classes want to get stuff out the database. SHould this be in core?  */
    public static SimulationRun findSimulationRun(String simName) {
        return SimulationRun.findByName(simName)
    }
}

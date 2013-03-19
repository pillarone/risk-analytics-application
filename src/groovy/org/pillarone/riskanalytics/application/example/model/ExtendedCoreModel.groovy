package org.pillarone.riskanalytics.application.example.model

import models.core.CoreModel
import org.pillarone.riskanalytics.core.simulation.IPeriodCounter
import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.simulation.VariableLengthPeriodCounter
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder

class ExtendedCoreModel extends CoreModel {

    IPeriodCounter createPeriodCounter(DateTime beginOfFirstPeriod) {
        return new VariableLengthPeriodCounter([
                new DateTime(2009, 1, 1, 0, 0, 0, 0),
                new DateTime(2010, 1, 1, 0, 0, 0, 0),
                new DateTime(2011, 1, 1, 0, 0, 0, 0)])
    }

    int getSimulationPeriodCount(List<ParameterHolder> parameters, int parameterPeriodCount) {
        return 3
    }

}
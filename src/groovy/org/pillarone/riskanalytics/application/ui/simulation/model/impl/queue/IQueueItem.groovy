package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import org.pillarone.riskanalytics.core.simulation.item.Simulation

public interface IQueueItem {

    Simulation getSimulation()

    String getBatchRun()

    String getP14n()

    String getResultConfiguration()

    Integer getIterations()

    Integer getPriority()

    String getAddedBy()

    String getConfiguredAt()
}
package org.pillarone.riskanalytics.application.ui.simulation.model;

import org.pillarone.riskanalytics.core.BatchRun;

interface IBatchListener {
    void newBatchAdded(BatchRun batchRun)
}
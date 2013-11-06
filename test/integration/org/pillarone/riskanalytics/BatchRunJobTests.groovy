package org.pillarone.riskanalytics

import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.batch.BatchRunService

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
import static org.junit.Assert.assertEquals
import static org.junit.Assert.assertNotNull

class BatchRunJobTests {

    BatchRunService service

    @Before
    void setUp() {
        BatchRun batchRun = new BatchRun(name: "test", executionTime: new DateTime(), comment: "")
        batchRun.save()
    }


    @Test
    void testExecute() {
        assertNotNull BatchRunService.getService().getActiveBatchRuns()
        assertEquals 1, BatchRunService.getService().getActiveBatchRuns().size()
    }
}

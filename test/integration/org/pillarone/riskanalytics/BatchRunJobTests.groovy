package org.pillarone.riskanalytics

import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.batch.BatchRunService

import static org.junit.Assert.assertEquals

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
import static org.junit.Assert.assertNotNull

class BatchRunJobTests {

    private BatchRunService service = BatchRunService.service

    @Before
    void setUp() {
        BatchRun batchRun = new BatchRun(name: "test", executionTime: new DateTime(), comment: "")
        batchRun.save()
    }


    @Test
    void testExecute() {
        assertNotNull service.activeBatchRuns
        assertEquals 1, service.activeBatchRuns.size()
    }
}

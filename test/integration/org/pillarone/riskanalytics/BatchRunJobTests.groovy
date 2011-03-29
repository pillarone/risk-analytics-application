package org.pillarone.riskanalytics

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.BatchRun
import org.pillarone.riskanalytics.core.batch.BatchRunService

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class BatchRunJobTests extends GroovyTestCase {

    BatchRunService service

    @Override
    protected void setUp() {
        super.setUp()
        BatchRun batchRun = new BatchRun(name: "test", executionTime: new DateTime(), comment: "")
        batchRun.save()
    }


    public void testExecute() {
        assertNotNull BatchRunService.getService().getActiveBatchRuns()
        assertEquals 1, BatchRunService.getService().getActiveBatchRuns().size()
    }
}

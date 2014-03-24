import org.pillarone.riskanalytics.core.batch.BatchRunService

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class BatchRunJob {

    BatchRunService batchRunService

    static triggers = {
        simple name: 'mySimpleTrigger', startDelay: 60000, repeatInterval: 10000
    }

    def group = "BatchRunGroup"

    def execute() {
        runBatches()
    }

    private runBatches() {
        batchRunService.findBatchRunsWhichShouldBeExecuted().each { batchRunService.runBatch(it) }
    }
}
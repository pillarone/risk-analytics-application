import grails.util.Holders
import org.pillarone.riskanalytics.core.batch.BatchRunService

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class BatchRunJob {

    static BatchRunService batchRunService

    static triggers = {
        simple name: 'mySimpleTrigger', startDelay: 60000, repeatInterval: 10000
    }

    def group = "BatchRunGroup"

    def execute() {
        synchronized (this) {
            getService().runBatches()
        }

    }

    public static BatchRunService getService() {
        if (!batchRunService)
            batchRunService = Holders.grailsApplication.getMainContext().getBean('batchRunService')
        return batchRunService
    }
}
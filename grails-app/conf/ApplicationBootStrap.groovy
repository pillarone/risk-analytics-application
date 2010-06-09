import grails.util.Environment

import org.pillarone.riskanalytics.application.jobs.JobScheduler
import com.ulcjava.base.client.ClientEnvironmentAdapter
import org.pillarone.riskanalytics.application.ui.util.SplashScreen
import org.pillarone.riskanalytics.application.ui.util.SplashScreenHandler

class ApplicationBootStrap {

    def init = {servletContext ->

        if (Environment.current == Environment.TEST) {
            return
        }

        // start a quartz job scheduler for a batch
        new JobScheduler().start()

    }

    def destroy = {
    }
}
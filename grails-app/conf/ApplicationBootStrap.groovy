import grails.util.Environment

import org.pillarone.riskanalytics.application.user.ApplicationUser
import org.pillarone.riskanalytics.application.jobs.JobScheduler
import org.springframework.transaction.TransactionStatus
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.application.example.constraint.LinePercentage

class ApplicationBootStrap {

    def init = {servletContext ->

        ConstraintsFactory.registerConstraint(new LinePercentage())

        ApplicationUser.withTransaction {TransactionStatus status ->
            if (ApplicationUser.countByUsername("testUser") == 0) {
                //create user for testing
                ApplicationUser user = new ApplicationUser()
                user.username = "testUser"
                user.lastname = "last"
                user.firstname = "first"
                user.email = "email@pillarone.com"
                user.password = "123456"
                user.save()
                //end create user for testing
            }
            if (ApplicationUser.countByUsername("actuary") == 0) {
                ApplicationUser user = new ApplicationUser()
                user.username = "actuary"
                user.lastname = "life"
                user.firstname = "pc"
                user.email = "actuary@pillarone.com"
                user.password = "123456"
                user.save()
            }
        }

        if (Environment.current == Environment.TEST) {
            return
        }

        // start a quartz job scheduler for a batch
        new JobScheduler().start()

    }

    def destroy = {
    }
}
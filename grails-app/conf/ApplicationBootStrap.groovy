import grails.util.Environment

import org.pillarone.riskanalytics.application.jobs.JobScheduler
import org.pillarone.riskanalytics.application.user.Person
import org.grails.plugins.springsecurity.service.AuthenticateService
import org.pillarone.riskanalytics.application.user.Authority
import org.pillarone.riskanalytics.application.user.UserSettings

class ApplicationBootStrap {

    AuthenticateService authenticateService

    def init = {servletContext ->

        if(Authority.count() == 0) {

            Authority adminGroup = new Authority()
            adminGroup.description = "admin group"
            adminGroup.authority = "ROLE_ADMIN"
            adminGroup.save()

            Authority userGroup = new Authority()
            userGroup.description = "admin group"
            userGroup.authority = "ROLE_USER"
            userGroup.save()

            Person admin = new Person()
            admin.username = "admin"
            admin.userRealName = "admin"
            admin.passwd = authenticateService.encodePassword("admin")
            admin.enabled = true
            admin.email = "admin@pillarone.org"
            admin.settings = new UserSettings(language: "en")
            admin.addToAuthorities(adminGroup)
            admin.save()
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
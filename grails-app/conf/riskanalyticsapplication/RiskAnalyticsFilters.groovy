package riskanalyticsapplication

import org.apache.log4j.MDC
import org.pillarone.riskanalytics.application.UserContext
import grails.plugins.springsecurity.SpringSecurityService
import org.codehaus.groovy.grails.plugins.springsecurity.GrailsUser
import org.pillarone.riskanalytics.core.user.UserManagement

class RiskAnalyticsFilters {

    def filters = {
        all(controller: '*', action: '*') {
            before = {
                try {
                    //do not use UserManagement.currentUser, because it accesses the DB every time
                    SpringSecurityService securityService = UserManagement.getSpringSecurityService()
                    GrailsUser user = securityService.getPrincipal()
                    MDC.put("username", user?.username)
                } catch (Exception e) {
                    //exception here should not crash the application
                }

            }
        }
    }

}

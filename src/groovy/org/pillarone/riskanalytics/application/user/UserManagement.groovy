package org.pillarone.riskanalytics.application.user

import org.grails.plugins.springsecurity.service.AuthenticateService
import org.codehaus.groovy.grails.commons.ApplicationHolder

public class UserManagement {

    public static Person getCurrentUser() {
        AuthenticateService authenticateService = ApplicationHolder.application.mainContext.getBean("authenticateService")
        def id = authenticateService.userDomain()?.id
        return id != null ? Person.get(id) : null
    }
}

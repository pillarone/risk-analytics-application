package org.pillarone.riskanalytics.application.user

class UserSettings {
    String language
    static belongsTo = [applicationUser: ApplicationUser]

    static constraints = {
        language nullable: true
    }
}

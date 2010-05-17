package org.pillarone.riskanalytics.application.user

class UserSettings {

    String language

    static belongsTo = Person

    static constraints = {
        language()
    }
}

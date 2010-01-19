package org.pillarone.riskanalytics.application.user

class ApplicationUser {
    String username
    String firstname
    String lastname
    String email
    String password
    UserSettings userSettings

    static constraints = {
        username blank: false, unique: true
        firstname minSize: 2, maxSize: 32
        lastname minSize: 2, maxSize: 32
        email blank: false, email: true, unique: true
        password blank: false, password: true
        userSettings nullable: true
    }
    String toString() { "$firstname $lastname" }

}

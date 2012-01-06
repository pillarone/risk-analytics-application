package org.pillarone.riskanalytics.application.output

import org.joda.time.DateTime
import org.pillarone.riskanalytics.core.persistence.DateTimeMillisUserType
import org.pillarone.riskanalytics.core.user.Person
import org.pillarone.riskanalytics.core.util.DatabaseUtils
import org.pillarone.riskanalytics.core.output.CollectorInformation

class CustomTableDAO {

    String name
    String comment
    String modelClassName

    DateTime creationDate
    DateTime modificationDate
    Person creator
    Person lastUpdater


    static hasMany = [entries: CustomTableEntry]

    static constraints = {
        comment(nullable: true)
        creationDate(nullable: true)
        modificationDate(nullable: true)
        creator nullable: true
        lastUpdater nullable: true
    }

    static mapping = {
        creator lazy: false
        lastUpdater lazy: false
        creationDate type: DateTimeMillisUserType
        modificationDate type: DateTimeMillisUserType
        if (DatabaseUtils.isOracleDatabase()) {
            comment(column: 'comment_value')
        }
    }
}

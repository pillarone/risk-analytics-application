package org.pillarone.riskanalytics.application.output

import org.pillarone.riskanalytics.core.output.PathMapping
import org.pillarone.riskanalytics.core.output.FieldMapping
import org.pillarone.riskanalytics.core.output.CollectorMapping


class CustomTableEntry {

    static belongsTo = [customTable: CustomTableDAO]

    int row
    int col

    String text

    PathMapping path
    FieldMapping field
    CollectorMapping collector
    Integer periodIndex

    static hasMany = [pairs: CustomTableEntryPair]

    static constraints = {
        row(min: 0)
        col(min: 0)

        text(nullable: true)
        path(nullable: true)
        field(nullable: true)
        collector(nullable: true)
        periodIndex(nullable: true)
    }
}

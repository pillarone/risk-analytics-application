package org.pillarone.riskanalytics.application.output


class CustomTableEntry {

    static belongsTo = [customTable: CustomTableDAO]

    int row
    int col

    static hasMany = [pairs: CustomTableEntryPair]

    static constraints = {
        row(min: 0)
        col(min: 0)
    }
}

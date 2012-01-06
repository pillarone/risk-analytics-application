package org.pillarone.riskanalytics.application.output


class CustomTableEntryPair {

    static belongsTo = [customTableEntry: CustomTableEntry]

    String key
    String value


    static constraints = {
        key(blank: false)
    }
}

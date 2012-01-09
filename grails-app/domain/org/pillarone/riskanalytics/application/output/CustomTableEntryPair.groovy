package org.pillarone.riskanalytics.application.output


class CustomTableEntryPair {

    static belongsTo = [customTableEntry: CustomTableEntry]

    String entryKey
    String entryValue


    static constraints = {
        entryKey(blank: false)
    }
}

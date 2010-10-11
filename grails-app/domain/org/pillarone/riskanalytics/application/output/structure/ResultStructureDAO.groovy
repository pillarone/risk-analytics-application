package org.pillarone.riskanalytics.application.output.structure

class ResultStructureDAO {

    String modelClassName
    String name
    String itemVersion

    static hasMany = [structureMappings: StructureMapping]

    String toString() {
        "$name v$itemVersion"
    }
}

package org.pillarone.riskanalytics.application.output.structure


class StructureMapping {

    ResultStructureDAO resultStructure

    StructureMapping parent
    String name
    String resultPath
    int orderWithinLevel

    static belongsTo = ResultStructureDAO

    static constraints = {
        parent(nullable: true)
        resultPath(nullable: true)
    }

    String toString() {
        "$resultPath"
    }
}

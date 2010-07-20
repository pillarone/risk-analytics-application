package org.pillarone.riskanalytics.application.output.structure


class StructureMapping {

    ResultStructureDAO resultStructure
    String resultPath
    String artificialPath

    static belongsTo = ResultStructureDAO

    String toString() {
        "$resultPath -> $artificialPath"
    }
}

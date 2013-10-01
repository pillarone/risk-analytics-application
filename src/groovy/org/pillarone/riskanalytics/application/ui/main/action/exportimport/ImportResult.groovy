package org.pillarone.riskanalytics.application.ui.main.action.exportimport

class ImportResult {
    String sheetName
    int rowIndex
    String message
    Type type

    ImportResult(String message, Type type) {
        this(null, -1, message, type)
    }

    ImportResult(String sheetName, int rowIndex, String message, Type type) {
        this.sheetName = sheetName
        this.rowIndex = rowIndex
        this.message = message
        this.type = type
    }

    enum Type {
        SUCCESS, ERROR, WARNING
    }
}

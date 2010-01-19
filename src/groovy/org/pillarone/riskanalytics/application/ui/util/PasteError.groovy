package org.pillarone.riskanalytics.application.ui.util

class PasteError extends Exception {
    List acceptedValues
    def pastedValue

    public String getMessage() {
        if (acceptedValues && pastedValue) {
            String acceptedValues = acceptedValues.toString()
            if (acceptedValues.size() > 100) { acceptedValues = acceptedValues[0..100] + "..." }
            return "Pasted value is ${pastedValue}. Accepted values: ${acceptedValues}"
        } else {
            return ""
        }
    }
}
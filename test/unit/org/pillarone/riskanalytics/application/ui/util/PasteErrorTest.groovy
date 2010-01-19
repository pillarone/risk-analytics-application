package org.pillarone.riskanalytics.application.ui.util

enum TestEnum {
    TEST1, TEST2, TEST3, TEST4, TEST5, TEST6, TEST7, TEST8
}

class PasteErrorTest extends GroovyTestCase {
    void testWithEnum() {
        PasteError e = new PasteError(pastedValue: "pastedValue", acceptedValues: TestEnum.values())
        assertEquals "Pasted value is pastedValue. Accepted values: [TEST1, TEST2, TEST3, TEST4, TEST5, TEST6, TEST7, TEST8]", e.message
    }

    void testLineCut() {
        List acceptedValues = "Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test Test".tokenize()
        PasteError e = new PasteError(pastedValue: "pastedValue", acceptedValues: acceptedValues)
        assertEquals "Pasted value is pastedValue. Accepted values: [Test, Test, Test, Test, Test, Test, Test, Test, Test, Test, Test, Test, Test, Test, Test, Test, Test...", e.message
    }
}
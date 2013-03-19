package org.pillarone.riskanalytics.application.document;

public interface IShowDocumentStrategy {

    void showDocument(String name, byte[] content, String mimeType);

}

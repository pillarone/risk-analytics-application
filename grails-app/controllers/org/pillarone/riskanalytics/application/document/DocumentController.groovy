package org.pillarone.riskanalytics.application.document

import org.pillarone.riskanalytics.application.document.BrowserStrategy.Document


class DocumentController {

    def show = {
        Document document = session.getAttribute(params.id)
        session.removeAttribute(params.id)

        response.contentType = document.mimeType
        response.outputStream << document.content
    }
}

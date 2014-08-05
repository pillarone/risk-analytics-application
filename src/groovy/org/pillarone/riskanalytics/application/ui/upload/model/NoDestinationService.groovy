package org.pillarone.riskanalytics.application.ui.upload.model

import org.springframework.stereotype.Component

@Component
class NoDestinationService implements IDestinationService {
    @Override
    Set<String> getDestinations() {
        []
    }
}

package org.pillarone.riskanalytics.application.document;

import org.pillarone.riskanalytics.application.UserContext;

public abstract class ShowDocumentStrategyFactory {

    public static IShowDocumentStrategy getInstance() {
        if(UserContext.isStandAlone()) {
            return new FileSystemStrategy();
        } else {
            return new BrowserStrategy();
        }
    }
}

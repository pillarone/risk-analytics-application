package org.pillarone.riskanalytics.application.document;

import groovy.transform.CompileStatic;
import org.pillarone.riskanalytics.application.UserContext;

@CompileStatic
public abstract class ShowDocumentStrategyFactory {

    public static IShowDocumentStrategy getInstance() {
        if(UserContext.isStandAlone()) {
            return new FileSystemStrategy();
        } else {
            return new StoreFileStrategy();
        }
    }
}

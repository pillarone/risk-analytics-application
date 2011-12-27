package org.pillarone.riskanalytics.application.ui.extension

import com.ulcjava.base.application.ULCComponent
import com.ulcjava.applicationframework.application.ApplicationContext


public interface ComponentCreator {

    ULCComponent createComponent(ApplicationContext context)

}

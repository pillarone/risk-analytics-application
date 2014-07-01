package org.pillarone.riskanalytics.application.ui.util

class DefaultResourceBundleResolver implements IResourceBundleResolver {

    String getText(Class objClass, String key, List argsValue = null) {
        UIUtils.getText(objClass, key, argsValue)
    }
}

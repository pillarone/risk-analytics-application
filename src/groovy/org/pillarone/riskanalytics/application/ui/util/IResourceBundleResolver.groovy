package org.pillarone.riskanalytics.application.ui.util

interface IResourceBundleResolver {
    String getText(Class objClass, String key)

    String getText(Class objClass, String key, List argsValue)

}
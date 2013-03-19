package org.pillarone.riskanalytics.application.util

class ResourceBundleFactory {

    private static Map<String, List<String>> replacements = [:]

    public static void addReplacement(String bundleToReplace, String newBundle) {
        List<String> replacementList = replacements.get(bundleToReplace)
        if (replacementList == null) {
            replacementList = []
            replacements.put(bundleToReplace, replacementList)
        }

        replacementList << newBundle
    }

    public static ResourceBundle getBundle(String bundle, Locale locale) {
        List<String> replacementList = replacements.get(bundle)
        if (replacementList == null) {
            return ResourceBundle.getBundle(bundle, locale)
        } else {
            List<ResourceBundle> bundles = replacementList.collect { ResourceBundle.getBundle(it, locale) }
            return new ResourceBundleDecorator(ResourceBundle.getBundle(bundle, locale), bundles)
        }
    }

    public static void reset() {
        replacements.clear()
    }
}

package org.pillarone.riskanalytics.application.util


class ResourceBundleDecorator extends ResourceBundle {

    private ResourceBundle originalBundle
    private List<ResourceBundle> replacementBundles

    public ResourceBundleDecorator(ResourceBundle originalBundle, List<ResourceBundle> replacementBundles) {
        this.originalBundle = originalBundle
        this.replacementBundles = replacementBundles
    }

    boolean containsKey(String key) {
        return originalBundle.containsKey(key)
    }

    Enumeration<String> getKeys() {
        return originalBundle.getKeys()
    }

    Locale getLocale() {
        return originalBundle.getLocale()
    }

    @Override
    protected Object handleGetObject(String key) {
        for (ResourceBundle bundle in replacementBundles) {
            try {
                return bundle.getObject(key)
            } catch (MissingResourceException mre) {
                //no replacement
            }
        }
        return originalBundle.getObject(key)
    }

    Set<String> keySet() {
        return originalBundle.keySet()
    }

}

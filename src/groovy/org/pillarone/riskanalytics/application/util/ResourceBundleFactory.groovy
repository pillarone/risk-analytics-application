package org.pillarone.riskanalytics.application.util

import org.apache.commons.lang.builder.EqualsBuilder
import org.apache.commons.lang.builder.HashCodeBuilder

class ResourceBundleFactory {

    private static Map<String, List<String>> replacements = [:]

    private static Map<BundleLocaleKey, ResourceBundle> bundleCache = [:]

    public static void addReplacement(String bundleToReplace, String newBundle) {
        List<String> replacementList = replacements.get(bundleToReplace)
        if (replacementList == null) {
            replacementList = []
            replacements.put(bundleToReplace, replacementList)
        }

        replacementList << newBundle
    }

    public static ResourceBundle getBundle(String bundle, Locale locale) {
        ClassLoader loader = Thread.currentThread().contextClassLoader
        List<String> replacementList = replacements.get(bundle)
        if (replacementList == null) {
            return getOrCreateBundle(bundle, locale, loader)
        } else {
            List<ResourceBundle> bundles = replacementList.collect { String it -> getOrCreateBundle(it, locale, loader) }
            return new ResourceBundleDecorator(getOrCreateBundle(bundle, locale, loader), bundles)
        }
    }

    public static void reset() {
        replacements.clear()
    }

    private static ResourceBundle getOrCreateBundle(String name, Locale locale, ClassLoader loader) {
        BundleLocaleKey key = new BundleLocaleKey(bundle: name, locale: locale)
        ResourceBundle bundle = bundleCache.get(key)
        if(bundle == null) {
            bundle = ResourceBundle.getBundle(name, locale, loader)
            bundleCache.put(key, bundle)
        }

        return bundle
    }

    private static class BundleLocaleKey {
        Locale locale
        String bundle

        @Override
        int hashCode() {
            return new HashCodeBuilder().append(locale.toString()).append(bundle).toHashCode()
        }

        @Override
        boolean equals(Object obj) {
            if(obj instanceof  BundleLocaleKey) {
                return new EqualsBuilder().append(locale.toString(), obj.locale.toString()).append(bundle, obj.bundle).equals
            }

            return false
        }
    }
}

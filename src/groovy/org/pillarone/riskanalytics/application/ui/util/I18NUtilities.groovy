package org.pillarone.riskanalytics.application.ui.util

import com.ulcjava.base.application.tabletree.ITableTreeNode
import com.ulcjava.base.application.util.HTMLUtilities
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.apache.maven.artifact.ant.shaded.StringUtils
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterObjectParameterTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.util.ResourceBundleRegistry

import java.text.MessageFormat

class I18NUtilities {
    private static final String PACKET_BUNDLE_FILENAME = "org.pillarone.riskanalytics.packets.I18NPacket"

    static boolean testMode = false
    static ResourceBundle testResourceBundle = null
    static Set<ResourceBundle> exceptionResourceBundles = null
    static Set<ResourceBundle> helpResourceBundles = null



    static final Log LOG = LogFactory.getLog(I18NUtilities)

    public static String findParameterTypeDisplayName(Class clazz, String key, String tooltip = "") {
        return findParameter(clazz, "displayName" + tooltip)

    }

    private static String findParameter(Class bundleClassName, String key) {
        String value = null
        String bundlePackage = bundleClassName.package.name
        String bundleName = StringUtils.lowercaseFirstLetter(bundleClassName.simpleName)
        String resuourceBundleName = "${bundlePackage}.${bundleName}Resources"
        try {
            ResourceBundle bundle = LocaleResources.getBundle(resuourceBundleName)
            value = bundle.getString(key)
        } catch (MissingResourceException ignore) {
            LOG.trace("Resource for ${resuourceBundleName} not found. Key: displayName")
        }
        return value
    }

    public static String findEnumDisplayName(Class declatingEnumClass, String enumValue) {

        return findParameter(declatingEnumClass, enumValue)
        // TODO Check for enum Resources.properties files
        //String enumClassName = StringUtils.lowercaseFirstLetter(declatingEnumClass.simpleName)
        //String enumType = "${declatingEnumClass.package.name}.${enumClassName}"
    }


    public static String findParameterDisplayName(ParameterObjectParameterTableTreeNode node, String subPath, String toolTip = "") {
        ParameterObjectParameterHolder parameter = node.parametrizedItem.getParameterHoldersForAllPeriods(node.parameterPath)[0]
        //TODO capitalize properties files.
        //int lastIndex = parameterType.lastIndexOf('.') + 1
        //parameterType = parameterType.substring(0, lastIndex) + parameterType.substring(lastIndex, lastIndex + 1).toLowerCase() + parameterType.substring(lastIndex + 1)
        return findParameter(parameter.classifier.class, subPath.replaceAll(":", "."))
    }

    public static String findParameterDisplayName(ComponentTableTreeNode componentNode, String subPath, String toolTip = "") {
        Component component = componentNode.component
        return findParameter(component.class, subPath.replaceAll(":", ".") + toolTip)
    }

    public static String findParameterDisplayName(ITableTreeNode node, String subPath, String toolTip = "") {
        return null
    }


    public static String findResultParameterDisplayName(ITableTreeNode simpleTableTreeNode, String parmKey, String toolTip = "") {
        String value = null
        if (simpleTableTreeNode instanceof SimpleTableTreeNode && simpleTableTreeNode.parent != null && simpleTableTreeNode.parent instanceof ComponentTableTreeNode) {
            Component component = simpleTableTreeNode.parent.component
            return findParameter(component.class, parmKey + toolTip)
        }
        return value
    }


    public static String findComponentDisplayNameInModelBundle(Class modelClass, String componentPath, String toolTip = "") {
        String name = null
        String componentSubPath = componentPath[(componentPath.indexOf(":") + 1)..(componentPath.length() - 1)]
        componentSubPath = componentSubPath.replaceAll(":", ".")
        try {
            name = getModelResourceBundle(modelClass.name).getString(componentSubPath + toolTip)
        } catch (java.util.MissingResourceException e) {
            LOG.trace("resource for ${modelClass.name} not found. Key: ${componentSubPath}")
        }
        return name
    }

    private static ResourceBundle getModelResourceBundle(String modelName) {
        if (testMode) {
            return testResourceBundle
        }
        // TODO check this.
        //Class modelClass = ModelRegistry.instance.getModelClass(modelName + "Model")
        //String packageName = modelClass?.package?.name
        //String simpleName = modelClass?.simpleName
        return LocaleResources.getBundle("${modelName}Resources")
    }

    public static String findComponentDisplayNameInComponentBundle(Component component, String toolTip = "") {
        return findParameter(component.class, "displayName" + toolTip)
    }

    public static String findComponentDisplayNameByTreeNode(ComponentTableTreeNode node, String toolTip = "") {
        String name = null
        if (node?.parent instanceof ComponentTableTreeNode) {
            name = findParameter(node?.parent?.component?.class, node?.name + toolTip)
        }
        return name
    }

    public static String getResultStructureString(Class model, String property, String tooltip = "") {
        try {
            ResourceBundle bundle = getModelResourceBundle(model.name)
            return bundle.getString(property + tooltip)
        } catch (MissingResourceException e) {
            return formatDisplayName(property)
        } catch (NullPointerException e) {
            return formatDisplayName(property)
        }
    }

    public static String formatDisplayName(String value) {
        return org.pillarone.riskanalytics.core.components.ComponentUtils.getNormalizedName(value)
    }

    //TODO : is that used ?
    public static String findResultDisplayName(String path, Component component) {
        path = path[(path.indexOf(":") + 1)..(path.length() - 1)]
        String resultKey = path.replaceAll(":", ".")
        return findParameter(component.class, resultKey)
    }

    //TODO : is that used ?
    public static String findDisplayNameByPacket(String parmKey) {
        return findParameter(PACKET_BUNDLE_FILENAME, parmKey)
    }

    public static String getPropertyDisplayName(Model model, String propertyName) {
        return findParameter(model.class, propertyName)
    }

    //TODO move that to a separate class.
    public static String getExceptionText(String exception) {
        String text = getPropertyText(exceptionResourceBundles, ResourceBundleRegistry.RESOURCE, exception)
        if (!text) {
            text = toLines(exception, 70)
        }
        text = text.replaceAll("<", "&lt;")
        text = text.replaceAll(">", "&gt;")
        return HTMLUtilities.convertToHtml(text)
    }

    public static String getPropertyText(Set bundles, String bundleKey, String arg) {
        if (!bundles)
            bundles = LocaleResources.getBundles(bundleKey)
        return getTextByResourceBundles(bundles, arg)
    }

    private static String toLines(String exception, int lineMaxLength) {
        List words = exception.split(" ") as List
        StringBuffer bf = new StringBuffer()
        int lineLength = 0
        for (String s in words) {
            if (lineLength + s.length() > lineMaxLength) {
                bf << "\n"
                lineLength = 0
            }
            bf << s + " "
            lineLength += (s.length() + 1)
        }
        return bf.toString()
    }

    private static String getTextByResourceBundles(Set<ResourceBundle> bundles, String argument) {
        def keys
        String text = ""
        try {
            argument = argument.replaceAll("\n", "")
            for (ResourceBundle resourceBundle : bundles) {
                keys = (List<String>) new GroovyShell().evaluate(argument)
                try {
                    text = resourceBundle.getString(keys[0])
                } catch (Exception ignore) {}
                if (text) {
                    List args = []
                    keys.eachWithIndex { String key, int index ->
                        if (index > 0) {
                            args << key
                        }
                    }
                    if (args.size() > 0)
                        text = MessageFormat.format(text, args.toArray())
                }
            }
        } catch (Exception ignore) {}
        return text;
    }

    public static String getHelpText(String arg) {
        return getPropertyText(helpResourceBundles, ResourceBundleRegistry.HELP, arg)
    }


}

package org.pillarone.riskanalytics.application.ui.util

import com.ulcjava.base.application.util.HTMLUtilities
import java.text.MessageFormat
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.base.model.ComponentTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterObjectParameterTableTreeNode
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.components.Component
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterObjectParameterHolder
import org.pillarone.riskanalytics.core.util.ResourceBundleRegistry
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.components.ComponentUtils

public class I18NUtils {

    static final String PACKET_BUNDLE_FILENAME = "org.pillarone.riskanalytics.packets.I18NPacketResources"

    static final String MODEL_PACKAGE = "models."
    static final Log LOG = LogFactory.getLog(I18NUtils)
    static boolean testMode = false
    static ResourceBundle testResourceBundle = null
    static Set exceptionResourceBundles = null
    static Set helpResourceBundles = null

    public static findParameterTypeDisplayName(String type, String tooltip = "") {
        String value = null
        try {
            ResourceBundle bundle = LocaleResources.getBundle(type + "Resources")
            value = bundle.getString("displayName" + tooltip)
        } catch (java.util.MissingResourceException e) {
            LOG.debug("resource for ${type} not found. Key: displayName")
        }
        return value
    }

    public static String findParameterDisplayName(ComponentTableTreeNode componentNode, String subPath, String toolTip = "") {
        Component component = componentNode.component
        String value = null
        String parmKey = subPath.replaceAll(":", ".")
        try {
            ResourceBundle bundle = findResourceBundle(component.getClass())
            value = bundle.getString(parmKey + toolTip)
        } catch (java.util.MissingResourceException e) {
            return findParameterDisplayNameBySuperClass(component.getClass().getSuperclass(), parmKey, toolTip)
        }
        return value
    }

    public static String findDisplayNameByParentComponent(ITableTreeNode simpleTableTreeNode, String parmKey, String toolTip = "") {
        if (simpleTableTreeNode?.parent instanceof ComponentTableTreeNode) {
            return findParameterDisplayNameBySuperClass(simpleTableTreeNode.parent.component.getClass(), parmKey + toolTip)
        }
        return null

    }

    /**
     * iterate the superclasses and get the resource
     * null if doesn't exist
     */
    public static String findParameterDisplayNameBySuperClass(Class componentClass, String parmKey, String toolTip = "") {
        String value
        try {
            if (componentClass != null && componentClass.name != "org.pillarone.riskanalytics.core.components.Component") {
                ResourceBundle bundle = findResourceBundle(componentClass)
                value = bundle.getString(parmKey + toolTip)
            }
        } catch (java.util.MissingResourceException e) {
            Class superClass = componentClass.getSuperclass()
            if (superClass != null && superClass.name != "org.pillarone.riskanalytics.core.components.Component") {
                value = findParameterDisplayNameBySuperClass(superClass, parmKey + toolTip)
            }
            else {
                LOG.debug("resource for ${componentClass.getSimpleName()} not found. Key: ${parmKey}")
            }
        }
        return value
    }

    public static String findResultParameterDisplayName(def simpleTableTreeNode, String parmKey, String toolTip = "") {
        String value = null
        if (simpleTableTreeNode instanceof SimpleTableTreeNode && simpleTableTreeNode.parent != null && simpleTableTreeNode.parent instanceof ComponentTableTreeNode) {
            Component component = simpleTableTreeNode.parent.component
            try {
                ResourceBundle bundle = findResourceBundle(component.getClass())
                value = bundle.getString(parmKey + toolTip)
            } catch (java.util.MissingResourceException e) {
                value = findResultParameterDisplayName(simpleTableTreeNode.parent, parmKey)
            }
        }
        return value
    }


    public static String findParameterDisplayName(ParameterObjectParameterTableTreeNode node, String subPath, String toolTip = "") {
        ParameterObjectParameterHolder parameter = node.parametrizedItem.getParameterHoldersForAllPeriods(node.parameterPath)[0]
        String parameterType = parameter.classifier.getClass().name
        int lastIndex = parameterType.lastIndexOf('.') + 1
        parameterType = parameterType.substring(0, lastIndex) + parameterType.substring(lastIndex, lastIndex + 1).toLowerCase() + parameterType.substring(lastIndex + 1)
        String value = null
        String parmKey = subPath.replaceAll(":", ".")
        try {
            ResourceBundle bundle = LocaleResources.getBundle(parameterType + "Resources")
            value = bundle.getString(parmKey + toolTip)
        } catch (java.util.MissingResourceException e) {
            LOG.debug("resource for ${parameterType} not found. Key: ${parmKey}")
        }
        return value
    }

    public static String findEnumDisplayName(String enumType, String enumValue) {
        String value
        try {
            ResourceBundle bundle = LocaleResources.getBundle(enumType + "Resources")
            value = bundle.getString(enumValue)
        } catch (java.util.MissingResourceException e) {
            LOG.debug("resource for ${enumType} not found. Key: ${enumValue}")
        }
        return value
    }


    public static String findComponentDisplayNameInModelBundle(String componentPath, String toolTip = "") {
        String name = null
        String modelName = componentPath[0..(componentPath.indexOf(":") - 1)]
        String componentSubPath = componentPath[(componentPath.indexOf(":") + 1)..(componentPath.length() - 1)]
        componentSubPath = componentSubPath.replaceAll(":", ".")
        try {
            name = getModelResourceBundle(modelName).getString(componentSubPath + toolTip)
        } catch (java.util.MissingResourceException e) {
            LOG.debug("resource for ${modelName} not found. Key: ${componentSubPath}")
        }
        return name
    }

    public static String findComponentDisplayNameInComponentBundle(Component component, String toolTip = "") {
        String name = null
        try {
            ResourceBundle bundle = findResourceBundle(component.getClass())
            name = bundle.getString("displayName" + toolTip)
        } catch (java.util.MissingResourceException e) {
            LOG.debug("resource for ${component.getClass().getSimpleName()} not found")
        }
        return name
    }

    public static String findComponentDisplayNameByTreeNode(ComponentTableTreeNode node, String toolTip = "") {
        String name = null
        try {
            if (node instanceof ComponentTableTreeNode && node?.parent instanceof ComponentTableTreeNode)
                name = findResourceBundle(((ComponentTableTreeNode) node?.parent)?.component?.class).getString(node?.name + toolTip)
        } catch (java.util.MissingResourceException e) {
            LOG.debug("resource for ComponentTableTreeNode  not found")
        }
        return name
    }



    public static String findResultDisplayName(String path, Component component) {
        path = path[(path.indexOf(":") + 1)..(path.length() - 1)]
        String resultKey = path.replaceAll(":", ".")
        String name = null
        try {
            name = findResourceBundle(component.getClass()).getString(resultKey)
        } catch (java.util.MissingResourceException e) {
            LOG.debug("resource for ${component.getClass().getSimpleName()} not found. Key: ${resultKey}")
        }
        return name
    }

    public static String findDisplayNameByPacket(String parmKey) {
        String value = null
        try {
            ResourceBundle bundle = LocaleResources.getBundle(PACKET_BUNDLE_FILENAME)
            value = bundle.getString(parmKey)
        } catch (java.util.MissingResourceException e) {
            LOG.debug("resource for $PACKET_BUNDLE_FILENAME not found. Key: ${parmKey}")
        }
        return value
    }

    private static String findDisplayNameByPacketSuperClass(Class packetClass, String parmKey) {
        String value
        try {
            if (packetClass != null) {
                ResourceBundle bundle = findResourceBundle(packetClass)
                value = bundle.getString(parmKey)
            }
        } catch (java.util.MissingResourceException e) {
            Class superClass = packetClass.getSuperclass()
            if (superClass != null) {
                value = findDisplayNameByPacketSuperClass(superClass, parmKey)
            }
            else {
                LOG.debug("resource for ${packetClass.getSimpleName()} not found. Key: ${parmKey}")
            }
        }
        return value
    }


    protected static ResourceBundle findResourceBundle(Class class2) {
        ResourceBundle bundle
        try {
            bundle = getResourceBundle(class2)
        } catch (java.util.MissingResourceException e) {
            Class superClass = class2.getSuperclass()
            if (superClass != null && class2.getSuperclass().name != "org.pillarone.riskanalytics.core.components.Component") {
                bundle = findResourceBundle(class2.getSuperclass())
            }
            else {
                throw e
            }
        }
        return bundle
    }

    protected static ResourceBundle getModelResourceBundle(String modelName) {
        if (testMode) {
            return testResourceBundle
        }
        try {
            Class modelClass = ModelStructure.findAllModelClasses().find { it.simpleName == modelName + "Model"}
            String packageName = modelClass?.getPackage()?.name
            return LocaleResources.getBundle(packageName + "." + modelName + "ModelResources")

        } catch (Exception e) {
            return LocaleResources.getBundle(MODEL_PACKAGE + modelName[0].toLowerCase() + modelName[1..(modelName.length() - 1)] + "." + modelName + "ModelResources")
        }
    }

    public static String getResultStructureString(Class model, String property, String tooltip = "") {
        try {
            ResourceBundle bundle = getModelResourceBundle(model.simpleName - "Model")
            return bundle.getString(property + tooltip)
        } catch (MissingResourceException e) {
            return formatDisplayName(property)
        } catch (NullPointerException e) {
            return formatDisplayName(property)
        }
    }

    protected static ResourceBundle getResourceBundle(Class class2) {
        if (testMode) {
            return testResourceBundle
        }
        String packageName = class2.getPackage().name
        String className = class2.getSimpleName()
        String resourceBundleName = packageName + "." + className[0].toLowerCase(LocaleResources.getLocale()) + className[1..(className.length() - 1)] + "Resources"
        return LocaleResources.getBundle(resourceBundleName)
    }

    public static String formatDisplayName(String value) {
        return ComponentUtils.getNormalizedName(value)
    }

    public static String getPropertyDisplayName(Model model, String propertyName) {
        String value = null
        try {
            ResourceBundle bundle = getResourceBundle(model.class)
            value = bundle.getString(propertyName)
        } catch (Exception) {
        }
        return value
    }

    public static String getExceptionText(String exception) {
        String text = getPropertyText(exceptionResourceBundles, ResourceBundleRegistry.RESOURCE, exception)
        if (!text) {
            text = toLines(exception, 70)
        }
        return HTMLUtilities.convertToHtml(text)
    }

    public static String getHelpText(String arg) {
        return getPropertyText(helpResourceBundles, ResourceBundleRegistry.HELP, arg)
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

    private static String getTextByResourceBundles(Set bundles, String argument) {
        def keys = null
        String text = ""
        try {
            argument = argument.replaceAll("\n", "")
            for (ResourceBundle resourceBundle: bundles) {
                keys = (List) new GroovyShell().evaluate(argument)
                try {
                    text = resourceBundle.getString(keys[0])
                } catch (Exception ex) {}
                if (text) {
                    List args = []
                    keys.eachWithIndex {String key, int index ->
                        if (index > 0) {
                            args << key
                        }
                    }
                    if (args.size() > 0)
                        text = MessageFormat.format(text, args.toArray())
                }
            }
        } catch (Exception ex) { /*ignore the exception*/}
        return text;
    }

}

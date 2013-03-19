package org.pillarone.riskanalytics.application.ui.main.action

import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.util.Cursor
import com.ulcjava.base.application.util.IFileLoadHandler
import org.apache.commons.lang.StringUtils
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.main.view.item.ModellingUIItem
import org.pillarone.riskanalytics.application.ui.main.view.item.UIItemFactory
import org.pillarone.riskanalytics.application.ui.util.ExceptionSafe
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ParameterizationImportError
import org.pillarone.riskanalytics.core.simulation.item.ModellingItem
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.core.util.GroovyUtils
import org.pillarone.riskanalytics.core.util.PropertiesUtils

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ItemLoadHandler implements IFileLoadHandler {
    ImportAction importAction
    boolean withVersionNumber = false
    boolean forceImport = false
    def node
    final static String DEFAULT_VERSION = "0.4.2"

    Log LOG = LogFactory.getLog(ItemLoadHandler)

    public ItemLoadHandler(ImportAction importAction, node) {
        this.importAction = importAction
        this.node = node
    }

    public ItemLoadHandler(ImportAction importAction, node, boolean withVersionNumber) {
        this(importAction, node)
        this.withVersionNumber = withVersionNumber
    }


    void onSuccess(InputStream[] inputStreams, String[] paths, String[] names) {
        importAction.userPreferences.setUserDirectory(paths, names)
        ExceptionSafe.protect {

            names.findAll {it.indexOf(".groovy") != -1}.eachWithIndex {String fileName, int index ->
                String itemName = names[index] - ".groovy"
                List lines = inputStreams[index].readLines()
                String fileContent = getFileContent(lines)
                try {
                    GroovyUtils.parseGroovyScript fileContent, { ConfigObject data ->
                        if (validate(data)) {
                            spreadRanges(data)
                            Model parentNodeModel = getModel(data)
                            if (!parentNodeModel || parentNodeModel.class.name != data.model.name) {
                                ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(importAction.tree), "differentsModel", [data.model.getSimpleName(), parentNodeModel.class.getSimpleName()])
                                alert.show()
                            } else {
                                VersionNumber versionNumber = getVersionNumber(itemName)
                                ModellingItem newItem
                                if (withVersionNumber && versionNumber)
                                    newItem = ModellingItemFactory.createParameterization(itemName, data, Parameterization.class, versionNumber)
                                else
                                    newItem = ModellingItemFactory.createItem(itemName, data, node ? node.itemClass : Parameterization.class, forceImport)

                                if (newItem != null) {
                                    ModellingUIItem modellingUIItem = UIItemFactory.createItem(newItem, parentNodeModel, importAction.model)
                                    modellingUIItem.importItem()
                                } else {
                                    ULCAlert alert = new I18NAlert(UlcUtilities.getWindowAncestor(importAction.tree), "lastVersionError")
                                    alert.show()
                                }
                            }
                        }
                        else {
                            LOG.error "error by importing a parameterization $itemName : config object is not valid"
                        }
                    }

                } catch (Throwable e) {
                    LOG.error "error by loading $itemName"
                    throw new ParameterizationImportError(e)
                } finally {
                    importAction.ancestor?.cursor = Cursor.DEFAULT_CURSOR
                }
                inputStreams[index].close()
                LOG.info "Parameterization ${itemName} has been imported"
            }
        }
    }

    void onFailure(int reason, String description) {
        if (IFileLoadHandler.CANCELLED != reason) {
            new ULCAlert(importAction.ancestor, "Import failed", description, "Ok").show()
        }
        importAction.ancestor?.cursor = Cursor.DEFAULT_CURSOR
    }

    private void spreadRanges(ConfigObject config) {
        def rangeKeys = [:]
        List ranges = []
        config.each {key, value ->
            if (value instanceof ConfigObject) {
                spreadRanges(value)
            }
            if (key instanceof Range) {
                ranges << key
                key.each {
                    rangeKeys[it] = value
                }
            }
        }
        config.putAll(rangeKeys)
        ranges.each {
            config.remove(it)
        }
    }

    private String getFileContent(List lines) {
        String fileContent = lines.join("\n")
        Properties properties = getProperties(lines)
        properties.propertyNames().each {String old ->
            fileContent = fileContent.replaceAll(old, properties.get(old))
        }
        return fileContent
    }

    private VersionNumber getVersionNumber(String fileName) {
        String value = fileName.substring(fileName.lastIndexOf("_v") + 2)
        return (value && value.isNumber()) ? new VersionNumber(value) : null
    }

    private String getVersion(List lines) {
        for (String str: lines) {
            if (StringUtils.isNotEmpty(str) && str.indexOf("applicationVersion") != -1) {
                return str.substring(str.indexOf("=") + 1).trim().replaceAll("'", "")
            }
        }
        return null
    }



    private Properties getProperties(List lines) {
        Properties properties = new Properties()
        String appVersion = new PropertiesUtils().getProperties("/version.properties").getProperty("version", "N/A")
        String pVersion = getVersion(lines)
        if (pVersion) {
            properties = new PropertiesUtils().getProperties("/parameterization_${pVersion}_${appVersion}.properties")
        } else {
            properties = new PropertiesUtils().getProperties("/parameterization_${DEFAULT_VERSION}_${appVersion}.properties")
        }
        return properties
    }

    Model getModel(ConfigObject data) {
        Model model = importAction.getSelectedModel()
        if (!model) {
            // get model by importAll
            model = data.model.newInstance()
            model.init()
        }
        return model
    }

    boolean validate(ConfigObject data) {
        if (importAction instanceof ImportAllAction) {
            return (data.containsKey("fileType") && data.fileType == 'Parameterization') || data.containsKey("applicationVersion")
        }
        return true
    }

}

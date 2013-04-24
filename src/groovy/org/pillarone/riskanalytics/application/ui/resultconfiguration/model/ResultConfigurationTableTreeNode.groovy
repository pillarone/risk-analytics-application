package org.pillarone.riskanalytics.application.ui.resultconfiguration.model

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.view.IMultiValueTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy
import org.pillarone.riskanalytics.core.output.PacketCollector
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.output.AggregatedWithSingleAvailableCollectingModeStrategy

class ResultConfigurationTableTreeNode extends SimpleTableTreeNode implements IMultiValueTableTreeNode {

    private BiMap valueToKey = HashBiMap.create()
    private BiMap keyToValue = valueToKey.inverse()

    private static final String NO_COLLECTOR = "NONE"

    ResultConfiguration configuration

    Class packetClass

    public ResultConfigurationTableTreeNode(String name, ResultConfiguration configuration, Class packetClass) {
        super(name);
        this.packetClass = packetClass
        this.configuration = configuration
        keyToValue.put(NO_COLLECTOR, getText(NO_COLLECTOR))
        Locale locale = LocaleResources.getLocale()
        for (ICollectingModeStrategy key in CollectingModeFactory.getAvailableStrategies()) {
            //AggregatedWithSingleAvailableCollectingModeStrategy is only used internally
            if (key.isCompatibleWith(packetClass)) {
                keyToValue.put(key.getIdentifier(), key.getDisplayName(locale))
            }
        }
    }

    public Object getExpandedCellValue(int i) {
        String identifier
        if (collector?.mode == null) {
            identifier = NO_COLLECTOR
        } else {
            identifier = collector.mode.identifier
        }
        return keyToValue[identifier]
    }

    public void setValueAt(Object value, int i) {
        value = valueToKey[value]
        ICollectingModeStrategy newValue = CollectingModeFactory.getStrategy(value)
        configuration.setCollector(path,newValue)
    }

    public PacketCollector getCollector(){
        configuration.getCollector(path)
    }

    public String getDisplayName() {
        String value = lookUp(null, "")
        if (value == null)
            value = super.getDisplayName()
        return value
    }

    public String getToolTip() {
        if (!cachedToolTip) {
            String value = name
            cachedToolTip = lookUp(value, TOOLTIP)
            if (!cachedToolTip)
                cachedToolTip = super.getToolTip()
        }
        return cachedToolTip
    }

    private lookUp(String value, String tooltip) {
        String displayName = I18NUtils.findResultParameterDisplayName(this, name, tooltip)
        if (displayName == null)
            displayName = I18NUtils.findDisplayNameByParentComponent(this, name, tooltip)
        return displayName
    }



    public List getValues() {
        valueToKey.keySet().toList().sort()
    }

    private String getText(String key) {
        LocaleResources.getString('ICollectingModeStrategy.' + key)
    }
}

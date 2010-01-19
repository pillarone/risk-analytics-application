package org.pillarone.riskanalytics.application.ui.resultconfiguration.model

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.ui.base.model.SimpleTableTreeNode
import org.pillarone.riskanalytics.application.ui.base.view.IMultiValueTableTreeNode
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.core.output.CollectingModeFactory
import org.pillarone.riskanalytics.core.output.ICollectingModeStrategy
import org.pillarone.riskanalytics.core.output.PacketCollector
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration

class ResultConfigurationTableTreeNode extends SimpleTableTreeNode implements IMultiValueTableTreeNode {

    private BiMap valueToKey = HashBiMap.create()
    private BiMap keyToValue = valueToKey.inverse()

    private static final String NO_COLLECTOR = "NONE"

    ResultConfiguration configuration
    PacketCollector collector

    public ResultConfigurationTableTreeNode(String name, ResultConfiguration configuration) {
        super(name);
        this.configuration = configuration
        keyToValue.put(NO_COLLECTOR, getText(NO_COLLECTOR))
        Locale locale = LocaleResources.getLocale()
        for (ICollectingModeStrategy key in CollectingModeFactory.getAvailableStrategies()) {
            keyToValue.put(key.getIdentifier(), key.getDisplayName(locale))
        }
    }

    //must be called when the tree is complete, not in the constructor or setParent of this class!
    public void findCollector() {
        collector = configuration.collectors.find {PacketCollector coll -> coll.path == path}
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
        if (newValue == null) {
            if (collector) {
                configuration.collectors.remove(collector)
                collector = null
            }
            return
        }

        if (!collector) {
            collector = new PacketCollector()
            collector.path = getPath()

            configuration.collectors << collector
        }
        collector.mode = newValue
    }

    public String getDisplayName() {
        String value = I18NUtils.findResultParameterDisplayName(this, name)
        if (value == null)
            value = I18NUtils.findDisplayNameByParentComponent(this, name)
        if (value == null)
            value = super.getDisplayName()
        return value
    }

    public List getValues() {
        valueToKey.keySet().toList()
    }

    private String getText(String key) {
        LocaleResources.getString('ICollectingModeStrategy.' + key)
    }

}

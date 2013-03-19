package org.pillarone.riskanalytics.application.ui.main.view.item

import com.ulcjava.base.application.ULCContainer
import com.ulcjava.base.application.util.ULCIcon
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.pillarone.riskanalytics.application.ui.resultconfiguration.model.ResultConfigurationViewModel
import org.pillarone.riskanalytics.application.ui.resultconfiguration.view.ResultConfigurationView
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.core.simulation.item.ModelStructure
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.RiskAnalyticsInconsistencyException
import com.ulcjava.base.application.tabletree.ITableTreeNode
import org.pillarone.riskanalytics.core.output.PacketCollector
import org.pillarone.riskanalytics.application.ui.resultconfiguration.model.ResultConfigurationTableTreeNode
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory

/**
 * @author fouad.jaada@intuitive-collaboration.com
 */
class ResultConfigurationUIItem extends ModellingUIItem {

    private static Log LOG = LogFactory.getLog(ResultConfigurationUIItem)

    public ResultConfigurationUIItem(RiskAnalyticsMainModel mainModel, Model simulationModel, ResultConfiguration resultConfiguration) {
        super(mainModel, simulationModel, resultConfiguration)
    }

    ULCContainer createDetailView() {
        ResultConfigurationView view = new ResultConfigurationView(getViewModel(), mainModel)
        mainModel.addModelItemChangedListener(view)
        return view.content
    }

    AbstractModellingModel getViewModel() {
        ResultConfigurationViewModel model = new ResultConfigurationViewModel(this.model, (ResultConfiguration) item, ModelStructure.getStructureForModel(this.model.class))
        model.mainModel = mainModel
        mainModel.registerModel(this, model)
        return model
    }

    @Override
    List<SimulationRun> getSimulations() {
        return item.getSimulations()
    }

    @Override
    ULCIcon getIcon() {
        return UIUtils.getIcon("resulttemplate-active.png")
    }

    @Override
    boolean isVersionable() {
        return true
    }

    @Override
    boolean isChangeable() {
        return true
    }

    @Override
    boolean isEditable() {
        return item.isEditable()
    }

    @Override
    void save() {
        ModellingUIItem modellingUIItem = mainModel.getAbstractUIItem(item)
        if (modellingUIItem) {
            AbstractModellingModel viewModel = mainModel.getViewModel(modellingUIItem)
            if (viewModel != null) {
                //consistency check

                List<PacketCollector> collectors = item.collectors.clone()
                List<PacketCollector> uiCollectors = []
                collectCollectors(viewModel.treeModel.root, uiCollectors)
                boolean error = false

                for (PacketCollector collector in uiCollectors) {
                    PacketCollector packetCollector = collectors.find { it.path == collector.path }
                    if (packetCollector == null) {
                        error = true
                        LOG.error("Collector ${collector.path} exists in the UI but not in the result configuration to be saved!")
                    } else if (!(collector.is(packetCollector))) {
                        error = true
                        LOG.error("Collector ${collector.path} has different instances in the UI and result configuration to be saved!")
                    } else {
                        collectors.remove(packetCollector)
                    }
                }

                if (!collectors.empty) {
                    for (PacketCollector collector in collectors) {
                        error = true
                        LOG.error("Collector ${collector.path} exists in the result configuration to be saved, but not in the UI!")
                    }
                }

                if (error) {
                    throw new RiskAnalyticsInconsistencyException("Collectors in the UI and the result configuration are different.")
                }
            }
        }
        super.save()
    }

    private void collectCollectors(ITableTreeNode node, List<PacketCollector> list) {
        for (int i = 0; i < node.childCount; i++) {
            ITableTreeNode child = node.getChildAt(i)
            if (child instanceof ResultConfigurationTableTreeNode) {
                if (child.collector != null) {
                    list.add(child.collector)
                }
            } else {
                collectCollectors(child, list)
            }
        }
    }
}

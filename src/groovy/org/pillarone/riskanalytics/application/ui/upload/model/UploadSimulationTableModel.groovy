package org.pillarone.riskanalytics.application.ui.upload.model

import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue.UlcSimulationRuntimeService
import org.pillarone.riskanalytics.application.ui.sortable.model.IOrderChangedListener
import org.pillarone.riskanalytics.application.ui.sortable.model.SortableTableModel
import org.pillarone.riskanalytics.application.ui.sortable.model.SortedEvent
import org.pillarone.riskanalytics.core.simulation.engine.ISimulationRuntimeInfoListener
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.SimulationProfile
import org.pillarone.riskanalytics.core.simulationprofile.SimulationProfileService
import org.springframework.beans.factory.config.ConfigurableBeanFactory
import org.springframework.context.annotation.Scope
import org.springframework.stereotype.Component

import javax.annotation.PostConstruct
import javax.annotation.Resource

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
class UploadSimulationTableModel extends SortableTableModel<SimulationRowInfoRowModel> {

    private final static Log LOG = LogFactory.getLog(UploadSimulationTableModel)

    private ISimulationRuntimeInfoListener simulationRuntimeInfoListener

    @Resource
    UlcSimulationRuntimeService ulcSimulationRuntimeService

    @Resource
    SimulationProfileService simulationProfileService

    private final IOrderChangedListener listener

    UploadSimulationTableModel() {
        super([])
        listener = new MyOrderListener()
        addOrderChangedListener(listener)
    }

    void setSimulations(List<Simulation> simulations, String simulationProfileName) {
        infos = createSimulationRowInfos(simulations, simulationProfileName)
    }

    @PostConstruct
    void initialize() {
    }

    void close() {
        simulationRuntimeInfoListener = null
    }

    @Override
    String getColumnName(int column) {
        SimulationRowInfoRowModel.COLUMN_NAMES[column]
    }

    @Override
    int getColumnCount() {
        SimulationRowInfoRowModel.SIZE
    }

    @Override
    Object getValueAt(int row, int column) {
        backedList[row].getValueAt(column)
    }

    private void setInfos(List<SimulationRowInfo> infos) {
        List<SimulationRowInfoRowModel> newModels = []
        infos.eachWithIndex { SimulationRowInfo info, int row ->
            newModels << new SimulationRowInfoRowModel(row, this, info, columnCount)
        }
        backedList = newModels
        fireTableDataChanged()
    }

    private List<SimulationRowInfo> createSimulationRowInfos(List<Simulation> simulations, String simulationProfileName) {
        Map<Class, SimulationProfile> byModelClass = simulationProfileService.getSimulationProfilesGroupedByModelClass(simulationProfileName)
        simulations.collect { Simulation sim -> new SimulationRowInfo(sim, byModelClass[sim.modelClass]) }
    }

    void simulationProfileNameChanged(String simulationProfileName) {
        Map<Class, SimulationProfile> byModelClass = simulationProfileService.getSimulationProfilesGroupedByModelClass(simulationProfileName)
        backedList.each { SimulationRowInfoRowModel infoRowModel ->
            infoRowModel.object.simulationProfile = byModelClass[infoRowModel.object.modelClass]
            infoRowModel.update()
        }
    }

    private void assignRowsToColumnModels() {
        backedList.eachWithIndex { SimulationRowInfoRowModel columnModel, int row ->
            columnModel.row = row
        }
    }

    List<SimulationRowInfo> getSimulationRowInfos() {
        backedList.object
    }


    private class MyOrderListener implements IOrderChangedListener {
        @Override
        void orderChanged(SortedEvent event) {
            assignRowsToColumnModels()
        }
    }
}

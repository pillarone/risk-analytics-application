package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.table.AbstractTableModel
import grails.util.Holders
import org.joda.time.DateTime
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.ui.util.IResourceBundleResolver
import org.pillarone.riskanalytics.core.simulation.SimulationState
import org.pillarone.riskanalytics.core.simulation.engine.SimulationRuntimeInfo

import static org.pillarone.riskanalytics.application.UserContext.hasCurrentUser

class SimulationRowModel extends AbstractTableRowModel<SimulationRuntimeInfo> {

    static final Map<Integer, String> COLUMN_NAME_KEYS = hasCurrentUser() ? [
            0: 'simulation',
            1: 'batch',
            2: 'p14n',
            3: 'template',
            4: 'iterations',
            5: 'priority',
            6: 'configuredAt',
            7: 'configuredBy',
            8: 'simulationState',
            9: 'time'
    ] : [
            0: 'simulation',
            1: 'batch',
            2: 'p14n',
            3: 'template',
            4: 'iterations',
            5: 'priority',
            6: 'configuredAt',
            7: 'simulationState',
            8: 'time'
    ] as Map<Integer, String>

    private static final Map<Integer, Closure<String>> COLUMN_VALUE_FACTORIES = hasCurrentUser() ? [
            0: { SimulationRuntimeInfo info -> info.simulation?.nameAndVersion },
            1: { SimulationRuntimeInfo info -> info.simulation?.batch?.name },
            2: { SimulationRuntimeInfo info -> info.parameterization?.nameAndVersion },
            3: { SimulationRuntimeInfo info -> info.resultConfiguration?.nameAndVersion },
            4: { SimulationRuntimeInfo info -> info.iterations?.toString() },
            5: { SimulationRuntimeInfo info -> info.priority?.toString() },
            6: { SimulationRuntimeInfo info -> info.configuredAt ? DateFormatUtils.getDateFormat("yyyy.MM.dd HH:mm:ss").print(new DateTime(info.configuredAt.time)) : '' },
            7: { SimulationRuntimeInfo info -> info.username},
            8: { SimulationRuntimeInfo info -> getDisplayText(info.simulationState) },
            9: { SimulationRuntimeInfo info -> info.estimatedTime }
    ] : [
            0: { SimulationRuntimeInfo info -> info.simulation?.nameAndVersion },
            1: { SimulationRuntimeInfo info -> info.simulation?.batch?.name },
            2: { SimulationRuntimeInfo info -> info.parameterization?.nameAndVersion },
            3: { SimulationRuntimeInfo info -> info.resultConfiguration?.nameAndVersion },
            4: { SimulationRuntimeInfo info -> info.iterations?.toString() },
            5: { SimulationRuntimeInfo info -> info.priority?.toString() },
            6: { SimulationRuntimeInfo info -> info.configuredAt ? DateFormatUtils.getDateFormat("yyyy.MM.dd HH:mm:ss").print(new DateTime(info.configuredAt.time)) : '' },
            7: { SimulationRuntimeInfo info -> getDisplayText(info.simulationState) },
            8: { SimulationRuntimeInfo info -> info.estimatedTime }
    ]

    private static IResourceBundleResolver getResolver() {
        Holders.grailsApplication.mainContext.getBean('resourceBundleResolver')
    }

    private static String getDisplayText(SimulationState simulationState) {
        resolver.getText(SimulationRowModel, simulationState.toString())
    }

    static final int COLUMN_COUNT = hasCurrentUser() ? 10 : 9

    SimulationRowModel(int row, AbstractTableModel tableModel, SimulationRuntimeInfo info) {
        super(row, tableModel, info, COLUMN_COUNT)
    }

    @Override
    Closure<String> getValueFactory(int index) {
        COLUMN_VALUE_FACTORIES[index]
    }
}

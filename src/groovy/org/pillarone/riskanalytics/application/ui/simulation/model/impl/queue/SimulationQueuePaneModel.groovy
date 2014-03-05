package org.pillarone.riskanalytics.application.ui.simulation.model.impl.queue

import com.ulcjava.base.application.event.ITableModelListener
import com.ulcjava.base.application.event.TableModelEvent
import org.pillarone.riskanalytics.core.simulation.item.Simulation

class SimulationQueuePaneModel {

    SortableListModel simulationTableModel

    private List<ISimulationOrderChangedListener> orderChangedListeners;

    SimulationQueuePaneModel() {
        initialize()
        attachListener()
    }

    private void attachListener() {

        simulationTableModel.addTableModelListener(new ITableModelListener() {
            @Override
            void tableChanged(TableModelEvent event) {
                println("event: $event")
            }
        })
    }

    void addOrderChangedListener(ISimulationOrderChangedListener listener) {
        orderChangedListeners.add(listener)
    }

    void removedOrderChangedListener(ISimulationOrderChangedListener listener) {
        orderChangedListeners.remove(listener)
    }

    private initialize() {
        orderChangedListeners = []
        List<IQueueItem> items = createSomeDummyItems()
        simulationTableModel = new QueueItemListModel(items)
    }

    List<IQueueItem> createSomeDummyItems() {
        (0..100).collect { def index ->
            [
                    getSimulation: { new Simulation("simulation-$index") },
                    getBatchRun: { "batch-$index".toString() },
                    getP14n: { "p14n".toString() },
                    getResultConfiguration: { "template".toString() },
                    getIterations: { index },
                    getPriority: { index },
                    getAddedBy: { "me".toString() },
                    getConfiguredAt: { "".toString() }
            ] as IQueueItem
        }
    }

    boolean moveFromTo(int[] from, int to) {
        simulationTableModel.moveFromTo(from, to)
    }
}


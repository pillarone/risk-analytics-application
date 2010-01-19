package org.pillarone.riskanalytics.application.ui.simulation.model

import com.ulcjava.base.application.IComboBoxModel
import com.ulcjava.base.application.event.IListDataListener
import org.pillarone.riskanalytics.core.output.*

class OutputStrategyComboBoxModel implements IComboBoxModel {

    private List<IListDataListener> listeners = new ArrayList<IListDataListener>()
    private Map<String, ICollectorOutputStrategy> content = new LinkedHashMap<String, ICollectorOutputStrategy>()
    private List values = []
    private int selectedIndex = 0

    public OutputStrategyComboBoxModel() {
        content.put("Database: Bulk Insert", new DBOutput())
        content.put("File", new FileOutput())
        content.put("No output", new NoOutput())

        values = content.keySet().toList()
    }

    void addListDataListener(IListDataListener listener) {
        listeners << listener
    }

    Object getElementAt(int i) {
        return values.get(i)
    }

    int getSize() {
        return values.size()
    }

    void removeListDataListener(IListDataListener listener) {
        listeners.remove(listener)
    }

    Object getSelectedItem() {
        return values.get(selectedIndex)
    }

    void setSelectedItem(Object o) {
        selectedIndex = values.indexOf(o)
    }

    ICollectorOutputStrategy getStrategy() {
        content.get(getSelectedItem())
    }

}

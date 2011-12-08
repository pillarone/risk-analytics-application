package org.pillarone.riskanalytics.application.ui.resultnavigator.model;

import com.ulcjava.base.application.IComboBoxModel;
import com.ulcjava.base.application.event.IListDataListener;
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.ResultAccess;
import org.pillarone.riskanalytics.core.output.SimulationRun;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class SimulationRunComboBoxModel implements IComboBoxModel {

    private List<SimulationRun> availableRuns = null;
    private SimulationRun selected = null;
    private static Comparator<SimulationRun> COMPARATOR = null;
    private List<IListDataListener> fListDataListeners;

    static {
        COMPARATOR = new Comparator<SimulationRun>() {
            public int compare(SimulationRun r1, SimulationRun r2) {
                String name1 = r1.getName();
                String name2 = r2.getName();
                return name1.compareTo(name2);
            }
        };
    }

    public SimulationRunComboBoxModel() {
        availableRuns = (List<SimulationRun>) ResultAccess.getSimulationRuns();
        if (availableRuns != null && availableRuns.size() > 0) {
            sortList(availableRuns);
            selected = availableRuns.get(0);
        }
    }

    public int getSize() {
        return availableRuns.size();
    }

    public Object getElementAt(int index) {
        return availableRuns.get(index).getName();
    }

    public void addListDataListener(IListDataListener listener) {
        if (fListDataListeners == null) {
            fListDataListeners = new ArrayList<IListDataListener>();
        }
        fListDataListeners.add(listener);
    }

    public void removeListDataListener(IListDataListener listener) {
        if (fListDataListeners != null && fListDataListeners.contains(listener)) {
            fListDataListeners.remove(listener);
        }
    }

    public void setSelectedItem(Object anItem) {
        if (anItem instanceof String) {
            String name = (String) anItem;
            SimulationRun run = null;
            int i = 0;
            while (run == null && i < availableRuns.size()) {
                if (availableRuns.get(i).getName().equals(name)) {
                    run = availableRuns.get(i);
                }
                i++;
            }
            selected = run;
        } else if (anItem instanceof SimulationRun) {
            selected = (SimulationRun) anItem;
        }
    }

    public Object getSelectedItem() {
        if (selected != null) {
            return selected.getName();
        }
        return null;
    }

    public SimulationRun getSelectedRun() {
        return selected;
    }

    private static void sortList(List<SimulationRun> runs) {
        if (runs != null) {
            Collections.sort(runs, COMPARATOR);
        }
    }
}

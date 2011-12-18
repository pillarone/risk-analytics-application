package org.pillarone.riskanalytics.application.ui.resultnavigator.model;

import com.ulcjava.base.application.DefaultComboBoxModel;
import com.ulcjava.base.application.IComboBoxModel;
import com.ulcjava.base.application.event.IListDataListener;
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.ResultAccess;
import org.pillarone.riskanalytics.core.output.SimulationRun;

import java.util.*;

public class SimulationRunsModel {

    private List<String> models = null;
    private Map<String, String> modelsLookup = null; // short name --> long name
    private String selectedModel = null; // short name
    private Map<String, Map<String,SimulationRun>> runsLookup = null; // simulation run name --> simulation run
    private Map<String,IComboBoxModel> runsComboBoxModels = null; // model name long name --> combo box model
    private String selectedRunName = null;

    static String getSimpleName(String modelName) {
        return  modelName.substring(modelName.lastIndexOf(".")+1);
    }

    public String getFullModelName(String shortModelName) {
        return modelsLookup.get(shortModelName);
    }

    public SimulationRunsModel() {
        List<SimulationRun> runs = ResultAccess.getSimulationRuns();
        models = new ArrayList<String>();
        Map<String, List<SimulationRun>> simulationRuns = new HashMap<String, List<SimulationRun>>();
        modelsLookup = new HashMap<String, String>();
        runsLookup = new HashMap<String, Map<String,SimulationRun>>();
        runsComboBoxModels = new HashMap<String,IComboBoxModel>();
        if (runs != null && runs.size() > 0) {
            for (SimulationRun run : runs) {
                String modelName = run.getModel();
                String modelShortName = getSimpleName(modelName);
                if (!modelsLookup.containsKey(modelShortName)) {
                    modelsLookup.put(modelShortName, modelName);
                }
                if (!simulationRuns.containsKey(modelName)) {
                    simulationRuns.put(modelName, new ArrayList<SimulationRun>());
                    models.add(modelName);
                }
                simulationRuns.get(modelName).add(run);
                String runName = run.getName();
                if (!runsLookup.containsKey(modelName)) {
                    runsLookup.put(modelName, new HashMap<String,SimulationRun>());
                }
                Map<String,SimulationRun> runsForModel = runsLookup.get(modelName);
                if (!runsForModel.containsKey(runName)) {
                    runsForModel.put(runName, run);
                }
            }
        }
        Collections.sort(models);
        for (String modelName : models) {
            List<String> runsForModel = new ArrayList<String>();
            for (SimulationRun run : simulationRuns.get(modelName)) {
                runsForModel.add(run.getName());
            }
            Collections.sort(runsForModel);
            runsComboBoxModels.put(modelName, new DefaultComboBoxModel(runsForModel));
        }

        selectedModel = getSimpleName(models.get(0));
        selectedRunName = simulationRuns.get(models.get(0)).get(0).getName();
    }

    public SimulationRun getSelectedRun() {
        return runsLookup.get(modelsLookup.get(selectedModel)).get(selectedRunName);
    }

    public String getSelectedModelShortName() {
        return selectedModel;
    }

    public String getSelectedModelName() {
        return modelsLookup.get(selectedModel);
    }

    public void setSelectedModelShortName(String modelName) {
        selectedModel = modelName;
    }

    public void setSelectedRun(String runName) {
        selectedRunName = runName;
    }

    public IComboBoxModel getModelComboBoxModel() {
        return new DefaultComboBoxModel(models);
    }

    public IComboBoxModel getSimulationRunsComboBoxModel() {
        return runsComboBoxModels.get(modelsLookup.get(selectedModel));
    }


}

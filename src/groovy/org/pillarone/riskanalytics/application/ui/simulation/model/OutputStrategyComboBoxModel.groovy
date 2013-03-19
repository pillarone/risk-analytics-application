package org.pillarone.riskanalytics.application.ui.simulation.model

import com.ulcjava.base.application.DefaultComboBoxModel
import org.pillarone.riskanalytics.core.output.DBOutput
import org.pillarone.riskanalytics.core.output.FileOutput
import org.pillarone.riskanalytics.core.output.ICollectorOutputStrategy
import org.pillarone.riskanalytics.core.output.NoOutput

class OutputStrategyComboBoxModel extends DefaultComboBoxModel {

    private Map<String, ICollectorOutputStrategy> content = new LinkedHashMap<String, ICollectorOutputStrategy>()

    public OutputStrategyComboBoxModel() {
        content.put("Database: Bulk Insert", new DBOutput())
        content.put("File", new FileOutput())
        content.put("No output", new NoOutput())

        content.keySet().each { addElement it }
    }

    ICollectorOutputStrategy getStrategy() {
        content.get(getSelectedItem())
    }

}

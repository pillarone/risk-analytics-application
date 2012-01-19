package org.pillarone.riskanalytics.application.ui.resultnavigator.view

import com.ulcjava.applicationframework.application.AbstractBean
import org.apache.commons.logging.Log
import org.apache.commons.logging.LogFactory
import com.ulcjava.base.application.ULCComponent
import com.ulcjava.base.application.ULCBoxPane
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.ResultAccess

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElement
import com.ulcjava.base.application.BorderFactory
import com.ulcjava.base.application.ULCScrollPane

import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElementTableModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryMappingRegistry
import org.pillarone.riskanalytics.application.ui.resultnavigator.categories.CategoryMapping
import com.ulcjava.base.application.ULCToolBar
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCFiller
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCButton
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.SimulationRunsModel
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.ULCSplitPane
import com.ulcjava.base.application.IComboBoxModel
import com.ulcjava.base.application.UlcUtilities
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCContainer
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.KeyfigureSelectionModel

/**
 * @author martin.melchior
 */
class ResultNavigator extends AbstractBean {

    private static Log LOG = LogFactory.getLog(ResultNavigator.class);

    private ULCBoxPane contents
    private ULCBoxPane resultEntryTable

    CategoryConfigurationDialog configurationDialog
    CategoryMapping categoryMapping

    private ResultAccess resultAccess

    /**
     */
    public ResultNavigator() {
        super()
        resultAccess = new ResultAccess()
        contents = createContentView(true)
        contents.setVisible true
    }

    public ResultNavigator(SimulationRun run) {
        super()
        resultAccess = new ResultAccess()
        contents = createContentView(false)
        contents.setVisible true
        loadSimulationRun(run)

    }

    /**
     *
     */
    public ULCContainer getContentView() {
        return contents;
    }

    private ULCToolBar createToolbar(boolean enabled) {
        SimulationRunsModel simulationRunsModel = new SimulationRunsModel()

        ULCToolBar toolBar = new ULCToolBar("Simulation Run", ULCToolBar.HORIZONTAL)
        ULCLabel modelLabel = new ULCLabel("Model ")
        toolBar.add(modelLabel)
        ULCComboBox modelSelector = new ULCComboBox(simulationRunsModel.getModelComboBoxModel())
        modelSelector.setEnabled(enabled)
        toolBar.add(modelSelector)
        toolBar.addSeparator()
        ULCLabel simRunLabel = new ULCLabel("Simulation Run ")
        toolBar.add(simRunLabel)
        ULCComboBox simulationRunSelector = new ULCComboBox(simulationRunsModel.getSimulationRunsComboBoxModel())
        simulationRunSelector.setEnabled(enabled)
        simulationRunSelector.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                ULCComboBox source = (ULCComboBox)event.getSource();
                String simulationRunName = source.getSelectedItem();
                simulationRunsModel.setSelectedRun(simulationRunName);
            }
        });
        modelSelector.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                ULCComboBox source = (ULCComboBox)event.getSource()
                String modelShortName = source.getSelectedItem()
                simulationRunsModel.setSelectedModelShortName(modelShortName);
                IComboBoxModel cbModel = simulationRunsModel.getSimulationRunsComboBoxModel()
                simulationRunSelector.setModel(cbModel);
            }
        });
        toolBar.add(simulationRunSelector)
        toolBar.addSeparator()
        ULCButton loadButton = new ULCButton("Load")
        loadButton.setEnabled(enabled)
        loadButton.addActionListener( new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                loadSimulationRun(simulationRunsModel.getSelectedRun())
            }
        })
        toolBar.add(loadButton)
        toolBar.add(ULCFiller.createHorizontalGlue())

        ULCButton configureMapping = new ULCButton("Mapping Specification")
        toolBar.add(configureMapping)
        configureMapping.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (configurationDialog==null) {
                    configurationDialog = new CategoryConfigurationDialog(UlcUtilities.getWindowAncestor(contents))
                }
                categoryMapping = CategoryMappingRegistry.getCategoryMapping(simulationRunsModel.getSelectedRun())
                if (categoryMapping) {
                    configurationDialog.createContent categoryMapping
                    configurationDialog.setVisible true
                } else {
                    ULCAlert alert = new ULCAlert(UlcUtilities.getWindowAncestor(configurationDialog), "No mapping available", "No mapping available for this simulation run", "ok")
                    alert.show()
                }
            }
        })

        return toolBar
    }

    private void loadSimulationRun(SimulationRun run) {
        List<OutputElement> elements = resultAccess.getOutputElements(run)
        categoryMapping = CategoryMappingRegistry.getCategoryMapping(run)
        if (categoryMapping) {
            categoryMapping.categorize(elements)
        }
        loadDataIntoResultEntriesArea(elements, run)
    }

    private void loadDataIntoResultEntriesArea(List<OutputElement> elements, SimulationRun run) {
        OutputElementTableModel model = new OutputElementTableModel(elements, categoryMapping)
        OutputElementTable table = new OutputElementTable(model)
        ULCScrollPane scrollPane = new ULCScrollPane()
        scrollPane.setViewPortView(table)

        table.setDragEnabled(true)

        KeyfigureSelectionModel keyfigureSelectionModel = new KeyfigureSelectionModel(run)
        table.keyfigureSelection = keyfigureSelectionModel
        FilterPanel filterPanel = new FilterPanel(model, keyfigureSelectionModel)

        resultEntryTable.removeAll()
        resultEntryTable.add(ULCBoxPane.BOX_EXPAND_TOP, filterPanel);
        resultEntryTable.add(ULCBoxPane.BOX_EXPAND_EXPAND, scrollPane);

        filterPanel.registerFilterListener table
        // table.addCategoryListChangeListener(filterPanel.getCategoryToFilter())
    }

    private ULCBoxPane createContentView(boolean toolbarEnabled) {
        ULCBoxPane contentView = new ULCBoxPane(true)
        // contentView.setPreferredSize(new Dimension(800, 600))
        contentView.setBorder(BorderFactory.createEmptyBorder())

        ULCBoxPane toolbarArea = new ULCBoxPane(false)
        toolbarArea.setBorder(BorderFactory.createTitledBorder("Data Access"))
        ULCToolBar toolbar = createToolbar(toolbarEnabled)
        toolbarArea.add(ULCBoxPane.BOX_EXPAND_TOP, toolbar);
        contentView.add(ULCBoxPane.BOX_EXPAND_TOP, toolbarArea);

        resultEntryTable = new ULCBoxPane(true)
        resultEntryTable.setBorder(BorderFactory.createTitledBorder("Data Selection"))
        contentView.add(ULCBoxPane.BOX_EXPAND_EXPAND, resultEntryTable)

        return contentView
    }
}

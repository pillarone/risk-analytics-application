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
 * The main component that provides the view for navigating within result entries.
 *
 * @author martin.melchior
 */
class ResultNavigator extends AbstractBean {

    private static Log LOG = LogFactory.getLog(ResultNavigator.class);

    private CategoryMapping categoryMapping
    private SimulationRunsModel simulationRunsModel
    private ResultAccess resultAccess

    private ULCBoxPane contents
    private ULCComboBox modelSelector
    private ULCComboBox simulationRunSelector
    private ULCButton loadButton
    private ULCBoxPane resultEntryTable
    private CategoryConfigurationDialog configurationDialog

    /**
     */
    public ResultNavigator() {
        super()

        // initialize data access helper
        resultAccess = new ResultAccess()

        // initialize the models for the simulaton run selection and the category mapping
        simulationRunsModel = new SimulationRunsModel()

        // initialize the views
        createContentView()
    }

    public ResultNavigator(SimulationRun run) {
        this()

        // load the run and put the data into the table
        loadSimulationRun(run)

        //freeze the selection of the simulation run and disable the load button
        freezeSimulationRunSelection(run)
    }

    /**
     *
     */
    public ULCContainer getContentView() {
        return contents;
    }

    /**
     * Initialize the view - note that the table area (from where data are selected) remains empty
     * since no data available yet.
     */
    private void createContentView() {
        // area where the output element table will go into
        resultEntryTable = new ULCBoxPane(true)
        resultEntryTable.setBorder(BorderFactory.createTitledBorder("Data Selection"))

        // toolbar area --> selection of simulation run, load button
        ULCBoxPane toolbarArea = new ULCBoxPane(false)
        toolbarArea.setBorder(BorderFactory.createTitledBorder("Data Access"))
        ULCToolBar toolbar = createToolbar()
        toolbarArea.add(ULCBoxPane.BOX_EXPAND_TOP, toolbar);

        // pack all into a content view
        contents = new ULCBoxPane(true)
        contents.setBorder(BorderFactory.createEmptyBorder())
        contents.add(ULCBoxPane.BOX_EXPAND_TOP, toolbarArea);
        contents.add(ULCBoxPane.BOX_EXPAND_EXPAND, resultEntryTable)
        contents.setVisible true
    }

    /**
     * Create the toolbar that contains the selection of the simulation run.
     * @return
     */
    private ULCToolBar createToolbar() {

        // initialize the components
        ULCToolBar toolBar = new ULCToolBar("Simulation Run", ULCToolBar.HORIZONTAL)
        ULCLabel modelLabel = new ULCLabel("Model ")
        modelSelector = new ULCComboBox(simulationRunsModel.getModelComboBoxModel())
        ULCLabel simRunLabel = new ULCLabel("Simulation Run ")
        simulationRunSelector = new ULCComboBox(simulationRunsModel.getSimulationRunsComboBoxModel())
        loadButton = new ULCButton("Load")
        ULCButton configureMapping = new ULCButton("Mapping Specification")

        // layout them
        toolBar.add(modelLabel)
        toolBar.add(modelSelector)
        toolBar.addSeparator()
        toolBar.add(simRunLabel)
        toolBar.add(simulationRunSelector)
        toolBar.addSeparator()
        toolBar.add(loadButton)
        toolBar.add(ULCFiller.createHorizontalGlue())
        toolBar.add(configureMapping)

        // attach listeners to react to selection changes or push button actions
        modelSelector.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                ULCComboBox source = (ULCComboBox)event.getSource()
                String modelShortName = source.getSelectedItem()
                simulationRunsModel.setSelectedModelShortName(modelShortName);
                IComboBoxModel cbModel = simulationRunsModel.getSimulationRunsComboBoxModel()
                simulationRunSelector.setModel(cbModel);
            }
        });
        simulationRunSelector.addActionListener(new IActionListener() {
            public void actionPerformed(ActionEvent event) {
                ULCComboBox source = (ULCComboBox)event.getSource();
                String simulationRunName = source.getSelectedItem();
                simulationRunsModel.setSelectedRun(simulationRunName);
            }
        });
        loadButton.addActionListener( new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                loadSimulationRun(simulationRunsModel.getSelectedRun())
            }
        })
        configureMapping.addActionListener(new IActionListener() {
            void actionPerformed(ActionEvent actionEvent) {
                if (configurationDialog==null) {
                    configurationDialog = new CategoryConfigurationDialog(UlcUtilities.getWindowAncestor(contents))
                    if (!categoryMapping) {
                        categoryMapping = CategoryMappingRegistry.getCategoryMapping(simulationRunsModel.getSelectedRun())
                    }
                    if (categoryMapping) {
                        configurationDialog.createContent categoryMapping
                    } else {
                        ULCAlert alert = new ULCAlert(UlcUtilities.getWindowAncestor(configurationDialog), "No mapping available", "No mapping available for this simulation run", "ok")
                        alert.show()
                    }
                }
                if (categoryMapping) {
                    configurationDialog.setVisible true
                }
            }
        })

        return toolBar
    }

    /**
     * Load the data associated with the simulation run and fill them into the result entry table.
     * Check for a category mapping and attach associated information also to the OutputElement's.
     * @param run
     */
    private void loadSimulationRun(SimulationRun run) {
        List<OutputElement> elements = resultAccess.getOutputElements(run)
        categoryMapping = CategoryMappingRegistry.getCategoryMapping(run)
        if (categoryMapping) {
            categoryMapping.categorize(elements)
        }
        loadDataIntoResultEntriesArea(elements, run)
    }

    /**
     * Load the result entries (List of OutputElement's) into the result entry table
      * @param elements
     * @param run
     */
    private void loadDataIntoResultEntriesArea(List<OutputElement> elements, SimulationRun run) {
        // instantiate the model
        KeyfigureSelectionModel keyfigureSelectionModel = new KeyfigureSelectionModel(run)
        OutputElementTableModel model = new OutputElementTableModel(elements, categoryMapping, keyfigureSelectionModel)

        // initialize the components that go into the resultEntryArea
        FilterPanel filterPanel = new FilterPanel(model)
        OutputElementTable table = new OutputElementTable(model)
        table.setDragEnabled(true)
        ULCScrollPane scrollPane = new ULCScrollPane()
        scrollPane.setViewPortView(table)

        // layout them
        resultEntryTable.removeAll()
        resultEntryTable.add(ULCBoxPane.BOX_EXPAND_TOP, filterPanel);
        resultEntryTable.add(ULCBoxPane.BOX_EXPAND_EXPAND, scrollPane);

        // attach listeners
        filterPanel.registerFilterListener table
        // table.addCategoryListChangeListener(filterPanel.getCategoryToFilter())
    }

    /**
     * Freezes the selection boxes at the values specified by the simulation run.
     * @param run
     */
    private void freezeSimulationRunSelection(SimulationRun run) {
        // first set the selected items in the combo boxes
        String model = run.model
        simulationRunsModel.setSelectedModel(model)
        simulationRunsModel.setSelectedRun(run.getName())

        // then disable the combo boxes and the load button
        modelSelector.setEnabled(false)
        simulationRunSelector.setEnabled(false)
        loadButton.setEnabled(false)
    }
}

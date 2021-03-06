package org.pillarone.riskanalytics.application.ui.result.view

import com.canoo.ulc.community.fixedcolumntabletree.server.ULCFixedColumnTableTree
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import grails.util.Holders
import org.pillarone.riskanalytics.application.dataaccess.function.DeviationAbsoluteFunction
import org.pillarone.riskanalytics.application.dataaccess.function.DeviationPercentageFunction
import org.pillarone.riskanalytics.application.dataaccess.function.FractionAbsoluteFunction
import org.pillarone.riskanalytics.application.dataaccess.function.FractionPercentageFunction
import org.pillarone.riskanalytics.application.ui.main.eventbus.event.OpenDetailViewEvent
import org.pillarone.riskanalytics.application.ui.main.eventbus.RiskAnalyticsEventBus
import org.pillarone.riskanalytics.application.ui.result.action.keyfigure.DefaultToggleValueProvider
import org.pillarone.riskanalytics.application.ui.result.action.keyfigure.ToggleKeyFigureAction
import org.pillarone.riskanalytics.application.ui.result.model.CompareSimulationsViewModel
import org.pillarone.riskanalytics.application.ui.util.SeriesColor
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.application.util.SimulationUtilities
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.*
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.getText
import static org.pillarone.riskanalytics.application.ui.util.UIUtils.spaceAround
import org.pillarone.riskanalytics.application.util.prefs.UserPreferences
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterizationUtilities
import org.pillarone.riskanalytics.application.ui.main.view.item.CompareParameterizationUIItem
import org.pillarone.riskanalytics.application.util.prefs.UserPreferencesFactory

class CompareSimulationsCriteriaView {

    ULCBoxPane content
    ULCBoxPane checkBoxPane
    ULCSpinner minSpinner
    ULCSpinner maxSpinner
    ULCLabel spinnerLabel
    ULCButton rendererTreeButton

    ULCComboBox simulationsComboBox


    List<ULCCheckBox> simulationCheckBoxes

    ULCToolBar toolbar
    ULCToggleButton columnOrder
    ULCCheckBox devPercentage
    ULCCheckBox devAbsolute
    ULCCheckBox frPercentage
    ULCCheckBox frAbsolute
    ULCButton compareParameterizations
    ULCRadioButton byPeriod
    ULCRadioButton byKeyFigure

    CompareSimulationsViewModel model
    ULCFixedColumnTableTree tree
    CompareSimulationsView compareSimulationTreeView


    public CompareSimulationsCriteriaView(CompareSimulationsView compareSimulationTreeView, CompareSimulationsViewModel model, tree) {
        this.compareSimulationTreeView = compareSimulationTreeView
        this.model = model
        this.tree = tree
        simulationCheckBoxes = []
        initView(model)
    }

    private def initView(def model) {
        if (model != null) {
            initComponents()
            layoutComponents()
            attachListeners()
        }
    }

    private void initComponents() {
        content = new ULCBoxPane(1, 2)
        content.setPreferredSize(new Dimension(500, 300))
        ULCSpinnerNumberModel spinnerModel = new ULCSpinnerNumberModel()
        spinnerModel.minimum = 0

        minSpinner = new ULCSpinner(spinnerModel)

        spinnerModel = new ULCSpinnerNumberModel()
        spinnerModel.minimum = 0
        maxSpinner = new ULCSpinner(spinnerModel)

        minSpinner.setPreferredSize(new Dimension(120, 20))
        maxSpinner.setPreferredSize(new Dimension(120, 20))

        spinnerLabel = new ULCLabel(getText(this.class, "interval"))
        rendererTreeButton = new ULCButton(getText(this.class, "update"))

        model.item.eachWithIndex { Simulation it, i ->
            ULCCheckBox checkBox = new SimulationCheckBox(it)
            simulationCheckBoxes.add(checkBox)
        }

        devPercentage = new ULCCheckBox(getText(this.class, "Percentage"))
        devPercentage.name = "devPercentage"
        devAbsolute = new ULCCheckBox(getText(this.class, "Absolute"))
        devAbsolute.name = "devAbsolute"
        frPercentage = new ULCCheckBox(getText(this.class, "Percentage"))
        frPercentage.name = "frPercentage"
        frAbsolute = new ULCCheckBox(getText(this.class, "Absolute"))
        frAbsolute.name = "frAbsolute"
        compareParameterizations = new ULCButton(getText(this.class, "CompareParameterizations"))
        byPeriod = new ULCRadioButton(getText(this.class, "Period"))
        byKeyFigure = new ULCRadioButton(getText(this.class, "KeyFigure"))
        ULCButtonGroup orderButtonGroup = new ULCButtonGroup()
        byPeriod.group = orderButtonGroup
        byKeyFigure.group = orderButtonGroup
        byKeyFigure.setSelected(true)
    }

    private void layoutComponents() {
        ULCBoxPane pane = new ULCBoxPane(3, 0)
        addSimulationsComboBox(pane)
        addSimulationsCheckBox(pane)
        addDeviationsCheckBox(pane)
        addFractionsCheckBox(pane)
        pane.add(2, ULCBoxPane.BOX_LEFT_TOP, spaceAround(compareParameterizations, 0, 5, 0, 0))
        pane.add(3, ULCBoxPane.BOX_EXPAND_EXPAND, ULCFiller.createVerticalStrut(10))
        content.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(pane, 2, 5, 0, 0))
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCSeparator(ULCSeparator.HORIZONTAL))

        ULCBoxPane displayPane = new ULCBoxPane(5, 0)
        displayPane.setPreferredSize(new Dimension(600, 60))
        displayPane.setMinimumSize(new Dimension(600, 60))
        addGroupByRadioButtons(displayPane)
        addSpinnerPane(displayPane)
        displayPane.add(5, ULCBoxPane.BOX_EXPAND_EXPAND, ULCFiller.createVerticalStrut(1))
        content.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(displayPane, 0, 5, 0, 0))
        content.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())
    }

    private void attachListeners() {
        simulationsComboBox.addActionListener([actionPerformed: { ActionEvent evt ->
            Collections.swap(simulationCheckBoxes, 0, simulationCheckBoxes.indexOf(simulationCheckBoxes.find { SimulationCheckBox cb -> cb.simulation == evt.source.model.selectedObject }))
            initCheckBoxes(checkBoxPane)
            model.setReferenceSimulation(simulationsComboBox.model.selectedObject)
        }] as IActionListener)

        simulationCheckBoxes.each { SimulationCheckBox cb ->
            cb.addValueChangedListener([valueChanged: { ValueChangedEvent evt ->
                SimulationCheckBox source = evt.getSource()
                boolean value = source.selected
                value ? model.addSimulation(source.simulation) : model.removeSimulation(source.simulation)
                updateCompareCheckBoxes()
            }] as IValueChangedListener)
        }

        devAbsolute.action = new ToggleKeyFigureAction(new DeviationAbsoluteFunction(), new DefaultToggleValueProvider(devAbsolute), model, tree.viewPortTableTree)
        devPercentage.action = new ToggleKeyFigureAction(new DeviationPercentageFunction(), new DefaultToggleValueProvider(devPercentage), model, tree.viewPortTableTree)
        frAbsolute.action = new ToggleKeyFigureAction(new FractionAbsoluteFunction(), new DefaultToggleValueProvider(frAbsolute), model, tree.viewPortTableTree)
        frPercentage.action = new ToggleKeyFigureAction(new FractionPercentageFunction(), new DefaultToggleValueProvider(frPercentage), model, tree.viewPortTableTree)

        compareParameterizations.addActionListener([actionPerformed: { event ->
            ArrayList parameters = ParameterizationUtilities.getParameters(model.treeModel.simulations)
            CompareParameterizationUIItem compareParameterizationUIItem = new CompareParameterizationUIItem(model.model, parameters)
            riskAnalyticsEventBus.post(new OpenDetailViewEvent(compareParameterizationUIItem))
        }] as IActionListener)

        //ColumnOrderAction
        byPeriod.addValueChangedListener([valueChanged: { ValueChangedEvent event -> model.orderByKeyfigure = !event.source.isSelected() }] as IValueChangedListener)
        byKeyFigure.addValueChangedListener([valueChanged: { ValueChangedEvent event -> model.orderByKeyfigure = event.source.isSelected() }] as IValueChangedListener)

        Closure updateAction = { event -> setInterval() }
        KeyStroke enter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0, false);
        devPercentage.registerKeyboardAction([actionPerformed: updateAction] as IActionListener, enter, ULCComponent.WHEN_IN_FOCUSED_WINDOW);
        rendererTreeButton.addActionListener([actionPerformed: updateAction] as IActionListener)

        minSpinner.addValueChangedListener([valueChanged: { evt ->
            if (minSpinner.value > maxSpinner.value) {
                maxSpinner.value = minSpinner.value
            }

        }] as IValueChangedListener)
    }

    private RiskAnalyticsEventBus getRiskAnalyticsEventBus() {
        Holders.grailsApplication.mainContext.getBean('riskAnalyticsEventBus', RiskAnalyticsEventBus)
    }

    private void setInterval() {
        model.setInterval(minSpinner.getValue(), maxSpinner.getValue())
    }

    private void addSimulationsComboBox(ULCBoxPane pane) {
        ItemsComboBoxModel<Simulation> simulationsComboBoxModel = new ItemsComboBoxModel<Simulation>(model.item)
        simulationsComboBox = new ULCComboBox(simulationsComboBoxModel)
        simulationsComboBox.name = "${CompareSimulationsCriteriaView.class.getSimpleName()}.simulationComboBox"
        simulationsComboBox.setMinimumSize(new Dimension(160, 20))
        ULCLabel simulationsComboBoxLabel = new ULCLabel(getText(this.class, "ReferenceResult") + ": ")
        pane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(simulationsComboBoxLabel, 5, 10, 0, 0))
        pane.add(2, ULCBoxPane.BOX_LEFT_TOP, spaceAround(simulationsComboBox, 5, 0, 0, 0))
    }


    private void addSimulationsCheckBox(ULCBoxPane pane) {
        ULCLabel label = new ULCLabel(getText(this.class, "DisplayResult") + ": ")
        pane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(label, 0, 10, 0, 0))
        checkBoxPane = new ULCBoxPane()
        initCheckBoxes(checkBoxPane)
        pane.add(2, ULCBoxPane.BOX_LEFT_TOP, checkBoxPane)
    }

    private void addDeviationsCheckBox(ULCBoxPane pane) {
        ULCLabel label = new ULCLabel(getText(this.class, "Deviation") + ": ")
        pane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(label, 0, 10, 0, 0))
        pane.add(ULCBoxPane.BOX_LEFT_TOP, devPercentage)
        pane.add(ULCBoxPane.BOX_LEFT_TOP, devAbsolute)
    }

    private void addFractionsCheckBox(ULCBoxPane pane) {
        ULCLabel label = new ULCLabel(getText(this.class, "Fraction") + ": ")
        pane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(label, 0, 10, 0, 0))
        pane.add(ULCBoxPane.BOX_LEFT_TOP, frPercentage)
        pane.add(ULCBoxPane.BOX_LEFT_TOP, frAbsolute)
    }

    private void addGroupByRadioButtons(ULCBoxPane pane) {
        ULCLabel label = new ULCLabel(getText(this.class, "GroupBy") + ": ")
        pane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(label, 0, 10, 0, 0))
        pane.add(ULCBoxPane.BOX_LEFT_TOP, byPeriod)
        pane.add(3, ULCBoxPane.BOX_LEFT_TOP, byKeyFigure)
    }


    private void initCheckBoxes(ULCBoxPane pane) {
        pane.removeAll()
        simulationCheckBoxes.eachWithIndex { SimulationCheckBox it, int i ->
            it.setText(getNamePrefix(i) + it.simulation.name)
            Color color = UIUtils.toULCColor(SeriesColor.seriesColorList[i])
            it.setBackground(color)
            it.setForeground UIUtils.getFontColor(color)
            it.setSelected(true)
            it.setEnabled(true)
            pane.add(ULCBoxPane.BOX_LEFT_TOP, it)
        }
    }

    private void updateCompareCheckBoxes() {
        boolean atLeatOneSelected = false
        simulationCheckBoxes.eachWithIndex { SimulationCheckBox it, int i ->
            if (i > 0 && it.isSelected()) {
                atLeatOneSelected = true
            }
        }
        if (!atLeatOneSelected) {
            devPercentage.setSelected(false)
            devAbsolute.setSelected(false)
            frPercentage.setSelected(false)
            frAbsolute.setSelected(false)
        }
    }

    private void addSpinnerPane(ULCBoxPane displayPane) {
        ULCLabel andLabel = new ULCLabel(getText(this.class, "And"))
        displayPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(spinnerLabel, 0, 10, 0, 0))
        displayPane.add(ULCBoxPane.BOX_LEFT_TOP, minSpinner)
        displayPane.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(andLabel, 0, 2, 0, 2))
        displayPane.add(ULCBoxPane.BOX_LEFT_TOP, maxSpinner)
        displayPane.add(ULCBoxPane.BOX_LEFT_TOP, rendererTreeButton)
    }

    static final String getNamePrefix(int i) {
        return SimulationUtilities.RESULT_CHAR_PREFIXES[i] + ": ";
    }

}

class ItemsComboBoxModel<T> extends DefaultComboBoxModel {

    List<T> items
    UserPreferences userPreferences = UserPreferencesFactory.getUserPreferences()
    String itemPreferenceKey

    public ItemsComboBoxModel(List<T> items) {
        super(items*.name)
        this.items = items
    }

    public ItemsComboBoxModel(List<T> items, String itemPreferenceKey) {
        super(items*.name)
        this.items = items
        this.itemPreferenceKey = itemPreferenceKey
        Object defaultItem = userPreferences.getPropertyValue(itemPreferenceKey)
        selectedItem = (defaultItem && items*.name.contains(defaultItem)) ? defaultItem : getElementAt(0)
    }

    T getSelectedObject() {
        int index = getIndexOf(getSelectedItem())
        return index >= 0 ? items.get(index) : null
    }

    void addItem(T item) {
        super.addElement(item.name)
        items << item
    }

    @Override
    void addElement(Object item) {
        throw new UnsupportedOperationException("use addItem instead")
    }

    @Override
    void removeElement(Object item) {
        throw new UnsupportedOperationException("use removeItem instead")
    }

    void removeItem(T item) {
        removeElement(item.name)
        items.remove(item)
    }

    @Override
    void removeElementAt(int index) {
        items.remove(index)
        super.removeElementAt(index)
    }

    @Override
    void removeAllElements() {
        items.clear()
        super.removeAllElements()
    }

    @Override
    void setSelectedItem(Object object) {
        if (itemPreferenceKey && object) {
            userPreferences.putPropertyValue(itemPreferenceKey, "" + object)
        }
        super.setSelectedItem(object)
    }


}

class SimulationCheckBox extends ULCCheckBox {

    Simulation simulation

    public SimulationCheckBox(Simulation s) {
        super(s.name)
        simulation = s
    }

}

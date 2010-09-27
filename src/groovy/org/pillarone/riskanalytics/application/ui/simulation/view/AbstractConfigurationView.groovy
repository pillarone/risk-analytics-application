package org.pillarone.riskanalytics.application.ui.simulation.view

import com.ulcjava.base.application.datatype.IDataType
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.util.Dimension
import java.text.SimpleDateFormat
import org.pillarone.riskanalytics.application.ui.simulation.model.AbstractConfigurationModel
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationConfigurationListener
import org.pillarone.riskanalytics.application.ui.simulation.model.ISimulationListener
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.I18NUtils
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import com.ulcjava.base.application.*

abstract class AbstractConfigurationView implements ISimulationListener, ISimulationConfigurationListener {

    ULCBoxPane content
    ConfigurationSettingView configurationSettingView
    ActionsView actionsView

    ULCCheckBox useUserDefinedSeed
    ULCTextField randomSeed
    String selectedDirectory

    AbstractConfigurationModel model

    ULCAlert alert

    private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm")

    public AbstractConfigurationView(AbstractConfigurationModel model) {
        this.model = model
        configurationSettingView = new ConfigurationSettingView(model)
        actionsView = new ActionsView(model)
        initComponents()
        layoutComponents()
        attachListeners()
        model.notifySimulationConfigurationChanged()
    }

    /**
     * Creates all view components which are shared by all subclasses of AbstractConfigurationView.
     * This method also calls an abstract method which allows subclasses to create additional components.
     *
     * Once a simulation has been startet a PolllingTimer is used to regularly update the UI.
     */
    void initComponents() {
        content = new ULCBoxPane(1, 2)

        useUserDefinedSeed = new ULCCheckBox()
        randomSeed = new ULCTextField("")
        randomSeed.enabled = false
        IDataType dataType = DataTypeFactory.numberDataType
        dataType.integer = true
        randomSeed.dataType = dataType

        initCustomComponents()
    }

    abstract protected void initCustomComponents()

    protected static ULCBoxPane boxLayout(String title, Closure body) {
        ULCBoxPane result = new ULCBoxPane()
        result.border = BorderFactory.createTitledBorder(" $title ")
        ULCBoxPane inner = new ULCBoxPane()
        body(inner)
        result.add ULCBoxPane.BOX_EXPAND_EXPAND, spaceAround(inner, 0, 5, 5, 5)
        return result
    }

    protected static ULCBoxPane spaceAround(ULCComponent comp, int top, int left, int bottom, int right) {
        ULCBoxPane deco = new ULCBoxPane()
        deco.border = BorderFactory.createEmptyBorder(top, left, bottom, right)
        deco.add(ULCBoxPane.BOX_EXPAND_EXPAND, comp)
        return deco
    }

    /**
     * Layouts all view components which are shared by all subclasses of AbstractConfigurationView.
     * This method also calls an abstract method which allows subclasses to layout their additional components.
     */
    void layoutComponents() {
        ULCBoxPane parameterSection = boxLayout(UIUtils.getText(AbstractConfigurationView.class, "SimulationSettings") + ":") {ULCBoxPane box ->
            ULCBoxPane boxPanne = new ULCBoxPane(3, 7)
            configurationSettingView.addParameterSection(boxPanne)

            layoutCustomComponents(boxPanne)

            configurationSettingView.addPeriodSection(boxPanne)

            box.add ULCBoxPane.BOX_EXPAND_EXPAND, boxPanne
        }
        ULCBoxPane holder = new ULCBoxPane(columns: 1, rows: 3)
        holder.maximumSize = new Dimension(500, 600)

        holder.add(ULCBoxPane.BOX_EXPAND_EXPAND, parameterSection)
        holder.add(ULCBoxPane.BOX_EXPAND_EXPAND, spaceAround(actionsView.content, 5, 2, 5, 0))
        content.add(ULCBoxPane.BOX_LEFT_TOP, holder)
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
    }

    abstract protected void layoutCustomComponents(ULCBoxPane content)


    void attachListeners() {

        model.addSimulationListener this
        model.addSimulationConfigurationListener this


        addCustomListeners()
        configurationSettingView.attachListeners()
        useUserDefinedSeed.addValueChangedListener([valueChanged: {event -> model.useUserDefinedSeed = useUserDefinedSeed.selected}] as IValueChangedListener)
        randomSeed.addValueChangedListener([valueChanged: {event ->
            int seed = randomSeed.value
            if (seed != null && seed > 0) {
                model.randomSeed = seed
            } else {
                new I18NAlert("NoInteger").show()
                randomSeed.value = null
            }
        }] as IValueChangedListener)
        configurationSettingView.attachListeners()
    }

    abstract protected void addCustomListeners()

    public void simulationStart(Simulation simulation) {
        alert = null
        displaySimulationStart(simulation)
        actionsView.startTimer()
    }

    public void simulationEnd(Simulation simulation, Model model) {
    }

    private void displaySimulationStart(Simulation simulation) {
        actionsView.setRemainingTime simulation
        updateUIState()

    }

    public void simulationConfigurationChanged() {
        updateUIState()
    }

    protected def updateUIState() {
        configurationSettingView.updateUIState()
        actionsView.updateUIState()
        randomSeed.enabled = model.useUserDefinedSeed
        if (model.simulationException != null) {
            if (alert == null) {
                showMessage()
            }
        }

        updateCustomUIState()
    }

    private void showMessage() {
        String exceptionMessage = model.simulationException.message
        if (exceptionMessage == null) {
            exceptionMessage = model.simulationException.class.name
        }
        alert = new ULCAlert(UlcUtilities.getWindowAncestor(content), "Error occured during simulation", I18NUtils.getExceptionText(exceptionMessage), "Ok")
        alert.show()
    }

    abstract protected void updateCustomUIState()

    public void batchAdded(String message, boolean error) {
        actionsView.batchAdded(message, error)
    }

}

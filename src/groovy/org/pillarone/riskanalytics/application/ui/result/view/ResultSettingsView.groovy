package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.IFileChooseHandler
import com.ulcjava.base.application.util.IFileStoreHandler
import com.ulcjava.base.shared.FileChooserConfig
import org.joda.time.format.DateTimeFormat
import org.pillarone.riskanalytics.application.dataaccess.item.ModellingItemFactory
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.util.DateFormatUtils
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.ModelDAO
import org.pillarone.riskanalytics.core.components.ComponentUtils
import org.pillarone.riskanalytics.core.model.DeterministicModel
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.core.util.IConfigObjectWriter
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.core.simulation.item.*
import org.pillarone.riskanalytics.application.ui.main.view.RiskAnalyticsMainModel
import org.joda.time.DateTime

class ResultSettingsView {

    Simulation simulation
    ULCBoxPane content
    RiskAnalyticsMainModel mainModel

    public ResultSettingsView(Simulation simulation, RiskAnalyticsMainModel mainModel) {
        this.simulation = simulation;
        this.mainModel = mainModel
        initComponents()
    }

    private void initComponents() {

        ULCBoxPane settings = boxLayout(getText('settings')) {ULCBoxPane box ->

            ULCBoxPane content = new ULCBoxPane(3, 0)
            addLabels(content, getText('name') + ":", "$simulation.name", new ULCFiller())
            addLabels(content, getText('creationDate') + ":", DateFormatUtils.formatDetailed(simulation.start), new ULCFiller())
            // TODO (msp): adjust to new user concept
            addLabels(content, "", "", new ULCFiller())
            addLabels(content, getText('modelLabel') + ":", "$simulation.modelClass.simpleName v${simulation.modelVersionNumber.toString()}", new ULCButton(new ExportModelItemAction(simulation)))
            addLabels(content, getText('structure') + ":", "$simulation.structure.name v${simulation.structure.versionNumber.toString()}", new ULCButton(new ExportStructureAction(simulation.structure)))
            ULCButton openParams = new ULCButton(getText('open'))
            openParams.addActionListener([actionPerformed: {event -> openItem(simulation.parameterization) }] as IActionListener)
            String paramText = getText("parameterization") + ":"
            addLabels(content, paramText, "$simulation.parameterization.name v${simulation.parameterization.versionNumber.toString()}", openParams)
            ULCButton openTemplate = new ULCButton(getText('open'))
            openTemplate.addActionListener([actionPerformed: {event -> openItem(simulation.template) }] as IActionListener)
            addLabels(content, getText('template') + ":", "$simulation.template.name v${simulation.template.versionNumber.toString()}", openTemplate)
            if (!DeterministicModel.isAssignableFrom(simulation.modelClass)) {
                addLabels(content, getText('randomSeed') + ":", "$simulation.randomSeed", new ULCFiller())
            } else {
                addLabels(content, getText('firstPeriod') + ":", DateTimeFormat.forPattern('dd.MM.yyyy').print(simulation.beginOfFirstPeriod), new ULCFiller())
            }
            addLabels(content, getText('periods') + ":", simulation.periodCount.toString(), new ULCFiller())
            int simulationDuration = (simulation.end.getMillis() - simulation.start.getMillis()) / 1000
            addLabels(content, getText('completedIterations') + ":", "${simulation.numberOfIterations.toString()} in ${simulationDuration} secs", new ULCFiller())

            box.add(content)
        }

        ULCBoxPane runtimeParameters = boxLayout("Runtime parameters") { ULCBoxPane box ->
            ULCBoxPane content = new ULCBoxPane(2, 0)
            for (ParameterHolder parameter in simulation.runtimeParameters) {
                content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(ComponentUtils.getNormalizedName(parameter.path)))
                content.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel(formatParameter(parameter.businessObject)))
            }

            box.add(content)
        }

        boolean hasRuntimeParameters = !simulation.runtimeParameters.empty

        ULCBoxPane holder = new ULCBoxPane(hasRuntimeParameters ? 3 : 2, 1)
        holder.add(ULCBoxPane.BOX_EXPAND_TOP, settings)
        holder.add(ULCBoxPane.BOX_EXPAND_TOP, new ULCFiller())

        content = new ULCBoxPane()
        content.add(ULCBoxPane.BOX_EXPAND_TOP, holder)
        if (hasRuntimeParameters) {
            content.add(ULCBoxPane.BOX_EXPAND_TOP, runtimeParameters)
        }
        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())

    }

    private String formatParameter(def parameter) {
        return parameter.toString()
    }

    private String formatParameter(DateTime parameter) {
        return DateTimeFormat.forPattern("yyyy-MM-dd").print(parameter)
    }

    private void openItem(ModellingItem item) {
        if (item instanceof ConfigObjectBasedModellingItem && item.data == null) {
            item.load()
        }
        Model model = simulation.modelClass.newInstance()
        model.init()
        //todo fja open item
        // p1ratModel.openItem(model, item)
    }

    private void addLabels(ULCBoxPane container, String key, ULCTextArea value) {
        def keyLabel = new ULCLabel(key)
        container.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(keyLabel, 5, 10, 0, 0))
        ULCScrollPane scrollPane = new ULCScrollPane(value)
        scrollPane.setPreferredSize(new Dimension(270, 60))
        scrollPane.border = BorderFactory.createEmptyBorder(5, 10, 0, 0)
        scrollPane.setVerticalScrollBarPolicy(ULCScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED)
        container.add(2, ULCBoxPane.BOX_LEFT_EXPAND, scrollPane)
    }

    private void addLabels(ULCBoxPane container, String key, String value, ULCComponent thirdComponent) {
        def keyLabel = new ULCLabel(key)
        container.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(keyLabel, 5, 10, 0, 0))
        def valueLabel = new ULCLabel(value)
        container.add(ULCBoxPane.BOX_LEFT_CENTER, spaceAround(valueLabel, 5, 10, 0, 0))
        thirdComponent.preferredSize = new Dimension(100, 25)
        container.add(ULCBoxPane.BOX_LEFT_TOP, spaceAround(thirdComponent, 5, 10, 0, 0))
    }

    private ULCBoxPane spaceAround(ULCComponent comp, int top, int left, int bottom, int right) {
        ULCBoxPane deco = new ULCBoxPane()
        deco.border = BorderFactory.createEmptyBorder(top, left, bottom, right)
        deco.add comp
        return deco
    }

    private ULCBoxPane boxLayout(String title, Closure body) {
        ULCBoxPane result = new ULCBoxPane()
        result.border = BorderFactory.createTitledBorder(" $title ")
        ULCBoxPane inner = new ULCBoxPane()
        body(inner)
        result.add spaceAround(inner, 0, 5, 5, 5)
        result.add ULCBoxPane.BOX_EXPAND_CENTER, new ULCFiller()
        return result
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("ResultSettingsView." + key);
    }
}

class ExportModelItemAction extends ResourceBasedAction {
    Simulation simulation
    private ModelItem modelItemI

    public ExportModelItemAction(Simulation simulation) {
        super("ExportModelItem")
        this.@simulation = simulation
    }

    public void doActionPerformed(ActionEvent event) {
        if (!modelItem.loaded) {
            modelItem.load()
        }

        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = "Export for ${modelItem.name}"
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.FILES_ONLY
        config.selectedFile = "${modelItem.name}_v${simulation.modelVersionNumber.toString()}.groovy"

        ULCWindow ancestor = UlcUtilities.getWindowAncestor(event.source)
        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    String selectedFile = filePaths[0]
                    ClientContext.storeFile([prepareFile: {OutputStream stream ->
                        BufferedWriter bw
                        try {
                            bw = new BufferedWriter(new OutputStreamWriter(stream))
                            bw.write modelItem.srcCode
                        } catch (Throwable t) {
                            new ULCAlert(ancestor, "Export failed", t.message, "Ok").show()
                        } finally {
                            bw.close()
                        }
                    }, onSuccess: {path, name ->
                    }, onFailure: {reason, description ->
                        new ULCAlert(ancestor, "Export failed", description, "Ok").show()
                    }] as IFileStoreHandler, selectedFile)

                },
                onFailure: {reason, description ->
                }] as IFileChooseHandler, config, ancestor)
    }

    public ModelItem getModelItem() {
        if (!modelItemI) {
            modelItemI = ModellingItemFactory.getModelItem(ModelDAO.findByNameAndItemVersion(simulation.modelClass.simpleName, simulation.modelVersionNumber.toString()))
        }
        modelItemI
    }
}

class ExportStructureAction extends ResourceBasedAction {
    ModelStructure item

    public ExportStructureAction(ModelStructure structure) {
        super("ExportStructure")
        this.@item = structure
    }

    public void doActionPerformed(ActionEvent event) {
        if (!item.loaded) {
            item.load()
        }
        IConfigObjectWriter writer = item.getWriter()


        FileChooserConfig config = new FileChooserConfig()
        config.dialogTitle = "Export for ${item.name}"
        config.dialogType = FileChooserConfig.SAVE_DIALOG
        config.FILES_ONLY
        config.selectedFile = "${item.name}.groovy"

        ULCWindow ancestor = UlcUtilities.getWindowAncestor(event.source)
        ClientContext.chooseFile([
                onSuccess: {filePaths, fileNames ->
                    String selectedFile = filePaths[0]
                    ClientContext.storeFile([prepareFile: {OutputStream stream ->
                        try {
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(stream))
                            writer.write(item.data, bw)
                        } catch (Throwable t) {
                            new ULCAlert(ancestor, "Export failed", t.message, "Ok").show()
                        } finally {
                            stream.close()
                        }
                    }, onSuccess: {path, name ->
                    }, onFailure: {reason, description ->
                        new ULCAlert(ancestor, "Export failed", description, "Ok").show()
                    }] as IFileStoreHandler, selectedFile)

                },
                onFailure: {reason, description ->
                }] as IFileChooseHandler, config, ancestor)
    }


}
package org.pillarone.riskanalytics.application.ui.chart.view

import com.ulcjava.base.application.event.IActionListener
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.application.ui.chart.model.DistributionChartsViewModel
import org.pillarone.riskanalytics.application.ui.chart.view.ChartView
import com.ulcjava.base.application.*
import com.ulcjava.base.application.event.ActionEvent

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */



public class DistributionChartsView extends ChartView {
    ULCButton applyButton
    ULCButton settingsButton
    ULCBoxPane typeMethodChooseBox
    ULCComboBox typeComboBox
    ULCComboBox methodComboBox

    public DistributionChartsView(DistributionChartsViewModel model) {
        super(model)
    }

    protected void initComponents() {
        super.initComponents()
        typeMethodChooseBox = new ULCBoxPane(8, 0)
        applyButton = new ULCButton(getText("apply"))
        settingsButton = new ULCButton(getText("settings"))
        settingsButton.enabled = model.isSettingsEnabled()
        typeComboBox = new ULCComboBox(model.typeComboBoxModel)
        methodComboBox = new ULCComboBox(model.methodComboBoxModel)
    }

    void layoutComponents() {
        if (model.periodCount != 1 || model.seriesNames.size() != 1) {
            controlsPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, periodPane)
        } else {
            model.setSeriesVisibility(0, 0, true)
        }

        typeMethodChooseBox.border = BorderFactory.createTitledBorder(getText("distributionType") + ":")

        typeMethodChooseBox.add(ULCBoxPane.BOX_CENTER_CENTER, new ULCLabel(getText("type") + ":"))
        typeMethodChooseBox.add(ULCBoxPane.BOX_LEFT_CENTER, typeComboBox)
        typeMethodChooseBox.add(new ULCFiller(30, 1))
        typeMethodChooseBox.add(ULCBoxPane.BOX_CENTER_CENTER, new ULCLabel(getText("smoothing") + ":"))
        typeMethodChooseBox.add(ULCBoxPane.BOX_LEFT_CENTER, methodComboBox)
        typeMethodChooseBox.add(new ULCFiller(30, 1))
        typeMethodChooseBox.add(ULCBoxPane.BOX_CENTER_CENTER, settingsButton)
        typeMethodChooseBox.add(ULCBoxPane.BOX_CENTER_CENTER, applyButton)
        controlsPane.add(ULCBoxPane.BOX_EXPAND_BOTTOM, typeMethodChooseBox)

        content.add(ULCBoxPane.BOX_EXPAND_EXPAND, ulcChart)
        content.add(ULCBoxPane.BOX_EXPAND_BOTTOM, controlsPane)
    }

    public void update() {
        super.update()
        settingsButton.enabled = model.isSettingsEnabled()
    }

    void attachListeners() {
        super.attachListeners()
        applyButton.addActionListener([actionPerformed: {event -> model.changeStrategy(); update()}] as IActionListener)
        settingsButton.addActionListener([actionPerformed: {event ->
            model.showProperties(UlcUtilities.getWindowAncestor(content))
        }] as IActionListener)
        
        typeComboBox.addActionListener([actionPerformed: {ActionEvent evt ->
            model.changeStrategy()
            settingsButton.enabled = model.isSettingsEnabled()
        }] as IActionListener)
        methodComboBox.addActionListener([actionPerformed: {ActionEvent evt ->
            model.changeStrategy()
            settingsButton.enabled = model.isSettingsEnabled()
        }] as IActionListener)
    }

    /**
     * Utility method to get resource bundle entries for this class
     *
     * @param key
     * @return the localized value corresponding to the key
     */
    protected String getText(String key) {
        return LocaleResources.getString("DistributionChartsView." + key);
    }

}
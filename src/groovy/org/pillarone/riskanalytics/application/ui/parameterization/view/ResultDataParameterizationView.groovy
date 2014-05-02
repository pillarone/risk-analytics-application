package org.pillarone.riskanalytics.application.ui.parameterization.view

import com.canoo.ulc.application.layout.ULCMigLayoutPane
import com.ulcjava.base.application.DefaultComboBoxModel
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCComboBox
import com.ulcjava.base.application.ULCLabel
import com.ulcjava.base.application.ULCTextField
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.IKeyListener
import com.ulcjava.base.application.event.KeyEvent
import org.pillarone.riskanalytics.application.ui.parameterization.model.ResultDataParameterizationModel
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.output.CollectingModeFactory


class ResultDataParameterizationView {

    ResultDataParameterizationModel model

    ULCMigLayoutPane content

    ULCComboBox modelClass
    ULCComboBox parameterization

    ULCTextField path
    ULCTextField fields
    ULCTextField periods

    ULCComboBox collector

    ResultDataParameterizationView(ResultDataParameterizationModel model) {
        this.model = model
        initComponents()
    }

    void initComponents() {
        content = new ULCMigLayoutPane("wrap 2")

        modelClass = new ULCComboBox(model.modelClasses)
        modelClass.selectedItem = model.definition.parameterization.modelClass.name
        modelClass.addActionListener(new IActionListener() {
            @Override
            void actionPerformed(ActionEvent actionEvent) {
                model.setModelClass(modelClass.selectedItem)

                Collection<String> parameterizationsForModel = model.parameterizationsForModel
                parameterization.model = new DefaultComboBoxModel(parameterizationsForModel)
                if(!parameterizationsForModel.empty) {
                    String first = parameterizationsForModel.toList()[0]
                    parameterization.model.selectedItem = first
                    model.setParameterization(first)
                }
            }
        })

        parameterization = new ULCComboBox(model.parameterizationsForModel)
        parameterization.selectedItem = model.definition.parameterization.nameAndVersion
        parameterization.addActionListener(new IActionListener() {
            @Override
            void actionPerformed(ActionEvent actionEvent) {
                model.setParameterization(parameterization.selectedItem)
            }
        })

        path = new ULCTextField(model.path)
        path.addKeyListener(new IKeyListener() {
            @Override
            void keyTyped(KeyEvent keyEvent) {
                model.path = path.text
            }
        })

        fields = new ULCTextField(model.fields)
        fields.addKeyListener(new IKeyListener() {
            @Override
            void keyTyped(KeyEvent keyEvent) {
                model.fields = fields.text
            }
        })

        periods = new ULCTextField(model.periods)
        periods.addKeyListener(new IKeyListener() {
            @Override
            void keyTyped(KeyEvent keyEvent) {
                try {
                    model.periods = periods.text.trim().split(" ").collect { Integer.parseInt(it) }
                } catch (NumberFormatException e) {
                    new ULCAlert("Invalid number format!", "Invalid number format!", "Ok").show()
                }
            }
        })

        collector = new ULCComboBox(model.collectorStrategies)
        collector.selectedItem = CollectingModeFactory.getStrategy(model.definition.collectorName).getDisplayName(LocaleResources.locale)
        collector.addActionListener(new IActionListener() {
            @Override
            void actionPerformed(ActionEvent actionEvent) {
                model.setCollector(collector.selectedItem)
            }
        })

        content.add(new ULCLabel("Model class"))
        content.add(modelClass)


        content.add(new ULCLabel("Parameterization"))
        content.add(parameterization)


        content.add(new ULCLabel("Path"))
        content.add(path)


        content.add(new ULCLabel("Fields"))
        content.add(fields)


        content.add(new ULCLabel("Periods"))
        content.add(periods)


        content.add(new ULCLabel("Collector"))
        content.add(collector)
    }
}

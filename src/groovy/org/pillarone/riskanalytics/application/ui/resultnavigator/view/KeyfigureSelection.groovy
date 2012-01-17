package org.pillarone.riskanalytics.application.ui.resultnavigator.view

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IActionListener
import com.ulcjava.base.application.event.KeyEvent
import com.ulcjava.base.application.table.TableRowFilter
import com.ulcjava.base.application.util.Dimension
import com.ulcjava.base.application.util.KeyStroke
import com.ulcjava.base.shared.UlcEventConstants
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.OutputElementTableModel
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.FilterFactory
import org.pillarone.riskanalytics.application.ui.resultnavigator.util.ITableRowFilterListener
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.core.output.SimulationRun
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.StatisticsKeyfigure
import org.pillarone.riskanalytics.application.ui.resultnavigator.model.KeyfigureSelectionModel
import com.ulcjava.base.application.util.Color
import com.ulcjava.base.application.event.IKeyListener

/**
 * @author martin.melchior
 */
class KeyfigureSelection extends ULCBoxPane {

    private ULCComboBox keyfigureSelector
    private ULCTextField keyfigureParameterSelector
    private ULCComboBox periodSelector
    KeyfigureSelectionModel model

    KeyfigureSelection(SimulationRun run) {
        super(false, 2)
        model = new KeyfigureSelectionModel(run)
        createView()
    }

    void createView() {
        this.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel("Statistics Measure: "))
        keyfigureSelector = new ULCComboBox(model.getKeyfigureModel())
        this.add(ULCBoxPane.BOX_LEFT_CENTER, keyfigureSelector)
        keyfigureSelector.addActionListener(
                new IActionListener() {
                    public void actionPerformed(ActionEvent actionEvent) {
                        StatisticsKeyfigure enumValue = StatisticsKeyfigure.getEnumValue( (String) keyfigureSelector.selectedItem)
                        if (enumValue.needsParameters()) {
                            keyfigureParameterSelector.setEditable(true)
                            keyfigureParameterSelector.setBackground(Color.white)
                        } else {
                            keyfigureParameterSelector.setEditable(false)
                            keyfigureParameterSelector.setBackground(Color.lightGray)
                        }
                    }
                }
        )
        this.add(ULCBoxPane.BOX_LEFT_CENTER, ULCFiller.createHorizontalGlue())

        this.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel("Parameter: "))
        keyfigureParameterSelector = new ULCTextField(30)
        keyfigureParameterSelector.setEditable(false)
        keyfigureParameterSelector.setBackground(Color.lightGray)
        keyfigureParameterSelector.addKeyListener(new IKeyListener() {
            void keyTyped(KeyEvent keyEvent) {
                String value = keyfigureParameterSelector.text
                StatisticsKeyfigure enumValue = StatisticsKeyfigure.getEnumValue( (String) keyfigureSelector.selectedItem)
                if (enumValue.equals(StatisticsKeyfigure.ITERATION)) {
                    model.keyfigureParameter = Integer.parseInt(value)
                } else {
                    model.keyfigureParameter = Double.parseDouble(value)
                }

            }
        })
        this.add(ULCBoxPane.BOX_LEFT_CENTER, keyfigureParameterSelector)

        this.add(ULCBoxPane.BOX_LEFT_CENTER, ULCFiller.createHorizontalGlue())

        this.add(ULCBoxPane.BOX_LEFT_CENTER, new ULCLabel("Period: "))
        periodSelector = new ULCComboBox(model.getPeriodSelectionModel())
        this.add(ULCBoxPane.BOX_LEFT_CENTER, periodSelector)
    }
}

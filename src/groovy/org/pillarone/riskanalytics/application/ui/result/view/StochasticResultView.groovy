package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.datatype.IDataType
import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.model.EnumI18NComboBoxModel
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.result.model.ProfitFunction
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeColumn
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.util.DataTypeFactory
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.dataaccess.function.*
import org.pillarone.riskanalytics.application.ui.result.action.*

class StochasticResultView extends ResultView {

    private ULCPopupMenu menu
    private ULCToggleButton meanButton
    private ULCToggleButton minButton
    private ULCToggleButton maxButton
    private ULCToggleButton sigmaButton
    EnumI18NComboBoxModel profitFunctionModel

    private int nextModelIndex = 0

    public StochasticResultView(ResultViewModel model) {
        super(model)

    }

    void setModel(AbstractModellingModel model) {
        nextModelIndex = model.periodCount + 1
        super.setModel(model)
    }

    protected void initComponents() {
        super.initComponents();
        menu = new ULCPopupMenu()
        tree.viewPortTableTree.tableTreeHeader.componentPopupMenu = menu
        // add default function to menu
        def function = meanButton.action.function
        menu.add(new ULCMenuItem(new RemoveFunctionAction(model, function, getToggleButton(function))))
    }

    protected ULCContainer layoutContent(ULCContainer content) {
        tabbedPane.removeAll()
        ULCBoxPane contentPane = new ULCBoxPane(1, 2)
        ULCBoxPane functionPane = new ULCBoxPane(0, 1)

        contentPane.add(ULCBoxPane.BOX_LEFT_TOP, functionPane)
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, content)

        tabbedPane.addTab(getText("TreeView"), UIUtils.getIcon(getText("TreeView.icon")), contentPane)
        tabbedPane.addTab(getText("Settings"), UIUtils.getIcon(getText("Settings.icon")), getResultSettingView())
        tabbedPane.setCloseableTab(0, false)
        tabbedPane.setCloseableTab(1, false)
        return tabbedPane
    }

    protected ULCBoxPane getResultSettingView() {
        return new ResultSettingsView(model.item, p1ratModel).content
    }

    protected void addToolBarElements(ULCToolBar toolbar) {
        meanButton = new ULCToggleButton(new MeanAction(model, tree.viewPortTableTree))
        meanButton.setSelected true
        toolbar.add(meanButton)
        minButton = new ULCToggleButton(new MinAction(model, tree.viewPortTableTree))
        toolbar.add(minButton)
        maxButton = new ULCToggleButton(new MaxAction(model, tree.viewPortTableTree))
        toolbar.add(maxButton)
        sigmaButton = new ULCToggleButton(new SigmaAction(model, tree.viewPortTableTree))
        toolbar.add(sigmaButton)

        addDoubleFunctions(toolbar)

        toolbar.addSeparator()

        addIntegerFunctions(toolbar)

        addPrecisionFunctions(selectionToolbar)

    }

    private ULCComponent addIntegerFunctions(ULCToolBar toolbar) {
        ULCTextField integerFunctionValue = new ULCTextField()
        ULCNumberDataType integerDataType = new ULCNumberDataType(ClientContext.locale)
        integerDataType.classType = Integer
        integerFunctionValue.dataType = integerDataType
        integerFunctionValue.columns = 6
        integerFunctionValue.value = 1
        toolbar.add integerFunctionValue
        ULCButton button = new ULCButton(new SingleIterationAction(model, tree.viewPortTableTree, integerFunctionValue))
        toolbar.add UIUtils.spaceAround(button, 0, 5, 0, 5)
    }

    private def addDoubleFunctions(ULCToolBar toolbar) {
        toolbar.add ULCFiller.createHorizontalStrut(5)
        toolbar.addSeparator()
        toolbar.add ULCFiller.createHorizontalStrut(5)
        IDataType dataType = DataTypeFactory.numberDataType
        dataType.classType = Double
        dataType.minFractionDigits = 1
        dataType.maxFractionDigits = 2
        dataType.groupingUsed = false
        ULCTextField functionValue = new ULCTextField()
        functionValue.dataType = dataType
        functionValue.value = 99.5
        functionValue.columns = 6
        profitFunctionModel = new EnumI18NComboBoxModel(ProfitFunction.values() as Object[])
        ULCComboBox profitComboBox = new ULCComboBox(profitFunctionModel)
        profitComboBox.name = "profitComboBox"
        toolbar.add profitComboBox
        toolbar.add ULCFiller.createHorizontalStrut(3)
        toolbar.add functionValue
        toolbar.add ULCFiller.createHorizontalStrut(3)
        toolbar.add new ULCLabel(getText("Percent"))
        toolbar.add ULCFiller.createHorizontalStrut(5)
        ULCButton percentileButton = new ULCButton(new PercentileAction(model, tree.viewPortTableTree, functionValue, profitFunctionModel))
        ULCButton varButton = new ULCButton(new VarAction(model, tree.viewPortTableTree, functionValue, profitFunctionModel))
        ULCButton tvarButton = new ULCButton(new TvarAction(model, tree.viewPortTableTree, functionValue, profitFunctionModel))
        percentileButton.name = "percentileButton"
        varButton.name = "varButton"
        tvarButton.name = "tvarButton"
        toolbar.add percentileButton
        toolbar.add varButton
        toolbar.add tvarButton
    }

    public void functionAdded(IFunction function) {
        ResultViewModel model = this.model as ResultViewModel
        for (int i = 0; i < model.periodCount; i++) {
            ULCTableTreeColumn column = new ResultTableTreeColumn(nextModelIndex++, tree.viewPortTableTree, function)
            column.setMinWidth 110
            column.setHeaderRenderer(new CenteredHeaderRenderer())
            tree.viewPortTableTree.addColumn column
        }
        menu.add(new ULCMenuItem(new RemoveFunctionAction(model, function, getToggleButton(function))))
    }


    public void functionRemoved(IFunction function) {
        def columns = tree.viewPortTableTree.columnModel.columns.findAll {ResultTableTreeColumn col ->
            col.function == function
        }
        columns.each {ResultTableTreeColumn col ->
            tree.viewPortTableTree.removeColumn(col)
        }
        ULCMenuItem item = menu.components.find {ULCMenuItem it ->
            it.text == "Remove " + function.name
        }
        menu.remove(item)
    }

    protected void addColumns() {
        for (int i = 1; i < model.treeModel.columnCount; i++) {
            ULCTableTreeColumn column = new ResultTableTreeColumn(i, tree.viewPortTableTree)
            column.setMinWidth(110)
            column.setHeaderRenderer(new CenteredHeaderRenderer())
            tree.viewPortTableTree.addColumn column
        }
    }


    private ULCToggleButton getToggleButton(IFunction function) {
        null
    }

    private ULCToggleButton getToggleButton(Min function) {
        minButton
    }

    private ULCToggleButton getToggleButton(Max function) {
        maxButton
    }

    private ULCToggleButton getToggleButton(Sigma function) {
        sigmaButton
    }

    private ULCToggleButton getToggleButton(Mean function) {
        meanButton
    }

}



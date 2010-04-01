package org.pillarone.riskanalytics.application.ui.result.view

import com.ulcjava.base.application.datatype.IDataType
import com.ulcjava.base.application.datatype.ULCNumberDataType
import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.tabletree.ULCTableTreeColumn
import org.pillarone.riskanalytics.application.dataaccess.function.IFunction
import org.pillarone.riskanalytics.application.dataaccess.function.Max
import org.pillarone.riskanalytics.application.dataaccess.function.Min
import org.pillarone.riskanalytics.application.dataaccess.function.Sigma
import org.pillarone.riskanalytics.application.ui.parameterization.view.CenteredHeaderRenderer
import org.pillarone.riskanalytics.application.ui.result.model.ResultTableTreeColumn
import org.pillarone.riskanalytics.application.ui.result.model.ResultViewModel
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.ui.result.action.*

class StochasticResultView extends ResultView {

    private ULCPopupMenu menu
    private ULCToggleButton minButton
    private ULCToggleButton maxButton
    private ULCToggleButton sigmaButton

    public StochasticResultView(ResultViewModel model) {
        super(model)
    }

    protected void initComponents() {
        super.initComponents();
        menu = new ULCPopupMenu()
        tree.viewPortTableTree.tableTreeHeader.componentPopupMenu = menu
    }

    protected ULCContainer layoutContent(ULCContainer content) {
        tabbedPane.removeAll()
        ULCBoxPane contentPane = new ULCBoxPane(1, 2)
        ULCBoxPane functionPane = new ULCBoxPane(0, 1)

        contentPane.add(ULCBoxPane.BOX_LEFT_TOP, functionPane)
        contentPane.add(ULCBoxPane.BOX_EXPAND_EXPAND, content)

        tabbedPane.addTab(getText("TreeView"), UIUtils.getIcon(getText("TreeView.icon")), contentPane)
        tabbedPane.addTab(getText("Settings"), UIUtils.getIcon(getText("Settings.icon")), new ResultSettingsView(model.item, p1ratModel).content)
        tabbedPane.setCloseableTab(0, false)
        tabbedPane.setCloseableTab(1, false)
        return tabbedPane
    }

    protected void addToolBarElements(ULCToolBar toolbar) {
        minButton = new ULCToggleButton(new MinAction(model, tree.viewPortTableTree))
        toolbar.add(minButton)
        maxButton = new ULCToggleButton(new MaxAction(model, tree.viewPortTableTree))
        toolbar.add(maxButton)
        sigmaButton = new ULCToggleButton(new SigmaAction(model, tree.viewPortTableTree))
        toolbar.add(sigmaButton)

        toolbar.addSeparator()

        addDoubleFunctions(toolbar)

        toolbar.addSeparator()

        addIntegerFunctions(toolbar)

        toolbar.addSeparator()
        addPrecisionFunctions(toolbar)

    }

    private ULCComponent addIntegerFunctions(ULCToolBar toolbar) {
        ULCTextField integerFunctionValue = new ULCTextField()
        ULCNumberDataType integerDataType = new ULCNumberDataType(ClientContext.locale)
        integerDataType.integer = true
        integerDataType.min = 0
        integerDataType.max = model.item.simulationRun.iterations - 1
        integerFunctionValue.dataType = integerDataType
        integerFunctionValue.columns = 6
        integerFunctionValue.value = 1
        toolbar.add integerFunctionValue
        ULCButton button = new ULCButton(new SingleIterationAction(model, tree.viewPortTableTree, integerFunctionValue))
        toolbar.add UIUtils.spaceAround(button, 0, 5, 0, 5)
    }

    private def addDoubleFunctions(ULCToolBar toolbar) {
        toolbar.add(new ULCLabel(getText("Add")))
        toolbar.add ULCFiller.createHorizontalStrut(5)
        IDataType dataType = new ULCNumberDataType(ClientContext.locale)
        dataType.integer = false
        dataType.minFractionDigits = 1
        dataType.maxFractionDigits = 2
        dataType.groupingUsed = false
        ULCTextField functionValue = new ULCTextField()
        functionValue.dataType = dataType
        functionValue.value = 99.5
        functionValue.columns = 6
        toolbar.add functionValue
        toolbar.add ULCFiller.createHorizontalStrut(3)
        toolbar.add new ULCLabel(getText("Percent"))
        toolbar.add ULCFiller.createHorizontalStrut(5)
        toolbar.add new ULCButton(new PercentileAction(model, tree.viewPortTableTree, functionValue))
        toolbar.add new ULCButton(new VarAction(model, tree.viewPortTableTree, functionValue))
        toolbar.add new ULCButton(new TvarAction(model, tree.viewPortTableTree, functionValue))
    }



    public void functionAdded(IFunction function) {
        ResultViewModel model = this.model as ResultViewModel
        int newColumnIndex = tree.viewPortTableTree.columnCount + 1
        for (int i = 0; i < model.periodCount; i++) {
            ULCTableTreeColumn column = new ResultTableTreeColumn(newColumnIndex + i, tree.viewPortTableTree, function)
            column.setMinWidth 110
            column.setHeaderRenderer(new CenteredHeaderRenderer())
            tree.viewPortTableTree.addColumn column
        }
        menu.add(new ULCMenuItem(new RemoveFunctionAction(model, function, getToggleButton(function))))

        nodeChanged()
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

}

class RemoveFunctionAction extends AbstractAction {

    private ResultViewModel model
    private IFunction function
    private ULCToggleButton button

    public RemoveFunctionAction(ResultViewModel model, IFunction function, ULCToggleButton button) {
        super("Remove " + function.name);
        this.function = function
        this.model = model
        this.button = button
    }

    public void actionPerformed(ActionEvent event) {
        model.removeFunction(function)
        if (button != null) {
            button.selected = false
        }
    }

}

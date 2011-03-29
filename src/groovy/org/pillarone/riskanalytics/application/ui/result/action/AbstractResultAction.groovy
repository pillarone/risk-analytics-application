package org.pillarone.riskanalytics.application.ui.result.action

import com.ulcjava.base.application.event.ActionEvent
import com.ulcjava.base.application.event.IKeyListener
import com.ulcjava.base.application.event.IValueChangedListener
import com.ulcjava.base.application.event.ValueChangedEvent
import org.pillarone.riskanalytics.application.ui.base.action.ResourceBasedAction
import org.pillarone.riskanalytics.application.ui.base.model.AbstractModellingModel
import org.pillarone.riskanalytics.application.ui.base.view.AbstractModellingTreeView
import org.pillarone.riskanalytics.application.ui.util.I18NAlert
import org.pillarone.riskanalytics.application.ui.util.UIUtils
import com.ulcjava.base.application.*
import org.pillarone.riskanalytics.application.dataaccess.function.*
import org.pillarone.riskanalytics.application.ui.result.view.ResultView

abstract class AbstractResultAction extends ResourceBasedAction {

    AbstractModellingModel model
    ULCTableTree tree

    public AbstractResultAction(AbstractModellingModel model, ULCTableTree tree, String key) {
        super(key)
        this.model = model
        this.tree = tree
    }

    protected void addFunction(IFunction function) {
        model.addFunction(function)
    }

    protected void removeFunction(IFunction function) {
        model.removeFunction(function)
    }
}

abstract class CheckboxResultAction extends AbstractResultAction {

    IFunction function

    public CheckboxResultAction(AbstractModellingModel model, ULCTableTree tree, IFunction function) {
        super(model, tree, function.name)
        this.@function = function
    }

    public void doActionPerformed(ActionEvent event) {
        event.source.selected ? addFunction(function) : removeFunction(function)
    }

}

abstract class CheckboxAction implements IValueChangedListener {
    AbstractModellingModel model
    ULCTableTree tree
    IFunction function

    public CheckboxAction(AbstractModellingModel model, ULCTableTree tree, IFunction function) {
        this.@model = model
        this.@tree = tree
        this.@function = function
    }

    public void doActionPerformed(ActionEvent event) {
        event.source.selected ? addFunction(function) : removeFunction(function)
    }

    public void valueChanged(ValueChangedEvent valueChangedEvent) {
        ULCCheckBox source = (ULCCheckBox) valueChangedEvent.getSource();
        source.isSelected() ? addFunction(function) : removeFunction(function)
    }

    protected void addFunction(IFunction function) {
        model.addFunction(function)
    }

    protected void removeFunction(IFunction function) {
        model.removeFunction(function)
    }

    protected String getValue(String name) {
        return UIUtils.getText(this.class, name)
    }
}


class MeanAction extends CheckboxResultAction {

    public MeanAction(AbstractModellingModel model, ULCTableTree tree) {
        super(model, tree, new Mean());
        function.i18nName = getValue(IAction.NAME)
    }
}



class MinAction extends CheckboxResultAction {

    public MinAction(AbstractModellingModel model, ULCTableTree tree) {
        super(model, tree, new Min());
    }
}
class MaxAction extends CheckboxResultAction {

    public MaxAction(AbstractModellingModel model, ULCTableTree tree) {
        super(model, tree, new Max());
    }
}

class PercisionAction extends ResourceBasedAction {
    def model
    int adjustment

    public PercisionAction(model, int adjustment, String label) {
        super(label)
        this.model = model
        this.adjustment = adjustment
    }

    public void doActionPerformed(ActionEvent event) {
        model.adjust(adjustment)
    }
}

class SigmaAction extends CheckboxResultAction {

    public SigmaAction(AbstractModellingModel model, ULCTableTree tree) {
        super(model, tree, new Sigma());
        function.i18nName = getValue(IAction.NAME)
    }
}

abstract class TextFieldResultAction extends AbstractResultAction {

    ULCTextField valueField
    List openedValues = []

    public TextFieldResultAction(AbstractModellingModel model, ULCTableTree tree, ULCTextField valueField, String key) {
        super(model, tree, key)
        this.valueField = valueField
    }

    public void doActionPerformed(ActionEvent event) {

        double value = valueField.value
        if (value != null) {
            if (!model.isFunctionAdded(function(value)) || !openedValues.contains(value)) {
                addFunction(function(value))
                if (!openedValues.contains(value))
                    openedValues << value
            }
        } else {
            ULCAlert alert = new I18NAlert("InvalidNumberFormat")

            alert.show()
        }

    }

    abstract IFunction function(double value)
}

class PercentileAction extends TextFieldResultAction {
    List percentileColumns = []

    public PercentileAction(AbstractModellingModel model, ULCTableTree tree, ULCTextField valueField) {
        super(model, tree, valueField, "Percentile");
    }

    public IFunction function(double value) {
        new Percentile(i18nName: getValue(IAction.NAME), percentile: value)
    }
}

class VarAction extends TextFieldResultAction {

    public VarAction(AbstractModellingModel model, tree, valueField) {
        super(model, tree, valueField, "Var");
    }

    public IFunction function(double value) {
        new Var(value)
    }
}

class TvarAction extends TextFieldResultAction {

    public TvarAction(AbstractModellingModel model, tree, ULCTextField valueField) {
        super(model, tree, valueField, "Tvar");
        valueField.addKeyListener([keyTyped: {e -> setEnabled(validate()) }] as IKeyListener)
    }

    public IFunction function(double value) {
        new Tvar(value)
    }

    boolean validate() {
        try {
            double value = valueField.value
            return value >= 0 && value < 100
        } catch (NumberFormatException ex) {
            return false
        }
    }
}

class SingleIterationAction extends TextFieldResultAction {

    public SingleIterationAction(AbstractModellingModel model, tree, valueField) {
        super(model, tree, valueField, "SingleIteration");
    }

    public SingleIterationAction(model, tree, valueField, String key) {
        super(model, tree, valueField, key)
    }

    public IFunction function(double value) {
        new SingleIteration((int) value)
    }

    public void doActionPerformed(ActionEvent event) {
        int value = valueField.value
        int iterationCount = model.item.numberOfIterations
        if (value != null) {
            if (value > 0 && value <= iterationCount) {
                super.doActionPerformed(event)
            } else {
                ULCAlert alert = new I18NAlert("IterationNumberNotAvailable")
                alert.message = alert.message + iterationCount
                alert.show()
            }
        } else {
            super.doActionPerformed(event)
        }
    }
}

class DeviationPercentageAction extends CheckboxAction {

    public DeviationPercentageAction(model, ULCTableTree tree, compareSimulationTreeView) {
        super(model, tree, new DeviationPercentage());
        function.i18nName = getValue(function.name)
    }
}

class DeviationAbsoluteDifferenceAction extends CheckboxAction {

    public DeviationAbsoluteDifferenceAction(model, ULCTableTree tree, compareSimulationTreeView) {
        super(model, tree, new DeviationAbsoluteDifference());
        function.i18nName = getValue(function.name)
    }
}

class FractionPercentageAction extends CheckboxAction {

    public FractionPercentageAction(model, ULCTableTree tree, compareSimulationTreeView) {
        super(model, tree, new FractionPercentage());
        function.i18nName = getValue(function.name)
    }
}

class FractionAbsoluteDifferenceAction extends CheckboxAction {

    public FractionAbsoluteDifferenceAction(model, ULCTableTree tree, compareSimulationTreeView) {
        super(model, tree, new FractionAbsoluteDifference());
        function.i18nName = getValue(function.name)
    }
}

class ApplySelectionAction extends ResourceBasedAction {

    AbstractModellingModel model
    AbstractModellingTreeView modellingTreeView

    public ApplySelectionAction(AbstractModellingModel model, AbstractModellingTreeView view) {
        super("ApplySelectionAction")
        this.model = model
        this.modellingTreeView = view
    }

    public void doActionPerformed(ActionEvent event) {
        //remove the action listener because the view is re-initialized and the same action instance used as listener in the new combo box
        modellingTreeView.selectView.removeActionListener(this)

        model.resultStructureChanged()
        modellingTreeView.setModel(model)
        modellingTreeView.filterSelection.setVisible(modellingTreeView.selectView.getSelectedIndex() == 0)
        modellingTreeView.filterLabel.setVisible(modellingTreeView.selectView.getSelectedIndex() == 0)
    }
}


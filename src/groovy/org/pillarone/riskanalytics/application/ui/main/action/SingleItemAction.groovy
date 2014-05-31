package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.ULCTableTree
/**
 * @author fazl.rahman@art-allianz.com
 *
 * Introduce class to hold common behaviour of actions that
 * only operate on a single item.
 *
 */
abstract class SingleItemAction extends SelectionTreeAction {

    // Ugly constructor because TreeDoubleClickAction somehow calls OpenItemAction which somehow calls
    // e.g. CreateNewMajorVersion (our subclass) with a single string ctor.
    //
    SingleItemAction(String name, ULCTableTree tree = null) {
        super(name, tree)
    }

    // Current approach to enable menu only when one item is selected :-
    //
    // 1) register menuitem itself as a tree selection listener
    // 2) menuitem query this method on tree selection events
    //
    // The EnabledCheckingMenuItem encapsulates this behaviour.
    //
    @Override
    boolean isEnabled() {
        if (getAllSelectedObjectsSimpler().size() != 1) {
            return false
        }
        return super.isEnabled()//generic checks like user roles
    }

    protected boolean quitWithAlertIfCalledWhenDisabled(){
        if( !isEnabled() ){
            showWarnAlert(
                    "Single-item action $actionName invoked on selection",
                    "To help improve this please tell developers how you got here.\nA screenshot helps - Use Ctrl+PrtScn on Windows\nThanks for your help."
            )
            return true
        }
        return false
    }

}


package org.pillarone.riskanalytics.application.ui.main.action
import com.ulcjava.base.application.ULCAlert
import com.ulcjava.base.application.ULCTableTree
import com.ulcjava.base.application.UlcUtilities
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
            ULCAlert alert = new ULCAlert(
                    UlcUtilities.getWindowAncestor(tree),
                    "Action $actionName not allowed here", //title
                    "Please inform devops how you got this dialog!\\n(Screenshot=ALT+PRINT SCREEN", //msg
                    "Ok")
            alert.messageType = ULCAlert.INFORMATION_MESSAGE
            alert.show()
            return true
        }
        return false
    }

}


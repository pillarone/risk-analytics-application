package org.pillarone.riskanalytics.application.ui.comment.view

import com.ulcjava.base.application.AbstractAction
import com.ulcjava.base.application.ULCBoxPane
import com.ulcjava.base.application.ULCButton
import com.ulcjava.base.application.ULCFiller
import org.pillarone.riskanalytics.core.parameter.comment.workflow.IssueStatus
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.workflow.WorkflowComment

import static org.pillarone.riskanalytics.core.parameter.comment.workflow.IssueStatus.*
import org.apache.commons.lang.builder.HashCodeBuilder
import org.pillarone.riskanalytics.core.user.UserManagement
import org.pillarone.riskanalytics.core.simulation.item.parameter.comment.Comment
import org.pillarone.riskanalytics.application.ui.parameterization.model.ParameterViewModel
import com.ulcjava.base.application.ULCLabel
import org.pillarone.riskanalytics.application.ui.base.action.ExceptionSafeAction
import org.pillarone.riskanalytics.application.ui.comment.action.workflow.ReopenWorkflowCommentAction
import org.pillarone.riskanalytics.application.ui.comment.action.workflow.ResolveWorkflowCommentAction
import org.pillarone.riskanalytics.application.ui.comment.action.workflow.CloseWorkflowCommentAction
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.user.Authority

class WorkflowCommentPane extends CommentPane {

    private static final StatusIdentifier OPEN_USER = new StatusIdentifier(status: OPEN, role: UserManagement.USER_ROLE)
    private static final StatusIdentifier OPEN_REVIEWER = new StatusIdentifier(status: OPEN, role: UserManagement.REVIEWER_ROLE)
    private static final StatusIdentifier RESOLVED_USER = new StatusIdentifier(status: RESOLVED, role: UserManagement.USER_ROLE)
    private static final StatusIdentifier RESOLVED_REVIEWER = new StatusIdentifier(status: RESOLVED, role: UserManagement.REVIEWER_ROLE)
    private static final StatusIdentifier CLOSED_USER = new StatusIdentifier(status: CLOSED, role: UserManagement.USER_ROLE)
    private static final StatusIdentifier CLOSED_REVIEWER = new StatusIdentifier(status: CLOSED, role: UserManagement.REVIEWER_ROLE)

    protected ULCButton reOpenButton
    protected ULCButton resolveButton
    protected ULCButton closeButton

    protected ULCLabel resolvedLabel
    protected ULCLabel closedLabel

    private Map<StatusIdentifier, Closure> uiStates

    public WorkflowCommentPane(ParameterViewModel model, Comment comment, String searchText) {
        super(model, comment, searchText);
    }

    private void initMap() {
        uiStates = new HashMap()
        uiStates.put((OPEN_USER), {
            content.removeAll()
            content.add(ULCBoxPane.BOX_LEFT_TOP, new ULCFiller());
            ULCBoxPane buttons = new ULCBoxPane(1, 1)
            buttons.add(resolveButton)
            content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
            content.add(ULCBoxPane.BOX_RIGHT_TOP, buttons)
            content.add(3, ULCBoxPane.BOX_LEFT_TOP, label);
        })
        uiStates.put((OPEN_REVIEWER), {
            content.removeAll()
            content.add(ULCBoxPane.BOX_LEFT_TOP, new ULCFiller());
            ULCBoxPane buttons = new ULCBoxPane(2, 1)
            buttons.add(editButton)
            buttons.add(deleteButton)
            content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
            content.add(ULCBoxPane.BOX_RIGHT_TOP, buttons)
            content.add(3, ULCBoxPane.BOX_LEFT_TOP, label);
        })
        uiStates.put((RESOLVED_USER), {
            content.removeAll()
            content.add(ULCBoxPane.BOX_LEFT_TOP, new ULCFiller());
            ULCBoxPane buttons = new ULCBoxPane(1, 1)
            buttons.add(resolvedLabel)
            content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
            content.add(ULCBoxPane.BOX_RIGHT_TOP, buttons)
            content.add(3, ULCBoxPane.BOX_LEFT_TOP, label);
        })
        uiStates.put((RESOLVED_REVIEWER), {
            content.removeAll()
            content.add(ULCBoxPane.BOX_LEFT_TOP, new ULCFiller());
            ULCBoxPane buttons = new ULCBoxPane(2, 1)
            buttons.add(reOpenButton)
            buttons.add(closeButton)
            content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
            content.add(ULCBoxPane.BOX_RIGHT_TOP, buttons)
            content.add(3, ULCBoxPane.BOX_LEFT_TOP, label);
        })
        uiStates.put((CLOSED_REVIEWER), {
            content.removeAll()
            content.add(ULCBoxPane.BOX_LEFT_TOP, new ULCFiller());
            ULCBoxPane buttons = new ULCBoxPane(1, 1)
            buttons.add(closedLabel)
            content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
            content.add(ULCBoxPane.BOX_RIGHT_TOP, buttons)
            content.add(3, ULCBoxPane.BOX_LEFT_TOP, label);
        })
        uiStates.put((CLOSED_USER), {
            content.removeAll()
            content.add(ULCBoxPane.BOX_LEFT_TOP, new ULCFiller());
            ULCBoxPane buttons = new ULCBoxPane(1, 1)
            buttons.add(closedLabel)
            content.add(ULCBoxPane.BOX_EXPAND_EXPAND, new ULCFiller())
            content.add(ULCBoxPane.BOX_RIGHT_TOP, buttons)
            content.add(3, ULCBoxPane.BOX_LEFT_TOP, label);
        })
    }

    private Map getUIStates() {
        if (uiStates == null) {
            initMap()
        }
        return uiStates
    }

    protected void initComponents() {
        super.initComponents()

        reOpenButton = new ULCButton(new ReopenWorkflowCommentAction(this))

        resolveButton = new ULCButton(new ResolveWorkflowCommentAction(this))

        closeButton = new ULCButton(new CloseWorkflowCommentAction(this))

        resolvedLabel = new ULCLabel("Resolved")
        closedLabel = new ULCLabel("Closed")
    }

    protected void layoutComponents() {
        updateUI()
    }

    public void updateUI() {
        setEnabled(actionsEnabled())
        getUIStates().get(new StatusIdentifier(status: getStatus(), role: getUserRole())).call()
    }

    public void setEnabled(boolean enabled) {
        reOpenButton.enabled = enabled
        resolveButton.enabled = enabled
        closeButton.enabled = enabled
        editButton.enabled = enabled
        deleteButton.enabled = enabled
    }

    private String getUserRole() {
        List<Authority> roles = UserManagement.getCurrentUser()?.getAuthorities()?.toList()
        if (roles == null || roles.size() != 1) {
            return UserManagement.USER_ROLE
        }
        return roles[0].authority
    }

    private WorkflowComment getWorkflowComment() {
        return (WorkflowComment) comment
    }

    private IssueStatus getStatus() {
        getWorkflowComment().status
    }

    protected boolean actionsEnabled() {
        Parameterization parameterization = model.item as Parameterization
        return parameterization.status != org.pillarone.riskanalytics.core.workflow.Status.REJECTED
    }


}


class StatusIdentifier {

    IssueStatus status
    String role

    boolean equals(Object obj) {
        if (obj instanceof StatusIdentifier) {
            return status == obj.status && role == obj.role
        } else {
            return false
        }
    }

    int hashCode() {
        return new HashCodeBuilder().append(role).append(status.toString()).toHashCode()
    }


}
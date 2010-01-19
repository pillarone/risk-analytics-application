package org.pillarone.riskanalytics.application.user

import org.pillarone.riskanalytics.application.UserContext

public class UserManagement {

    public static boolean login(String user, String password) {
        ApplicationUser applicationUser = ApplicationUser.findByUsernameAndPassword(user, password)
        if (applicationUser == null) return false
        UserContext.setAttribute("CURRENT_USER", applicationUser.id)
        return true
    }

    public static ApplicationUser getCurrentUser() {
        return (ApplicationUser) ApplicationUser.get((long) UserContext.getAttribute("CURRENT_USER"))
    }
}

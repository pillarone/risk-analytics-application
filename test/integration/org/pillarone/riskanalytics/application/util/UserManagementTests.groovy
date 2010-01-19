package org.pillarone.riskanalytics.application.util

import org.pillarone.riskanalytics.application.user.ApplicationUser
import org.pillarone.riskanalytics.application.user.UserManagement

/**
 * @author: fouad.jaada (at) intuitive-collaboration (dot) com
 */

public class UserManagementTests extends GroovyTestCase{

    void testLogin(){
        assertFalse UserManagement.login("testUser2","123456")
        ApplicationUser user = new ApplicationUser()
        user.username = "testUser2"
        user.lastname = "last"
        user.firstname = "first"
        user.email = "email2@pillarone.com"
        user.password = "123456"
        user.save()
        assertTrue UserManagement.login("testUser2","123456")
    }

    void testGetCurrentUser(){
        testLogin()
        ApplicationUser user = UserManagement.getCurrentUser()
        assertNotNull user
        assertTrue user.username == "testUser2"
        assertTrue user.lastname == "last"
        assertTrue user.firstname == "first"
        assertTrue user.email ==  "email2@pillarone.com"
        assertTrue user.password == "123456"
        
    }

}
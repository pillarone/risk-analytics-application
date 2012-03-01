package riskanalyticsapplication

import org.apache.log4j.MDC
import org.pillarone.riskanalytics.application.UserContext

class RiskAnalyticsFilters {

    def filters = {
        all(controller: '*', action: '*') {
            before = {
                try {
                    MDC.put("username", UserContext.currentUser?.username)
                } catch (Exception e) {
                    //exception here should not crash the application
                }

            }
        }
    }

}

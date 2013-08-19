package org.pillarone.riskanalytics.application.ui.util

class I18NAlertTests extends GroovyTestCase{

    void testListFormatting(){
        assert 'testMessage 1 2' == I18NAlert.formatMessage('testMessage {0} {1}',['1','2'])
        assert 'testMessage' == I18NAlert.formatMessage('testMessage',null)

    }
}

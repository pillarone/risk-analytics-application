package models.application

import org.pillarone.riskanalytics.application.fileimport.ResultStructureImportService

model = ApplicationModel
displayName = "de: " + ResultStructureImportService.DEFAULT_NAME
language = "de"

mappings = [
        "Application:dynamicComponent:[%subcomponents%]:outSecondValue:value": "Application:dynamicComponent:[%subcomponents%]:outSecondValue:value",
        "Application:dynamicComponent:[%subcomponents%]:outFirstValue:value": "Application:dynamicComponent:[%subcomponents%]:outFirstValue:value",
        "Application:composedComponent:subDynamicComponent:[%subcomponents%]:outFirstValue:value": "Application:composedComponent:subDynamicComponent:[%subcomponents%]:outFirstValue:value",
        "Application:composedComponent:subDynamicComponent:outValue1:value": "Application:composedComponent:subDynamicComponent:outValue1:value",
        "Application:dynamicComponent:outValue1:value": "Application:dynamicComponent:outValue1:value",
        "Application:composedComponent:subDynamicComponent:[%subcomponents%]:outSecondValue:value": "Application:composedComponent:subDynamicComponent:[%subcomponents%]:outSecondValue:value"

]
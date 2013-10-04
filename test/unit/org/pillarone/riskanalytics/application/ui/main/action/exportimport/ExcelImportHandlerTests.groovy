package org.pillarone.riskanalytics.application.ui.main.action.exportimport

import models.application.ApplicationModel
import org.pillarone.riskanalytics.application.ui.main.action.exportimport.ExcelExportHandler
import org.pillarone.riskanalytics.application.ui.main.action.exportimport.ExcelImportHandler
import org.pillarone.riskanalytics.application.ui.main.action.exportimport.ImportResult
import org.pillarone.riskanalytics.application.util.LocaleResources
import org.pillarone.riskanalytics.core.example.parameter.ExampleEnum
import org.pillarone.riskanalytics.core.example.parameter.ExampleResourceConstraints
import org.pillarone.riskanalytics.core.model.Model
import org.pillarone.riskanalytics.core.parameterization.ConstraintsFactory
import org.pillarone.riskanalytics.core.parameterization.ParameterizationHelper
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.parameter.EnumParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.MultiDimensionalParameterHolder
import org.pillarone.riskanalytics.core.simulation.item.parameter.ParameterHolder
import org.pillarone.riskanalytics.domain.pc.cf.claim.ClaimTypeSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.discounting.YieldCurveTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.AnnualIndexTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.DeterministicIndexTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.FrequencyIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.LinkRatioIndexTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.PolicyIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.PremiumIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.ReservesIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.RunOffIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.indexing.SeverityIndexSelectionTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.legalentity.LegalEntityPortionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.pattern.PatternTableConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumSelectionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureAPConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureProfitCommissionConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.constraints.PremiumStructureReinstatementConstraints
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.contract.stateless.cover.ContractConstraint
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.CoverMap
import org.pillarone.riskanalytics.domain.pc.cf.reinsurance.cover.MatrixStructureContraints
import org.pillarone.riskanalytics.domain.utils.constraint.DateTimeConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.DoubleConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.IntDateTimeDoubleConstraints
import org.pillarone.riskanalytics.domain.utils.constraint.PerilPortion
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractBasedOn
import org.pillarone.riskanalytics.domain.utils.constraint.ReinsuranceContractContraints
import org.pillarone.riskanalytics.domain.utils.constraint.ReservePortion
import org.pillarone.riskanalytics.domain.utils.constraint.SegmentPortion
import org.pillarone.riskanalytics.domain.utils.constraint.UnderwritingPortion

class ExcelImportHandlerTests extends GroovyTestCase {
    File exportFile

    @Override
    protected void setUp() throws Exception {
        super.setUp()
        LocaleResources.setTestMode()
        ConstraintsFactory.registerConstraint(new ExampleResourceConstraints())
        ConstraintsFactory.registerConstraint(new AnnualIndexTableConstraints())
        ConstraintsFactory.registerConstraint(new LinkRatioIndexTableConstraints())
        ConstraintsFactory.registerConstraint(new DeterministicIndexTableConstraints())
        ConstraintsFactory.registerConstraint(new PolicyIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new PremiumIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new FrequencyIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new SeverityIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new RunOffIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new ReservesIndexSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new LegalEntityPortionConstraints())
        ConstraintsFactory.registerConstraint(new PatternTableConstraints())
        ConstraintsFactory.registerConstraint(new CoverMap())
        ConstraintsFactory.registerConstraint(new MatrixStructureContraints())
        ConstraintsFactory.registerConstraint(new DoubleConstraints())
        ConstraintsFactory.registerConstraint(new IntDateTimeDoubleConstraints())
        ConstraintsFactory.registerConstraint(new DateTimeConstraints())
        ConstraintsFactory.registerConstraint(new ClaimTypeSelectionTableConstraints())
        ConstraintsFactory.registerConstraint(new YieldCurveTableConstraints())
        ConstraintsFactory.registerConstraint(new SegmentPortion())
        ConstraintsFactory.registerConstraint(new ReinsuranceContractContraints())
        ConstraintsFactory.registerConstraint(new PremiumSelectionConstraints())
        ConstraintsFactory.registerConstraint(new ReinsuranceContractBasedOn())
        ConstraintsFactory.registerConstraint(new ContractConstraint())

        ConstraintsFactory.registerConstraint(new PremiumStructureReinstatementConstraints())
        ConstraintsFactory.registerConstraint(new PremiumStructureConstraints())
        ConstraintsFactory.registerConstraint(new PremiumStructureAPConstraints())
        ConstraintsFactory.registerConstraint(new PremiumStructureProfitCommissionConstraints())
        ConstraintsFactory.registerConstraint(new PerilPortion())
        ConstraintsFactory.registerConstraint(new ReservePortion())
        ConstraintsFactory.registerConstraint(new UnderwritingPortion())
        exportFile = File.createTempFile('excel', '.xlsx')
        exportFile.bytes = new ExcelExportHandler(new ApplicationModel()).exportModel()
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown()
        LocaleResources.clearTestMode()
    }

    void testValidation() {
        ExcelImportHandler handler = new ExcelImportHandler(exportFile)
        List<ImportResult> result = handler.validate()
        assert 0 == result.size()
    }

    void testParseExcel() {
        ExcelImportHandler handler = new ExcelImportHandler(new File('/home/detlef/develop/pillarone/risk-analytics-application/exportresult3-withData.xlsx'))
//        ExcelImportHandler handler = new ExcelImportHandler(new File('/home/detlef/temp/pmo-2449/exportresult.xlsx'))
        List result = handler.process()
        Model model = handler.modelInstance
        List<ParameterHolder> parameterHolders = ParameterizationHelper.extractParameterHoldersFromModel(model, 1)
        //ParameterHolder enumHolder = parameterHolders.find{it.path == 'parameterComponent:parmEnumParameter'}
        assert parameterHolders
        Parameterization parameterization = new Parameterization('detlef123', model.class)
        parameterization.parameterHolders = parameterHolders
        assert 27 == result.size()
        //assert enumHolder instanceof EnumParameterHolder
        //assert ExampleEnum.SECOND_VALUE == enumHolder.businessObject
    }
}

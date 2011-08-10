package org.pillarone.riskanalytics.application.ui.result.model

import models.core.CoreModel
import org.pillarone.riskanalytics.core.fileimport.ParameterizationImportService
import org.pillarone.riskanalytics.core.fileimport.ResultConfigurationImportService
import org.pillarone.riskanalytics.core.simulation.item.Parameterization
import org.pillarone.riskanalytics.core.simulation.item.ResultConfiguration
import org.pillarone.riskanalytics.core.simulation.item.Simulation
import org.pillarone.riskanalytics.core.simulation.item.VersionNumber
import org.pillarone.riskanalytics.application.dataaccess.function.*

class CompareTableTreeModelTests extends GroovyTestCase {


    Simulation simulation1
    Simulation simulation2
    Simulation simulation3
    Simulation simulation4

    void setUp() {
        new ParameterizationImportService().compareFilesAndWriteToDB(['CoreParameters'])
        new ResultConfigurationImportService().compareFilesAndWriteToDB(['CoreResultConfiguration'])
    }

    void testGetSimulationIndexOnePeriodTwoFunction() {
        simulation1 = createSimulation("simulation", 1)
        simulation2 = createSimulation("simulation2", 1)

        List simulations = [simulation1, simulation2]
        CompareResultTableTreeModel model = new CompareResultTableTreeModel(null, simulations, null, null)

        MeanFunction mean = new MeanFunction()
        MinFunction min = new MinFunction()
        DeviationPercentageFunction percentage = new DeviationPercentageFunction()

        model.addFunction(mean)
        model.addFunction(min)
        model.addCompareFunction(percentage)

        //Standard order by key figure:
        //Name | MeanS0 | MeanS1 | % | MinS0 | MinS1 | %

        assertEquals 0, model.getSimulationRunIndex(0)
        assertTrue model.getFunction(0) instanceof NodeNameFunction

        assertEquals 0, model.getSimulationRunIndex(1)
        assertEquals 1, model.getSimulationRunIndex(2)
        assertSame mean, model.getFunction(1)
        assertSame mean, model.getFunction(2)

        assertEquals(-1, model.getSimulationRunIndex(3))
        assertSame percentage, model.getFunction(3)

        assertEquals 0, model.getSimulationRunIndex(4)
        assertEquals 1, model.getSimulationRunIndex(5)
        assertSame min, model.getFunction(4)
        assertSame min, model.getFunction(5)

        assertEquals(-1, model.getSimulationRunIndex(6))
        assertSame percentage, model.getFunction(6)
    }

    void testGetSimulationIndexOnePeriodOneFunction() {

        simulation1 = createSimulation("simulation", 1)
        simulation2 = createSimulation("simulation2", 1)


        List simulations = [simulation1, simulation2]
        CompareResultTableTreeModel model = new CompareResultTableTreeModel(null, simulations, null, null)

        MeanFunction mean = new MeanFunction()

        DeviationPercentageFunction percentage = new DeviationPercentageFunction()

        model.addFunction(mean)
        model.addCompareFunction(percentage)

        //Standard order by key figure:
        //Name | MeanS0 | MeanS1 | %

        assertEquals 0, model.getSimulationRunIndex(0)
        assertEquals 0, model.getPeriodIndex(0)
        assertTrue model.getFunction(0) instanceof NodeNameFunction

        assertEquals 0, model.getSimulationRunIndex(1)
        assertEquals 1, model.getSimulationRunIndex(2)
        assertEquals 0, model.getPeriodIndex(1)
        assertEquals 0, model.getPeriodIndex(2)
        assertSame mean, model.getFunction(1)
        assertSame mean, model.getFunction(2)

        assertEquals(-1, model.getSimulationRunIndex(3))
        assertEquals 0, model.getPeriodIndex(3)
        assertSame percentage, model.getFunction(3)

    }

    void testGetSimulationIndexTwoPeriodOneFunction() {
        simulation1 = createSimulation("simulation", 2)
        simulation2 = createSimulation("simulation2", 2)

        List simulations = [simulation1, simulation2]
        CompareResultTableTreeModel model = new CompareResultTableTreeModel(null, simulations, null, null)

        model.addFunction(new MeanFunction())
        model.addCompareFunction(new DeviationPercentageFunction())

        //Standard order by key figure:
        //Name | MeanS0P0 | MeanS1P0 | % | MeanS0P1 | MeanS1P1 | %

        assertEquals 0, model.getSimulationRunIndex(0)
        assertEquals 0, model.getPeriodIndex(0)

        assertEquals 0, model.getSimulationRunIndex(1)
        assertEquals 1, model.getSimulationRunIndex(2)
        assertEquals 0, model.getPeriodIndex(1)
        assertEquals 0, model.getPeriodIndex(2)

        assertEquals(-1, model.getSimulationRunIndex(3))
        assertEquals 0, model.getPeriodIndex(3)

        assertEquals 0, model.getSimulationRunIndex(4)
        assertEquals 1, model.getSimulationRunIndex(5)
        assertEquals 1, model.getPeriodIndex(4)
        assertEquals 1, model.getPeriodIndex(5)

        assertEquals(-1, model.getSimulationRunIndex(6))
        assertEquals 1, model.getPeriodIndex(6)

    }

    void testGetSimulationIndexTwoPeriodTwoFunction() {
        simulation1 = createSimulation("simulation", 2)
        simulation2 = createSimulation("simulation2", 2)

        List simulations = [simulation1, simulation2]
        CompareResultTableTreeModel model = new CompareResultTableTreeModel(null, simulations, null, null)

        MeanFunction mean = new MeanFunction()
        MinFunction min = new MinFunction()
        DeviationPercentageFunction percentage = new DeviationPercentageFunction()

        model.addFunction(mean)
        model.addFunction(min)
        model.addCompareFunction(percentage)

        //Standard order by key figure:
        //Name | MeanS0P0 | MeanS1P0 | % | MeanS0P1 | MeanS1P1 | % | MinS0P0 | MinS1P0 | % | MinS0P1 | MinS1P1 | %

        assertEquals 0, model.getSimulationRunIndex(0)

        assertEquals 0, model.getSimulationRunIndex(1)
        assertEquals 1, model.getSimulationRunIndex(2)

        assertEquals(-1, model.getSimulationRunIndex(3))

        assertEquals 0, model.getSimulationRunIndex(4)
        assertEquals 1, model.getSimulationRunIndex(5)

        assertEquals(-1, model.getSimulationRunIndex(6))

        assertEquals 0, model.getSimulationRunIndex(7)
        assertEquals 1, model.getSimulationRunIndex(8)

        assertEquals(-1, model.getSimulationRunIndex(9))

        assertEquals 0, model.getSimulationRunIndex(10)
        assertEquals 1, model.getSimulationRunIndex(11)

        assertEquals(-1, model.getSimulationRunIndex(12))

        model.orderByKeyfigure = false

        //Standard order by key figure:
        //Name | MeanS0P0 | MeanS1P0 | % |  MinS0P0 | MinS1P0 | % | MeanS0P1 | MeanS1P1 | % | MinS0P1 | MinS1P1 | %

        assertEquals 0, model.getSimulationRunIndex(0)

        assertEquals 0, model.getSimulationRunIndex(1)
        assertEquals 1, model.getSimulationRunIndex(2)

        assertEquals(-1, model.getSimulationRunIndex(3))

        assertEquals 0, model.getSimulationRunIndex(4)
        assertEquals 1, model.getSimulationRunIndex(5)

        assertEquals(-1, model.getSimulationRunIndex(6))

        assertEquals 0, model.getSimulationRunIndex(7)
        assertEquals 1, model.getSimulationRunIndex(8)

        assertEquals(-1, model.getSimulationRunIndex(9))

        assertEquals 0, model.getSimulationRunIndex(10)
        assertEquals 1, model.getSimulationRunIndex(11)

        assertEquals(-1, model.getSimulationRunIndex(12))

        assertEquals 0, model.getPeriodIndex(1)
        assertEquals 0, model.getPeriodIndex(2)
        assertEquals 0, model.getPeriodIndex(3)

        assertEquals 0, model.getPeriodIndex(4)
        assertEquals 0, model.getPeriodIndex(5)
        assertEquals 0, model.getPeriodIndex(6)

        assertEquals 1, model.getPeriodIndex(7)
        assertEquals 1, model.getPeriodIndex(8)
        assertEquals 1, model.getPeriodIndex(9)

        assertEquals 1, model.getPeriodIndex(10)
        assertEquals 1, model.getPeriodIndex(11)
        assertEquals 1, model.getPeriodIndex(12)

        assertSame mean, model.getFunction(1)
        assertSame mean, model.getFunction(2)
        assertSame percentage, model.getFunction(3)

        assertSame min, model.getFunction(4)
        assertSame min, model.getFunction(5)
        assertSame percentage, model.getFunction(6)

        assertSame mean, model.getFunction(7)
        assertSame mean, model.getFunction(8)
        assertSame percentage, model.getFunction(9)

        assertSame min, model.getFunction(10)
        assertSame min, model.getFunction(11)
        assertSame percentage, model.getFunction(12)
    }

    void testGetSimulationIndexTwoPeriodTwoFunctionTwoCompareFunction() {
        simulation1 = createSimulation("simulation", 2)
        simulation2 = createSimulation("simulation2", 2)

        List simulations = [simulation1, simulation2]
        CompareResultTableTreeModel model = new CompareResultTableTreeModel(null, simulations, null, null)

        MeanFunction mean = new MeanFunction()
        MinFunction min = new MinFunction()
        DeviationPercentageFunction percentage = new DeviationPercentageFunction()
        DeviationAbsoluteFunction difference = new DeviationAbsoluteFunction()

        model.addFunction(mean)
        model.addFunction(min)
        model.addCompareFunction(percentage)
        model.addCompareFunction(difference)

        //Standard order by key figure:
        //Name | MeanS0P0 | MeanS1P0 | % | AD | MeanS0P1 | MeanS1P1 | % | AD | MinS0P0 | MinS1P0 | % | AD | MinS0P1 | MinS1P1 | % | AD

        assertEquals 0, model.getSimulationRunIndex(0)
        assertTrue model.getFunction(0) instanceof NodeNameFunction

        assertEquals 0, model.getSimulationRunIndex(1)
        assertEquals 1, model.getSimulationRunIndex(2)
        assertSame mean, model.getFunction(1)
        assertSame mean, model.getFunction(2)

        assertEquals(-1, model.getSimulationRunIndex(3))
        assertEquals(-1, model.getSimulationRunIndex(4))
        assertSame percentage, model.getFunction(3)
        assertSame difference, model.getFunction(4)

        assertEquals 0, model.getSimulationRunIndex(5)
        assertEquals 1, model.getSimulationRunIndex(6)

        assertEquals(-1, model.getSimulationRunIndex(7))
        assertEquals(-1, model.getSimulationRunIndex(8))

        assertEquals 0, model.getSimulationRunIndex(9)
        assertEquals 1, model.getSimulationRunIndex(10)

        assertEquals(-1, model.getSimulationRunIndex(11))
        assertEquals(-1, model.getSimulationRunIndex(12))

        assertEquals 0, model.getSimulationRunIndex(13)
        assertEquals 1, model.getSimulationRunIndex(14)
        assertSame min, model.getFunction(13)
        assertSame min, model.getFunction(14)

        assertEquals(-1, model.getSimulationRunIndex(15))
        assertEquals(-1, model.getSimulationRunIndex(16))
        assertSame percentage, model.getFunction(15)
        assertSame difference, model.getFunction(16)
    }

    void testGetSimulationIndexTwoCompareFunctionTreeSimulationsbyPeriod() {
        simulation1 = createSimulation("simulation", 2)
        simulation2 = createSimulation("simulation2", 2)
        simulation3 = createSimulation("simulation3", 2)

        List simulations = [simulation1, simulation2, simulation3]
        CompareResultTableTreeModel model = new CompareResultTableTreeModel(null, simulations, null, null)

        MeanFunction mean = new MeanFunction()
        MinFunction min = new MinFunction()
        DeviationPercentageFunction percentage = new DeviationPercentageFunction()
        DeviationAbsoluteFunction difference = new DeviationAbsoluteFunction()

        model.addFunction(mean)
        model.addFunction(min)
        model.addCompareFunction(percentage)
        model.addCompareFunction(difference)
        model.orderByKeyfigure = false

        //Standard order by period:
        //0:name| 1: MeanS0P0 | 2: MeanS1P0 | 3: MeanS2P0 | 4: Percentage:runB=S1:S-1P0 | 5: Percentage:runB=S2:S-1P0 | 6: AbsoluteDifference:runB=S1:S-1P0 | 7: AbsoluteDifference:runB=S2:S-1P0 |
        // 8: MinS0P0 | 9: MinS1P0 | 10: MinS2P0 | 11: Percentage:runB=S1:S-1P0 | 12: Percentage:runB=S2:S-1P0 | 13: AbsoluteDifference:runB=S1:S-1P0 | 14: AbsoluteDifference:runB=S2:S-1P0 |
        // 15: MeanS0P1 | 16: MeanS1P1 | 17: MeanS2P1 | 18: Percentage:runB=S1:S-1P1 | 19: Percentage:runB=S2:S-1P1 | 20: AbsoluteDifference:runB=S1:S-1P1 | 21: AbsoluteDifference:runB=S2:S-1P1 |
        // 22: MinS0P1 | 23: MinS1P1 | 24: MinS2P1 | 25: Percentage:runB=S1:S-1P1 | 26: Percentage:runB=S2:S-1P1 | 27: AbsoluteDifference:runB=S1:S-1P1 | 28: AbsoluteDifference:runB=S2:S-1P1 |


        assertEquals 0, model.getSimulationRunIndex(0)
        assertTrue model.getFunction(0) instanceof NodeNameFunction

        assertEquals 0, model.getSimulationRunIndex(1)
        assertEquals 1, model.getSimulationRunIndex(2)
        assertEquals 2, model.getSimulationRunIndex(3)
        assertEquals(-1, model.getSimulationRunIndex(4))
        assertEquals(-1, model.getSimulationRunIndex(5))
        assertEquals(-1, model.getSimulationRunIndex(6))
        assertEquals(-1, model.getSimulationRunIndex(7))

        assertEquals 0, model.getSimulationRunIndex(22)
        assertEquals 1, model.getSimulationRunIndex(23)
        assertEquals 2, model.getSimulationRunIndex(24)
        assertEquals(-1, model.getSimulationRunIndex(25))
        assertEquals(-1, model.getSimulationRunIndex(26))
        assertEquals(-1, model.getSimulationRunIndex(27))


    }

    void testGetSimulationIndexTwoCompareFunctionTreeSimulationsByKeyFigure() {
        simulation1 = createSimulation("simulation", 2)
        simulation2 = createSimulation("simulation2", 2)
        simulation3 = createSimulation("simulation3", 3)

        List simulations = [simulation1, simulation2, simulation3]
        CompareResultTableTreeModel model = new CompareResultTableTreeModel(null, simulations, null, null)

        MeanFunction mean = new MeanFunction()
        MinFunction min = new MinFunction()
        DeviationPercentageFunction percentage = new DeviationPercentageFunction()
        DeviationAbsoluteFunction difference = new DeviationAbsoluteFunction()

        model.addFunction(mean)
        model.addFunction(min)
        model.addCompareFunction(percentage)
        model.addCompareFunction(difference)

        //Standard order by key figure:
        //1: MeanS0P0 | 2: MeanS1P0 | 3: MeanS2P0 | 4: Percentage:runB=S1:S-1P0 | 5: Percentage:runB=S2:S-1P0 | 6: AbsoluteDifference:runB=S1:S-1P0 | 7: AbsoluteDifference:runB=S2:S-1P0 |
        // 8: MeanS0P1 | 9: MeanS1P1 | 10: MeanS2P1 | 11: Percentage:runB=S1:S-1P1 | 12: Percentage:runB=S2:S-1P1 | 13: AbsoluteDifference:runB=S1:S-1P1 | 14: AbsoluteDifference:runB=S2:S-1P1 |
        // 15: MinS0P0 | 16: MinS1P0 | 17: MinS2P0 | 18: Percentage:runB=S1:S-1P0 | 19: Percentage:runB=S2:S-1P0 | 20: AbsoluteDifference:runB=S1:S-1P0 | 21: AbsoluteDifference:runB=S2:S-1P0 |
        // 22: MinS0P1 | 23: MinS1P1 | 24: MinS2P1 | 25: Percentage:runB=S1:S-1P1 | 26: Percentage:runB=S2:S-1P1 | 27: AbsoluteDifference:runB=S1:S-1P1 | 28: AbsoluteDifference:runB=S2:S-1P1 |

        assertEquals 0, model.getSimulationRunIndex(1)
        assertEquals 1, model.getSimulationRunIndex(2)
        assertEquals 2, model.getSimulationRunIndex(3)
        assertEquals(-1, model.getSimulationRunIndex(4))
        assertEquals(-1, model.getSimulationRunIndex(5))
        assertEquals(-1, model.getSimulationRunIndex(6))
        assertEquals(-1, model.getSimulationRunIndex(7))

        assertEquals 0, model.getSimulationRunIndex(22)
        assertEquals 1, model.getSimulationRunIndex(23)
        assertEquals 2, model.getSimulationRunIndex(24)
        assertEquals(-1, model.getSimulationRunIndex(25))
        assertEquals(-1, model.getSimulationRunIndex(26))
        assertEquals(-1, model.getSimulationRunIndex(27))
        assertEquals(-1, model.getSimulationRunIndex(28))


    }


    void testGetSimulationIndexFourSimulationsbyKeyFigure() {
        simulation1 = createSimulation("simulation", 2)
        simulation2 = createSimulation("simulation2", 2)
        simulation3 = createSimulation("simulation3", 3)
        simulation4 = createSimulation("simulation4", 2)

        List simulations = [simulation1, simulation2, simulation3, simulation4]
        CompareResultTableTreeModel model = new CompareResultTableTreeModel(null, simulations, null, null)

        MeanFunction mean = new MeanFunction()
        MinFunction min = new MinFunction()
        model.addFunction(mean)
        model.addFunction(min)
        //1: MeanS0P0 | 2: MeanS1P0 | 3: MeanS2P0 | 4: MeanS3P0 | 5: MeanS0P1 | 6: MeanS1P1 | 7: MeanS2P1 | 8: MeanS3P1 |
        // 9: MinS0P0 | 10: MinS1P0 | 11: MinS2P0 | 12: MinS3P0 | 13: MinS0P1 | 14: MinS1P1 | 15: MinS2P1 | 16: MinS3P1 |


        assertEquals 0, model.getSimulationRunIndex(1)
        assertEquals 1, model.getSimulationRunIndex(2)
        assertEquals 2, model.getSimulationRunIndex(3)
        assertEquals 3, model.getSimulationRunIndex(4)
        assertEquals 0, model.getSimulationRunIndex(13)
        assertEquals 1, model.getSimulationRunIndex(14)
        assertEquals 2, model.getSimulationRunIndex(15)
        assertEquals 3, model.getSimulationRunIndex(16)
    }

    void testGetSimulationIndexFourSimulationsbyPeriod() {
        simulation1 = createSimulation("simulation", 2)
        simulation2 = createSimulation("simulation2", 2)
        simulation3 = createSimulation("simulation3", 3)
        simulation4 = createSimulation("simulation4", 2)

        List simulations = [simulation1, simulation2, simulation3, simulation4]

        CompareResultTableTreeModel model = new CompareResultTableTreeModel(null, simulations, null, null)
        model.orderByKeyfigure = false
        MeanFunction mean = new MeanFunction()
        MinFunction min = new MinFunction()
        model.addFunction(mean)
        model.addFunction(min)

        //1: MeanS0P0 | 2: MeanS1P0 | 3: MeanS2P0 | 4: MeanS3P0 | 5: MinS0P0 | 6: MinS1P0 | 7: MinS2P0 | 8: MinS3P0 |
        // 9: MeanS0P1 | 10: MeanS1P1 | 11: MeanS2P1 | 12: MeanS3P1 | 13: MinS0P1 | 14: MinS1P1 | 15: MinS2P1 | 16: MinS3P1 |

        assertEquals 0, model.getSimulationRunIndex(1)
        assertEquals 1, model.getSimulationRunIndex(2)
        assertEquals 2, model.getSimulationRunIndex(3)
        assertEquals 3, model.getSimulationRunIndex(4)
        assertEquals 0, model.getSimulationRunIndex(13)
        assertEquals 1, model.getSimulationRunIndex(14)
        assertEquals 2, model.getSimulationRunIndex(15)
        assertEquals 3, model.getSimulationRunIndex(16)


    }

    private Simulation createSimulation(String name, int period) {
        Simulation simulation = new Simulation(name)
        simulation.modelClass = CoreModel
        simulation.modelVersionNumber = new VersionNumber("1")
        simulation.periodCount = period
        simulation.numberOfIterations = 100
        simulation.randomSeed = 10
        Parameterization parameterization = new Parameterization("CoreParameters", CoreModel)
        parameterization.load()
        simulation.parameterization = parameterization
        ResultConfiguration resultConfiguration = new ResultConfiguration("CoreResultConfiguration")
        resultConfiguration.modelClass = CoreModel
        resultConfiguration.load()
        simulation.template = resultConfiguration
        simulation.save()
        return simulation
    }


}
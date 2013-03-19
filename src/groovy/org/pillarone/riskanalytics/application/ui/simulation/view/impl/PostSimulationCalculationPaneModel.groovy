package org.pillarone.riskanalytics.application.ui.simulation.view.impl

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.pillarone.riskanalytics.core.output.PostSimulationCalculation


class PostSimulationCalculationPaneModel {

    //default = nothing selected
    boolean standardDeviation = false
    List<Double> percentileLoss = []
    List<Double> percentileProfit = []
    List<Double> varLoss = []
    List<Double> varProfit = []
    List<Double> tvarLoss = []
    List<Double> tvarProfit = []
    List<Double> pdf = []

    PostSimulationCalculationPaneModel() {
        Map keyFigures = ApplicationHolder.application.config.keyFiguresToCalculate
        def stDev = keyFigures.get(PostSimulationCalculation.STDEV)
        if (stDev instanceof Boolean) {
            standardDeviation = stDev
        }
        def lossPercentile = keyFigures.get(PostSimulationCalculation.PERCENTILE)
        if (lossPercentile instanceof Collection) {
            percentileLoss.addAll(lossPercentile)
        }
        def profitPercentile = keyFigures.get(PostSimulationCalculation.PERCENTILE_PROFIT)
        if (profitPercentile instanceof Collection) {
            percentileProfit.addAll(profitPercentile)
        }
        def lossVar = keyFigures.get(PostSimulationCalculation.VAR)
        if (lossVar instanceof Collection) {
            varLoss.addAll(lossVar)
        }
        def profitVar = keyFigures.get(PostSimulationCalculation.VAR_PROFIT)
        if (profitVar instanceof Collection) {
            varProfit.addAll(profitVar)
        }
        def lossTvar = keyFigures.get(PostSimulationCalculation.TVAR)
        if (lossTvar instanceof Collection) {
            tvarLoss.addAll(lossTvar)
        }
        def profitTvar = keyFigures.get(PostSimulationCalculation.TVAR_PROFIT)
        if (profitTvar instanceof Collection) {
            tvarProfit.addAll(profitTvar)
        }
        def pdf = keyFigures.get(PostSimulationCalculation.PDF)
        if (pdf instanceof Collection) {
            this.pdf.addAll(pdf)
        }

        //values in config may be big decimal or integer
        percentileLoss = percentileLoss.collect { it.toDouble() }
        percentileProfit = percentileProfit.collect { it.toDouble() }
        varLoss = varLoss.collect { it.toDouble() }
        varProfit = varProfit.collect { it.toDouble() }
        tvarLoss = tvarLoss.collect { it.toDouble() }
        tvarProfit = tvarProfit.collect { it.toDouble() }
        this.pdf = this.pdf.collect { it.toDouble() }
    }

    Map getKeyFigureMap() {
        Map result = [:]
        result[PostSimulationCalculation.STDEV] = standardDeviation
        result[PostSimulationCalculation.PERCENTILE] = new ArrayList(percentileLoss)
        result[PostSimulationCalculation.PERCENTILE_PROFIT] = new ArrayList(percentileProfit)
        result[PostSimulationCalculation.VAR] = new ArrayList(varLoss)
        result[PostSimulationCalculation.VAR_PROFIT] = new ArrayList(varProfit)
        result[PostSimulationCalculation.TVAR] = new ArrayList(tvarLoss)
        result[PostSimulationCalculation.TVAR_PROFIT] = new ArrayList(tvarProfit)
        result[PostSimulationCalculation.PDF] = new ArrayList(pdf)
        return result
    }
}

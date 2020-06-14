package com.backwards.retirement

import scala.annotation.tailrec

object RetirementCalculator {
  def futureCapital(returns: Returns, nbOfMonths: Int, netIncome: Int, currentExpenses: Int, initialCapital: Double): Double = {
    val monthlySavings = netIncome - currentExpenses

    def nextCapital(accumulated: Double, month: Int): Double =
      accumulated * (1 + Returns.monthlyRate(returns, month)) + monthlySavings

    (0 until nbOfMonths).foldLeft(initialCapital)(nextCapital)
  }

  def simulatePlan(returns: Returns, params: RetCalcParams, nbOfMonthsSavings: Int) : (Double, Double) = {
    import params._

    val capitalAtRetirement = futureCapital(returns, nbOfMonthsSavings, netIncome, currentExpenses, initialCapital)

    val capitalAfterDeath = futureCapital(OffsetReturns(returns, nbOfMonthsSavings), nbOfMonthsInRetirement, netIncome = 0, currentExpenses, initialCapital = capitalAtRetirement)

    (capitalAtRetirement, capitalAfterDeath)
  }

  def nbOfMonthsSaving(params: RetCalcParams, returns: Returns): Int = {
    import params._

    @tailrec
    def loop(months: Int): Int = {
      val (capitalAtRetirement, capitalAfterDeath) = simulatePlan(returns, params, months)

      if (capitalAfterDeath > 0.0)
        months
      else
        loop(months + 1)
    }

    if (netIncome > currentExpenses)
      loop(0)
    else
      Int.MaxValue
  }
}
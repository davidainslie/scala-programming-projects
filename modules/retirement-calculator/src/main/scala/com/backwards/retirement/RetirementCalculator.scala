package com.backwards.retirement

import scala.annotation.tailrec
import com.backwards.retirement.RetCalcError.MoreExpensesThanIncome

object RetirementCalculator {
  def futureCapital(returns: Returns, nbOfMonths: Int, netIncome: Int, currentExpenses: Int, initialCapital: Double): RetCalcError Either Double = {
    val monthlySavings = netIncome - currentExpenses

    (0 until nbOfMonths).foldLeft[RetCalcError Either Double](Right(initialCapital)) {
      case (accumulated, month) =>
        for {
          acc <- accumulated
          monthlyRate <- Returns.monthlyRate(returns, month)
        } yield acc * (1 + monthlyRate) + monthlySavings
    }
  }

  def simulatePlan(returns: Returns, params: RetCalcParams, nbOfMonthsSavings: Int, monthOffset: Int = 0): RetCalcError Either (Double, Double) = {
    import params._

    for {
      capitalAtRetirement <- futureCapital(
        returns = OffsetReturns(returns, monthOffset),
        nbOfMonths = nbOfMonthsSavings, netIncome = netIncome,
        currentExpenses = currentExpenses,
        initialCapital = initialCapital
      )
      capitalAfterDeath <- futureCapital(
        returns = OffsetReturns(returns, monthOffset + nbOfMonthsSavings),
        nbOfMonths = nbOfMonthsInRetirement,
        netIncome = 0, currentExpenses = currentExpenses,
        initialCapital = capitalAtRetirement
      )
    } yield (capitalAtRetirement, capitalAfterDeath)
  }

  def nbOfMonthsSaving(params: RetCalcParams, returns: Returns): RetCalcError Either Int = {
    import params._

    @tailrec
    def loop(months: Int): RetCalcError Either Int = {
      simulatePlan(returns, params, months) match {
        case Right((capitalAtRetirement, capitalAfterDeath)) =>
          if (capitalAfterDeath > 0.0) Right(months)
          else loop(months + 1)

        case Left(err) => Left(err)
      }
    }

    if (netIncome > currentExpenses)
      loop(0)
    else
      Left(MoreExpensesThanIncome(netIncome, currentExpenses))
  }
}
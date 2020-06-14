package com.backwards.retirement

import scala.annotation.tailrec

object RetirementCalculator {
  def futureCapital(interestRate: Double, nbOfMonths: Int, netIncome: Int, currentExpenses: Int, initialCapital: Double): Double = {
    val monthlySavings = netIncome - currentExpenses

    def nextCapital(accumulated: Double, month: Int): Double =
      accumulated * (1 + interestRate) + monthlySavings

    (0 until nbOfMonths).foldLeft(initialCapital)(nextCapital)
  }

  def simulatePlan(interestRate: Double, nbOfMonthsSavings: Int, nbOfMonthsInRetirement: Int, netIncome: Int, currentExpenses: Int, initialCapital: Double) : (Double, Double) = {
    val capitalAtRetirement = futureCapital(interestRate, nbOfMonthsSavings, netIncome, currentExpenses, initialCapital)

    val capitalAfterDeath = futureCapital(interestRate, nbOfMonthsInRetirement, netIncome = 0, currentExpenses, initialCapital = capitalAtRetirement)

    (capitalAtRetirement, capitalAfterDeath)
  }

  def nbOfMonthsSaving(interestRate: Double, nbOfMonthsInRetirement: Int, netIncome: Int, currentExpenses: Int, initialCapital: Double): Int = {
    @tailrec
    def loop(months: Int): Int = {
      val (capitalAtRetirement, capitalAfterDeath) = simulatePlan(interestRate, nbOfMonthsSavings = months, nbOfMonthsInRetirement, netIncome, currentExpenses, initialCapital)

      if (capitalAfterDeath > 0.0)
        months
      else
        loop(months + 1)
    }

    // TODO - Following is rubbish but will be resolved in another chapter of the book
    if (netIncome > currentExpenses)
      loop(0)
    else
      Int.MaxValue
  }
}
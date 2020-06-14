package com.backwards.retirement

import org.scalactic.{Equality, TolerantNumerics, TypeCheckedTripleEquals}
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

// TypeCheckedTripleEquals provides a powerful assertion, should ===, that ensures at compile time that both sides of the equality have the same type.
class RetirementCalculatorSpec extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {
  implicit val doubleEquality: Equality[Double] =
    TolerantNumerics.tolerantDoubleEquality(0.0001)

  "Retirement Calculator futureCapital" should {
    "calculate the amount of savings I will have in n months" in {
      val actual = RetirementCalculator.futureCapital(
        interestRate = 0.04 / 12, nbOfMonths = 25 * 12,
        netIncome = 3000, currentExpenses = 2000,
        initialCapital = 10000
      )

      val expected = 541267.1990
      actual must ===(expected)
    }

    "calculate how much savings will be left after having taken a pension for n months" in {
      val actual = RetirementCalculator.futureCapital(
        interestRate = 0.04/12, nbOfMonths = 40 * 12,
        netIncome = 0, currentExpenses = 2000,
        initialCapital = 541267.1990
      )

      val expected = 309867.53176
      actual must ===(expected)
    }
  }

  "Retirement Calculator simulatePlan" should {
    "calculate the capital at retirement and the capital after death" in {
      val (capitalAtRetirement, capitalAfterDeath) =
        RetirementCalculator.simulatePlan(
          interestRate = 0.04 / 12,
          nbOfMonthsSavings = 25 * 12, nbOfMonthsInRetirement = 40 * 12,
          netIncome = 3000, currentExpenses = 2000,
          initialCapital = 10000
        )

      capitalAtRetirement must === (541267.1990)
      capitalAfterDeath must === (309867.5316)
    }
  }

  "Retirement Calculator nbOfMonthsSaving" should {
    "calculate how long I need to save before I can retire" in {
      val actual = RetirementCalculator.nbOfMonthsSaving(
        interestRate = 0.04 / 12, nbOfMonthsInRetirement = 40 * 12,
        netIncome = 3000, currentExpenses = 2000, initialCapital = 10000
      )

      val expected = 23 * 12 + 1
      actual must ===(expected)
    }

    "not crash if the resulting nbOfMonths is very high" in {
      val actual = RetirementCalculator.nbOfMonthsSaving(
        interestRate = 0.01 / 12, nbOfMonthsInRetirement = 40 * 12,
        netIncome = 3000, currentExpenses = 2999, initialCapital = 0
      )

      val expected = 8280
      actual must ===(expected)
    }

    "not loop forever if I enter bad parameters" in {
      val actual = RetirementCalculator.nbOfMonthsSaving(
        interestRate = 0.04 / 12, nbOfMonthsInRetirement = 40 * 12,
        netIncome = 1000, currentExpenses = 2000, initialCapital = 10000
      )

      actual must === (Int.MaxValue)
    }
  }
}
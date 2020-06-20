package com.backwards.retirement

/**
 * java -jar /Users/davidainslie/workspace/scala-programming-projects/modules/retirement-calculator/target/scala-2.13/retirement-calculator-assembly-1.0.29-SNAPSHOT.jar 1997.09,2017.09 25 40 3000 2000 10000
 */
object SimulatePlanApp extends App {
  println(strMain(args))

  def strMain(args: Array[String]): String = {
    val from +: until +: Nil = args(0).split(",").toList
    val nbOfYearsSaving = args(1).toInt
    val nbOfYearsInRetirement = args(2).toInt

    val allReturns = Returns.fromEquityAndInflationData(
      equities = EquityData.fromResource("sp500.tsv"),
      inflations = InflationData.fromResource("cpi.tsv")
    )

    val (capitalAtRetirement, capitalAfterDeath) =
      RetirementCalculator.simulatePlan(
        returns = allReturns.fromUntil(from, until),
        params = RetCalcParams(
          nbOfMonthsInRetirement = nbOfYearsInRetirement * 12,
          netIncome = args(3).toInt,
          currentExpenses = args(4).toInt,
          initialCapital = args(5).toInt),
        nbOfMonthsSavings = nbOfYearsSaving * 12
      )

    s"""
       |Capital after $nbOfYearsSaving years of savings:    ${capitalAtRetirement.round}
       |Capital after $nbOfYearsInRetirement years in retirement: ${capitalAfterDeath.round}
        """.stripMargin
  }
}
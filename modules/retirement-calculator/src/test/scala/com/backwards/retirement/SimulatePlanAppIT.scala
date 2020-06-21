package com.backwards.retirement

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest.matchers.must.Matchers
import org.scalatest.wordspec.AnyWordSpec

class SimulatePlanAppIT extends AnyWordSpec with Matchers with TypeCheckedTripleEquals {
  "SimulatePlanApp.strMain" should {
    "simulate a retirement plan using market returns" in {
      val actualResult = SimulatePlanApp.strMain(Array("1952.09,2017.09", "25", "40", "3000", "2000", "10000"))

      val expectedResult =
        s"""
           |Capital after 25 years of savings:    468925
           |Capital after 40 years in retirement: 2958842
        """.stripMargin

      actualResult must === (expectedResult)
    }

    "return an error when the period exceeds the returns bounds" in {
      val actualResult = SimulatePlanApp.strMain(Array("1952.09,2017.09", "25", "60", "3000", "2000", "10000"))

      val expectedResult = "Cannot get the return for month 780. Accepted range: 0 to 779"

      actualResult must === (expectedResult)
    }
  }
}
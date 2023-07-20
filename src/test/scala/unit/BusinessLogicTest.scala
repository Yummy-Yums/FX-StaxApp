package org.fx.application
package unit

import org.fx.application.domain.Bank._
import org.fx.application.domain.BusinessLogic._
import org.fx.application.domain.Event._

import utest._

import java.time.LocalDate
import scala.collection.mutable.ArrayBuffer


object BusinessLogicTest extends TestSuite {
//  HashMap(amount -> 23, name -> Elias, currency -> USD, expiry -> 2023-07-04)
  val date = LocalDate.of(2023, 7, 4)

  val expectedResponseForBuildEvent: (PurchaseRequest, ArrayBuffer[(Double, String)]) = (PurchaseRequest(Trader(235,"Elias",2,"USD"),Currency("USD"), date, 23.0), ArrayBuffer((10.9,"Kel6 Exchange-32"), (10.8,"Kel23 Exchange"), (10.7,"Teiman-an Exchange"), (10.6,"Oyarifa Exchange"), (10.5,"Kax Exchange"), (10.3,"Scom Exchange")))

  val tests = Tests{

    test("Build Event Functionality Tests"){
      val mapTest = collection.mutable.Map(
        "name" -> "Elias",
        "currency" -> "USD",
        "amount" -> "23",
        "expiry" -> "2023-07-04"
      )

      val response = buildEvent(mapTest)
      println(response)

//      assert(response == expectedResponseForBuildEvent)

    }

    test(""){

    }
  }
}

package org.fx.application
package unit

import org.fx.application.util.FxUtil._

import utest._

import scala.collection.mutable.ArrayBuffer

object FxUtilTest extends TestSuite {

  val tests = Tests{

    test("Cedi To FX "){
      val expectedDollars = 1000
      val expectedPounds = 1200

      val responseDollars = cediToFX("dollars", 100)
      val responsePounds = cediToFX("pounds", 100)

      assert(
        responsePounds == expectedPounds,
        responseDollars == expectedDollars
      )

    }

    test("Comma Formatter"){
      val expected: String = "200,000,000.00"
      val response: String = commaFormatter(20000000000L)

      assert(expected == response)
    }

    test("Get Best Rate From Providers"){

      val expectedResponseSuccess = (10.9,ArrayBuffer((10.9,"Kel6 Exchange-32"), (10.8,"Kel23 Exchange"), (10.7,"Teiman-an Exchange"), (10.6,"Oyarifa Exchange"), (10.5,"Kax Exchange"), (10.3,"Scom Exchange")))

      import org.fx.application.domain.Provider.providers
      val responseSuccess = getBestRateFromProviders(providers, "USD/CEDI", 2400)
      val responseFail = getBestRateFromProviders(providers, "USD/CEDI", 2034555)

     val checkResponse = responseFail._2.forall{p =>
       p._2.contains("but volume is low")
     }
      assert(responseSuccess == expectedResponseSuccess)
      assert(checkResponse)
    }
  }

}

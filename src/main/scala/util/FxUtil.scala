package org.fx.application
package util

import org.fx.application.domain.Event.RequestStatus
import org.fx.application.domain.Provider
import scalatags.Text.Frag

import scala.collection.mutable.ArrayBuffer

object FxUtil {

  import scala.util.Random

    val accountNumberGen = new Random(100)

  //  accountNumberGen.nextInt()

  val g : (Random) => Int = (accountNumberGen) => accountNumberGen.nextInt.abs

  val rate: Map[String, Int] = Map("pounds" -> 12, "dollars" -> 10)

  val converter: (Int, Int) => Int = _ * _

  def cediToFX(currency: String, amount: Int): Int = currency match {
    case _ if rate.contains(currency) => converter(rate(currency), amount)
  }

  // status List, might have to move it
  def statusList(f: (RequestStatus => RequestStatus), x: RequestStatus): Frag = {
    f(x).toFrag()
  }

  val justReturnArequest: RequestStatus => RequestStatus = (r: RequestStatus) => r

  def getBestRateFromProviders(providers: List[Provider], rateCurrencies: String, amountNumber: Int): (Double, ArrayBuffer[(Double, String)]) = {

    var initialRate = 0.0
    var exchangeName = ""
    var volume: Long = 0L
    var response = ""

    var listOfBestProviders = ArrayBuffer[(Double, String)]()

    for (p <- providers) {

      val matchingRate: Option[Double] =
        p.rates
          .find(_._1 == rateCurrencies)
          .map(_._2)

      matchingRate.foreach { rate =>
        if (rate > initialRate) {

          initialRate = rate
          exchangeName = p.name
          volume = p.volume

          val checkIfVolumeIsNotSufficient = amountNumber * initialRate > volume
          if (checkIfVolumeIsNotSufficient) {
            response += s"initialRate of ${p.name} is good but volume is low \n"
          }

          listOfBestProviders = (rate, p.name) +: listOfBestProviders
        }
      }
    }

    // check for next best volume


//    println("exchange Name " + exchangeName)

    if (response.isEmpty) {
      (initialRate, listOfBestProviders)
    } else (initialRate, ArrayBuffer((0.0, response)))

  }

  def transactWithProvider(provider: Provider) = {

  }

  def commaFormatter(number: Long): String = {

    import java.text.DecimalFormat

    val formatter = new DecimalFormat("#,##0.00")
    val formattedAmount = formatter.format(number / 100.0)
    formattedAmount

  }

  def courier(queryAction: String) = {

    /*
     strings containing checks
     "   "   "      updates
     */
    queryAction match {
      case "check for Trader" => ""
    }
  }

}

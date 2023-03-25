package org.fx.application

import org.fx.application.Event.{PurchaseRequest, RequestStatus}
import scalatags.Text.Frag
import scalatags.Text.all.frag

import scala.collection.mutable

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

  def getBestRateFromProviders(providers: List[Provider], rateCurrencies: String, amountNumber: Int): (Double, String) = {

    var rate = 0.0
    var exchangeName = ""
    var volume : Long = 0
    var response = ""

    for (p <- providers) {
      val matchingRate = p.rates.find(_._1 == rateCurrencies).map(_._2)
      matchingRate.foreach { r =>
        if (r > rate) {
          rate = r
          exchangeName = p.name
          volume = p.volume

          if (amountNumber * rate > volume) {
            response += s"rate of ${p.name} is good but volume is not much\n"
          }
        }
      }
    }

    // check for next best volume

    if(response != ""){
      return  (rate, response)
    } else return (rate, exchangeName)

  }

  def transactWithProvider(provider: Provider) = {

  }

  def commaFormatter(number: Long) = {

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

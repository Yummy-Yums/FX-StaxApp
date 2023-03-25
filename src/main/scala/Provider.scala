package org.fx.application

import Bank.Currency
import FxUtil.commaFormatter

import scalatags.Text.Frag
import scalatags.Text.all._

case class Provider(name: String, currency: Currency, volume: Long, rates: Array[(String, Double)]){

  def toFrag(): Frag = frag(

    tr(
      td(name + ""),
      td(currency.curr.map {curr => curr + ", "}),
      td(commaFormatter(volume)),
      td(rates.map { case (currencyPair, rate) => li(s"$currencyPair: $rate")})
    )
  )
}

object Provider {

  // random thread to randomize and update offers, maybe

  /*
   provides available offers
   */
  val providers = List(
    Provider("Ucom Exchange", Currency("USD"), 20_1000, Array(("USD/CEDI", 10.3))),
    Provider("Kax Exchange", Currency("USD", "POUNDS"), 20_1000, Array(("USD/CEDI", 10.3), ("POUNDS/CEDI", 10.3))),
    Provider("Mon Exchange", Currency("USD", "EUROS"), 20_1000, Array(("USD/CEDI", 10.3), ("USD/EUROS", 10.3))),
    Provider("Kel Exchange", Currency("USD", "YEN"), 30_1000, Array(("USD/CEDI", 10.3), ("USD/EUROS", 10.3))),
  )

  val offersList = frag(
    for (provider <- providers) yield provider.toFrag()
  )


}

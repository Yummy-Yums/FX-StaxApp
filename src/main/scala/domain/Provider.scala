package org.fx.application
package domain

import domain.Bank.Currency
import util.FxUtil.commaFormatter

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

  def toFragDropdown(): Frag = frag(
    option(value := name, name),
  )
}

object Provider {

  // random thread to randomize and update offers, maybe

  /*
   provides available offers
   */
  val providers: List[Provider] = List(
    Provider("Scom Exchange", Currency("USD"), 10_1000, Array(("USD/CEDI", 10.3))),
    Provider("Kax Exchange", Currency("USD", "POUNDS"), 20_1000, Array(("USD/CEDI", 10.5), ("POUNDS/CEDI", 10.3))),
    Provider("Oyarifa Exchange", Currency("USD", "EUROS"), 20_1000, Array(("USD/CEDI", 10.6), ("USD/EUROS", 10.3))),
    Provider("Teiman-an Exchange", Currency("USD", "YEN"), 30_1000, Array(("USD/CEDI", 10.7), ("USD/EUROS", 10.3))),
    Provider("Kel23 Exchange", Currency("USD", "YEN"), 30_1000, Array(("USD/CEDI", 10.8), ("USD/EUROS", 10.3))),
    Provider("Kel6 Exchange-32", Currency("USD", "YEN"), 30_1000, Array(("USD/CEDI", 10.9), ("USD/EUROS", 10.3))),
    Provider("I'm trying to do", Currency("USD", "YEN"), 30_1000, Array(("USD/CEDI", 10.1), ("USD/EUROS", 10.3))),
  )

  val offersList = frag(
    for (provider <- providers) yield provider.toFrag()
  )

  val providersList = frag(
    for (provider <- providers) yield provider.toFragDropdown()
  )

}
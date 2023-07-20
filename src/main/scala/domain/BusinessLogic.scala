package org.fx.application
package domain

import DatabaseCxn._
import domain.Bank.{Account, Currency}
import domain.Event.{PurchaseRequest, Requests}
import util.FxUtil._
import util.ServerUtil._

import scala.collection.mutable.ArrayBuffer

object BusinessLogic {

  // check if name is in db and return the Trader, if not create account for it

  // take request and create an event for it to update the Requests section

  def buildEvent(args: collection.mutable.Map[String, String]): (PurchaseRequest, ArrayBuffer[(Double, String)]) = {

    val trader = args match {
      case _ if args("name").nonEmpty => getTrader(args("name"))
    }
    /*
      what happens if a trader doesn't exist
     */

    val pr: PurchaseRequest = PurchaseRequest(trader.head, Currency(args("currency")), datetimeFormatter(args("expiry")), args("amount").toDouble)

    // check whether Provider can honour requests.

    val currString = args("currency") + "/" + "CEDI"

    import org.fx.application.domain.Provider.providers
    val receivedRate: (Double, ArrayBuffer[(Double, String)]) = getBestRateFromProviders(providers, currString, args("amount").toInt)

    val checkForListOfProviders =
      if (receivedRate._2.length > 1){
        println("list of best providers")
      } else {
        println(" Volume too low")
      }

    // return list of best providers from highest to lowest for each currency
    // volume by rates = total,
    (pr, receivedRate._2)
  }

  def makeTransactionToDB(): Unit = {

    // create a Provider table to DB
    //first have to create the table for Providers
    // deduct from their volume
    // should be an atomic transaction

  }

  def complete(): Unit = {

    val e: Requests = Requests.retrieveRequests.headOption.get

    val balance = Account.balance - e.amount

//    val volume =

//    Array(balance, )

  }

}

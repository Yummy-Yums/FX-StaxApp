package org.fx.application

import DatabaseCxn._
import ServerUtil._
import FxUtil._

import org.fx.application.Bank.Currency
import org.fx.application.Event.PurchaseRequest

object BusinessLogic {

  // check if name is in db and return the Trader, if not create account for it

  // take request and create an event for it to update the Requests section

  def buildEvent(vars: collection.mutable.Map[String, String]) = {

    val exists = vars match {
      case _ if vars("name").nonEmpty => dbCheckAndGet(vars("name"))
    }

    println(exists)

    val pr = PurchaseRequest(exists.head, Currency(vars("currency")),  datetimeFormatter(vars("expiry")), vars("amount").toDouble)

    // check whether Provider can honour requests. c

    var currString = vars("currency") + "/" + "CEDI"

    import Provider.providers

    (pr, getBestRateFromProviders(providers, currString, vars("amount").toInt)._2.length < 15)
  }

  def makeTransactionToDB(): Unit = {

    // create a Provider table to DB
    //first have to create the table for Providers
    // deduct from their volume
    // should be an atomic transaction

  }

}

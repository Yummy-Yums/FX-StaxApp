package org.fx.application

import Bank._
import DatabaseCxn._

import scalatags.Text.Frag
import scalatags.Text.all._

import scala.util.Random

sealed trait Event
object Event{

  case class PurchaseRequest(trader: Trader, currency: Currency, date: java.time.LocalDate, amount: Double) extends Event
//  case class AvailableOffers(provider: Provider, currency: Currency, amount: Double, rate: Double) extends Event
  case class RequestStatus(pr: PurchaseRequest, status: String) extends Event{

    def toFrag(): Frag = frag(
      tr(
        td(
          li("purchase request details"),
          li(pr.date.toString),
          li(pr.currency.curr),
        ),
        td(status),
      )
    )

    def passToDb() = {

      val e = Requests(pr.trader.name, pr.amount, pr.date)
      UpdateDb.singleInsert(e)

    }
  }

  case class Requests(trader_name: String, amount: Double, date: java.time.LocalDate) extends Event

}

sealed trait Bank
object Bank {

  // use unique-id

  case class Trader(id: Long, name: String, bankaccount_id: Long, fxTender: String) extends Bank{

    def getDetails(): String = s"Trader - ${name} , BankName - ${bankaccount_id}, fx - ${fxTender}"

  }

  case class Currency(curr: String*)

  case class Account(id: Int, balance: Double, currency: String, trader_id: Int, number: Int)

  case class CreateBankAccount(trader: Trader, currency: Currency, initialBalance: Double) extends Bank{

    def createAccount(): Option[Account] = {
      // check in the db if the Trader doesn't exist and create one

      getDetailsFromDB().foreach{ person =>

        if (person._1 == trader.name){

          return None

        } else {

          import FxUtil._

          val newacc = Account(
             new Random(20).nextInt(),
              0.0,
              "USD",
            g(new Random(10)),
            g(new Random(100)),
          )

          import dbConn.ctx
          import dbConn.ctx._

          ctx.run(query[Account].insert(lift(newacc)))

          return Some(newacc)

        }
      }

      None
    }

  }

  case class UpdateBankAccount(trader: Trader, currency: Currency, currentBalance: Double) extends Bank{

    def updateAccount(action: String): Option[Account] = {
      // check in db for current balance and update accordingly ( + or - )
      None
    }
  }

  case class GetBankAccount(trader: Trader, currency: Currency, initialBalance: Double) extends Bank


}

package org.fx.application
package server

import org.fx.application.domain.Bank.Account.{balance, currentBalance}
import org.fx.application.domain.BusinessLogic._
import DatabaseCxn.{dbConn, getTrader}

import org.fx.application.domain.Event.RequestStatus
import org.fx.application.domain.Event.Requests.statusList
import org.fx.application.domain.Provider.{offersList, providersList}

import org.fx.application.util.ServerUtil._
import cask.endpoints._
import cask.model.Response
import org.fx.application.domain.{Bank, Event}
import scalatags.Text
import scalatags.Text.all._
import scalatags.Text.tags2.section
import ujson.Obj

import scala.collection.immutable._
import scala.collection.mutable.ArrayBuffer

object WebServer extends cask.MainRoutes{

  val dbThread = new Thread(() => {
    println("running db connection in another Thread")
    while (true) {
      dbConn
    }
  })

  var status = false

  dbThread.start()

  private var openConnections = Set.empty[cask.WsChannelActor]

  @cask.staticFiles("/static")
  def staticFileRoutes() = "src/main/resources/static"

  @get("/")
  def getEndpoint() = {
    ujson.Obj("response" -> "hello World" )
  }

  @cask.get("/index")
  def index(): Text.all.doctype = doctype("html")(
    html(
      head(
        script(src:="/static/resourcemiddleware.js")
      ),
      body(
        h1("FX Purchase Request"),
        form(onsubmit := "submitFXPurchaseForm()")(
          h2("Request FX Purchase"),
          label(`for` := "currency", "Currency:"),
          select(id := "currency")(
            option(value := "USD", "USD"),
            option(value := "EUR", "EUR"),
          ),
          br, br,
          label(`for` := "amount", "Amount:"),
          input(`type` := "text", id := "amount"),
          br, br,
          label(`for` := "Name", "Trader Name:"),
          input(`type` := "text", id := "nameInput"),
          br, br,
          label(`for` := "expiry", "Expiry Date:"),
          input(`type` := "date", id := "expiry"),
          br, br,
          button(`type` := "submit", "Submit Request")
        ),
        br,
        h4("Best Offers"),
        p(id := "bestoffers")(),
        h3("Available Balance"),
        p(id := "availableBalance")("0"),
        input(id := "inputAmount"),
        br,br,
        button(id := "topUpButton" , onclick :="topUp()")("Top Up"),
        br, br,
        h3("Current Balance"),
        p(id := "currentBalance")(currentBalance),
        br, br,
        h3("Current Volume"),
        p(id := "currentVolume")("currentVolume"),

        h2("Available Offers"),
        table(
          thead(
            tr(
              th("Provider"),
              th("Currency"),
              th("Volume"),
              th("Rate")
            )
          ),
          tbody(
            tr(
              tr(id := "offersList")(offersList),
            ),
            br, br
//            tr(
//              td(id := "messageList")(queryresults()),
//            )
          ),
        ),
        br, br,
        form(onsubmit := "submitProviderAndAccountForm()", method := "POST")(
          h2("Select Preferred Provider and Bank Account"),
          label(`for` := "provider", "Preferred Provider: "),
          select(id := "provider")(providersList),
          br, br,
          label(`for` := "bankaccount", "Bank Account: "),
          input(`type` := "text", id := "bank_account"),
          br, br,
          button(`type` := "submit", "Submit Selection")
        ),
        br, br,
        h2("Requests"),
        table(
          thead(
            tr(
              th("Name"),
              th("Amount"),
              th("Date"),
              th("Buy Time"),
            )
          ),
          tbody(
            tr(id := "statusList")(statusList),
          )
        ),
      )
    )
  )

  @get("/test")
  def test(): Text.all.doctype = doctype("html")(
    html(lang := "en")(
      head(script(indexjs)),
      body(
        section(id := "header")(
          h2("Welcome"),
          h2("Log In or Sign Up"),
          button(onclick := "testEndpoint()")("Login"),
          button(`type` := "submit")("SignUp"),
          p(id := "test")
        )
      ),
   )
  )

  @postJson("/submitFXPurchaseForm")
  def purchaseHandler(currency: String, amount: String, name: String, expiry: String): Response[String] = {

    /*
      currency coming is in cedis by default
     */

    val infoMap = collection.mutable.Map.newBuilder[String, String]

    infoMap += ("name" -> name)
    infoMap += ("currency" -> currency)
    infoMap += ("amount" -> amount)
    infoMap += ("expiry" -> expiry)

    val trader: Option[Bank.Trader] = getTrader(name)
    var response: Obj = Obj()

    if (trader.isEmpty){

      response = ujson.Obj("success" -> false, "err" -> "Trader doesn't exist , create a profile")

    } else {

      val buildEventResponse: (Event.PurchaseRequest, ArrayBuffer[(Double, String)]) = {
        println(infoMap.result())
        buildEvent(infoMap.result())
      }
      println("build event " + buildEventResponse)
      // checking whether there is a best rate available
      val bestRate: Boolean = buildEventResponse._2.nonEmpty

      if (bestRate) {

        // take the purchase request and update make a Request Status
        val successfulRequest: RequestStatus = RequestStatus(buildEventResponse._1, "success")

        successfulRequest.persistToDb()

        // send status of request to Request Table
        // val rr: Text.Frag = statusList(justReturnArequest, successfulRequest)

        response = ujson.Obj("success" -> true, "err" -> "", "listOfbestproviders" -> s"${buildEventResponse._2}")

      } else {

        // save in db
        val unsuccessfulRequest = RequestStatus(buildEventResponse._1, "failure")
        unsuccessfulRequest.persistToDb()
        //      for (conn <- openConnections) conn.send(cask.Ws.Text(statusList.render))
        response = ujson.Obj("success" -> false, "err" -> "cannot buy amount")

      }
    }

    println(currency, amount, name)
    println(response)

    Response(response.render())
  }

  @postJson("/submitProviderAndAccount")
  def providerFormHandler(provider: String, bankaccount: String) = {

    println(provider, bankaccount)

  }

  @websocket("/updates")
  def updates(): WsHandler =
    cask.WsHandler { connection =>

      connection.send(cask.Ws.Text("Welcome to the Cask server!"))
      connection.send(cask.Ws.Text(offersList.render))
      openConnections += connection

      cask.WsActor {
        case cask.Ws.Text(msg) =>
          // Process the received message
          println("Received message from client:", msg)
        case cask.Ws.Close(_, _) => openConnections -= connection
      }
    }


  @websocket("/provider-offer-updates")
  def updatesProvider() = cask.WsHandler { connection =>

      connection.send(cask.Ws.Text(providersList.render))
      openConnections += connection

      cask.WsActor {
        case cask.Ws.Close(_, _) => openConnections -= connection
      }
  }

  @websocket("/requests-updates")
  def updateRequests() = cask.WsHandler { cxn =>
    cxn.send(cask.Ws.Text(statusList.render))
    openConnections += cxn

    cask.WsActor {
      case cask.Ws.Close(_, _) => openConnections -= cxn
    }
  }

  @get("/success-status")
  def updateUI(): Unit = {

    if (status){
      balance - 2
    }

  }

  override def port: Int = 8040

  log.debug(s"server has started on port $port")

  initialize()
}

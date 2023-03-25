package org.fx.application

import ServerUtil._
import FxUtil._
import BusinessLogic._
import Event.RequestStatus

import cask.endpoints._
import org.fx.application.Provider.offersList
import scalatags.Text
import scalatags.Text.all._
import scalatags.Text.tags.div
import scalatags.Text.tags2.section

import scala.collection.mutable

object WebServer extends cask.MainRoutes{

  import DatabaseCxn.dbConn

  val dbThread = new Thread(() => {
    println("running db connection in other Thread")
    while (true) {
      dbConn
    }
  })

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
        script(indexjs)
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
        br, br,
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
              tr(id := "providerList")(offersList),
            ),
            br , br
//            tr(
//              td(id := "messageList")(queryresults()),
//            )
          ),
        ),
        br, br,
        form(
          h2("Select Preferred Provider and Bank Account"),
          label(`for` := "provider", "Preferred Provider:"),
          select(id := "provider", name := "provider")(
            // need to push updates here as well
            option(value := "Provider A", "Provider A"),
            option(value := "Provider B", "Provider B"),
            option(value := "Provider C", "Provider C")
          ),
          br, br,
          label(`for` := "bank_account", "Bank Account:"),
          input(`type` := "text", id := "bank_account", name := "bank_account"),
          br, br,
          button(`type` := "submit", "Submit Selection")
        ),
        br, br,
        h2("Requests"),
        table(
          thead(
            tr(
              th("Currency"),
              th("Volume"),
              th("Provider"),
              th("Bank Account"),
              th("Status")
            )
          ),
          tbody(
            tr(id := "statusList")(),
          )
        ),
      )
    )
  )

  @get("/test")
  def test() = doctype("html")(
    html(lang := "en")(
      head(
       script(indexjs),
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
  )

  @postJson("/submitFXPurchaseForm")
  def purchaseHandler(currency: String, amount: String, name: String, expiry: String) = {

    /*
      currency coming in is cedis by default
     */

    val infoMap = collection.mutable.Map.newBuilder[String, String]

    infoMap += ("name" -> name)
    infoMap += ("currency" -> currency)
    infoMap += ("amount" -> amount)
    infoMap += ("expiry" -> expiry)

    val buildEventResponse = buildEvent(infoMap.result())

    if(buildEventResponse._2) {

      // take the purchase request and update make a Request Status
      val r: RequestStatus = RequestStatus(buildEventResponse._1, "success")


      ujson.Obj("success" -> true, "err" -> "")

      r.passToDb()
      val rr = statusList(justReturnArequest, r)
      ujson.Obj("success" -> true,  "err" -> "")

    } else {

      // save in db
      RequestStatus(buildEventResponse._1, "failure")
//      for (conn <- openConnections) conn.send(cask.Ws.Text(statusList.render))
      ujson.Obj("success" -> false, "err" -> "cannot buy amount")

    }

    println(currency, amount, name)
  }

  @websocket("/updates")
  def updates() = cask.WsHandler { connection =>

    connection.send(cask.Ws.Text(queryresults().render))
    openConnections += connection

    cask.WsActor {
      case cask.Ws.Close(_, _) => openConnections -= connection
    }
  }

  @websocket("/provider-offer-updates")
  def updatesProvider() = cask.WsHandler { connection =>

      connection.send(cask.Ws.Text(offersList.render))
      openConnections += connection

      cask.WsActor {
        case cask.Ws.Close(_, _) => openConnections -= connection
      }
  }

  override def port: Int = 8040

  log.debug(s"server has started on port $port")

  initialize()
}

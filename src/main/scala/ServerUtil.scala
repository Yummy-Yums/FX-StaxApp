package org.fx.application

import scalatags.Text.all._
import io.getquill._
import org.fx.application.Bank.Trader

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ServerUtil {

  val indexjs = raw(
    """
     function testEndpoint(){

      fetch('/')
        .then(response => response.json())
        .then(data => {

          console.log(data)

          const p = document.getElementById('test')

          p.innerText = data["response"]

        })
        .catch(error => console.error(error));

      }

      function submitFXPurchaseForm() {
      var e = JSON.stringify({
                        currency: currency.value,
                        amount: amount.value,
                        name: nameInput.value,
                        expiry : expiry.value
                    })
      fetch(
            "/submitFXPurchaseForm",
            {
                method: "POST",
                body: e
            }
      ).then(response => response.json())
      console.log(e)

    }

    var socket = new WebSocket("ws://" + location.host + "/updates")
    var socketForUpdates = new WebSocket("ws://" + location.host + "/provider-offer-updates")

    socket.onmessage = function(ev) { messageList.innerHTML = ev.data }
    socketForUpdates.onmessage = function(ev){ providersList.innerHTML = ev.data }
  """
  )

  val datetimeFormatter: String => LocalDate = (dateStr: String) => {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    LocalDate.parse(dateStr, formatter)

  }

  val results = DatabaseCxn.getDetailsFromDB()

  def queryresults() = frag(
    for((m, n) <- results) yield p(b(m), " ", n)
  )

}

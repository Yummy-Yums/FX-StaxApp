package org.fx.application
package util

import scalatags.Text.all._
import scalatags.generic.Frag
import scalatags.text.Builder

import java.time.LocalDate
import java.time.format.DateTimeFormatter

object ServerUtil {

  val indexjs = raw(
    """
    function testEndpoint(){

        fetch('/')
            .then(response => response.json())
            .then(data => {

                const p = document.getElementById('test')

                p.innerText = data["response"]

            })
            .catch(error => console.error(error));

    }

    function submitFXPurchaseForm() {

        //field validation here

        var payload = JSON.stringify({
            currency: currency.value,
            amount: amount.value,
            name: nameInput.value,
            expiry : expiry.value
        })
        fetch(
            "/submitFXPurchaseForm",
            {
                method: "POST",
                body: payload
            }
        ).then(response => response.json())

    }

    function submitProviderAndAccountForm() {

        var payload = JSON.stringify({
            provider: provider.value,
            bankaccount: bankaccount.value
        })

        fetch(
            "/submitProviderAndAccount",
            {
                method: "POST",
                body: payload
            }
        ).then(response => response.json())
        console.log(payload)
    }

    var socket = new WebSocket("ws://" + location.host + "/updates")
    var socketForUpdates = new WebSocket("ws://" + location.host + "/provider-offer-updates")

//    var messageList = document.getElementById("providersList")
    var providersList = document.getElementById("providersList")

    socket.onmessage = function(ev) { messageList.innerHTML = ev.data }
    socketForUpdates.onmessage = function(ev){ providersList.innerHTML = ev.data }
  """
  )

  val datetimeFormatter: String => LocalDate = (dateStr: String) => {

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    LocalDate.parse(dateStr, formatter)

  }

  val results = DatabaseCxn.getDetailsFromDB()

  def queryresults(): Frag[Builder, String] = frag(
    for((m, n) <- results) yield p(b(m), " ", n)
  )

}

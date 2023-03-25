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
    console.log(e)

}

var socket       = new WebSocket("ws://" + location.host + "/updates")
var socketForUpdates       = new WebSocket("ws://" + location.host + "/provider-offer-updates")


socket.onmessage = function(ev) { messageList.innerHTML = ev.data }
socketForUpdates.onmessage = function(ev){ providersList.innerHTML = ev.data }
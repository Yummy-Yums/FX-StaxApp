window.onload = (event) => {
  const topUp = document.getElementById("availableBalance")
  var savedBalance = localStorage.getItem("balance")

  topUp.textContent = new Intl.NumberFormat().format(savedBalance)
}

function testEndpoint(){

    fetch('/')
        .then(response => response.json())
        .then(data => {

            const p = document.getElementById('test')

            p.innerText = data["response"]

        })
        .catch(error => console.error(error));

}

function topUp(){

  let res;

  var savedBalance = localStorage.getItem("balance")
  console.log("savedBalance " + savedBalance)

  savedBalanceCheck = savedBalance == null ? "false" : savedBalance 
  console.log("savedBalanceCheck " + savedBalanceCheck)

  const input = document.getElementById("inputAmount").value

  const topUp = document.getElementById("availableBalance")

  if (savedBalanceCheck != "false"){
    res = parseInt(topUp.textContent) + parseInt(input)
  } else {
    
    res = parseInt(topUp.textContent) + parseInt(savedBalance)
  }

  localStorage.setItem("balance", res)

//  topUp.textContent = res.toLocaleString()
  topUp.textContent = new Intl.NumberFormat().format(res)

}

function submitFXPurchaseForm() {

  /* field validation here.
  amount should be more that 100, expiry date should be in the future */

  var availableBalance = document.getElementById("availableBalance").textContent
  availableBalance = parseInt(availableBalance)

  currency = currency.value
  amount = amount.value
  nameInput = nameInput.value
  expiry = expiry.value

  var isValid = true;

  switch (true) {

    case !amount && availableBalance < amount:
      alert("Amount is required");
      isValid = false;
      break;

    case availableBalance < amount:
      alert("Amount is below Available Balance");
      isValid = false;
      break;

    case !nameInput:
      alert("Name is required");
      isValid = false;
      break;

    case !expiry:
      alert("Expiry is required");
      isValid = false;
      break;

  }

  if (!isValid) {
    // Stop execution if any validation fails
    return;
  } 

  var payload = JSON.stringify({
      currency: currency,
      amount: amount,
      name: nameInput,
      expiry : expiry
  })

  fetch("/submitFXPurchaseForm",{
      method: "POST",
      headers: {
        'Content-Type': 'application/json'
      },
      body: payload
  })
    .then(response => response.json())
    .then(data => {
          console.log(data)

          if (data['success'] == "true") {

            console.log("success")
            alert('success')



          } else {

            console.log("failure")
            alert('failure')

          }

          const bestoffers = document.getElementById("bestoffers")

          bestoffers.innerText = data["listOfbestproviders"]

          console.log(data["listOfbestproviders"])
    })
    .catch(error => {
      console.error("Error:", error);
    });

    console.log("end of form method")

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
var socketForProviderUpdates = new WebSocket("ws://" + location.host + "/provider-offer-updates")
var socketForRequests = new WebSocket("ws://" + location.host + "/requests-updates")

const data = { key: 'value' };
socket.onopen = (event) => {

//  socket.send("Here's some text that server is urgently awaiting");
//  socket.send("Hello, Cask Server")
//  socket.send(JSON.stringify(data));
}

//var messageList = document.getElementById("providersList")
var offersList = document.getElementById("offersList")
var providersList =  document.getElementById("providersList")
var statusList = document.getElementById("statusList")

socket.onmessage = function(ev) {

//    console.log(ev.data)
    offersList = ev.data

}

socketForProviderUpdates.onmessage = function(ev){
//    console.log(ev.data)
    providerList = ev.data
}

socketForRequests.onmessage = function(ev){
  statusList = ev.data
}


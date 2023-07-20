package org.fx.application
package mocking

import org.fx.application.domain.Bank.{Account, Trader}
import org.fx.application.domain.Event.Requests

import org.mockito.Mockito._
import org.scalatest.BeforeAndAfterEach
import org.scalatest.funsuite.AnyFunSuite
import org.scalatestplus.mockito.MockitoSugar

import java.time.LocalDate

class DatabaseCxnTest extends AnyFunSuite with BeforeAndAfterEach with MockitoSugar{

  val mockDbInteractions: Cxn = mock[Cxn]
  val detailsData: List[(String, Long)] = List(("Elias", 2))
  val traderDetails: Option[Trader] = Some(Trader(235,"Elias",2,"USD"))

  val date: LocalDate = java.time.LocalDate.now()
  val requestsData: List[Requests] = List(Requests("Elias", 100, date), Requests("Walton", 200, date))
  val accountData: List[Account] = List(Account(1, 2300.0, "USD", 234, 4565))
//  val requestsData = List()

  override def beforeEach(): Unit = {

    super.beforeEach()
    
    when(mockDbInteractions.getDetailsFromDB).thenReturn(detailsData)
    when(mockDbInteractions.getTrader("Elias")).thenReturn(traderDetails)
    when(mockDbInteractions.getTrader("DefaultUser")).thenReturn(None)
    when(mockDbInteractions.getRequest).thenReturn(requestsData)
    when(mockDbInteractions.getAccount(1)).thenReturn(accountData)

  }

  test("getDetailsFromDb"){

    val result = mockDbInteractions.getDetailsFromDB

    assert(result == detailsData)
  }

  test("getTrader") {

    val result = mockDbInteractions.getTrader("Elias")
    val result2 = mockDbInteractions.getTrader("DefaultUser")

    assert(result == traderDetails)
    assert(result2.isEmpty)

  }

  test("getRequest") {

    val result = mockDbInteractions.getRequest

    if (result.isEmpty){
      assert(result.isEmpty)
    }

    assert(result == requestsData)

  }

  test("getAccount"){

    val result = mockDbInteractions.getAccount(1)
    println(result)

    assert(result == accountData)

  }

  test("Verifications") {

    verify(mockDbInteractions).getDetailsFromDB
    verify(mockDbInteractions).getTrader("Elias")
    verify(mockDbInteractions).getTrader("DefaultUser")
    verify(mockDbInteractions).getRequest
    verify(mockDbInteractions).getAccount(1)

  }
}

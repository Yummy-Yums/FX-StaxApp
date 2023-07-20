package org.fx.application
package functional

import org.openqa.selenium.By
import org.openqa.selenium.chrome.ChromeDriver
import org.scalatest.BeforeAndAfterAll
import org.scalatest.flatspec.AnyFlatSpec
import org.scalatest.matchers._
import org.scalatestplus.selenium._

class WebServerTest extends AnyFlatSpec with should.Matchers with HtmlUnit with BeforeAndAfterAll{

  val host = "http://localhost:8040/index"

  implicit val driver = new ChromeDriver()

  println(driver.getTitle)

  "The blog app home page" should "have the correct title" in {
    println(pageTitle)
    go to (host)
    pageTitle should be("")
  }

  "updating money" should "increase my amount" in {
    val script =
      """
        var set = localStorage.setItem("balance", 0)
        |""".stripMargin
    executeScript(script)

    val elemInput = driver.findElement(By.cssSelector("#inputAmount"))
    val topupBtn = driver.findElement(By.id("topUpButton"))
    val balance = driver.findElement(By.id("availableBalance"))

    elemInput.sendKeys("20")
    topupBtn.click()

    assert(balance.getText == "20")

  }

  override def afterAll(): Unit = {
    driver.quit()
  }

}

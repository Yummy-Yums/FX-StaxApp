package org.fx.application
package server

import org.fx.application.domain.Bank.Trader

object MainAppServer {

  def main(args: Array[String]): Unit = {
    println("Hello world!")
  }


  import DatabaseCxn.dbConn
  import DatabaseCxn.dbConn.ctx
  import DatabaseCxn.dbConn.ctx._

  val dbThread = new Thread(() => {
    println("running db connection in other Thread")
    while(true){
      dbConn
    }
  })

//  dbThread.start()

  def dbCheckAndGet(whatToCheck: String) = {

    val q = quote {

      query[Trader]
        .filter(_.name == lift(whatToCheck))
        .map(m => Trader(m.id, m.name, m.bankaccount_id, m.fxTender ))

    }

    ctx.run(q)

  }

  println(dbCheckAndGet("John Doe"))

  println("main Thread")
}
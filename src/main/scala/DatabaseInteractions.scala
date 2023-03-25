package org.fx.application


import ujson._
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import io.getquill._
import Bank.{Account, Trader}

import cask.Request
import org.fx.application.Event.Requests

trait Cxn {

}

object DatabaseCxn extends Cxn {

  import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
  import io.getquill._

//  val queries: List[String] = List(
//
//  )

  object dbConn {

    val server: EmbeddedPostgres = EmbeddedPostgres.builder()
      .setDataDirectory(System.getProperty("user.home") + "/data")
      .setCleanDataDirectory(false).setPort(5434)
      .start()

    val pgDataSource = new org.postgresql.ds.PGSimpleDataSource()
    pgDataSource.setUser("postgres")
    pgDataSource.setPassword("drowssap2603")
    pgDataSource.setPortNumber(5434)

    val hikariConfig = new HikariConfig()
    hikariConfig.setDataSource(pgDataSource)

    val ctx = new PostgresJdbcContext(LowerCase, new HikariDataSource(hikariConfig))
      println("established connection")

  }

  import dbConn.ctx
  import dbConn.ctx._

  // put a list of case classes here since queries cannot have generic types

  def getDetailsFromDB() = {

    // what Data Table should be gotten

    val q = quote {
      for {
        p <- query[Trader]
      } yield {
        (p.name, p.bankaccount_id)
      }
    }

    ctx.run(q)

  }

  def dbCheckAndGet(whatToCheck: String) = {

    val q = quote {

      query[Trader]
        .filter(_.name == lift(whatToCheck))
        .map(m => Trader(m.id, m.name, m.bankaccount_id, m.fxTender ))

    }

    ctx.run(q)

  }

  object UpdateDb {

    /*
      i. single insert
      ii. batch insert
      iii. single update
      iv. batch update
     */

    //i.
    def singleInsert(r: Requests, b: Option[Bank] = None) = {

        val q = quote {

          query[Requests]
            .insert(lift(r))
        }

        ctx.run(q)

    }



  }

}



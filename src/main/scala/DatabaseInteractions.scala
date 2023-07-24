package org.fx.application


import org.fx.application.domain.Bank.{Account, Trader}
import org.fx.application.domain.Event.Requests
import com.opentable.db.postgres.embedded.EmbeddedPostgres
import org.fx.application.domain.Bank

trait Cxn {

  def getDetailsFromDB: List[(String, Long)]
  def getTrader(whatToCheck: String): Option[Trader]
  def getRequest: List[Requests]
  def getAccount(id: Int): List[Account]

}

object DatabaseCxn extends Cxn {

  import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
  import io.getquill._

  object dbConn {

    val server: EmbeddedPostgres = EmbeddedPostgres.builder()
      .setDataDirectory(System.getProperty("user.home") + "/data")
      .setCleanDataDirectory(false)
      .setPort(5438)
      .start()

    val pgDataSource = new org.postgresql.ds.PGSimpleDataSource()


    val hikariConfig = new HikariConfig()
    hikariConfig.setDataSource(pgDataSource)

    val ctx = new PostgresJdbcContext(LowerCase, new HikariDataSource(hikariConfig))
    println("established connection")

  }

  import dbConn.ctx
  import dbConn.ctx._

  // put a list of case classes here since queries cannot have generic types

  def getDetailsFromDB(): List[(String, Long)] = {

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

  def getTrader(whatToCheck: String): Option[Trader]  = {

    val q = quote {

      query[Trader]
        .filter(_.name == lift(whatToCheck))
        .map(m => Trader(m.id, m.name, m.bankaccount_id, m.fxTender))

    }

    ctx.run(q).headOption

  }

  def getRequest: List[Requests] = {

    val q = quote {
      query[Requests].sortBy(_.inserted_at)(Ord.desc)

    }

    ctx.run(q)
  }

  def getAccount(id: Int): List[Account] = {
    val q = quote {
      query[Account].filter(p => p.id == lift(id))
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
    def singleInsert(r: Requests, b: Option[Bank] = None): Long = {

        val q = quote {

          query[Requests]
            .insert(lift(r))
        }

        ctx.run(q)

    }

  }

}



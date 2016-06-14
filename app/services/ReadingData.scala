package services

import java.util.Locale
import javax.inject._

import models.NodeReading
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.db.slick.{DatabaseConfigProvider, HasDatabaseConfigProvider}
import slick.driver.JdbcProfile
import slick.driver.MySQLDriver.api._

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.{Future, _}

//! Mapping to DB
class ReadingTableDef(tag: Tag) extends Table[NodeReading](tag, "nodereading")
{
	override def * =
		(id, nodeId, dataId, uptime, temperature, voltage, date) <>(NodeReading.tupled, NodeReading.unapply)

	def id = column[Long]("id", O.PrimaryKey, O.AutoInc)

	def nodeId = column[Long]("nodeId")

	def dataId = column[Long]("dataId")

	def uptime = column[Long]("uptime")

	def temperature = column[Float]("temperature")

	def voltage = column[Float]("voltage")

	def date = column[Long]("date")
}

//! Service interface
trait NodeReadingStore
{
	def add(reading: NodeReading): Future[String]

	def getAll(): Future[Seq[NodeReading]]

	def getFromTo(from: Long, to: Long): Future[Seq[NodeReading]]

	def getLatestDates(): Future[Seq[Long]]

	def getLatest(): Future[Seq[NodeReading]]
}

//! Service implementation in DB
@Singleton
class NodeReadingStoreImpl @Inject()(protected val dbConfigProvider: DatabaseConfigProvider)
	extends HasDatabaseConfigProvider[JdbcProfile] with NodeReadingStore
{
	val readings = TableQuery[ReadingTableDef]

	def add(reading: NodeReading): Future[String] =
	{
		dbConfig.db.run(readings += reading).map(res => "Reading successfully added.").recover
		{
			case ex: Exception => ex.getCause.getMessage
		}
	}

	def delete(id: Long): Future[Int] =
	{
		dbConfig.db.run(readings.filter(_.id === id).delete)
	}

	def get(id: Long): Future[Option[NodeReading]] =
	{
		dbConfig.db.run(readings.filter(_.id === id).result.headOption)
	}

	def listAll: Future[Seq[NodeReading]] =
	{
		dbConfig.db.run(readings.result)
	}

	def getAll(): Future[Seq[NodeReading]] =
	{
		dbConfig.db.run(readings.result)
	}

	def getFromTo(from: Long, to: Long): Future[Seq[NodeReading]] =
	{
		dbConfig.db.run(readings.filter(reading => reading.date >= from && reading.date <= to).result)
	}

	def getLatestDates(): Future[Seq[Long]] =
	{
		dbConfig.db.run(readings.filter(_.nodeId >= 0.toLong).filter(_.nodeId < 65536.toLong).groupBy(_.nodeId).map { case (a, b) => b.map(_.date).max.get }.result)
	}

	def getLatest(): Future[Seq[NodeReading]] =
	{
		getLatestDates().map
		{
			dates =>
				dates.flatMap(date => Await.result(getFromTo(date, date), 100 seconds))
		}
	}
}

package controllers

import java.util.Locale
import javax.inject._

import akka.actor.ActorSystem
import models.ReadingDateHelper
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api._
import play.api.data.format
import play.api.mvc._
import services.NodeReadingStore

import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext}

/**
	* Parses date range as string to milliseconds in tuple
	*/
object DateInputHelper
{
	//! Splits given string and retrieves a value at an index
	def stringDateToMillis(dateString: String, index: Int) = ReadingDateHelper.DateFormat.parseDateTime(dateString.split(" to ")(index)).getMillis

	//! Splits given string, converts it to milliseconds and returns in a tuple of two values
	def stringDateRangeToMillis(dateRangeString: String) = (stringDateToMillis(dateRangeString, 0), stringDateToMillis(dateRangeString, 1))
}

/**
	* Handles data ranges
	*
	* @param nodeReadingStore instance of NodeReadingStore
	* @param actorSystem akka instance for async
	* @param exec execution environment
	*/
class RangeController @Inject()(nodeReadingStore: NodeReadingStore, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller
{
	//! All results in case class parsable string
	def getAll = Action.async
	{
		nodeReadingStore.getAll.map
		{
			result =>
				Ok(result.toString)
		}
	}

	//! Sets the default range if not given
	private def fixRange(range: String) =
	{
		if (range.isEmpty)
			ReadingDateHelper.currentRangeString
		else
			range
	}

	//! shows a graph of range from string "date to date", see: ReadingDateHelper.DatePattern
	def getFromTo(range: String) = Action.async
	{
		(nodeReadingStore.getFromTo _ tupled(DateInputHelper.stringDateRangeToMillis(fixRange(range)))).map
		{
			result =>
				Ok(views.html.dateRange(
						result.groupBy(_.nodeId).toList.sortWith(_._1 < _._1).map
						{
								case (id, grp) => (id, grp.filter(_.nodeId == id))
						}.toList, fixRange(range)
					)
				)
		}
	}

	//! Shows table with latest data
	def getLatest() = Action.async
	{
		nodeReadingStore.getLatest().map
		{
			dates =>
				Ok(views.html.index(dates))
		}
	}
}

package controllers

import javax.inject._

import akka.actor.ActorSystem
import models.NodeReading
import org.joda.time.DateTime
import play.api._
import play.api.mvc._

import scala.xml._
import services.NodeReadingStore

import scala.concurrent.ExecutionContext

/**
	* DataInController class handles data input
	*
	* @param nodeReadingStore instance of NodeReadingStore
	* @param actorSystem akka instance for async
	* @param exec execution environment
	*/
@Singleton
class DataInController @Inject()(nodeReadingStore: NodeReadingStore, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller
{
	def checkData(reading: NodeReading) =
		reading.nodeId >= 0 && reading.nodeId <= 256 &&
		  reading.temperature >= 0.0 && reading.temperature <= 100.0 &&
		  reading.voltage >= 0.0 && reading.voltage <= 10.0

	def addData = Action
	{
		request =>
			val xml = request.body.asXml.get
			val dev = xml \ "Device"
			val reading = NodeReading(
				0,
				(dev \ "NodeId").text.toLong,
				(dev \ "DataId").text.toLong,
				(dev \ "Uptime").text.toLong,
				(dev \ "Temperature").text.toFloat,
				(dev \ "Voltage").text.toFloat,
				DateTime.now().getMillis) // by default the time is received immediatelly

			if (checkData(reading))
				nodeReadingStore.add(reading)

			Ok(reading.toString)
	}

	def addDataWithDate(time: Long) = Action
	{
		request =>
			val xml = request.body.asXml.get
			val dev = xml \ "Device"
			val reading = NodeReading(
				0,
				(dev \ "NodeId").text.toLong,
				(dev \ "DataId").text.toLong,
				(dev \ "Uptime").text.toLong,
				(dev \ "Temperature").text.toFloat,
				(dev \ "Voltage").text.toFloat,
				time) // when importing data, we want to specify the time explicitly

			if (checkData(reading))
				nodeReadingStore.add(reading)

			Ok(reading.toString)
	}
}

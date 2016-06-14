package controllers

import javax.inject._

import akka.actor.ActorSystem
import play.api.mvc._
import services.NodeReadingStore

import scala.concurrent.ExecutionContext

/**
	* Shows the default screen
	*
	* @param nodeReadingStore instance of NodeReadingStore
	* @param actorSystem akka instance for async
	* @param exec execution environment
	*/
@Singleton
class HomeController @Inject()(nodeReadingStore: NodeReadingStore, actorSystem: ActorSystem)(implicit exec: ExecutionContext) extends Controller
{
	def index = Action.async
	{
		nodeReadingStore.getLatest().map
		{
			dates =>
				// get the list of latest readings from all nodes
				Ok(views.html.index(dates.toList.sortWith(_.nodeId < _.nodeId)))
		}
	}
}

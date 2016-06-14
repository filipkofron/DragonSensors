package models

import java.util.Locale

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import scala.concurrent.duration._

/**
	* ReadingDateHelper singleton - contains date formatting constants
	*/
object ReadingDateHelper
{
	//! Predefined pattern
	final val DatePattern = "yyyy-MM-dd HH:mm a"

	//! Predefined format for predefined pattern and English Locale
	final val DateFormat = DateTimeFormat.forPattern(DatePattern).withLocale(Locale.ENGLISH)

	//! Returns current week range as string
	def currentRangeString : String = DateFormat.print((new DateTime).getMillis - (7 * 24 hours).toMillis) + " to " + DateFormat.print(new DateTime)
}

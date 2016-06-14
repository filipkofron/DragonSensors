package models

/**
	* Measured node reading.
	*
	* @param id database id
	* @param nodeId id of the hardware node, is unique
	* @param dataId id of the sequential date, is not unique
	* @param uptime current time the hardware has node spent not being in sleep
	* @param temperature measured temperature
	* @param voltage measured battery voltage
	* @param date time in milliseconds when the measurement was done
	*/
case class NodeReading(id: Long,
                       nodeId: Long, // Origin node id
                       dataId: Long, // Id of the received data, this helps to recognize the correct order of messages that don't come sequantially
                       uptime: Long, // Time in ms for how long has this been the current node up
                       temperature: Float, // measured temperature
                       voltage: Float, // measured voltage
                       date: Long) // milliseconds since 1970
{
	//! returns nice date in string in the ReadingDateHelper.DatePattern
	def niceDate = ReadingDateHelper.DateFormat.print(date)
}

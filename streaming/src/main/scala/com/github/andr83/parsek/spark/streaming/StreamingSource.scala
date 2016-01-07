package com.github.andr83.parsek.spark.streaming

import com.github.andr83.parsek.PValue
import com.typesafe.config.Config
import com.typesafe.scalalogging.slf4j.LazyLogging
import net.ceedubs.ficus.Ficus._
import org.apache.spark.streaming.dstream.DStream

/**
  * @author andr83
  */
abstract class StreamingSource extends LazyLogging {
  def apply(job: StreamingJob): DStream[PValue]
}

object StreamingSource {
  def apply(config: Config): StreamingSource = {
    val sourceType = config.as[Option[String]]("type")
      .getOrElse(throw new IllegalStateException("Source config should have type property"))
    val className = if (sourceType.contains(".")) sourceType
    else
      "com.github.andr83.parsek.spark.streaming.source." + sourceType.head.toUpper + sourceType.substring(1) + "Source"
    val constructor = Class.forName(className).getConstructor(classOf[Config])
    constructor.newInstance(config).asInstanceOf[StreamingSource]
  }
}

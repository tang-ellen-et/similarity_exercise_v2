package karius.exercise.genome.similarity.utils

import org.slf4j.{Logger, LoggerFactory}
 

trait Logging extends Serializable {
  @transient protected lazy val logName = s"${this.getClass.getName.stripSuffix("$")}"

  @transient private lazy val log: Logger = LoggerFactory.getLogger(logName)

  protected def logInfo(msg: => String) = if (log.isInfoEnabled) log.info(formatMessage(msg))

  protected def logDebug(msg: => String) = if (log.isDebugEnabled) log.debug(formatMessage(msg))

  protected def logTrace(msg: => String) = if (log.isTraceEnabled) log.trace(formatMessage(msg))

  protected def logWarning(msg: => String) = if (log.isWarnEnabled) log.warn(formatMessage(msg))

  protected def logError(msg: => String) = if (log.isErrorEnabled) log.error(formatMessage(msg))

  protected def logInfo(msg: => String, throwable: Throwable)  = if (log.isInfoEnabled) log.info(formatMessage(msg), throwable)

  protected def logDebug(msg: => String, throwable: Throwable) = if (log.isDebugEnabled) log.debug(formatMessage(msg), throwable)

  protected def logTrace(msg: => String, throwable: Throwable) = if (log.isTraceEnabled) log.trace(formatMessage(msg), throwable)

  protected def logWarning(msg: => String, throwable: Throwable) = if (log.isWarnEnabled) log.warn(formatMessage(msg), throwable)

  protected def logError(msg: => String, throwable: Throwable) = if (log.isErrorEnabled) log.error(formatMessage(msg), throwable)

  private def formatMessage(msg: => String) = s"${Logging.LogPrefix}-${msg}"
}

object Logging {
  val LogPrefix = "m.a"
}
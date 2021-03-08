package io.parapet.scheduler.benchmark

import io.parapet.scheduler.benchmark.Recorder.{Stopwatch, TimeWindow}
import io.parapet.scheduler.benchmark.WorkerPorocess._
import io.parapet.scheduler.{Channel, EventHandler, Frame, Scheduler}

import scala.concurrent.Promise
import scala.util.Try

class WorkerPorocess(val name: String,
                     scheduler: Scheduler,
                     recorder: Recorder,
                     onBatchCompelte: (Int, Long) => Unit) {

  private var _count = 0 // request counter
  private var batchStarted = false
  private var stopwatch: Stopwatch = null
  private val timeWindow = TimeWindow()


  private val handler = EventHandler[Event] {
    case BatchStart(size) =>
      require(!batchStarted, s"process[$name] batch already started")
      batchStarted = true
      stopwatch = recorder.start(name, Map(BatchSize -> size))
      timeWindow.reset()
    case Req =>
      _count = _count + 1
      timeWindow.tick()
    case BatchEnd =>
      require(batchStarted, s"process[$name] batch not started")
      stopwatch.stop(timeWindow.window)
      onBatchCompelte(_count, stopwatch.time)
      _count = 0
      batchStarted = false
  }

  private val frame = new Frame[Event](scheduler, handler)

  val channel: Channel[Event] = new Channel[Event](frame)

  def count: Int = _count
}


object WorkerPorocess {

  // API
  trait Event
  case object Req extends Event
  case class BatchStart(size: Int) extends Event
  case object BatchEnd extends Event

  // Metadata
  val BatchSize = "batch-size"

}
package io.parapet.benchmark

import io.parapet.benchmark.CounterProcess._
import io.parapet.scheduler.{Channel, EventHandler, Frame, Scheduler}

import scala.concurrent.Promise
import scala.util.Try

class CounterProcess(scheduler: Scheduler,
                     recorder: Recorder,
                     onCompelte: Promise[Int],
                     barrier: Int) {

  private var _count = 0
  private var stopCount = 0
  private var startCount = 0

  private val handler = EventHandler[Event] {
    case Start =>
      startCount = startCount + 1
      if (startCount == barrier)
        recorder.start()
    case Inc => _count = _count + 1
    case Stop =>
      stopCount = stopCount + 1
      if (stopCount == barrier)
        recorder.stop()
        onCompelte.complete(Try(_count))


  }

  private val frame = new Frame[Event](scheduler, handler)

  val channel: Channel[Event] = new Channel[Event](frame)

  def count: Int = _count
}


object CounterProcess {

  // API
  trait Event

  case object Start extends Event

  case object Stop extends Event

  case object Inc extends Event

}
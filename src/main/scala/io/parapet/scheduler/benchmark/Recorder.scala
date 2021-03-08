package io.parapet.scheduler.benchmark

import io.parapet.scheduler.benchmark.Recorder.{State, Stopwatch}

import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.{ConcurrentHashMap, ConcurrentLinkedQueue, TimeUnit}
import scala.collection.mutable.ListBuffer
import scala.jdk.CollectionConverters._

// Thread safe
class Recorder {
  private val _records = new ConcurrentHashMap[String, ListBuffer[Stopwatch]]()

  def start(id: String, metadata: Map[String, Any]): Stopwatch = {
    val stopwatch = Stopwatch(id, metadata)
    _records.compute(id, (k, v) => {
      val list = if v != null then v else ListBuffer.empty
      list += stopwatch
      list
    })
    stopwatch
  }

  // returns total time recored, but why ?
  def time: Long = {
    var total = 0L
    val it = _records.values().iterator()
    while it.hasNext do it.next().foreach(s => total = total + s.time)
    total
  }

  def timeMs: Long = {
    TimeUnit.MILLISECONDS.convert(time, TimeUnit.NANOSECONDS)
  }

  def values: Map[String, Seq[Stopwatch]] = {
    _records.asScala.view.mapValues(_.toSeq).toMap
  }
}

object Recorder {

  enum State:
    case Started, Stopped

  val NoWindow = Window(-1, -1)

  // Thread safe
  class Stopwatch(val name: String, val metadata: Map[String, Any]) {
    val startTime = System.nanoTime()
    private var _stopTime = 0L
    private var _time = 0L
    private val state = new AtomicReference[State](State.Started)
    private var _window = NoWindow

    def stopTime: Long = _stopTime

    def time: Long = _time

    def timeMs: Long = TimeUnit.MILLISECONDS.convert(_time, TimeUnit.NANOSECONDS)

    def window: Window = _window

    def stop(window: Window = NoWindow): Boolean = {
      if state.compareAndSet(State.Started, State.Stopped) then
        _stopTime = System.nanoTime()
        _time = _stopTime - startTime
        _window = window
        true
      else
        false
    }
  }

  case class Window(min: Long, max: Long)

  // Not thread safe
  class TimeWindow {
    // todo to implement avg we need to store data points off-heap asynchronously
    private var _min = Long.MaxValue
    private var _max = 0L
    private var start = System.nanoTime()

    def min: Long = _min

    def max: Long = _max

    // call before each session
    def reset(): Unit = start = System.nanoTime()

    def tick(): Long = {
      val now = System.nanoTime()
      val time = now - start
      _max = Math.max(_max, time)
      _min = Math.min(_min, time)
      start = now
      time
    }

    def window: Window = Window(_min, _max)
  }

}

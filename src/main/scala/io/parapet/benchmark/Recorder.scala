package io.parapet.benchmark

import java.util.concurrent.atomic.AtomicReference
import Recorder.State

// thread safe
class Recorder {
  private var _startTimeNanos = 0L
  private var _stopTimeNanos = 0L
  private var _totalTimeNanos = 0L
  private val state = new AtomicReference[State](State.Init)

  def start(): Boolean = {
    if state.compareAndSet(State.Init, State.Started) then
      _startTimeNanos = System.nanoTime()
      true
    else
      false
  }

  def stop(): Boolean = {
    if state.compareAndSet(State.Started, State.Stopped) then
      _stopTimeNanos = System.nanoTime()
      _totalTimeNanos = _stopTimeNanos - _startTimeNanos
      true
    else
      false
  }

  // retrun time recorder in nanos
  def time: Long = _totalTimeNanos
}

object Recorder {

  enum State:
    case Init, Started, Stopped

}

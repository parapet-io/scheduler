package io.parapet.scheduler.benchmark

import io.parapet.scheduler.Channel
import io.parapet.scheduler.benchmark.WorkerPorocess._

class Sender(name: String,
             events: Int,
             channelQueue: ChannelQueue[Event]
            ) extends Runnable {

  override def run(): Unit = {
    val ch = channelQueue.deque()
    ch.send(BatchStart(events))
    (0 until events).foreach(_ => ch.send(Req))
    ch.send(BatchEnd)
    channelQueue.enqueue(ch)
  }
}

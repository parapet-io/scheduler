package io.parapet.benchmark

import io.parapet.scheduler.Channel

import CounterProcess._

class Sender(channel: Channel[Event], total: Int) extends Runnable {

  override def run(): Unit = {
    channel.send(Start)
    (0 until total).foreach(_ => channel.send(Inc))
    channel.send(Stop)
  }
}

package io.parapet.scheduler

import io.parapet.scheduler.Frame

import java.util.concurrent.ExecutorService
import scala.annotation.tailrec

import Scheduler.Worker

class Scheduler(es: ExecutorService) {
  self =>

  def schedule(frame: Frame[_]): Unit = {
    // can be interrupted here
    if (frame.activate()) {
      // can be interrupted here
      es.submit(new Worker(frame, self))
    } else {
      // can be interrupted here
      frame.setMore()
    }
  }
}

object Scheduler {
  class Worker(frame: Frame[_], scheduler: Scheduler) extends Runnable {
    override def run(): Unit = {
      require(frame.active(), "frame should be active")
      drain()
      // can be interrupted here
      frame.deactivate()
      // can be interrupted here
      val hasMore = frame.resetMore()

      if (hasMore) {
        scheduler.schedule(frame)
      }
    }

    @tailrec
    private def drain(): Unit = {
      frame.queue.deque() match {
        case Some(event) =>
          frame.execute(event)
          drain()
        case None => ()
      }
    }
  }

}



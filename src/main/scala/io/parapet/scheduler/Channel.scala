package io.parapet.scheduler

class Channel[T](frame: Frame[T]) {
  def send(event: T): Unit = {
    frame.queue.enqueue(event)
    frame.scheduler.schedule(frame)
  }
}

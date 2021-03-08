package io.parapet.scheduler

import java.util.concurrent.ConcurrentLinkedQueue

class EventQueue[T] {
  private val queue = new ConcurrentLinkedQueue[T]()
  def enqueue(x: T): Unit = queue.add(x)
  def deque(): Option[T] = Option(queue.poll())
}

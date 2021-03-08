package io.parapet.scheduler

class EventHandler[T](onEvent: PartialFunction[T, Unit]) {
  def canHandle(event: T): Boolean = onEvent.isDefinedAt(event)
  def handle(event: T): Unit = onEvent(event)
}

object EventHandler {
  def apply[T](onEvent: PartialFunction[T, Unit]): EventHandler[T] = {
    new EventHandler(onEvent)
  }
}
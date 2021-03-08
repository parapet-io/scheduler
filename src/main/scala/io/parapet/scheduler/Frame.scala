package io.parapet.scheduler
import io.parapet.scheduler.Scheduler

import java.util.concurrent.atomic.AtomicBoolean

class Frame[T](val scheduler: Scheduler, handler: EventHandler[T]) {
  private val _active: AtomicBoolean = new AtomicBoolean()
  private val _more: AtomicBoolean = new AtomicBoolean()
  
  def activate(): Boolean = _active.compareAndSet(false, true)
  def active():Boolean = _active.get()
  def deactivate():Boolean = _active.compareAndSet(true, false)
  def setMore():Unit = _more.set(true)
  def resetMore():Boolean = _more.compareAndSet(true, false)
  
  private[scheduler] val queue = new EventQueue[T]()
  
  def execute(event:T):Unit= {
    if(handler.canHandle(event)) {
      handler.handle(event)
    }
  }

}

package io.parapet.scheduler.benchmark

import java.util.concurrent.ArrayBlockingQueue
import  io.parapet.scheduler.Channel

// fixed size blocking quueue
class ChannelQueue[T](channels: Seq[Channel[T]]) {

  private val queue = new ArrayBlockingQueue[Channel[T]](channels.size)

  channels.foreach(w => queue.add(w))
  
  //  returning true upon success and throwing an IllegalStateException if this queue is full
  def enqueue(ch: Channel[T]): Boolean = queue.add(ch)
  
  // Retrieves and removes the head of this queue, waiting if necessary until an element becomes available
  def deque(): Channel[T] = queue.take()
  
}

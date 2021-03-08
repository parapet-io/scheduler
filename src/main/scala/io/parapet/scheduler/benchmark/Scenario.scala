package io.parapet.scheduler.benchmark

case class Scenario(
                     workersThreads: Int,
                     workers: Int,
                     senders: Int,
                     sendersThreads: Int,
                     events: Int,
                     timeoutMs: Long
                   ) {

  override def toString: String =
    s"""
       |workers: $workers
       |workersThreads: $workersThreads
       |senders: $senders
       |sendersThreads: $sendersThreads
       |events: $events
       |""".stripMargin
}

package io.parapet.benchmark

case class Scenario(
                     workersThreads: Int,
                     senders: Int,
                     sendersThreads: Int,
                     events: Int,
                     timeoutMs: Long
                   ) {

  override def toString: String = 
  s"""
      |workersThreads: $workersThreads
      |senders: $senders
      |sendersThreads: $sendersThreads
      |events: $events
      |timeoutMs: $timeoutMs
      |""".stripMargin

}

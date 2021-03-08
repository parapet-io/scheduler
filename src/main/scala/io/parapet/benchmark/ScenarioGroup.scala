package io.parapet.benchmark

case class ScenarioGroup(val name: String,
                         val workersThreads: Int,
                         val senders: Int,
                         val sendersThreads: Int,
                         val timeoutMs: Long,
                         series: List[Int]) {

  val scenarios = series.map(events => Scenario(
    workersThreads = workersThreads,
    senders = senders,
    sendersThreads = sendersThreads,
    events = events,
    timeoutMs = timeoutMs
  ))

}

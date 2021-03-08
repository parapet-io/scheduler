package io.parapet.scheduler.benchmark

import io.parapet.scheduler.benchmark.Recorder.Stopwatch

case class Result(
                   scenario: Scenario,
                   totalTimeMs: Long,
                   processingTimeMs: Long,
                   data: Map[String, Seq[Stopwatch]])

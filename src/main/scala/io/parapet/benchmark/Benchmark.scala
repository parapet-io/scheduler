package io.parapet.benchmark

import java.io.{BufferedWriter, File, FileWriter}
import java.nio.file.Paths
import java.text.SimpleDateFormat
import java.util.Date

object Benchmark {

  def main(args: Array[String]): Unit = {

    val fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date())
    val file = new File(s"./reports/${fileName}.txt")
    file.createNewFile()
    val br = new BufferedWriter(new FileWriter(file))

    println("Scheduler Benchmark")

    val series = List(1000, 10000, 100000, 500000, 1000000, 5000000, 10000000)

    val scenarios = List(
      ScenarioGroup(
        name = "group1",
        workersThreads = 1,
        senders = 1,
        sendersThreads = 1,
        timeoutMs = 10000,
        series = series),
      ScenarioGroup(
        name = "group2",
        workersThreads = 1,
        senders = 2,
        sendersThreads = 2,
        timeoutMs = 10000,
        series = series),
      ScenarioGroup(
        name = "group3",
        workersThreads = 1,
        senders = 4,
        sendersThreads = 4,
        timeoutMs = 10000,
        series = series),
      ScenarioGroup(
        name = "group4",
        workersThreads = 1,
        senders = 10,
        sendersThreads = 4,
        timeoutMs = 10000,
        series = series),
      ScenarioGroup(
        name = "group5",
        workersThreads = 1,
        senders = 100,
        sendersThreads = 4,
        timeoutMs = 10000,
        series = series),
      ScenarioGroup(
        name = "group6",
        workersThreads = 1,
        senders = 1000,
        sendersThreads = 4,
        timeoutMs = 10000,
        series = series)
    )

    scenarios.foreach(sg => {
      val results = Runner.run(sg)
      br.write(s"${sg.name} -- results:\n")
      results.foreach(r => br.write(s"events: ${r.scenario.events}}, time: ${r.timeMs}ms\n"))
      br.write("=" * 50)
      br.write("\n")
      Reporter.save(sg, results)
    })

    br.close()
  }
}

package io.parapet.scheduler.benchmark

import de.vandermeer.asciitable.AsciiTable

import java.io.{BufferedWriter, File, FileWriter, Writer}
import java.nio.file.{Files, Paths}
import java.text.SimpleDateFormat
import java.util.Date

object Benchmark {

  extension (writer: Writer)
    def write2H(value: String): Unit = {
      writer.write(s"## $value\n\n")
    }

    def write3H(value: String): Unit = {
      writer.write(s"### $value\n\n")
    }

    def code(): Unit = {
      writer.write("\n```\n")
    }

  def main(args: Array[String]): Unit = {
    Files.createDirectories(Paths.get("./reports"));
    val file = new File(s"./reports/report.md")
    file.createNewFile()
    val writer = new BufferedWriter(new FileWriter(file))

    println("Scheduler Benchmark")

    val series = List(1000, 10000, 100000, 500000, 1000000, 5000000, 10000000)

    val scenarios = List(
      ScenarioGroup(
        name = "group-1",
        workers = 1,
        workersThreads = 1,
        senders = 1,
        sendersThreads = 1,
        timeoutMs = 10000,
        series = series),
      ScenarioGroup(
        name = "group-2",
        workers = 1,
        workersThreads = 1,
        senders = 2,
        sendersThreads = 2,
        timeoutMs = 10000,
        series = series),
      ScenarioGroup(
        name = "group-3",
        workers = 2,
        workersThreads = 1,
        senders = 2,
        sendersThreads = 2,
        timeoutMs = 10000,
        series = series),
      ScenarioGroup(
        name = "group-4",
        workers = 2,
        workersThreads = 2,
        senders = 2,
        sendersThreads = 2,
        timeoutMs = 10000,
        series = series),
      ScenarioGroup(
        name = "group-5",
        workers = 4,
        workersThreads = 4,
        senders = 4,
        sendersThreads = 4,
        timeoutMs = 10000,
        series = series),
      ScenarioGroup(
        name = "group-6",
        workers = 4,
        workersThreads = 4,
        senders = 20,
        sendersThreads = 4,
        timeoutMs = 10000,
        series = series),
      ScenarioGroup(
        name = "group-7",
        workers = 20,
        workersThreads = 8,
        senders = 100,
        sendersThreads = 4,
        timeoutMs = 10000,
        series = List(1000000, 5000000, 10000000, 50000000, 100000000))
    )

    val width = 70
    val delim = "=" * width
    val dateStr = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss").format(new Date())
    writer.write2H("Benchmark")
    writer.write(s"_Benchmark Date: ${dateStr}_\n")
    writer.write(s"_Scala3 (Dotty): 3.0.0-M2_\n\n")
    writer.write2H("Content")
    // write contents
    scenarios.foreach { sg =>
      writer.write(s"* [${sg.name}](#${sg.name})\n")
    }
    writer.write("\n")
    scenarios.foreach { sg =>
      val results = Runner.run(sg)
      writer.write(s"## ${sg.name}\n\n")
      sg.scenarios.foreach { s =>
        writer.write(s"* [${s.events} events](#${sg.name}-${s.events}-events)\n")
      }
      results.foreach { res =>
        writer.write3H(s"${sg.name} ${res.scenario.events} events")
        writer.write(s"navigate [${sg.name}](#${sg.name}) / [top](#content)\n")
        writer.write(s"**Config**:\n")
        writer.code()
        writer.write(res.scenario.toString)
        writer.code()
        writer.write(s"**total time: ${res.totalTimeMs} ms, total processing time (sum): ${res.processingTimeMs} ms**\n\n")
        res.data.foreach { (workerId, metrics) =>
          writer.code()
          val table = new AsciiTable()
          table.addRule()
          table.addRow(null, null, null, s"$workerId stats")
          table.addRule()
          table.addRow("bath-size", "time (ms)", "min (μs)", "max (μs)")
          metrics.foreach { m =>
            val batchSize = m.metadata(WorkerPorocess.BatchSize)
            val timeMs = m.timeMs
            val window = m.window
            table.addRule()
            table.addRow(batchSize, timeMs, window.min, window.max)
          }
          table.addRule()
          writer.write(table.render(width))
          writer.code()
          writer.write(s"navigate [${sg.name}](#${sg.name}) / [top](#content)\n")
          writer.write("\n")
        }
      }

      writer.write(delim)
      writer.write("\n" * 2)
      Reporter.save(sg, results)
    }

    writer.close()
  }
}

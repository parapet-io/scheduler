package io.parapet.scheduler.benchmark

import io.parapet.scheduler.Scheduler

import java.util.concurrent.atomic.{AtomicInteger, AtomicLong}
import java.util.concurrent.{CountDownLatch, Executors, ExecutorService, TimeUnit}
import scala.concurrent.duration._
import scala.concurrent.{Await, Promise}

object Runner {

  def run(scenarioGroup: ScenarioGroup): List[Result] = {
    scenarioGroup.scenarios.map(s => run(s))
  }

  def run(scenario: Scenario): Result = {
    println("=" * 20)
    println(s"Run scenario: $scenario")
    val workersPool = Executors.newFixedThreadPool(scenario.workersThreads)
    val sendersPool = Executors.newFixedThreadPool(scenario.sendersThreads)
    val scheduler = Scheduler(workersPool)
    val recorder = Recorder()
    val chunkSize = scenario.events / scenario.senders
    require(scenario.events % scenario.senders == 0, "number of event should be equally devided bu number of senders")
    val cl = new CountDownLatch(scenario.senders) // each sender will send at most one batch
    val total = new AtomicInteger()
    val processingTime = new AtomicLong()

    def onBatchComplete(batchSize: Int, timeNanos: Long): Unit = {
      processingTime.addAndGet(timeNanos)
      total.addAndGet(batchSize)
      cl.countDown()
    }

    val workers =
      (0 until scenario.workers).map(i => WorkerPorocess(s"worker-$i", scheduler, recorder, onBatchComplete))

    val workersQueue = ChannelQueue(workers.map(_.channel))


    val senders = (0 until scenario.senders).map(i => Sender(s"sender-$i", chunkSize, workersQueue))
    val startTime = System.nanoTime()
    senders.map(s => sendersPool.submit(s)).foreach(f => f.get())

    cl.await()
    val endTime = System.nanoTime()
    val totalTimeMillis = TimeUnit.MILLISECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS)
    val processingTimeMillis = TimeUnit.MILLISECONDS.convert(processingTime.get(), TimeUnit.NANOSECONDS)
    shutdown(workersPool)
    shutdown(sendersPool)

    require(total.get() == scenario.events, "invalid number of processed events")

    Result(scenario = scenario,
      totalTimeMs = totalTimeMillis,
      processingTimeMs = processingTimeMillis,
      data = recorder.values)

  }

  def shutdown(es: ExecutorService): Unit = {
    try {
      es.shutdownNow()
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

}

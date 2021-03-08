package io.parapet.benchmark

import io.parapet.scheduler.Scheduler

import java.util.concurrent.{Executors, TimeUnit, TimeoutException, ExecutorService}
import scala.concurrent.{Await, Promise}
import scala.concurrent.duration._

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
    val onComplete = Promise[Int]()
    val counterProcess = CounterProcess(scheduler, recorder, onComplete, scenario.senders)

    (0 until scenario.senders)
      .map(_ => sendersPool.submit(Sender(counterProcess.channel, chunkSize)))
      .foreach(f => f.get())

    try {
      Await.result(onComplete.future, scenario.timeoutMs.millis)
    } catch {
      case e: TimeoutException => println(s"scenario has failed after ${scenario.timeoutMs} millis")
    }

    shutdown(workersPool)
    shutdown(sendersPool)
    
    Result(scenario, TimeUnit.MILLISECONDS.convert(recorder.time, TimeUnit.NANOSECONDS))
    
    // complete
  }

  def shutdown(es: ExecutorService): Unit = {
    try {
      es.shutdownNow()
    } catch {
      case e: Exception => e.printStackTrace()
    }
  }

}

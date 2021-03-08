package io.parapet.benchmark

import org.knowm.xchart.XYChart
import org.knowm.xchart.BitmapEncoder
import org.knowm.xchart.style.markers.SeriesMarkers

object Reporter {

  def save(scenarioGroup: ScenarioGroup, results: List[Result]): Unit = {
    val chart = XYChart(700, 700);
    chart.setTitle(
      s"""
         |worker-threads: ${scenarioGroup.workersThreads};
         |senders-threads: ${scenarioGroup.sendersThreads};
         |senders:${scenarioGroup.senders}
         |""".stripMargin);
    chart.setXAxisTitle("events");
    chart.setYAxisTitle("ms");
    val xData = results.map(r => r.scenario.events.toDouble).toArray
    val yData = results.map(r => r.timeMs.toDouble).toArray
    val series = chart.addSeries("timeMs", xData, yData);
    series.setMarker(SeriesMarkers.CIRCLE);

    BitmapEncoder.saveBitmap(chart, s"./reports/${scenarioGroup.name}", BitmapEncoder.BitmapFormat.PNG);
  }
}

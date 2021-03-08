
val dottyVersion = "3.0.0-M2"

lazy val root = project
  .in(file("."))
  .settings(
    name := "scheduler",
    version := "0.1.0",

    scalaVersion := dottyVersion,

    libraryDependencies ++=
      Seq(
        "org.knowm.xchart" % "xchart" % "3.8.0",
        "de.vandermeer" % "asciitable" % "0.3.2",
        "com.novocode" % "junit-interface" % "0.11" % "test"
      )

  )

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = true)
mainClass in assembly := Some("io.parapet.scheduler.benchmark.Benchmark")
mainClass in(Compile, run) := Some("io.parapet.scheduler.benchmark.Benchmark")
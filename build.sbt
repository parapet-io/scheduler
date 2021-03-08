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
        "com.novocode" % "junit-interface" % "0.11" % "test"
      )

  )


mainClass in(Compile, run) := Some("io.parapet.benchmark.Benchmark")
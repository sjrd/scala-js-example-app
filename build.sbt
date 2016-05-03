// Turn this project into a Scala.js project by importing these settings
enablePlugins(ScalaJSPlugin)

name := "Example"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.8"

persistLauncher in Compile := true

persistLauncher in Test := false

scalaJSUseRhino := false

scalaJSOptimizerOptions in Compile ~= {
  _.withDisableOptimizer(true)
}

scalaJSOptimizerOptions in (Compile, fullOptJS) ~= {
  _.withDisableOptimizer(false).withUseClosureCompiler(false)
}

scalaJSSemantics ~= {
  _.withAsInstanceOfs(org.scalajs.core.tools.sem.CheckedBehavior.Unchecked)
}

addCommandAlias("bench",
    ";set scalaJSStage in Global := FastOptStage;run" +
    ";set scalaJSStage in Global := FullOptStage;run")

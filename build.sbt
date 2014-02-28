// Turn this project into a Scala.js project by importing these settings
scalaJSSettings

name := "Example"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
    "org.scala-lang.modules.scalajs" %% "scalajs-jasmine-test-framework" % scalaJSVersion % "test"
)

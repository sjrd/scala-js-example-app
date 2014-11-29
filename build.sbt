lazy val root = project.in(file(".")).
  enablePlugins(ScalaJSPlugin)

name := "Example"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.5"

persistLauncher := true

persistLauncher in Test := false

libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.7.0"
)

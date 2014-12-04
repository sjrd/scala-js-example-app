lazy val rootProject = project.in(file(".")).
  aggregate(rootJVM, rootJS)

lazy val root = crossProject.in(file(".")).
  settings(
    normalizedName := "example",

    version := "0.1-SNAPSHOT",

    scalaVersion := "2.11.2",

    libraryDependencies ++= Seq(
        "com.lihaoyi" %%% "utest" % "0.2.5-M1" % "test"
    ),

    testFrameworks += new TestFramework("utest.runner.Framework")
  ).
  jvmSettings(
    name := "Example JVM"
  ).
  jsSettings(
    name := "Example JS",

    persistLauncher := true,

    persistLauncher in Test := false,

    libraryDependencies ++= Seq(
        "org.scala-js" %%% "scalajs-dom" % "0.7.0"
    )
  )

lazy val rootJVM = root.jvm
lazy val rootJS = root.js

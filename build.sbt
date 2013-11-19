resolvers += Resolver.url("Scala.js temp repository",
    url("http://lampwww.epfl.ch/~doeraene/scala-js-repo"))(Resolver.ivyStylePatterns)

// Turn this project into a Scala.js project by importing these settings
scalaJSSettings

name := "Mastermind"

version := "0.1-SNAPSHOT"

// Specify additional .js file to be passed to package-js and optimize-js
unmanagedSources in (Compile, ScalaJSKeys.packageJS) +=
    baseDirectory.value / "js" / "startup.js"

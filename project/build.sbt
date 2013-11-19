resolvers += Resolver.url("Scala.js temp repository",
    url("http://lampwww.epfl.ch/~doeraene/scala-js-repo"))(Resolver.ivyStylePatterns)

addSbtPlugin("org.scala-lang.modules.scalajs" % "scalajs-sbt-plugin" % "0.1-SNAPSHOT")

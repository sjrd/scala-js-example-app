# Barebone application written in Scala.js

[![Codacy Badge](https://api.codacy.com/project/badge/Grade/4dd76174da204498941e0c0863467bbe)](https://www.codacy.com/app/sjrdoeraene/scala-js-example-app?utm_source=github.com&utm_medium=referral&utm_content=sjrd/scala-js-example-app&utm_campaign=badger)

This is a barebone example of an application written in
[Scala.js](http://www.scala-js.org/).

## Get started

To get started, open `sbt` in this example project, and execute the task
`fastOptJS`. This creates the file `target/scala-2.11/example-fastopt.js`.
You can now open `index-fastopt.html` in your favorite Web browser!

During development, it is useful to use `~fastOptJS` in sbt, so that each
time you save a source file, a compilation of the project is triggered.
Hence only a refresh of your Web page is needed to see the effects of your
changes.

## Run the tests

To run the test suite, execute the task `test`. If you have installed
[Node.js](http://nodejs.org/), you can use that runtime to run the tests,
which is faster:

    > set scalaJSStage in Global := FastOptStage
    > test

## The fully optimized version

For ultimate code size reduction, use `fullOptJS`. This will take several
seconds to execute, so typically you only use this for the final, production
version of your application. While `index-fastopt.html` refers to the
JavaScript emitted by `fastOptJS`, `index.html` refers to the optimized
JavaScript emitted by `fullOptJS`.

If Node.js is installed, the tests can also be run in their fully optimized
version with:

    > set scalaJSStage in Global := FullOptStage
    > test

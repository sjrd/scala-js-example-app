# Example application written in Scala.js

This is a barebone example of an application written in
[Scala.js](https://github.com/lampepfl/scala-js).

## Get started

To get started, open `sbt` in this example project, and issue the task
`packageJS`. This creates the file `target/scala-2.10/example.js` and its
siblings `example-extdeps.js` and `example-intdeps.js`.
You can now open `index-dev.html` in your favorite Web browser!

During development, it is useful to use `~packageJS` in sbt, so that each
time you save a source file, a compilation of the project is triggered.
Hence only a refresh of your Web page is needed to see the effects of your
changes.

## The preoptimized version

Instead of running `packageJS`, you can also run `preoptimizeJS` to generate
a much more compact version of the JavaScript code in virtually no more time
than you run `packageJS`. In that case, open the `example-preopt.html` file
to execute the application. Note that browser refresh is much faster in this
case.

`preoptimizeJS` should be fast enough for you to use `~preoptimizeJS` in sbt
for your development cycle.

## The optimized version

For ultimate code size reduction, use `optimizeJS`. This will take several
seconds to execute, so typically you only use this for the final, production
version of your application. While `index-dev.html` (resp. `index-preopt.html`)
refers to the JavaScript emitted by `packageJS` (resp. `preoptimizeJS`),
`index.html` refers to the optimized JavaScript emitted by `optimizeJS`.

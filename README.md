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

## The optimized version

Instead of running `packageJS`, you can also run `optimizeJS` to generate
a much more compact version of the JavaScript code. While `index-dev.html`
refers to the JavaScript emitted by `packageJS`, `index.html` refers to the
optimized JavaScript emitted by `optimizeJS`.

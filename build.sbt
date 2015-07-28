// Turn this project into a Scala.js project by importing these settings
enablePlugins(ScalaJSPlugin)

name := "Example"

version := "0.1-SNAPSHOT"

scalaVersion := "2.11.7"

persistLauncher in Compile := true

persistLauncher in Test := false

testFrameworks += new TestFramework("utest.runner.Framework")

libraryDependencies ++= Seq(
    "org.scala-js" %%% "scalajs-dom" % "0.8.1",
    "com.lihaoyi" %%% "utest" % "0.3.0" % "test"
)

def patchHackedFile(file: File): Unit = {
  import org.scalajs.core.ir._
  import Trees._
  import Types._
  import org.scalajs.core.tools.io._

  val vfile = FileVirtualScalaJSIRFile(file)
  val (classInfo, classDef) = vfile.infoAndTree
  println(classDef)
  val className = classDef.name.name
  val classType = ClassType(className)

  val (fieldIdent, alreadyMutable) = classDef.defs collectFirst {
    case FieldDef(ident @ Ident(_, Some(origName)), _, mutable)
        if origName.trim == "x" =>
      (ident, mutable)
  } getOrElse {
    throw new Exception("Could not find field `x`")
  }
  println(fieldIdent)

  if (alreadyMutable) {
    println("The field is already mutable. Don't do anything.")
    return
  }

  val newDefs = classDef.defs map { memberDef =>
    implicit val pos = memberDef.pos

    memberDef match {
      case FieldDef(`fieldIdent`, tpe, _) =>
        FieldDef(fieldIdent, tpe, mutable = true)

      case MethodDef(false, ident @ Ident("setX__I__V", origName), params, resultType, _) =>
        val paramRef = params.head.ref
        val newBody =
            Assign(Select(This()(classType), fieldIdent)(IntType), paramRef)
        val newDef = MethodDef(false, ident, params, resultType, newBody)(
            OptimizerHints.empty, None)
        Hashers.hashMethodDef(newDef)

      case _ =>
        memberDef
    }
  }

  val newClassDef = classDef.copy(defs = newDefs)(
    classDef.optimizerHints)(classDef.pos)
  println(newClassDef)

  val out = WritableFileVirtualBinaryFile(file)
  val outputStream = out.outputStream
  try {
    InfoSerializers.serialize(outputStream, classInfo)
    Serializers.serialize(outputStream, newClassDef)
  } finally {
    outputStream.close()
  }
}

compile in Compile := {
  val analysis = (compile in Compile).value
  val classDir = (classDirectory in Compile).value

  val hackedFile = classDir / "example" / "Foo.sjsir"
  if (hackedFile.exists)
    patchHackedFile(hackedFile)

  analysis
}

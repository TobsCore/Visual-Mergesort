name := "Visual Mergesort"
version := "1.0"
scalaVersion := "2.11.8"
test in assembly := {}
libraryDependencies += "org.scalafx" %% "scalafx" % "8.0.92-R10"

addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)

libraryDependencies += "org.scalafx" %% "scalafxml-core-sfx8" % "0.2.2"

// Includes the JavaFX Stylesheet, when SBT is run from the terminal
unmanagedJars in Compile += Attributed.blank(file(System.getenv("JAVA_HOME") + "/lib/ext/jfxrt.jar"))

// Allows the process to be restarted, when run from sbt
fork in run := true

mainClass in Compile := Some("projektarbeit.VisualMergesort")
mainClass in assembly := Some("projektarbeit.VisualMergesort")

// Sets the output name of the jar file
assemblyJarName in assembly := "Visual Mergesort.jar"

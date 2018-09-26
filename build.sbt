name := "fs2-playground"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies += "co.fs2" %% "fs2-core" % "0.10.6" // For cats 1.1.0 and cats-effect 0.10
libraryDependencies += "co.fs2" %% "fs2-io" % "0.10.6"
scalacOptions ++= Seq(
  "-encoding",
  "UTF-8", // source files are in UTF-8
  "-deprecation", // warn about use of deprecated APIs
  "-unchecked", // warn about unchecked type parameters
  "-feature", // warn about misused language features
  "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
  "-Xlint", // enable handy linter warnings
  //  "-Xfatal-warnings", // turn compiler warnings into errors
  "-Ypartial-unification" // allow the compiler to unify type constructors of different arities
)

resolvers += Resolver.sonatypeRepo("releases")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.7")


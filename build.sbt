name := "concurrency-case-study"
version := "0.0.1"
scalaVersion := "2.12.10"

scalacOptions ++= Seq(
  "-encoding", "UTF-8",   // source files are in UTF-8
  "-deprecation",         // warn about use of deprecated APIs
  "-unchecked",           // warn about unchecked type parameters
  "-feature",             // warn about misused language features
  "-language:higherKinds",// allow higher kinded types without `import scala.language.higherKinds`
  // "-Xlint",               // enable handy linter warnings
  "-Ypartial-unification" // allow the compiler to unify type constructors of different arities
)

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full)

resolvers += Resolver.bintrayRepo("dmbl","dinogroup")

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.5.26",
  "org.typelevel" %% "cats-effect" % "2.0.0",
  "io.monix" %% "monix" % "3.1.0"
)

ThisBuild / useSuperShell := false

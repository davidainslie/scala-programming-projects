import sbt._

lazy val IT = config("it") extend Test

lazy val root = project("scala-programming-projects", file("."))
  .settings(description := "Scala by Backwards following the book Scala Programming Projects")
  .aggregate(retirementCalculator)

lazy val retirementCalculator = project("retirement-calculator", file("modules/retirement-calculator"))
  .settings(mainClass in assembly := Some("com.backwards.retirement.SimulatePlanApp"))

def project(id: String, base: File): Project =
  Project(id, base)
    .enablePlugins(JavaAppPackaging)
    .configs(IT)
    .settings(inConfig(IT)(Defaults.testSettings))
    .settings(Defaults.itSettings)
    .settings(
      resolvers ++= Seq(
        Resolver.sonatypeRepo("releases"),
        Resolver.sonatypeRepo("snapshots"),
        "jitpack" at "https://jitpack.io"
      ),
      scalaVersion := BuildProperties("scala.version"),
      sbtVersion := BuildProperties("sbt.version"),
      organization := "com.backwards",
      name := id,
      autoStartServer := false,
      addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.11.0" cross CrossVersion.full),
      addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
      libraryDependencies ++= Dependencies(),
      scalacOptions ++= Seq(
        "-encoding", "utf8",
        "-deprecation",
        "-unchecked",
        "-language:implicitConversions",
        "-language:higherKinds",
        "-language:existentials",
        "-language:postfixOps",
        "-Ymacro-annotations"
        //"-Xfatal-warnings"
        // "-Ywarn-value-discard"
      ),
      fork := true,
      publishArtifact in Test := true,
      publishArtifact in IT := true,
      addArtifact(artifact in (IT, packageBin), packageBin in IT).settings
    )
ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

lazy val root = (project in file("."))
  .settings(
    name := "FX-App",
    idePackagePrefix := Some("org.fx.application"),
    libraryDependencies ++= Seq(
      "io.getquill" %% "quill-jdbc" % "3.5.2",
      "org.postgresql" % "postgresql" % "42.2.8",
      "com.opentable.components" % "otj-pg-embedded" % "0.13.1",
      "com.lihaoyi" %% "scalatags" % "0.12.0",
      "com.lihaoyi" %% "cask" % "0.9.0"
    )
  )



ThisBuild / scalaVersion := "3.1.1"

val modules = file("modules")

publish / skip := true

val publishSettings = Vector(
  organization := "ru.tinkoff",
  version      := "0.0.2",
)

val testDependencies = libraryDependencies ++= Vector(
  "org.scalactic" %% "scalactic" % Version.scalaTest ,
  "org.scalatest" %% "scalatest" % Version.scalaTest % Test,
  "org.scalameta" %% "munit" % Version.munit % Test
)

val lamrDependencies2 = libraryDependencies ++= Vector(
  "tf.tofu"       %% "tofu-kernel" % Version.tofu,
  "org.typelevel" %% "cats-free"   % Version.cats,
).map(_.cross(CrossVersion.for3Use2_13))

val parseDependencies2 = libraryDependencies ++= Vector(
  "org.typelevel" %% "cats-parse" % Version.catsParse,
).map(_.cross(CrossVersion.for3Use2_13))

val compilerSettints = scalacOptions ++= Vector(
  "-Yexplicit-nulls",
  "-encoding", "utf-8",
)

val defaultSettings = publishSettings ++ testDependencies ++ compilerSettints

lazy val lamr  = project
  .in(modules / "lamr")
  .settings(name := "cherry-lamr")
  .settings(lamrDependencies2)
  .settings(defaultSettings)

lazy val parse = project
  .in(modules / "parse")
  .settings(name := "cherry-parse")
  .settings(defaultSettings)
  .settings(parseDependencies2)
  .dependsOn(lamr)

lazy val tests = project
  .in(modules / "tests")
  .settings(publish / skip := true)
  .settings(defaultSettings)
  .dependsOn(lamr, parse)

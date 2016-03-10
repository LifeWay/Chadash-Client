name := "chadash-client"
version := scala.util.Properties.envOrElse("BUILD_NUMBER", "DEV")
scalaVersion := "2.11.5"
scalacOptions ++= Seq("-feature", "-target:jvm-1.8")

resolvers += "Typesafe" at "http://repo.typesafe.com/typesafe/maven-releases"

libraryDependencies ++= Seq(
  "com.typesafe" % "config" % "1.2.1",
  "com.typesafe.play" %% "play-json" % "2.3.8",
  "com.typesafe.play" %% "play-ws" % "2.3.8",
  "com.typesafe.play" %% "play-iteratees" % "2.3.0",
  "org.scalactic" %% "scalactic" % "2.2.4"
)

libraryDependencies ~= { _ map {
  case m if m.organization == "com.typesafe.play" =>
    m.exclude("commons-logging", "commons-logging").
      exclude("com.typesafe.play", "sbt-link").
      exclude("com.typesafe.play", "build-link")
  case m => m
}}

mainClass in assembly := Some("com.lifeway.chadash.client.Runner")

lazy val sampleTask = taskKey[Unit]("A sample task.")

fullRunTask(sampleTask, Compile, "com.lifeway.chadash.client.Runner")

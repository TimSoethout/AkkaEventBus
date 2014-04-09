name := "AkkaEventBus"

version := "1.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor"   % "2.2.3",
  "com.typesafe.akka" %% "akka-slf4j"   % "2.2.3",
  "com.typesafe.akka" %% "akka-remote"  % "2.2.3",
  "com.typesafe.akka" %% "akka-agent"   % "2.2.3",
 // "org.specs2" %% "specs2" % "2.3.10" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.2.3" % "test",
  "org.scalatest" %% "scalatest" % "2.1.3" % "test",
  "com.github.nscala-time" %% "nscala-time" % "0.8.0"
)


lazy val root = (project in file(".")).
  settings(
    name := "rock-paper-scissors",
    version := "1.0.0",
    organization := "me.akrivos",
    scalaVersion := "2.11.7",
    scalacOptions := Seq("-unchecked", "-deprecation", "-encoding", "utf8", "-Xfatal-warnings", "-feature", "-Xfuture"),
    libraryDependencies ++= {
      Seq(
        "org.scalatest" %% "scalatest"   % "2.2.5"   % Test,
        "org.mockito"   %  "mockito-all" % "1.10.19" % Test
      )
    }
  )


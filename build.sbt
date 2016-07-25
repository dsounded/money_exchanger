name := """money_exchanger"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayJava).dependsOn(swagger)
lazy val swagger = RootProject(uri("https://github.com/CreditCardsCom/swagger-play.git"))

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  "com.typesafe.play" %% "play-slick" % "2.0.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "2.0.0",
  "mysql" % "mysql-connector-java" % "5.1.36",
  "com.github.t3hnar" %% "scala-bcrypt" % "2.6",
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  // TODO: Replace with original repo when they fix it for 2.5 Play
  "io.swagger" %% "swagger-play2" % "1.5.2-SNAPSHOT"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"

fork in run := false

javaOptions in Test += "-Dconfig.file=conf/application-test.conf"

name := """dragonsensors"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.7"

libraryDependencies ++= Seq(
  cache,
  ws,
  "org.scalatestplus.play" %% "scalatestplus-play" % "1.5.1" % Test,
  "mysql" % "mysql-connector-java" % "5.1.34",
  "com.typesafe.play" %% "play-slick" % "1.1.0",
  "com.typesafe.play" %% "play-slick-evolutions" % "1.1.0"
)

resolvers += "scalaz-bintray" at "http://dl.bintray.com/scalaz/releases"


fork in run := false

import com.typesafe.sbt.packager.archetypes.ServerLoader.{Systemd, SystemV, Upstart}
// import com.typesafe.sbt.SbtNativePackager.autoImport._

enablePlugins(JavaAppPackaging)
// enablePlugins(JDebPackaging)
enablePlugins(JavaServerAppPackaging)

serverLoading in Debian := Systemd

//import com.typesafe.sbt.SbtNativePackager.Linux
//import NativePackagerKeys._

linuxPackageMappings += packageTemplateMapping(s"/var/run/${name.value}/")() withUser name.value withGroup name.value
maintainer in Linux := "Filip Kofron <filip.kofron.cz@gmail.com>"
packageSummary in Linux := "DragonSensors web server"
packageDescription := "DragonSensors provides our private web server containing temperature readings from bearded dragon terraria."

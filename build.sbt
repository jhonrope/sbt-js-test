sbtPlugin := true

name := "sbt-js-test"

organization := "com.joescii"

homepage := Some(url("https://github.com/joescii/sbt-js-test"))

version := "0.1.0-SNAPSHOT"

val htmlunitVersion = settingKey[String]("Version of htmlunit")
htmlunitVersion := "2.19"

val webjarLocatorVersion = settingKey[String]("Version of webjar locator")
webjarLocatorVersion := "0.30"

val jasmineVersion = settingKey[String]("Version of jasmine")
jasmineVersion := "2.4.1"

libraryDependencies ++= Seq(
  "net.sourceforge.htmlunit"  %  "htmlunit"         % htmlunitVersion.value       % "compile",
  "org.webjars"               %  "webjars-locator"  % webjarLocatorVersion.value  % "compile",
  "org.webjars.bower"         %  "jasmine"          % jasmineVersion.value        % "provided",
  "org.scalatest"             %% "scalatest"        % "2.2.6"                     % "test,it"
)

// don't bother publishing javadoc
publishArtifact in (Compile, packageDoc) := false

sbtVersion in Global := {
  scalaBinaryVersion.value match {
    case "2.10" => "0.13.9"
//    case "2.9.2" => "0.12.4"
  }
}

scalaVersion in Global := "2.10.5"

crossScalaVersions := Seq("2.10.5")

scalacOptions ++= Seq("-unchecked", "-deprecation")

//publishMavenStyle := false

//bintrayPublishSettings

//repository in bintray := "sbt-plugins"

licenses += ("Apache-2.0", url("http://www.apache.org/licenses/LICENSE-2.0.html"))

//bintrayOrganization in bintray := None

val itDebug = settingKey[Boolean]("Enables debug printing of the Integration Tests")
itDebug := true

lazy val root = (project in file(".")).
  configs(IntegrationTest).
  settings(Defaults.itSettings: _*).
  enablePlugins(BuildInfoPlugin)

parallelExecution in IntegrationTest := false

(test in IntegrationTest) <<= (test in IntegrationTest).dependsOn(Keys.`package` in Compile)
(testOnly in IntegrationTest) <<= (testOnly in IntegrationTest).dependsOn(Keys.`package` in Compile)

buildInfoKeys := Seq[BuildInfoKey](
  version,
  itDebug,
  scalaVersion,
  scalaBinaryVersion,
  sbtVersion,
  htmlunitVersion,
  webjarLocatorVersion,
  jasmineVersion
)

buildInfoPackage := "com.joescii.sbtjs.build"


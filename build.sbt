name := """sbt-slickgen"""

version := "1.0.0"

scalaVersion := "2.10.5"

organization := "com.carlossouza"

sbtPlugin := true

libraryDependencies ++= Seq(
  "mysql"               % "mysql-connector-java"  % "5.1.38",
  "com.typesafe.slick"  %% "slick"                % "3.1.1",
  "com.typesafe.slick"  %% "slick-codegen"        % "3.1.1",
  "org.scalatest"       %% "scalatest"            % "2.2.1" % "test"
)
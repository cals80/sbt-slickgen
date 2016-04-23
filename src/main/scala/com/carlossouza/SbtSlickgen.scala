package com.carlossouza

import java.io.File

import sbt.Keys._
import sbt.{SettingKey, TaskKey, AutoPlugin}

object SbtSlickgen extends AutoPlugin {

  object autoImport {
    lazy val genTables  = TaskKey[Unit]("gen-tables", "Generate the tables.scala from the database set in application.conf file")
    lazy val genFormats = TaskKey[Unit]("gen-formats", "Generate the formats.scala from the database set in application.conf file")
    lazy val genDaos    = TaskKey[Unit]("gen-daos", "Generate the DAO service files from the database set in application.conf file")
    lazy val genAll     = TaskKey[Unit]("gen-all", "Generate all the files described above")

    object GenSettings {
      lazy val playFramework      = SettingKey[Boolean]("playFramework")
    }
  }

  import autoImport._

  /**
    * Provide default settings
    */
  override lazy val projectSettings = Seq(
    GenSettings.playFramework := false,
    genTables := {
      println("Hello world! - " + GenSettings.playFramework.value.toString)
    },
    genFormats := {},
    genDaos := {},
    genAll := {}
  )
}

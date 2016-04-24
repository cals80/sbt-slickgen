package com.carlossouza

import java.io.File

import sbt.{AutoPlugin, TaskKey}

object SbtSlickgen extends AutoPlugin {

  object autoImport {
    lazy val genTables  = TaskKey[Seq[File]]("gen-tables", "Generate the tables.scala from the database set in application.conf file")
    lazy val genFormats = TaskKey[Unit]("gen-formats", "Generate the formats.scala from the database set in application.conf file")
    lazy val genDaos    = TaskKey[Unit]("gen-daos", "Generate the DAO service files from the database set in application.conf file")
    lazy val genAll     = TaskKey[Unit]("gen-all", "Generate all the files described above")
  }

  import autoImport._

  /**
    * Provide default settings
    */
  override lazy val projectSettings = Seq(
    genTables <<= Tables.generate,
    genFormats := Formats.generate,
    genDaos := Daos.generate,
    genAll := {
      genTables
      genFormats
      genDaos
    }
  )
}

package com.carlossouza

import java.io.File

import sbt.Keys._
import sbt._

import scala.io.Source
import java.io.File
import scala.util.matching.Regex

/**
  * Created by carlossouza on 4/22/16.
  */
object Tables {

  /**
    * This function generate the tables using Slick codegen plugin and put it in the right Play folder (models)
    */
  def generate = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>

    toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(
      getConfiguration(Settings.configDir, "slick.dbs.default.driver").dropRight(1),
      getConfiguration(Settings.configDir, "slick.dbs.default.db.driver"),
      getConfiguration(Settings.configDir, "slick.dbs.default.db.url"),
      Settings.outputDir,
      Settings.packageName,
      getConfiguration(Settings.configDir, "slick.dbs.default.db.user"),
      getConfiguration(Settings.configDir, "slick.dbs.default.db.password")),
      s.log))
    val fname = Settings.outputDir + Settings.packageDir + "/Tables.scala"
    Seq(file(fname))
  }

  /**
    * Reads the key in the application.conf file located at configuration directory
    * @param confDir the configuration directory
    * @param key the key to read
    * @return the value for the key in the application.conf file
    */
  def getConfiguration(confDir: String, key: String): String = {
    val filename = new File(".").getAbsolutePath + confDir + "application.conf"
    var result = ""
    for (line <- Source.fromFile(filename).getLines()) {
      val index = line indexOf key
      if (index == 0) {
        result = line.drop(line.indexOf("=") + 1)
        if (result.take(1) == "\"") result = result.drop(1)
        if (result.takeRight(1) == "\"") result = result.dropRight(1)
      }
    }
    result
  }

}

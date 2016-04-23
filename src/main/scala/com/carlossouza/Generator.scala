package com.carlossouza

import java.io.File

import sbt.Keys._
import sbt._

import scala.io.Source

/**
  * Created by carlossouza on 4/22/16.
  */
object Generator {

  /**
    * This function generate the tables using Slick codegen plugin and put it in the right Play folder (models)
    */
  def generateTables(playFramework: Boolean, packageName: String) = (sourceManaged, dependencyClasspath in Compile, runner in Compile, streams) map { (dir, cp, r, s) =>
    var outputDir: String = ""
    var pkgDir: String = ""
    var pkgName: String = ""
    var confDir: String = ""
    if (playFramework) {
      //val fullPath = new File("").getAbsolutePath + "/app/models/"
      pkgDir = "models"
      pkgName = "models"
      outputDir = new File("").getAbsolutePath + "/app/"
      confDir = "/conf/"
    } else {
      pkgDir = packageName.replace('.', '/')
      pkgName = packageName
      outputDir = new File("").getAbsolutePath + "/src/main/scala/"
      confDir = "/src/main/resources/"
    }
    toError(r.run("slick.codegen.SourceCodeGenerator", cp.files, Array(
      getConfiguration(confDir, "slick.dbs.default.driver").dropRight(1),
      getConfiguration(confDir, "slick.dbs.default.db.driver"),
      getConfiguration(confDir, "slick.dbs.default.db.url"),
      outputDir,
      pkgName,
      getConfiguration(confDir, "slick.dbs.default.db.user"),
      getConfiguration(confDir, "slick.dbs.default.db.password")),
      s.log))
    val fname = outputDir + pkgDir + "/Tables.scala"
    Seq(file(fname))
  }

  /**
    * This function reads the 'application.conf' configuration file
    */
  def getConfiguration(confDir: String, key: String): String = {
    val filename = new File(".").getAbsolutePath + confDir + "application.conf"
    var result = ""
    for (line <- Source.fromFile(filename).getLines()) {
      var index = line indexOf key
      if (index == 0) {
        result = line.drop(line.indexOf("=") + 1)
        if (result.take(1) == "\"") result = result.drop(1)
        if (result.takeRight(1) == "\"") result = result.dropRight(1)
      }
    }
    result
  }

}

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
    val playFramework: Boolean = new java.io.File(new File("").getAbsolutePath + "/conf/application.conf").exists
    var packageName: String = ""
    if (!playFramework) packageName = getPackage
    var outputDir: String = ""
    var pkgDir: String = ""
    var pkgName: String = ""
    var confDir: String = ""
    if (playFramework) {
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
    * Reads the key in the application.conf file located at configuration directory
    * @param confDir the configuration directory
    * @param key the key to read
    * @return the value for the key in the application.conf file
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

  /**
    * Return the package string of the main source scala file
    * @return
    */
  def getPackage: String = {
    val searchPath = new File(new File("").getAbsoluteFile + "/src/main/scala")
    val list: Array[File] = recursiveListFiles(searchPath, ".scala".r)
    var result: String = ""

    list.foreach { file =>
      if (isMainFile(file))
        result = file.getParent.replace(new File("").getAbsoluteFile + "/src/main/scala/", "").replace('/', '.')
    }
    if (result == "") throw new Exception("Could not identify main scala file.")
    result
  }

  /**
    * List all files and directories within a directory, matching a regex expression
    * @param f the starting directory
    * @param r the regex expression
    * @return an array of files and directories matching the regex expression
    */
  def recursiveListFiles(f: File, r: Regex): Array[File] = {
    val these = f.listFiles
    val good = these.filter(f => r.findFirstIn(f.getName).isDefined)
    good ++ these.filter(_.isDirectory).flatMap(recursiveListFiles(_,r))
  }

  /**
    * Function to check if file is the main source scala file
    * @param f the file to be check
    * @return return true if it is the main file
    */
  def isMainFile(f: File): Boolean = {
    var result: Boolean = false
    val fileLines = io.Source.fromFile(f).getLines.toList
    fileLines.foreach { line =>
      if ((line.indexOf("def main(args") != -1) && (line.indexOf("indexOf") == -1)) result = true
      if ((line.indexOf("extends App") != -1) && (line.indexOf("indexOf") == -1)) result = true
    }
    result
  }


}

package com.carlossouza

import java.io.File

import scala.util.matching.Regex

/**
  * Created by carlossouza on 4/23/16.
  */
object Settings {

  def playFramework: Boolean = new java.io.File(new File("").getAbsolutePath + "/conf/application.conf").exists

  def packageName: String = if (playFramework) "models" else getPackage + ".models"

  def outputDir: String = if (playFramework) new File("").getAbsolutePath + "/app/" else new File("").getAbsolutePath + "/src/main/scala/"

  def packageDir: String = if (playFramework) packageName else packageName.replace('.', '/')

  def configDir: String = if (playFramework) "/conf/" else "/src/main/resources/"

  /**
    * Return the package string of the main source scala file
    *
    * @return
    */
  private def getPackage: String = {
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
    *
    * @param f the starting directory
    * @param r the regex expression
    * @return an array of files and directories matching the regex expression
    */
  private def recursiveListFiles(f: File, r: Regex): Array[File] = {
    val these = f.listFiles
    val good = these.filter(f => r.findFirstIn(f.getName).isDefined)
    good ++ these.filter(_.isDirectory).flatMap(recursiveListFiles(_,r))
  }

  /**
    * Function to check if file is the main source scala file
    *
    * @param f the file to be check
    * @return return true if it is the main file
    */
  private def isMainFile(f: File): Boolean = {
    var result: Boolean = false
    val fileLines = io.Source.fromFile(f).getLines.toList
    fileLines.foreach { line =>
      if ((line.indexOf("def main(args") != -1) && (line.indexOf("indexOf") == -1)) result = true
      if ((line.indexOf("extends App") != -1) && (line.indexOf("indexOf") == -1)) result = true
    }
    result
  }

  //var packageName: String = ""
  //if (!playFramework) packageName = getPackage
  //var outputDir: String = ""
  //var pkgDir: String = ""
  //var pkgName: String = ""
  //var confDir: String = ""

  //if (playFramework) {
    //pkgDir = "models"
    //pkgName = "models"
    //outputDir = new File("").getAbsolutePath + "/app/"
    //confDir = "/conf/"
  //} else {
    //pkgDir = packageName.replace('.', '/')
    //pkgName = packageName
    //outputDir = new File("").getAbsolutePath + "/src/main/scala/"
    //confDir = "/src/main/resources/"
  //}

}

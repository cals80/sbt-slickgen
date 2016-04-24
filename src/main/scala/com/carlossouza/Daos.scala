package com.carlossouza

import scala.collection.mutable
import scala.io.Source

/**
  * Created by carlossouza on 4/24/16.
  */
object Daos {

  def generate: Unit = {
    getTables.foreach(println)
  }

  def getTables: List[String] = {
    val result: mutable.MutableList[String] = new mutable.MutableList[String]
    val caseClassFilePath = Settings.outputDir + Settings.packageDir + "/TableRows.scala"
    for (line <- Source.fromFile(caseClassFilePath).getLines()) {
      if (line.startsWith("case class")) {
        val className: String = line.split('(')(0).replace("case class ", "")
        result += className
      }
    }
    result.toList
  }

}

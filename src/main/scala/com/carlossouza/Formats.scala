package com.carlossouza

import java.io.{PrintWriter, File}

import scala.io.Source

/**
  * Created by carlossouza on 4/23/16.
  */
object Formats {

  def generate: Unit = {
    modifyTablesFunction()
    generateFormatsFunction()
  }

  /**
    * Modify the Tables.scala auto-generated slick tables to save case class in a separate file
    * Slick 3.0 Json formats will only work if case classes are in separate file
    */
  def modifyTablesFunction(): Unit = {
    val originFilePath    = Settings.outputDir + Settings.packageDir + "/Tables.scala"
    val originFile        = new File(originFilePath)
    val caseClassFilePath = Settings.outputDir + Settings.packageDir + "/TableRows.scala"
    val caseClassOut      = new PrintWriter(caseClassFilePath , "UTF-8")
    val newTablesFilePath = Settings.outputDir + Settings.packageDir + "/NewTables.scala"
    val newTablesOut      = new PrintWriter(newTablesFilePath , "UTF-8")
    val newTablesFile     = new File(newTablesFilePath)
    try {
      for (line <- Source.fromFile(originFilePath).getLines()) {
        if (line.trim.startsWith("package")) {
          caseClassOut.print(line + "\n\n")
          newTablesOut.print(line + "\n\n")
        } else {
          if (line.trim.startsWith("case class")) {
            caseClassOut.print(line.trim + "\n")
          } else {
            newTablesOut.print(line + "\n")
          }
        }
      }
    } finally {
      caseClassOut.close()
      newTablesOut.close()
      originFile.delete()
      newTablesFile.renameTo(originFile)
    }
  }

  /**
    * Generates Formats.scala trait, with Json formats for all case classes and correction for timestamp
    */
  def generateFormatsFunction(): Unit = {
    val preset: Seq[String] = Seq(
      "import java.sql.Timestamp\n",
      "import play.api.libs.functional.syntax._\n",
      "import play.api.libs.json._\n\n",
      "trait Formats {\n",
      "  implicit val rds: Reads[Timestamp] = (__ \\ \"time\").read[Long].map{ long => new Timestamp(long) }\n",
      "  implicit val wrs: Writes[Timestamp] = (__ \\ \"time\").write[Long].contramap{ (a: Timestamp) => a.getTime }\n",
      "  implicit val fmt: Format[Timestamp] = Format(rds, wrs)\n\n"
    )
    val formatsFilePath = Settings.outputDir + Settings.packageDir + "/Formats.scala"
    val formatsOut      = new PrintWriter(formatsFilePath , "UTF-8")
    val caseClassFilePath = Settings.outputDir + Settings.packageDir + "/TableRows.scala"

    try {
      for (line <- Source.fromFile(caseClassFilePath).getLines()) {
        if (line.startsWith("package")) {
          formatsOut.print(line + "\n\n")
          preset.foreach(presetLine => formatsOut.print(presetLine))
        } else {
          if (line.startsWith("case class")) {
            val caseClass = line.drop("case class ".length).split("Row")(0) + "Row"
            formatsOut.print("  implicit val " + caseClass + "Format = Json.format[" + caseClass + "]\n")
          }
        }
      }
      formatsOut.print("}")
    } finally {
      formatsOut.close()
    }
  }

}

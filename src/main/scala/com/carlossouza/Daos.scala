package com.carlossouza

import java.io.{File, PrintWriter}

import scala.collection.mutable
import scala.io.Source

/**
  * Created by carlossouza on 4/24/16.
  */
object Daos {

  def generate: Unit = {
    getTables.foreach(createDaoFile)
  }

  def getTables: List[String] = {
    val result: mutable.MutableList[String] = new mutable.MutableList[String]
    val caseClassFilePath = Settings.outputDir + Settings.packageDir + "/TableRows.scala"
    for (line <- Source.fromFile(caseClassFilePath).getLines()) {
      if (line.startsWith("case class")) {
        val className: String = line.split('(')(0).replace("case class ", "").replace("Row", "")
        result += className
      }
    }
    result.toList
  }

  def createDaoFile(className: String): Unit = {
    val packageName = if (Settings.playFramework) "dao" else Settings.packageName + ".dao"
    val modelsName = if (Settings.playFramework) "models" else Settings.packageName + ".models"
    val targetFile = if (Settings.playFramework) Settings.outputDir + "dao/" + className + "DAO.scala" else Settings.outputDir + Settings.packageDir.replace("/models", "/dao/") + className + "DAO.scala"
    if (Settings.playFramework) (new File(Settings.outputDir + "dao/")).mkdir() else (new File(Settings.outputDir + Settings.packageDir.replace("/models", "/dao"))).mkdir()
    val preset: Seq[String] = Seq(
      "package " + packageName + "\n\n",
      "import " + modelsName + ".{" + className + "Row, Tables}\n",
      "import javax.inject.Inject\n",
      "import scala.concurrent._\n",
      "import slick.driver.MySQLDriver\n",
      "import slick.driver.MySQLDriver.api._\n\n",
      "class " + className + "DAO @Inject() (db: MySQLDriver.backend.Database) {\n",
      "\t" + "def fetchAll(): Future[Seq[" + className + "Row]] = db.run(Tables." + className + ".result)\n",
      "\t" + "def count(): Future[Int] = db.run(Tables." + className + ".length.result)\n",
      "\t" + "def insert(newRow: " + className + "Row): Future[Int] = db.run((Tables." + className + " returning Tables." + className + ".map(_.id)) += newRow)\n",
      "\t" + "def save(maybeNewRow: " + className + "Row): Future[Int] = db.run(Tables." + className + ".insertOrUpdate(maybeNewRow))\n",
      "\t" + "def findById(id: Int): Future[Option[" + className + "Row]] = db.run(Tables." + className + ".filter(_.id === id).result.headOption)\n",
      "\t" + "def deleteById(id: Int): Future[Int] = db.run(Tables." + className + ".filter(_.id === id).delete)\n",
      "\t" + "def update(updatedRow: " + className + "Row): Future[Int] = db.run(Tables." + className + ".filter(_.id === updatedRow.id).update(updatedRow))\n",
      "}"
    )
    val fileOut = new PrintWriter(targetFile, "UTF-8")
    try {
      preset.foreach(presetLine => fileOut.print(presetLine))
    } finally {
      fileOut.close()
    }
  }

}

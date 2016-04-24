# SBT Slickgen plugin

This plugin auto-generates scala code to access database using *Typesafe Slick 3+*. It works with **scala-play** and **minimal-scala** projects.

## Installation

Add the following code in your ```projects/plugins.sbt``` file:

```scala
addSbtPlugin("com.carlossouza" % "sbt-slickgen" % "1.0.0")
```

Then, in your ```build.sbt``` file:

```scala
lazy val root = (project in file(".")).enablePlugins(SbtSlickgen)

libraryDependencies ++= Seq(
  "com.typesafe.play"   %% "play-json"            % "2.5.2",
  "com.google.inject"   % "guice"                 % "4.0",
  "mysql"               % "mysql-connector-java"  % "5.1.38",
  "com.typesafe.slick"  %% "slick"                % "3.1.1",
  "com.typesafe.slick"  %% "slick-codegen"        % "3.1.1",
  "com.typesafe.play"   %% "play-slick"           % "2.0.0",
  "org.slf4j"           % "slf4j-nop"             % "1.7.10"
)
```

Finally in your ```application.conf``` file (either on ```conf/``` or ```src/main/resources/```):

```nginx
slick.dbs.default.driver="slick.driver.MySQLDriver$"
slick.dbs.default.db.driver="com.mysql.jdbc.Driver"
slick.dbs.default.db.url="jdbc:mysql://localhost/mlg"
slick.dbs.default.db.user="root"
slick.dbs.default.db.password=""

localhost = {
  dataSourceClass = "slick.jdbc.DatabaseUrlDataSource"
  properties = {
    driver = "slick.driver.MySQLDriver$"
    url = "jdbc:mysql://localhost/mlg"
    user = "root"
    password = ""
  }
  numThreads = 5
  keepAliveConnection = true
}
```

Replace the parameters above with your specifics.

## Running

Now, in order to auto-generate the files to access the database using Slick, just enter:

```
sbt gen-tables gen-formats gen-daos
```

or each one separately.

## Testing

To check that your program worked, for example, if you are working on a **minimal-scala** project, just add this in your main file:

```scala
package com.example

import com.example.models.dao.CityDAO
import slick.driver.MySQLDriver.api._
import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object Hello extends App {

  val db = Database.forConfig("localhost")
  val cityDAO = new CityDAO(db)
  val f = cityDAO.fetchAll()

  println("Beginning...")

  f.map { result =>
    result.foreach { cityRow =>
      println("City: " + cityRow.city)
    }
  }

  Await.ready(f, Duration.Inf)
  Thread.sleep(50)
  println("Finishing...")
  db.close()

}
```

For more information, check [Slick documentation website](http://slick.typesafe.com/doc/3.1.1/) .
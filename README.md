################## README of ScalaProject #################

This repository contain the source code for our scala
project.

The aim is to have a simple application that parse three
csv files named country, airports and runways, and use
theirs informations to give to the user a query and report
function.

The query function permits the user to enter a country code
or country name to have a list of all airports and their
runways in this country.

The reports function gives the list of the ten countries
with the most airports, the ten with the less. The runways
type per country and finally the ten most common runway
latitude.

We used the scalafx library to make a simple GUI for this
application. Scalatest for the test suite and the assembly
plugin to generate a jar of your application.

The database interface and loader is in the ScalaProj.scala
file, and the gui part in the gui.scala file.

The database interface will load the three files, and construct
hashMap for quick response when given a country code or name
from the query. The reports is generated one time at the start
of the application.

It is easy to add informations or modify the display of
country, airport and runway for the query function, all the printer
are in each respective case class and contain the complete line
from the csv.

######## Build ######

To build this project, you need sbt, on linux you probably will have to install
java-openjfx or its equivalence from your package manager.

While in the repository launch sbt assembly, this will generate a jar file
in target/scala-2.12/ScalaProj-assembly-1.0.0.jar. You can launch it
with java -jar ScalaProj-assembly-1.0.0.jar.

####################

Authors:

Timothee Brenet (brenet_t)
Nacim Arrahmane (arrahm_n)

###########################################################

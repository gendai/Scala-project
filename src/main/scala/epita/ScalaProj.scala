package epita

import collection.mutable.HashMap
import scalafx.scene.control.TextArea
import scala.io.Source
import scala.io.BufferedSource

case class DatabaseInterface() {
  import DatabaseInterface._

  def MakeQuery(str: String): List[String] = Query(str)
  def MakeReports(): List[String] = Reports()
}

class CSVLine (_line : String) {

  val line = _line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)", -1)
                  .map(el => el.replace("\"", ""))

  def getColumnValue(index : Int): Option[String] =
  {
    line.size match
    {
      case n if n <= index => None
      case n if n > index && line(index) == "" => None
      case n if n > index && line(index) != "" => Some(line(index))
    }
  }
}

case class Country (_countryLine : String) extends CSVLine(_countryLine) {

  def Code(): Option[String] =
  {
    getColumnValue(1)
  }

  def Name(): Option[String] =
  {
    getColumnValue(2)
  }
}

case class Airport (_airportLine : String) extends CSVLine(_airportLine) {

   def Id(): Option[String] =
  {
    getColumnValue(1)
  }

  def CountryCode(): Option[String] =
  {
    getColumnValue(8)
  }

   def Print(): String =
  {
    s"    - Airport id is ${line(0)}, ident is ${line(1)}, airport type : ${line(2)}, name is ${line(3)} :"
  }
}

case class Runway (_runwayLine : String) extends CSVLine(_runwayLine) {

  def AirportRef(): Option[String] =
  {
    getColumnValue(2)
  }

  def Latitude(): Option[String] =
  {
    getColumnValue(8)
  }

  def Type(): Option[String] =
  {
    getColumnValue(5)
  }

  def HasLatitude(): Boolean =
  {
    line.size match
    {
      case n if n < 8 => false
      case n if n > 8 && line(8) != "" => true
      case n if n > 8 && line(8) == "" => false
    }
  }

  def HasType(): Boolean =
  {
    line.size match
    {
      case n if n < 5 => false
      case n if n > 5 && line(5) != "" => true
      case n if n > 5 && line(5) == "" => false
    }
  }

  def Print(): String =
  {
     s"        - Runway id is ${line(0)}, length of ${line(3)} and width of ${line(4)}, runway surface type : ${line(5)}"
  }
}

abstract class CSVFile(_fileName : String) {

  val fileName = _fileName
  val content : List[String] = Source.fromInputStream(getClass().getClassLoader()
                                                                .getResourceAsStream(fileName), enc="UTF8")
                                     .getLines
                                     .mkString("\n")
                                     .split("\n")
                                     .drop(1)
                                     .toList
}

case class CountryFile(_fileName : String) extends CSVFile(_fileName) {

  val countryList = content.map{el => Country(el)}

  val countriesInfosMap: HashMap[String, Country] = HashMap()
  val countriesNameIdMap: HashMap[String, String] = HashMap()

  def SetUp(): Unit =
  {
    countryList.map{country => countriesInfosMap.put(country.Code().get, country)
                               countriesNameIdMap.put(country.Name().get, country.Code().get)
    }
  }

  def CountryFromId(id : String): Option[Country] =
  {
    countriesInfosMap.get(id)
  }

  def CountryFromName(name : String): Option[Country] =
  {
     val codeCountry = countriesNameIdMap.get(name)
     codeCountry.isEmpty match
     {
      case true => None
      case false => CountryFromId(codeCountry.get)
     }
  }

  SetUp()
}

case class AirportFile(_fileName : String) extends CSVFile(_fileName) {

  val airportList = content.map{el => Airport(el)}

  val countriesAirportsMap: Map[String, List[Airport]] = airportList.groupBy{airport => airport.CountryCode().get}

  def GetAirportsFromCountryCode(code : String): Option[List[Airport]] =
  {
    countriesAirportsMap.get(code)
  }

}

case class RunwayFile(_fileName : String) extends CSVFile(_fileName) {

  val runwayList = content.map{el => Runway(el)}

  val airportsRunwaysMap: Map[String, List[Runway]] = runwayList.groupBy{runway => runway.AirportRef().get}

  def GetRunwaysFromAirportRef(ref : String): Option[List[Runway]] =
  {
    airportsRunwaysMap.get(ref)
  }
}

object DatabaseInterface {

  val countryFile = CountryFile("countries.csv")
  val airportFile = AirportFile("airports.csv")
  val runwayFile  = RunwayFile("runways.csv")

  val CountryAirportsRunwaysMap: HashMap[String, List[(Airport, List[Runway])]] = HashMap()
  CountryAirportsRunwaysInit()

  def CountryAirportsRunwaysInit(): Unit =
  {
     countryFile.countryList.map{country =>
         val listAirports = airportFile.GetAirportsFromCountryCode(country.Code().get)
         listAirports.isEmpty match
         {
           case false => {
               val subAirportsList = listAirports.get.map{airport =>
                     val runList = runwayFile.GetRunwaysFromAirportRef(airport.Id().get)
                     runList.isEmpty match
                     {
                       case true  => (airport, List())
                       case false => (airport, runList.get)
                     }
                   }
               CountryAirportsRunwaysMap.put(country.Code().get, subAirportsList)
           }
           case true => CountryAirportsRunwaysMap.put(country.Code().get, List())
         }
      }
  }

  /*Query input manager*/
  def GetCountryFromInput(input: String): Option[Country] =
  {
    input.length match
    {
      case 2 => countryFile.CountryFromId(input)
      case x => countryFile.CountryFromName(input)
    }
  }

  /*Query func*/
  def Query(country_info: String): List[String] =
  {
    val foundCountry:Option[Country] = GetCountryFromInput(country_info.replace("\"", ""))
    foundCountry.isEmpty match
    {
      case true  => List(s"Country code or name not found : ${country_info}")
      case false =>
        {
          val header:String = s"List of airports and their runways in ${foundCountry.get.Name().get} :\n"
          val res:List[String] = CountryAirportsRunwaysMap.get(foundCountry.get.Code().get)
                                   .get
                                   .flatMap{airportAndRunways =>
                                         val airportLine:String = airportAndRunways._1.Print() + "\n"
                                         airportLine::airportAndRunways._2.map{runway => runway.Print() + "\n"}
                                    }
          header::res
        }
    }
  }

  /*Reports func*/
  def Reports(): List[String] =
  {
    val countryAirportNum = countryFile.countryList.map{country =>
                                                        val aiportNum = airportFile.GetAirportsFromCountryCode(country.Code().get)
                                                        aiportNum.isEmpty match
                                                        {
                                                          case true  => (country, 0)
                                                          case false => (country, aiportNum.get.size)
                                                        }}
                                         .sortWith(_._2 > _._2)

    val header1:String = "The ten countries with the more airports are :\n"
    val mostAirports:List[String] = (header1::countryAirportNum.take(10).map{el => s"    - ${el._1.Name().get} with ${el._2}\n"})


    val header2:String = "The ten countries with the less airports are :\n"
    val lessAirports:List[String] = (header2::countryAirportNum.takeRight(10).reverseMap{el => s"    - ${el._1.Name().get} with ${el._2}\n"})

    val header3:String = "The runways type per country :\n"

    val runwPerCountry:List[String] = countryFile.countryList.flatMap{country =>
      val header4:String = s"    - ${country.Name().get} :\n"
      val runwPerNum:List[String] = CountryAirportsRunwaysMap.get(country.Code().get)
                                                                 .get
                                                                 .flatMap{x => x._2}
                                                                 .filter{runway => runway.HasType()}
                                                                 .groupBy{runway => runway.Type()}
                                                                 .mapValues(_.size)
                                                                 .toList
                                                                 .map(res => s"        - ${res._1.get} (nb = ${res._2})\n")
      header4::runwPerNum
    }
    val runwPerCountryNum = header3::runwPerCountry

    val header5:String = "The ten most common runway latitude :\n"
    val mostCommonLat:List[String] = (header5::runwayFile.runwayList.filter{runway => runway.HasLatitude()}
                 .groupBy{runway => runway.Latitude()}
                 .mapValues(_.size)
                 .toList
                 .sortWith(_._2 > _._2)
                 .take(10)
                 .map(res => s"    - ${res._1.get} (nb = ${res._2})\n"))

    mostAirports:::lessAirports:::runwPerCountryNum:::mostCommonLat
  }
}

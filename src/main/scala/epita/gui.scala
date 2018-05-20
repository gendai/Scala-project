package epita

import scalafx.Includes._
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.event.ActionEvent
import scalafx.geometry.Insets
import scalafx.scene.Scene
import scalafx.scene.control.{Label, TextArea, TextField, Separator, SplitPane}
import scalafx.scene.layout.{ColumnConstraints, Priority, RowConstraints, VBox, HBox, BorderPane}
import epita.DatabaseInterface._
import scalafx.geometry.Orientation

object ScalaFXApp extends JFXApp {

  val di:DatabaseInterface = new DatabaseInterface

  val QueryArea = new TextArea {
    editable = false
    focusTraversable = false
  }

  val ReportArea = new TextArea {
    editable = false
    focusTraversable = false
  }

  val QueryLabel = new Label {
      text = "Enter country name or code:"
    }

  val QueryInput = new TextField {
    text = ""
    onAction = (event: ActionEvent) => {
      val input = text()
      QueryArea.text =  di.MakeQuery(input).mkString
      text() = ""
    }
  }

  val bottomPane = new SplitPane{
    orientation = Orientation.Horizontal
    items ++= List(QueryArea, ReportArea)
  }

  val contentPane = new VBox {
    spacing = 6
    padding = Insets(10)
    vgrow = Priority.Always
    hgrow = Priority.Always
    children = List(
        new HBox{ spacing = 6.0
                  children = List(QueryLabel, QueryInput)
                },
        new Separator(),
        bottomPane
    )
  }

  stage = new PrimaryStage {
    title = "Scala project SCIA 2019"
    scene = new Scene {
    root = new BorderPane {
        center = contentPane
      }
    }
  }

  ReportArea.text = di.MakeReports().mkString
}

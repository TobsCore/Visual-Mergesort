package example

import scalafx.scene.control.{Button, TextField}
import javafx.scene.canvas.Canvas
import javafx.scene.layout.BorderPane

import scalafxml.core.macros.sfxml

/**
  * Created by Patrick König on 19.08.16.
  */
@sfxml
class MyController (
  private val button: Button,
  private val clickTimeTextField: TextField) {

  def countClicks(): Unit = {
    val content = clickTimeTextField.getText
    val timesClicked = if (content.equals("")) 1 else content.toInt + 1
    clickTimeTextField.setText(timesClicked.toString)
    //var borderPane: BorderPane = null
    val canvas: Canvas = ExampleApplication.stage.scene.value.getRoot match {
      case a: BorderPane =>  a.getCenter match {
        case b: Canvas => b
        case _ => throw new ClassCastException("Cannot cast to Canvas")
      }
      case _ => throw new ClassCastException("Cannot cast to Border Pane")
    }

    canvas.getGraphicsContext2D.fillRect(10,10,10,50)



  }
 }









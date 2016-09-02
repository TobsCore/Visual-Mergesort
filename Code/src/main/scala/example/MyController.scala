package example

import scalafx.scene.control.{Button, Label, TextField}
import javafx.scene.canvas.Canvas
import javafx.scene.layout.BorderPane
import javafx.scene.paint.Color

import scalafxml.core.macros.sfxml

/**
  * Created by Patrick König on 19.08.16.
  */
@sfxml
class MyController (
  private val button: Button,
  private val clickTimeTextField: TextField,
  private val infolabel: Label) {

  val amountOfShit = 20

  def countClicks(): Unit = {
    val content = clickTimeTextField.getText


    /* val timesClicked = if (content.equals("")) 1 else content.toInt + 1
    clickTimeTextField.setText(timesClicked.toString)*/
    //var borderPane: BorderPane = null
    val canvas: Canvas = ExampleApplication.stage.scene.value.getRoot match {
      case a: BorderPane => a.getCenter match {
        case b: Canvas => b
        case _ => throw new ClassCastException("Cannot cast to Canvas")
      }
      case _ => throw new ClassCastException("Cannot cast to Border Pane")
    }
    println("Hello World")


      def getUserInput = () => {try {
      content.toInt
    }
    catch {
      case e: NumberFormatException =>  0
    }}

    val userSetLimit: Integer = getUserInput()

    if (userSetLimit >= 1 && userSetLimit <= 15) {
      canvas.getGraphicsContext2D.clearRect(0,0,400,300)
      infolabel.text_=("")
      for (i <- 1 to userSetLimit) {
        canvas.getGraphicsContext2D.setFill(Color.color(Math.random(),Math.random(),Math.random()))
        val random = Math.random()*100
        canvas.getGraphicsContext2D.fillRect(i * 20, 100-random , 10, random)
      }
    } else {
      infolabel.text_=("Please enter a number between 1 and 15");
    }




  }
 }









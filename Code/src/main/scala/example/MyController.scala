package example

import scalafx.scene.control.{Button, Label, TextField}
import javafx.scene.layout.{BorderPane, Pane}
import javafx.scene.paint.Color
import scalafx.scene.shape.Rectangle

import scalafxml.core.macros.sfxml

/**
  * Created by Patrick König on 19.08.16.
  */
@sfxml
class MyController (
  private val button: Button,
  private val clickTimeTextField: TextField,
  private val infolabel: Label) {



  def countClicks(): Unit = {

    val content = clickTimeTextField.getText


    val canvas: Pane = ExampleApplication.stage.scene.value.getRoot match {
      case a: BorderPane => a.getCenter match {
        case b: Pane => b
        case _ => throw new ClassCastException("Cannot cast to Pane")
      }
      case _ => throw new ClassCastException("Cannot cast to BorderPane")
    }
    


      def getUserInput = () => {try {
      content.toInt
    }
    catch {
      case e: NumberFormatException =>  0
    }}

    val userSetLimit: Integer = getUserInput()

    if (userSetLimit >= 1 && userSetLimit <= 15) {
      infolabel.text_=("")
      canvas.getChildren.clear()

      for (i <- 1 to userSetLimit) {


       // canvas.getGraphicsContext2D.setFill(Color.color(Math.random(),Math.random(),Math.random()))
        val random = Math.random()*100
        var rectangle = new Rectangle(new javafx.scene.shape.Rectangle(i * 20, 100-random, 10, random))
        rectangle.id = "jo" + i
        canvas.getChildren.add(rectangle)
      }
    } else {
      infolabel.text_=("Please enter a number between 1 and 15");
    }

    canvas.lookup("#jo1").relocate(300,100)






  }
 }









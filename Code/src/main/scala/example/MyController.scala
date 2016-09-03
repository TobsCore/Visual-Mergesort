package example

import javafx.scene.layout.{BorderPane, Pane}
import scalafx.animation.FadeTransition
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.image.ImageView
import scalafx.scene.shape.Rectangle
import scalafx.util.Duration
import scalafxml.core.macros.sfxml

/**
  * Created by Patrick König on 19.08.16.
  */
@sfxml
class MyController (
  private val button: Button,
  private val clickTimeTextField: TextField,
  private val infoLabel: Label,
  private val infoImage: ImageView) {




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
      hideInfoMessageOnCorrectInput()
      canvas.getChildren.clear()

      for (i <- 1 to userSetLimit) {
        val random = Math.random()*100
        var rectangle = new Rectangle(new javafx.scene.shape.Rectangle(i * 20, 100 - random, 10, random))
        rectangle.id = "jo" + i
        canvas.getChildren.add(rectangle)
      }
    } else {
      displayInfoMessageOnError()
    }

  }

  var isPermanentlySet = false

  def displayInfoMessageOnError(): Unit = {
    infoLabel.opacity = 1
    isPermanentlySet = true
  }

  def hideInfoMessageOnCorrectInput(): Unit = {
    infoLabel.opacity = 0
    isPermanentlySet = false
  }

  def displayInfoMessage(): Unit = {
    if(!isPermanentlySet) {
      val fadeIn: FadeTransition = new FadeTransition {
        duration = Duration(150)
        fromValue = 0
        toValue = 1
        node = infoLabel
      }
      fadeIn.play()
    }
  }



  def hideInfoMessage(): Unit ={
    if(!isPermanentlySet) {
      val fadeOut: FadeTransition = new FadeTransition {
        duration = Duration(150)
        fromValue = 1
        toValue = 0
        node = infoLabel
      }
      fadeOut.play()

    }
  }

}









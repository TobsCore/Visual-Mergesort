package example

import java.io.IOException
import javafx.scene.layout.{BorderPane, Pane}

import scala.concurrent.forkjoin.ThreadLocalRandom
import scalafx.Includes._
import scalafx.animation.FadeTransition
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, TextField}
import scalafx.scene.image.ImageView
import scalafx.scene.shape.Rectangle
import scalafx.stage.Stage
import scalafx.util.Duration
import scalafxml.core.macros.sfxml
import scalafxml.core.{FXMLView, NoDependencyResolver}

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
        val min = 1
        val max = 10
        val random: Int = ThreadLocalRandom.current.nextInt(min, max + 1)
        var sortElement = new SortElement(random, i * 20, 100 - random)
        canvas.getChildren.add(sortElement)
      }
    } else {
      displayInfoMessageOnError()
    }

  }
var aboutStage: Stage = null;

  def openAboutDialog(): Unit = {
    if (aboutStage == null) {
      val resource = getClass.getResource("/AboutDialog.fxml")
      if (resource == null) {
        //TODO: Externalize String
        throw new IOException("Cannot load resource: AboutDialog.fxml")
      }

      val root = FXMLView(resource, NoDependencyResolver)

      aboutStage = new Stage() {
        title = "About Dialog"
        scene = new Scene(root)
        resizable = false
      }
    }

    if (!aboutStage.showing()) {
      aboutStage.show()
    } else {
      aboutStage.requestFocus()
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









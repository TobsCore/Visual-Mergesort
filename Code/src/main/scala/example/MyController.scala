package example

import java.io.IOException

import scalafx.scene.layout.{BorderPane, Pane}
import scala.concurrent.forkjoin.ThreadLocalRandom
import scalafx.Includes._
import scalafx.animation.FadeTransition
import scalafx.beans.binding.Bindings
import scalafx.beans.property.DoubleProperty
import scalafx.scene.Scene
import scalafx.scene.control.{Button, Label, Slider, TextField}
import scalafx.scene.image.ImageView
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.Text
import scalafx.stage.Stage
import scalafx.util.Duration
import scalafx.util.converter.{DoubleStringConverter, NumberStringConverter}
import scalafxml.core.macros.sfxml
import scalafxml.core.{FXMLView, NoDependencyResolver}

/**
  * Created by Patrick König on 19.08.16.
  */
@sfxml
class MyController (
  private val generateButton: Button,
  private val runButton: Button,
  private val amountOfElementsSlider: Slider,
  private val amountOfThreadsSlider: Slider,
  private val amountOfElementsLabel: Text,
  private val amountOfThreadsLabel: Text,
  private val borderPane: BorderPane,
  private val pane: Pane
                   ) {

  var aboutStage: Stage = null;
  val originalPattern = "%.0f"
  amountOfElementsSlider.labelFormatter = new DoubleStringConverter
  amountOfElementsLabel.text <== amountOfElementsSlider.value.asString(originalPattern)
  amountOfThreadsSlider.labelFormatter = new DoubleStringConverter
  amountOfThreadsLabel.text <== amountOfThreadsSlider.value.asString(originalPattern)


  def generateNumbers(): Unit = {
    val canvas: Pane = pane
    val userSetLimit: Integer = amountOfElementsSlider.value.toInt

      canvas.getChildren.clear()

      for (i <- 1 to userSetLimit) {
        val min = 1
        val max = 99
        val random: Int = ThreadLocalRandom.current.nextInt(min, max + 1)
        var sortElement = new SortElement(random, i * 20, 100 - random)
        canvas.getChildren.add(sortElement)
      }

  }


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


}









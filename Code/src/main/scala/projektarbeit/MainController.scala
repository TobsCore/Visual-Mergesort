package projektarbeit

import java.io.IOException

import scala.concurrent.forkjoin.ThreadLocalRandom
import scalafx.Includes._
import scalafx.scene.control._
import scalafx.scene.layout.{BorderPane, Pane}
import scalafx.scene.text.Text
import scalafx.scene.{Group, Scene}
import scalafx.stage.Stage
import scalafx.util.converter.DoubleStringConverter
import scalafxml.core.macros.sfxml
import scalafxml.core.{FXMLView, NoDependencyResolver}

/**
  * Created by Patrick König on 19.08.16.
  */
@sfxml
class MainController(
                      private val generateButton: Button,
                      private val runButton: Button,
                      private val amountOfElementsSlider: Slider,
                      private val amountOfThreadsSlider: Slider,
                      private val amountOfElementsLabel: Text,
                      private val amountOfThreadsLabel: Text,
                      private val borderPane: BorderPane,
                      private val scrollPane: ScrollPane,
                      private val pane: Pane) {

  var aboutStage: Stage = _
  val originalPattern = "%.0f"
  amountOfElementsSlider.labelFormatter = new DoubleStringConverter
  amountOfElementsLabel.text <== amountOfElementsSlider.value.asString(originalPattern)
  amountOfThreadsSlider.labelFormatter = new DoubleStringConverter
  amountOfThreadsLabel.text <== amountOfThreadsSlider.value.asString(originalPattern)


  def generateNumbers(): Unit = {

    // Setting up the canvas
    pane.getChildren.clear()
    val elementGroup = new Group()

    // Get the selected amount of elements
    val userSetLimit: Integer = amountOfElementsSlider.value.toInt

    // Place the elements on the pane
    for (elementID <- 1 to userSetLimit) {
      val min = MainController.DefaultMinimumNumber
      val max = MainController.DefaultMaximumNumber
      val random: Int = ThreadLocalRandom.current.nextInt(min, max + 1)
      val sortElement = new SortElement(random, elementID * SortElement.wholeElementWidth, SortElement.maxHeight - random)
      elementGroup.getChildren.add(sortElement)
    }
    pane.getChildren.add(elementGroup)
    pane.setPrefWidth(elementGroup.getBoundsInParent.getWidth)
  }


  def openAboutDialog(): Unit = {
    if (aboutStage == null) {
      val aboutDialogFXML: String = "/AboutDialog.fxml"
      val resource = getClass.getResource(aboutDialogFXML)
      if (resource == null) {
        throw new IOException(s"Cannot load resource: $aboutDialogFXML")
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

object MainController {
  val DefaultMinimumNumber = 1
  val DefaultMaximumNumber = 99
}









package projektarbeit

import java.io.IOException

import eu.lestard.advanced_bindings.api.MathBindings
import projektarbeit.ElementOrder.EnumVal

import scala.concurrent.forkjoin.ThreadLocalRandom
import scalafx.Includes._
import scalafx.animation.SequentialTransition
import scalafx.beans.property.BooleanProperty
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.layout.{AnchorPane, BorderPane, Pane}
import scalafx.scene.text.Text
import scalafx.scene.{Group, Node, Scene}
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
                      private val playPauseMenu: MenuItem,
                      private val playPauseButton: Button,
                      private val playbackSpeed: Slider,
                      private val playbackSpeedLabel: Label,
                      private val consoleLog: TextArea,
                      private val borderPane: BorderPane,
                      private val scrollPane: ScrollPane,
                      private val pane: Pane,
                      private val actionBar: AnchorPane) {


  val defaultMinimumNumber = 1
  val defaultMaximumNumber = 99
  var aboutStage: Stage = _
  val originalPattern = "%.0f"
  var transition: SequentialTransition = _
  var isPlaying: BooleanProperty = BooleanProperty(false)
  amountOfElementsSlider.labelFormatter = new DoubleStringConverter
  amountOfElementsLabel.text <== amountOfElementsSlider.value.asString(originalPattern)
  amountOfThreadsSlider.labelFormatter = new DoubleStringConverter
  amountOfThreadsLabel.text <== amountOfThreadsSlider.value.asString(originalPattern)

  playbackSpeed.labelFormatter = new DoubleStringConverter
  playbackSpeedLabel.text <== MathBindings.pow(2.0, playbackSpeed.value).asString("%.2f")

  runButton.defaultButton <== !generateButton.defaultButton
  playPauseMenu.text <==> playPauseButton.text

  /*consoleLog {
    //consoleLog.positionCaret(100)
    consoleLog.scrollTop() = Double.MaxValue
  }

  consoleLog.scrollTop.onChange {
    if (consoleLog.scrollTop() == 0.0) {
      println("Moving from zero to hero")
      consoleLog.scrollTop() = Double.MaxValue
    }
  }*/
  def toggleActionBar(): Unit = {
    if (borderPane.top() != null) {
      borderPane.top() = null
    } else {
      borderPane.top() = actionBar
    }
  }
  def toggleLogging(): Unit = {
    if (borderPane.bottom() != null) {
      borderPane.bottom() = null
    } else {
      borderPane.bottom() = consoleLog
    }
  }

  def changeButtonActivationToRun(): Unit = {
    runButton.disable() = false
    generateButton.defaultButton() = false
  }

  def generateRandomNumbers(): Unit = {
    generateNumbers(ElementOrder.Random)
    changeButtonActivationToRun()
  }

  def generateOrderedNumbers(): Unit = {
    generateNumbers(ElementOrder.Ordered)
    changeButtonActivationToRun()
  }

  def generateInverseNumbers(): Unit = {
    generateNumbers(ElementOrder.Inverse)
    changeButtonActivationToRun()
  }

  def createCustomElements(): Unit = {
    createCustomElements(None)
    changeButtonActivationToRun()
  }

  def createCustomElements(preselectedValue: Option[String]): Unit = {

    val preselectedValueText = preselectedValue.getOrElse("")
    val dialog = new TextInputDialog(preselectedValueText) {
      title = "Create Custom Elements"
      headerText = "Create a custom list of elements"
      contentText = "Enter the values:"
    }

    val result = dialog.showAndWait.getOrElse("")
    val resultMap = result.split(",").map(_.trim)

    try {
      if (!result.equals("")) {
        val elementList: List[Int] = resultMap.map(_.toInt).toList
        placeElementsOnPane(elementList)
      }
    } catch {
      case ex: NumberFormatException => new Alert(AlertType.Error) {
        title = "Input error"
        headerText = "Input error!"
        contentText = "Cannot convert this to a list of elements"
      }.showAndWait
        createCustomElements(Some(result))
    }
  }

  def generateNumbers(elementOrder: EnumVal): Unit = {

    // Get the selected amount of elements
    val amountOfElements: Integer = amountOfElementsSlider.value.toInt

    val randomNumberList = List.tabulate(amountOfElements)(_ => ThreadLocalRandom.current.nextInt(defaultMinimumNumber, defaultMaximumNumber + 1))
    val elements = elementOrder match {
      case ElementOrder.Random => randomNumberList
      case ElementOrder.Ordered => randomNumberList.sorted
      case ElementOrder.Inverse => randomNumberList.sorted.reverse
      case _ => throw new IllegalArgumentException(s"$elementOrder is not supported")
    }

    placeElementsOnPane(elements)
  }


  def placeElementsOnPane(elements: List[Int]): Unit = {

    cleanEverythingUp
    // Stop the running transition, and then place the new elements on the pane
    if (transition != null) transition.stop()
    // Setting up the canvas
    val elementGroup = new Group()
    elementGroup.id() = "level-1"
    // Place the elements on the pane
    for ((value, position) <- elements.zipWithIndex) {
      val xPos: Double = (position * SortElement.wholeElementWidth).toDouble
      val yPos: Double = (SortElement.maxHeight - value).toDouble
      val sortElement = new SortElement(value, xPos, yPos)
      sortElement.id() = s"sortElement-$position"
      elementGroup.children.add(sortElement)
    }

    elementGroup.translateX <== scrollPane.getScene.getWindow.width/2 - elementGroup.getBoundsInParent.getWidth/2

    elementGroup.id = "level-0"

    consoleLog.text() = ""
    pane.children.clear()
    pane.children.add(elementGroup)
    pane.setPrefWidth(elementGroup.getBoundsInParent.getWidth)
    scrollPane.vvalue = 0.0

    val depth = Math.ceil(Math.log(elementGroup.children.size) / Math.log(2)) * 2 + 1
    //TODO: Die 130 auslagern, denn diese ist eigentlich SortElementsController.moveDownByPixel
    pane.setPrefHeight(depth * 130)
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

  def runSorting():Unit = {
    runButton.disable = true
    isPlaying() = true
    playPauseMenu.disable = false
    playPauseButton.disable = false
    val elementGroup: javafx.scene.Group = pane.children.get(0).asInstanceOf[javafx.scene.Group]

    val sorter = new SortElementsController(pane, consoleLog)
    sorter.sort(elementGroup, 0)
    transition = sorter.getSequence
    transition.rate <== MathBindings.pow(2.0, playbackSpeed.value)

    transition.play()
    transition.onFinished = {
      event: ActionEvent =>
        cleanEverythingUp
    }

  }

  def cleanEverythingUp: Unit = {
    isPlaying() = false
    playPauseButton.text = "Pause"
    playPauseButton.disable = true
    playPauseMenu.disable = true
  }

  def playPauseSequence(): Unit = {
    if (this.isPlaying()) {
      transition.pause()
      playPauseButton.text = "Play"
    } else {
      transition.play()
      playPauseButton.text = "Pause"
    }

    this.isPlaying() = !this.isPlaying()
  }

}


object ElementOrder {
  sealed trait EnumVal
  case object Random extends EnumVal
  case object Ordered extends EnumVal
  case object Inverse extends EnumVal
  val elementOrder = Seq(Random, Ordered, Inverse)
}

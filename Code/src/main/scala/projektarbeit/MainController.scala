package projektarbeit

import java.awt.Desktop
import java.io.IOException
import java.net.URI

import eu.lestard.advanced_bindings.api.MathBindings
import projektarbeit.ElementOrder.EnumVal

import scala.concurrent.forkjoin.ThreadLocalRandom
import scalafx.Includes._
import scalafx.animation.SequentialTransition
import scalafx.application.Platform
import scalafx.beans.property.BooleanProperty
import scalafx.event.ActionEvent
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control._
import scalafx.scene.layout.{AnchorPane, BorderPane, Pane}
import scalafx.scene.text.Text
import scalafx.scene.{Group, Scene}
import scalafx.stage.Stage
import scalafx.util.converter.DoubleStringConverter
import scalafxml.core.macros.sfxml
import scalafxml.core.{FXMLView, NoDependencyResolver}

/**
  * Created by Patrick König & Tobias Kerst on 19.08.16.
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
  var transition: List[SequentialTransition] = _
  var isPlaying: BooleanProperty = BooleanProperty(false)
  amountOfElementsSlider.labelFormatter = new DoubleStringConverter
  amountOfElementsLabel.text <== amountOfElementsSlider.value.asString(originalPattern)
  amountOfThreadsSlider.labelFormatter = new DoubleStringConverter
  amountOfThreadsLabel.text <== amountOfThreadsSlider.value.asString(originalPattern)

  playbackSpeed.labelFormatter = new DoubleStringConverter
  playbackSpeedLabel.text <== MathBindings.pow(2.0, playbackSpeed.value).asString("%.2f")

  runButton.defaultButton <== !generateButton.defaultButton
  playPauseMenu.text <==> playPauseButton.text
  playPauseMenu.disable <==> playPauseButton.disable


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

  def quitApplication(): Unit = {
    Platform.exit()
    System.exit(0)
  }

  def openHelp(): Unit = {
    if (Desktop.isDesktopSupported) {
      Desktop.getDesktop.browse(new URI("https://github.com/TobsCore/Visual-Mergesort"))
    }
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
        if (elementList.exists(number => number > 99 || number <= 0)) {
          throw new IllegalArgumentException
        }
        placeElementsOnPane(elementList)
      }
    } catch {
      case ex: NumberFormatException => new Alert(AlertType.Error) {
        title = "Input error"
        headerText = "Input error!"
        contentText = "Cannot convert this to a list of elements"
      }.showAndWait
        createCustomElements(Some(result))
      case ex: IllegalArgumentException => new Alert(AlertType.Error) {
        title = "Input error"
        headerText = "Input error!"
        contentText = "Numbers must be between 1 and 99"
      }.showAndWait
        createCustomElements(Some(result))
    }
  }

  def generateNumbers(elementOrder: EnumVal): Unit = {

    // Get the selected amount of elements
    val amountOfElements: Integer = amountOfElementsLabel.text().toInt

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
    if (transition != null) transition.foreach(_.stop)
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

    pane.minWidth <== pane.getScene.getWindow.width - 40
    pane.setPrefWidth(elementGroup.getBoundsInParent.getWidth * 2)
    elementGroup.translateX <== pane.width/2 - elementGroup.getBoundsInParent.getWidth/2

    elementGroup.id = "level-0"

    consoleLog.text() = ""
    pane.children.clear()
    pane.children.add(elementGroup)
    scrollPane.vvalue = 0.0

    val depth = Math.ceil(Math.log(elementGroup.children.size) / Math.log(2)) * 2 + 2 - (amountOfThreadsLabel.text()
      .toInt - 1)
    pane.setPrefHeight(depth * SortElementsController.moveDownByPixel + SortElementsController.textOffset)
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

  def runSorting(): Unit = {
    runButton.disable() = true
    isPlaying() = true
    playPauseButton.disable() = false
    val elementGroup: javafx.scene.Group = pane.children.get(0).asInstanceOf[javafx.scene.Group]
    val amountOfThreads = amountOfThreadsLabel.text().toInt

    val sorter = new SortElementsController(pane, consoleLog, elementGroup, amountOfThreads)
    sorter.maxDepth = Math.ceil(Math.log(elementGroup.children.size) / Math.log(2)) * 2 + 1
    sorter.run()

    transition = sorter.sequences
    transition.foreach(_.rate <== MathBindings.pow(2.0, playbackSpeed.value))

    sorter.play()



    transition(1).onFinished = {
      event: ActionEvent =>
        if (amountOfThreads > 1) {
          sorter.merge(sorter.finalGroups(0), sorter.finalGroups(1), 0, 3)
        }
        sorter.playFinalMerge()
        cleanEverythingUp
    }

  }

  def cleanEverythingUp: Unit = {
    isPlaying() = false
    playPauseButton.text = "Pause"
    playPauseButton.disable() = true
  }

  def playPauseSequence(): Unit = {
    if (this.isPlaying()) {
      transition.foreach(_.pause)
      playPauseButton.text = "Play"
      playPauseButton.disable() = false
      transition.foreach(_.rate.unbind())
    } else {
      transition.foreach(_.play)
      playPauseButton.text = "Pause"
      playPauseButton.disable() = false
      transition.foreach(_.rate <== MathBindings.pow(2.0, playbackSpeed.value))
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

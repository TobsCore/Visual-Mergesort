package projektarbeit

import javafx.scene.control.ScrollPane

import akka.actor.{ActorSystem, Props}
import projektarbeit.Part.{EnumVal, Left, Right}

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.animation.{SequentialTransition, Timeline}
import scalafx.beans.binding.NumberBinding
import scalafx.event.ActionEvent
import scalafx.scene.Group
import scalafx.scene.control.TextArea
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color

/**
  * Created by Patrick König on 08.09.16.
  */
class SortElementsController(val pane: Pane, val consoleLog: TextArea, val initialGroup: Group, val amountOfThreads: Int) {


  val initialGroupElements: List[SortElement] = initialGroup.children.toList.asInstanceOf[List[SortElement]]
  val amountOfElementsPerThread: Int = Math.ceil(initialGroupElements.size / amountOfThreads.toDouble).toInt
  val system = ActorSystem("Sorting")

  def calculatedXPosition(index: Int, group: Group): NumberBinding = {
    amountOfThreads match {
      case 1 => initialGroup.translateX + 0
      case 2 => {
        index match {
          case 0 => group.children.toList.asInstanceOf[List[SortElement]].foreach(_.changeColor(Color.Cyan)); initialGroup.translateX - group.getBoundsInParent.getWidth / 2 + SortElement.width / 2
          case 1 => group.children.toList.asInstanceOf[List[SortElement]].foreach(_.changeColor(Color.Red)); initialGroup.translateX + initialGroup.getBoundsInParent.getWidth / 2 - group.getBoundsInParent.getWidth / 2 - SortElement.width / 2
        }
      }
      case number => throw new IllegalArgumentException(s"Cannot run on more than 2 Threads! You tried to run it on $number threads")
    }
  }

  def createThread(index: Int, list: List[SortElement]): Unit = {
    val group = new Group() {
      children.addAll(list.asJava)
      id = s"thread-${index}"
      opacity = 1.0
    }

    group.translateX <== calculatedXPosition(index, group)
    group.translateY() = initialGroup.translateY() + Sorter.moveDownByPixel
    pane.children.add(group)

    val sortActor = system.actorOf(Props(new Sorter(group, pane, consoleLog)), name = "sortActor")

    sortActor ! "startSorting"

    //system.terminate()
  }

  def run(): Unit = {
    val threadElements = initialGroupElements.grouped(amountOfElementsPerThread)
    for ((list, index) <- threadElements.zipWithIndex) {
      createThread(index, list.map(_.duplicate))
    }
  }

  def changePlayBackSpeed(speed: Double): Unit = {
    system.actorSelection("/sortActor/*") ! speed
  }
}


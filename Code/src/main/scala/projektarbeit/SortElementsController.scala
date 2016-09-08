package projektarbeit

import java.util

import scala.collection.JavaConverters._
import scalafx.animation.{Interpolator, KeyFrame, PathTransition, Timeline, Transition}
import scalafx.scene.Group
import scalafx.Includes._
import scalafx.event.ActionEvent
import scalafx.scene.layout.Pane
import scalafx.scene.transform.Translate
import scalafx.util.Duration

/**
  * Created by Patrick König on 08.09.16.
  */
class SortElementsController(val pane: Pane) {

  private var groupCounter = 0


  def relocateElementGroup(group: Group, list: List[SortElement]) = {
    pane.prefWidth() = pane.prefWidth() + 200
    val timeline = new Timeline {
      autoReverse = false
      keyFrames = Seq(
        at (2 s) {group.translateY -> 200d }

      )
    }
    timeline.play()
    timeline.onFinished = {
      event: ActionEvent =>
        //ForTobyFallsErDasVorMorgenLiest: Nimmt man hierfür die eigentliche Liste, werden die Elemente dennoch um weitere 200 heruntergesetzt
      var newlist = list.map(_.duplicate())
        newlist.foreach(_.yPos += 200)
       sort(newlist)
    }


  }

  def sort(elements: List[SortElement]): Unit = {
    if (elements.size > 1) {
      groupCounter += 1

      var firstListLength = (elements.size / 2)
      var splitList = elements.splitAt(firstListLength)

      val first = splitList._1
      val second = splitList._2

      val duplicateEntryList = first.map(_.duplicate())


      val group = new Group()
      group.getChildren.addAll((duplicateEntryList).asJava)
      group.id = "level-" + groupCounter
      relocateElementGroup(group, duplicateEntryList)
      pane.getChildren.add(group)


    }
  }

  /*def sortOriginal(elements: List[SortElement]): SortElement = {

    if (elements.size > 1 ){
      var firstListLength = (elements.size/2)
      var splitList = elements.splitAt(firstListLength)


      var first = splitList._1
      var second = splitList._2
      println(s"All: ${elements.size} - Left: ${first.size} - Right: ${second.size}")

      val newFirst: List[SortElement] = first.map(_.duplicate)
      def changeYValue(e: SortElement): SortElement = {
        e.yPos = 200;
        e
      }
      val newFirst2: List[SortElement] = newFirst.map(changeYValue(_))

      newFirst2.foreach(pane.getChildren.add(_))


      sortOriginal(first)
      sortOriginal(second)
    //  merge(elements, first, second)
    }
  }*/
/*
  def merge(resunlt:List[SortElement], first:List[SortElement], second:List[SortElement]) {
    var i = 0
    var j = 0

    for (k <- 0 until result.length) {

      if(i < first.length && j < second.length){

        if (first(i) < second(j)){
          result(k) = first(i)
          i=i+1
        } else {
          result(k) = second(j)
          j=j+1
        }
      }else if(i>=first.length && j<second.length){
        result(k) = second(j)
        j=j+1
      } else {
        result(k) = first(i)
        i=i+1
      }
    }
  }*/

}

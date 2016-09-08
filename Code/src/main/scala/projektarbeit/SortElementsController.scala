package projektarbeit

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.animation.Timeline
import scalafx.event.ActionEvent
import scalafx.scene.Group
import scalafx.scene.layout.Pane

/**
  * Created by Patrick König on 08.09.16.
  */
class SortElementsController(val pane: Pane) {

  private var groupCounter = 1
  def relocateElementGroup(group: Group) = {
    pane.prefWidth() = pane.prefWidth() + 200
    val timeline = new Timeline {
      autoReverse = false
      keyFrames = Seq(
        at (2 s) {
          group.translateY -> 200d
        }
      )
    }
    timeline.play()
    timeline.onFinished = {
      event: ActionEvent =>

        group.translateY() -= 200
        group.getChildren.toList.asInstanceOf[List[SortElement]].foreach(_.yPos += 200)

        sort(group.getChildren.toList.asInstanceOf[List[SortElement]])

    }


  }

  def sort(elements: List[SortElement]): Unit = {
    if (elements.size > 1) {
      groupCounter += 1

      val firstListLength = elements.size / 2
      val splitList = elements.splitAt(firstListLength)

      val first = splitList._1
      val second = splitList._2

      val duplicateFirst = first.map(_.duplicate())
      val duplicateSecond = second.map(_.duplicate())


      val groupFirst = new Group()
      groupFirst.getChildren.addAll(duplicateFirst.asJava)
      groupFirst.id = s"level-$groupCounter"
      pane.getChildren.add(groupFirst)

      val groupSecond = new Group()
      groupSecond.getChildren.addAll(duplicateSecond.asJava)
      groupSecond.id = s"level-$groupCounter"
      pane.getChildren.add(groupSecond)

      relocateElementGroup(groupFirst)
      relocateElementGroup(groupSecond)
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

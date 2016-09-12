package projektarbeit

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.animation.{SequentialTransition, Timeline}
import scalafx.event.ActionEvent
import scalafx.scene.{Group, Node}
import scalafx.scene.layout.Pane

/**
  * Created by Patrick König on 08.09.16.
  */
class SortElementsController(val pane: Pane) {

  private val sequence: SequentialTransition = new SequentialTransition()

  def relocateElementGroup(group: Group, depth: Int) = {
    pane.prefWidth() = pane.prefWidth() + 200

    val timeline = new Timeline {
      autoReverse = false
      keyFrames = Seq(
        at (0.2 s) {
          group.opacity -> 1.0},
        at(1 s) {
          group.translateY -> (group.translateY() + 200)
        }
      )
    }
   /* timeline.onFinished = {
      event: ActionEvent =>

        group.translateY() -= 200
        group.getChildren.toList.asInstanceOf[List[SortElement]].foreach(_.yPos += 200)
    }*/
    sequence.children.add(timeline)
    sort(group, depth + 1)

  }

  def sort(elementGroup: Group, depth: Int): Unit = {
    val elements: List[SortElement] = elementGroup.getChildren.toList.asInstanceOf[List[SortElement]]
    if (elements.size > 1) {

      val firstListLength = elements.size / 2
      val splitList = elements.splitAt(firstListLength)

      val first = splitList._1
      val second = splitList._2

      val duplicateFirst = first.map(_.duplicate())
      val duplicateSecond = second.map(_.duplicate())


      val groupFirst = new Group()
      //groupFirst.visible = false;
      groupFirst.opacity = 0.0
      groupFirst.getChildren.addAll(duplicateFirst.asJava)
      groupFirst.translateX <== elementGroup.translateX - groupFirst.getBoundsInParent.getWidth/2
      groupFirst.translateY() = elementGroup.translateY() + 200
      groupFirst.id = s"level-${depth + 1}"
      pane.getChildren.add(groupFirst)

      relocateElementGroup(groupFirst, depth)



      val groupSecond = new Group()
      groupSecond.opacity = 0.0
      groupSecond.getChildren.addAll(duplicateSecond.asJava)
      groupSecond.id = s"level-${depth + 1}"
      pane.getChildren.add(groupSecond)
      groupSecond.translateX <== elementGroup.translateX + elementGroup.getBoundsInParent.getWidth / 2 -  groupSecond.getBoundsInParent.getWidth/2 + SortElement.width
      groupSecond.translateY() = elementGroup.translateY() + 200

      relocateElementGroup(groupSecond, depth)
    }
  }

  def getSequence : SequentialTransition = {
    this.sequence
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

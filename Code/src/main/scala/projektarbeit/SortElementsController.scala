package projektarbeit

import javafx.collections.ObservableList
import javafx.scene.Node

import projektarbeit.Part.{EnumVal, Left, Right}

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.animation.{SequentialTransition, Timeline}
import scalafx.scene.Group
import scalafx.scene.layout.Pane

/**
  * Created by Patrick König on 08.09.16.
  */
class SortElementsController(val pane: Pane) {

  private val moveDownByPixel = 130

  private val sequence: SequentialTransition = new SequentialTransition()

  def relocateElementGroup(group: Group, depth: Int) = {
    val timeline = new Timeline {
      autoReverse = false
      keyFrames = Seq(
        at (0.2.s) {
          group.opacity -> 1.0 },
        at(1.s) {
          group.translateY -> (group.translateY() + moveDownByPixel)
        }
      )
    }

    sequence.children.add(timeline)
    sort(group, depth + 1)

  }

  def createGroup(parentGroup: Group, splitList: (List[SortElement], List[SortElement]), part: EnumVal, depth: Int): Group = {

    val duplicateList = (part match {
      case Part.Left => splitList._1
      case Part.Right => splitList._2
      case _ => throw new IllegalArgumentException(s"$part is not a valid argument")
    }).map(_.duplicate())

    val group = new Group() {
        opacity = 0.0
        children.addAll(duplicateList.asJava)
        translateY = parentGroup.translateY() + (if (depth > 0) moveDownByPixel else 0)
        id = s"level-${depth + 1}"
      }
      part match {
        case Part.Left =>
          group.translateX <== parentGroup.translateX - group.getBoundsInParent.getWidth / 2 + SortElement.width / 2
        case Part.Right =>
          group.translateX <== parentGroup.translateX + parentGroup.getBoundsInParent.getWidth / 2 - group.getBoundsInParent.getWidth/2 - SortElement.width/2

      }
    group
  }

  def sort(parentGroup: Group, depth: Int): Unit = {
    val elements: List[SortElement] = parentGroup.getChildren.toList.asInstanceOf[List[SortElement]]
    if (elements.size > 1) {

      val firstListLength = (elements.size / 2.0).ceil.toInt
      val splitList = elements.splitAt(firstListLength)

      val left = createGroup(parentGroup, splitList, Left, depth)
      pane.getChildren.add(left)
      relocateElementGroup(left, depth)

      val right = createGroup(parentGroup, splitList, Right, depth)
      pane.getChildren.add(right)
      relocateElementGroup(right, depth)

      val mergeGroup = merge(left, right)
      println(mergeGroup.getChildren.toList)

    }
  }

  def merge (leftGroup: Group, rightGroup: Group): Group = {
    val leftList: List[SortElement] = leftGroup.getChildren.toList.asInstanceOf[List[SortElement]]
    val rightList: List[SortElement] = rightGroup.getChildren.toList.asInstanceOf[List[SortElement]]

    val leftSize: Int = leftList.size
    val rightSize: Int = rightList.size
    val totalSize: Int = leftSize + rightSize

    var resultList: List[SortElement] = List.fill(totalSize)(null)
    val group = new Group() {
      translateY() = Math.max(leftGroup.translateY(), rightGroup.translateY()) + moveDownByPixel
    }

    var i = 0
    var j = 0

   for (k <- 0 until totalSize) {

     if(i < leftSize && j < rightSize) {
       if(leftList(i) < rightList(j)) {
          resultList = resultList.updated(k, leftList(i))
         i += 1
       } else {
         resultList = resultList.updated(k, rightList(j))
         j += 1
       }
     } else if(i >= leftSize && j < rightSize){
       resultList = resultList.updated(k, rightList(j))
       j += 1
     } else {
       resultList = resultList.updated(k, leftList(i))
       i += 1
     }

   }
    group.getChildren.addAll(resultList.asJava)
    return group
  }

  def getSequence : SequentialTransition = {
    this.sequence
  }

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


object Part {
  sealed trait EnumVal
  case object Left extends EnumVal
  case object Right extends EnumVal
  val partitions = Seq(Left, Right)
}

package projektarbeit

import javafx.scene.control.ScrollPane

import projektarbeit.Part.{EnumVal, Left, Right}

import scala.collection.JavaConverters._
import scala.collection.mutable.ListBuffer
import scalafx.Includes._
import scalafx.animation.{ParallelTransition, SequentialTransition, Timeline}
import scalafx.beans.binding.NumberBinding
import scalafx.beans.property.DoubleProperty
import scalafx.event.ActionEvent
import scalafx.scene.Group
import scalafx.scene.control.TextArea
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color

/**
  * Created by Patrick König on 08.09.16.
  */
class SortElementsController(val pane: Pane, val consoleLog: TextArea, val initialGroup: Group, val amountOfThreads: Int) {
  def getSequences(): List[SequentialTransition] = {
    List(seq1, seq2, groupSeq)
  }

  def play(): Unit = {
    seq1.play()
    seq2.play()
  }

  def playFinalMerge(): Unit = {
    groupSeq.play()
  }


  private val moveDownByPixel = 130
  var maxDepth: Double = _

  val initialGroupElements: List[SortElement] = initialGroup.children.toList.asInstanceOf[List[SortElement]]
  val amountOfElementsPerThread: Int = Math.ceil(initialGroupElements.size / amountOfThreads.toDouble).toInt
  val seq1 = new SequentialTransition()
  val seq2 = new SequentialTransition()
  val groupSeq = new SequentialTransition()
  var consoleText: String = ""
  var hackedValue = 0
  var autoScrollActivated = true
  var finalGroups: Array[Group] = new Array[Group](2)

  def calculatedXPosition(index: Int, group: Group): NumberBinding = {
    amountOfThreads match {
      case 1 => autoScrollActivated = true; initialGroup.translateX + 0
      case 2 => autoScrollActivated = false;{
        index match {
          case 0 => group.children.toList.asInstanceOf[List[SortElement]].foreach(_.changeColor(Color.DarkGreen));initialGroup.translateX - group.getBoundsInParent.getWidth / 2 + SortElement.width / 2
          case 1 => group.children.toList.asInstanceOf[List[SortElement]].foreach(_.changeColor(Color.DarkRed));initialGroup.translateX + initialGroup.getBoundsInParent.getWidth / 2 - group.getBoundsInParent.getWidth / 2 - SortElement.width / 2
        }
      }
      case number => throw new IllegalArgumentException(s"Cannot run on more than 2 Threads! You tried to run it on $number threads")
    }
  }

  def createThread(index: Int, list: List[SortElement]): Group = {
    val group = new Group() {
      children.addAll(list.asJava)
      id = s"thread-${index}"
      opacity = 1.0
    }

    group.translateX <== calculatedXPosition(index, group)
    group.translateY() = initialGroup.translateY() + moveDownByPixel
    pane.children.add(group)
    sort(group, 0, index)
  }

  def run(): Unit = {
    val threadElements = initialGroupElements.grouped(amountOfElementsPerThread)
    for ((list, index) <- threadElements.zipWithIndex) {
      finalGroups(index) = createThread(index, list.map(_.duplicate))
    }
  }

  def relocateElementGroup(group: Group, depth: Int, threadNumber: Int): Timeline = {
    //val factor = 1.0/(maxDepth)
    val consoleTextLength: Int = consoleText.length
    val timeline = new Timeline {
      autoReverse = false
      keyFrames = Seq(
        /*at (0.15.s) {
          group.getScene.lookup("#scrollPaneID").asInstanceOf[ScrollPane].vvalue -> (factor * (if(depth == 0){depth} else {depth + 1}))
        },*/
        at (0.2.s) {
          group.opacity -> 1.0 },
        at(1.s) {
          group.translateY -> (group.translateY() + moveDownByPixel)
        }
      )
    }
    if (threadNumber == 0) seq1.children.add(timeline)
    else seq2.children.add(timeline)

    timeline

  }

  def createMergeGroup( mergeList: List[SortElement], depth: Int): Group = {
    val group = new Group() {
      opacity = 0.0
      children.addAll(mergeList.asJava)
      id = s"level-${depth + 1}"
    }
    group
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


  def scroll(group: Group, depth: Int) = {
    if(autoScrollActivated) {
      val factor = 1.0 / (maxDepth)
      val timeline = new Timeline {
        autoReverse = false
        keyFrames = Seq(
          at(0.5.s) {
            group.getScene.lookup("#scrollPaneID").asInstanceOf[ScrollPane].vvalue -> (factor * (depth + 1))
          }
        )
      }

      seq1.children.add(timeline)
    }

  }

  def sort(parentGroup: Group, depth: Int, threadNumber: Int): Group = {
    val elements: List[SortElement] = parentGroup.children.toList.asInstanceOf[List[SortElement]]
    if (elements.size > 1) {

      val firstListLength = (elements.size / 2.0).ceil.toInt
      val splitList = elements.splitAt(firstListLength)

      val left = createGroup(parentGroup, splitList, Left, depth)
      pane.children.add(left)
      scroll(left, depth)
      val timelineLeft = relocateElementGroup(left, depth, threadNumber)
      consoleText += s"SPLIT $elements - ${elements.size} elements\n"
      timelineLeft.keyFrames.add(at(0.1.s) {consoleLog.text -> consoleText})
      timelineLeft.keyFrames.add(at(0.12.s) {consoleLog.scrollTop -> Double.MaxValue})
      val sortedLeft = sort(left, depth + 1, threadNumber)


      val right = createGroup(parentGroup, splitList, Right, depth)
      pane.children.add(right)
      scroll(right, depth)
      val timelineRight = relocateElementGroup(right, depth, threadNumber)
      timelineRight.keyFrames.add(at(0.1.s) {consoleLog.text -> consoleText})
      val sortedRight = sort(right, depth + 1, threadNumber)

      return merge(sortedLeft, sortedRight, depth + 1, threadNumber)

    }
    parentGroup
  }



  def merge(leftGroup: Group, rightGroup: Group, depth: Int, threadNumber: Int): Group = {


    val leftList: List[SortElement] = leftGroup.getChildren.toList.asInstanceOf[List[SortElement]]
    val rightList: List[SortElement] = rightGroup.children.toList.asInstanceOf[List[SortElement]]

    val leftSize: Int = leftList.size
    val rightSize: Int = rightList.size
    val totalSize: Int = leftSize + rightSize
    var resultList: List[SortElement] = List.fill(totalSize)(null)
    val newYValue = Math.max(leftGroup.translateY(), rightGroup.translateY()) + moveDownByPixel

    consoleText += s"MERGE $leftList and $rightList\n"
    val timeline = new Timeline {
      autoReverse = false
      keyFrames = Seq(
        at (0.05.s) {
          consoleLog.text -> consoleText
        },
        at (0.09.s) {
          consoleLog.scrollTop -> Double.MaxValue
        }
      )
    }

    if (threadNumber == 0) seq1.children.add(timeline)
    else if (threadNumber == 1) seq2.children.add(timeline)
    else groupSeq.children.add(timeline)
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

    val resultListDuplicate: List[SortElement] = resultList.map(_.duplicate())
    val group = createMergeGroup(resultListDuplicate, depth)
    group.translateY = newYValue

    //TODO: In eigene methode auslagern
    val rightestPosition = rightGroup.translateX + rightGroup.children.get(0).asInstanceOf[SortElement].xPos + rightGroup.getBoundsInParent.getWidth
    val leftestPosition = leftGroup.translateX
    val widthOfParentsGroups = rightestPosition - leftestPosition
    val middleOfParentGroup = rightestPosition - widthOfParentsGroups / 2
    val widthOfNewGroup = group.children.size * SortElement.wholeElementWidth - SortElement.offsetToNextElement

    val newXPosition = middleOfParentGroup - widthOfNewGroup / 2

    if (resultList.size == 2) {
      val hackedValueBySuspicion: Int = 20
      group.translateX <== newXPosition + (hackedValue * hackedValueBySuspicion)
      hackedValue += 1
    } else {
      group.translateX <== newXPosition
    }

    //group.translateX() = leftGroup.translateX()
    pane.children.add(group)
    showGroup(group, threadNumber)
    val realdepth: Int = maxDepth.toInt - depth
    scroll(group, realdepth)
    for ((element, i) <- resultListDuplicate.zipWithIndex) {
      element.opacity() = 0
      relocateElement(element, i, depth, threadNumber)
      element.xPos = i * SortElement.wholeElementWidth
    }

    group
  }

  def relocateElement(element: SortElement, i: Int, depth: Int, threadNumber: Int) = {
    val factor = 1.0/(maxDepth)
    val timeline = new Timeline {
      autoReverse = false
      keyFrames = Seq(
        /*at (0.15.s) {
          element.getScene.lookup("#scrollPaneID").asInstanceOf[ScrollPane].vvalue -> (factor*(maxDepth-depth + 1))
        },*/
        at (0.2.s) {
          element.opacityProperty -> 1.0
        },
        at (1.0.s) {
          element.translateY -> (element.translateY() + moveDownByPixel)
        }

      )
    }
    if (threadNumber == 0) seq1.children.add(timeline)
    else if (threadNumber == 1) seq2.children.add(timeline)
    else groupSeq.children.add(timeline)

  }

  def showGroup(group: Group, threadNumber: Int) = {
    val timeline = new Timeline {
      autoReverse = false

      keyFrames = Seq(
        at (0.2.s) {
          group.opacity -> 1.0 }

        )

    }
    if (threadNumber == 0) seq1.children.add(timeline)
    else if (threadNumber == 1 )seq2.children.add(timeline)
    else groupSeq.children.add(timeline)

  }

}


object Part {
  sealed trait EnumVal
  case object Left extends EnumVal
  case object Right extends EnumVal
  val partitions = Seq(Left, Right)
}

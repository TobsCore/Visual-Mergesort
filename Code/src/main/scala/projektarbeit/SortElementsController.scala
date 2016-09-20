package projektarbeit

import javafx.scene.control.ScrollPane

import projektarbeit.Part.{EnumVal, Left, Right}

import scala.collection.JavaConverters._
import scalafx.Includes._
import scalafx.animation.{SequentialTransition, Timeline}
import scalafx.beans.binding.NumberBinding
import scalafx.scene.Group
import scalafx.scene.control.TextArea
import scalafx.scene.layout.Pane
import scalafx.scene.paint.Color
import scalafx.scene.text.Text

/**
  * Created by Patrick König & Tobias Kerst on 08.09.16.
  */
class SortElementsController(val pane: Pane, val consoleLog: TextArea, val initialGroup: Group, val amountOfThreads: Int) {


  private val moveDownByPixel = 130
  var maxDepth: Double = _

  val initialGroupElements: List[SortElement] = initialGroup.children.toList.asInstanceOf[List[SortElement]]
  val amountOfElementsPerThread: Int = Math.ceil(initialGroupElements.size / amountOfThreads.toDouble).toInt
  val seq1 = new SequentialTransition()
  val seq2 = new SequentialTransition()
  val groupSeq = new SequentialTransition()
  var consoleText: String = ""
  var hackedValue = 0
  val autoScrollActivated = amountOfThreads == 1
  var finalGroups: Array[Group] = new Array[Group](2)

  def run(): Unit = {
    val threadElements = initialGroupElements.grouped(amountOfElementsPerThread)
    for ((list, index) <- threadElements.zipWithIndex) {
      finalGroups(index) = createThread(index, list.map(_.duplicate()))
    }
  }

  def play(): Unit = {
    seq1.play()
    seq2.play()
  }

  def createThread(index: Int, list: List[SortElement]): Group = {

    if (amountOfThreads > 1) {
      val color =
      index match {
        case 0 => Color.DarkBlue
        case 1 => Color.SlateBlue
        case _ => throw new IllegalArgumentException(s"Index $index is not supported")
      }
      list.foreach(_.changeColor(color))
    }
    val group = new Group() {
      children.addAll(list.asJava)
      id = s"thread-$index"
      opacity = 1.0
    }

    consoleText += s"Create Thread ${index + 1} with elements $list \n"
    val timeline = new Timeline {
      autoReverse = false
      keyFrames = Seq(
        at(0.05.s) {
          consoleLog.text -> consoleText
        },
        at(0.09.s) {
          consoleLog.scrollTop -> Double.MaxValue
        }
      )
    }
    addToSequence(index, timeline)
    group.translateX <== calculatedXPosition(index, group)
    group.translateY() = initialGroup.translateY() + moveDownByPixel + 50
    pane.children.add(group)


    val threadText = new Text(s"Thread ${index + 1}") {
      x <== group.translateX + group.getBoundsInParent.getWidth / 2 - 20 + (index * (2 * amountOfElementsPerThread *
        10))
      y <== group.translateY - 20
    }
    println(s"Index: $index: Text: ${threadText.translateX()} | Group: ${group.translateX()}")
    pane.children.add(threadText)
    sort(group, 0, index)
  }

  def sort(parentGroup: Group, depth: Int, threadNumber: Int): Group = {

    val elements: List[SortElement] = parentGroup.children.toList.asInstanceOf[List[SortElement]]
    if (elements.size > 1) {

      val firstListLength = (elements.size / 2.0).ceil.toInt
      val splitList = elements.splitAt(firstListLength)

      val left = createGroup(parentGroup, splitList, Left, depth)
      pane.children.add(left)
      scroll(left, depth, threadNumber)
      val timelineLeft = relocateElementGroup(left, depth, threadNumber)
      consoleText += s"SPLIT $elements - ${elements.size} elements\n"
      timelineLeft.keyFrames.add(at(0.1.s) {
        consoleLog.text -> consoleText
      })
      timelineLeft.keyFrames.add(at(0.12.s) {
        consoleLog.scrollTop -> Double.MaxValue
      })
      val sortedLeft = sort(left, depth + 1, threadNumber)


      val right = createGroup(parentGroup, splitList, Right, depth)
      pane.children.add(right)
      scroll(right, depth, threadNumber)
      val timelineRight = relocateElementGroup(right, depth, threadNumber)
      timelineRight.keyFrames.add(at(0.1.s) {
        consoleLog.text -> consoleText
      })
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
        at(0.05.s) {
          consoleLog.text -> consoleText
        },
        at(0.09.s) {
          consoleLog.scrollTop -> Double.MaxValue
        }
      )
    }

    addToSequence(threadNumber, timeline)
    var i = 0
    var j = 0

    for (k <- 0 until totalSize) {

      if (i < leftSize && j < rightSize) {
        if (leftList(i) < rightList(j)) {
          resultList = resultList.updated(k, leftList(i))
          i += 1
        } else {
          resultList = resultList.updated(k, rightList(j))
          j += 1
        }
      } else if (i >= leftSize && j < rightSize) {
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


    val newXPosition = {
      val rightestPosition = rightGroup.translateX + rightGroup.children.get(0).asInstanceOf[SortElement].xPos + rightGroup.getBoundsInParent.getWidth
      val leftestPosition = leftGroup.translateX
      val widthOfParentsGroups = rightestPosition - leftestPosition
      val middleOfParentGroup = rightestPosition - widthOfParentsGroups / 2
      val widthOfNewGroup = group.children.size * SortElement.wholeElementWidth - SortElement.offsetToNextElement
      middleOfParentGroup - widthOfNewGroup / 2
    }

    if (resultList.size == 2) {
      val hackedValueBySuspicion: Int = 20
      group.translateX <== newXPosition + (hackedValue * hackedValueBySuspicion)
      hackedValue += 1
    } else {
      group.translateX <== newXPosition
    }

    pane.children.add(group)
    showGroup(group, threadNumber)
    val realdepth: Int = if(amountOfThreads == 1) maxDepth.toInt - depth else  maxDepth.toInt - depth - 1
    scroll(group, realdepth, threadNumber)
    for ((element, i) <- resultListDuplicate.zipWithIndex) {
      element.opacity() = 0
      relocateElement(element, i, depth, threadNumber)
      element.xPos = i * SortElement.wholeElementWidth
    }

    group
  }

  def sequences: List[SequentialTransition] = {
    List(seq1, seq2, groupSeq)
  }

  def playFinalMerge(): Unit = {
    groupSeq.play()
  }

  def calculatedXPosition(index: Int, group: Group): NumberBinding = {
    amountOfThreads match {
      case 1 => initialGroup.translateX + 0
      case 2 => index match {
        case 0 => initialGroup.translateX - group.getBoundsInParent.getWidth / 2 + SortElement.width / 2
        case 1 => initialGroup.translateX + initialGroup.getBoundsInParent.getWidth / 2 - group.getBoundsInParent.getWidth / 2 - SortElement.width / 2
      }
      case number => throw new IllegalArgumentException(s"Cannot run on more than 2 Threads! You tried to run it on $number threads")
    }
  }

  def relocateElementGroup(group: Group, depth: Int, threadNumber: Int): Timeline = {
    val timeline = new Timeline {
      autoReverse = false
      keyFrames = Seq(
        at(0.2.s) {
          group.opacity -> 1.0
        },
        at(1.s) {
          group.translateY -> (group.translateY() + moveDownByPixel)
        }
      )
    }
    addToSequence(threadNumber, timeline)

    timeline
  }

  def addToSequence(threadNumber: Int, timeline: Timeline): Boolean = {

    if (threadNumber == 0) seq1.children.add(timeline)
    else if (threadNumber == 1) seq2.children.add(timeline)
    else {println("Adds transition to last group"); groupSeq.children.add(timeline)}
  }


  def createMergeGroup(mergeList: List[SortElement], depth: Int): Group = {

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
        group.translateX <== parentGroup.translateX + parentGroup.getBoundsInParent.getWidth / 2 - group.getBoundsInParent.getWidth / 2 - SortElement.width / 2

    }
    group
  }


  def scroll(group: Group, depth: Int, threadNumber: Int) = {

      val factor = 1.0 / maxDepth
      val timeline = new Timeline {
        autoReverse = false
        keyFrames = Seq(
          at(0.5.s) {
            group.getScene.lookup("#scrollPaneID").asInstanceOf[ScrollPane].vvalue -> (factor * (depth + 1))
          }
        )
      }

    addToSequence(threadNumber, timeline)
  }

  def relocateElement(element: SortElement, i: Int, depth: Int, threadNumber: Int) = {

    val timeline = new Timeline {
      autoReverse = false
      keyFrames = Seq(
        /*at (0.15.s) {
          element.getScene.lookup("#scrollPaneID").asInstanceOf[ScrollPane].vvalue -> (factor*(maxDepth-depth + 1))
        },*/
        at(0.2.s) {
          element.opacityProperty -> 1.0
        },
        at(1.0.s) {
          element.translateY -> (element.translateY() + moveDownByPixel)
        }

      )
    }
    addToSequence(threadNumber, timeline)
  }

  def showGroup(group: Group, threadNumber: Int) = {

    val timeline = new Timeline {
      autoReverse = false

      keyFrames = Seq(
        at(0.2.s) {
          group.opacity -> 1.0
        }

      )

    }
    if (threadNumber == 0) seq1.children.add(timeline)
    else if (threadNumber == 1) seq2.children.add(timeline)
    else groupSeq.children.add(timeline)

  }

}


object Part {

  sealed trait EnumVal

  case object Left extends EnumVal

  case object Right extends EnumVal

  val partitions = Seq(Left, Right)
}

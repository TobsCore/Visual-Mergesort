package projektarbeit




import javafx.scene.Group

import scalafx.geometry.Pos
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Text, TextAlignment}

/**
  * Created by Patrick König on 06.09.16.
  */
class SortElement(val number: Int, var xPos: Int, var yPos: Int) extends Group  {
  require(number >= 1 && number <= 99 , "the number must be between 1 and 99 (inclusive)")

  var text = new Text(number.toString)
  text.style = "-fx-font-size: 10px; -fx-background: #f00"

  val offset = if (number < 10) { SortElement.smallNumberOffset } else { 0 }
  text.translateX = xPos + offset
  text.translateY = yPos + number + SortElement.width

  var rectangle = new Rectangle(new javafx.scene.shape.Rectangle(xPos, yPos, SortElement.width, number))
  this.getChildren.addAll(rectangle,text)
}

object SortElement{
  // To place small numbers (numbers < 10) centered under the rectangle, this offset is needed.
  val smallNumberOffset = 3
  // This is the rectangles width
  val width = 12
  // This is the space between two rectangles
  val offsetToNextElement = 8

  val wholeElementWidth = width + offsetToNextElement

  val maxHeight = 99
  val minHeight = 1
}

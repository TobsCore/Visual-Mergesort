package projektarbeit




import javafx.beans.property.IntegerProperty
import javafx.scene.Group

import scalafx.beans.property.DoubleProperty
import scalafx.geometry.Pos
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Text, TextAlignment}

/**
  * Created by Patrick König on 06.09.16.
  */
class SortElement(val number: Int, var _xPos: Double, var _yPos: Double) extends Group with Ordered[SortElement]  {
  require(number >= 1 && number <= 99 , "the number must be between 1 and 99 (inclusive)")

  var text = new Text(number.toString)
  text.style = "-fx-font-size: 10px; -fx-background: #f00"

  val offset = if (number < 10) { SortElement.smallNumberOffset } else { 0 }
  text.translateX() = xPos + offset
  text.translateY() = _yPos + number + SortElement.width

  var rectangle = new Rectangle(new javafx.scene.shape.Rectangle(_xPos, _yPos, SortElement.width, number))
  this.getChildren.addAll(rectangle,text)



  def xPos_= (x: Double) {
    _xPos = x
    text.translateX = _xPos + offset
    rectangle.x = x
  }

 def yPos_= (y: Double) {
    _yPos = y
   text.translateY = _yPos + number + SortElement.width
    rectangle.y = y
  }

  def xPos = _xPos

  def yPos = _yPos


  def duplicate(): SortElement = {
    return new SortElement(number, _xPos, _yPos)
  }

  override def compare(that: SortElement): Int = {
    this.number - that.number
  }

  override def <(that: SortElement): Boolean = {
    (this.number < that.number)
  }

  override def <=(that: SortElement): Boolean = {
    (this.number <= that.number)
  }

  override def >(that: SortElement): Boolean = {
    (this.number > that.number)
  }

  override def >=(that: SortElement): Boolean = {
    (this.number > that.number)
  }

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

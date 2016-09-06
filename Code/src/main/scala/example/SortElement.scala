package example




import javafx.scene.Group

import scalafx.geometry.Pos
import scalafx.scene.shape.Rectangle
import scalafx.scene.text.{Text, TextAlignment}

/**
  * Created by patrickkoenig on 06.09.16.
  */
class SortElement(val number: Int, var xPos: Int, var yPos: Int) extends Group  {
  require(number >= 1 && number <= 99 , "the number must be between 1 and 99 (inclusive)")
  var text = new Text(number.toString)
  text.style = "-fx-font-size: 10px; -fx-background: #f00"
  val offset = if (number < 10) { 3 } else { 0 }
  text.translateX = xPos + offset
  text.translateY = yPos + number + 12
  var rectangle = new Rectangle(new javafx.scene.shape.Rectangle(xPos, yPos, SortElement.width, number))
  this.getChildren.addAll(rectangle,text)


}

object SortElement{
  val width = 12
}

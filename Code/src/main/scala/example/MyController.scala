package example

import scalafx.scene.control.{Button, TextField}
import scalafxml.core.macros.sfxml

/**
  * Created by Patrick König on 19.08.16.
  */
@sfxml
class MyController (
  private val button: Button,
  private val clickTimeTextField: TextField) {

  def countClicks(): Unit = {
    val content = clickTimeTextField.getText
    val timesClicked = if (content.equals("")) 1 else content.toInt + 1
    clickTimeTextField.setText(timesClicked.toString)
  }
 }









package example
import javafx.fxml.FXML

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.BorderPane
import scalafxml.core.macros.sfxml
import scalafxml.core.{ControllerDependencyResolver, FXMLView, FxmlProxyGenerator, NoDependencyResolver}

/**
  * Created by patrickkoenig on 19.08.16.
  */
@sfxml
class MyController (private val button: Button) {



  @FXML def buttonPressed() {
    System.out.println("jo")
  }
 }









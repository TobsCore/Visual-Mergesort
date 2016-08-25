package example

import java.io.IOException

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafx.scene.control.Label
import scalafx.scene.layout.BorderPane
import scalafxml.core.{FXMLView, NoDependencyResolver}

object ExampleApplication extends JFXApp {
  val resource = getClass.getResource("BasicApplication_css/BasicApplication_css.fxml")
  if (resource == null) {
    throw new IOException("Cannot load resource: AdoptionForm.fxml")
  }

  val root = FXMLView(resource, NoDependencyResolver)

  stage = new PrimaryStage() {
    title = "Tobys und Pattys neue beste und faszinierende Application"
    var borderPane = new BorderPane()
    var joman = new Label("hello")
    borderPane.setCenter(root)
    scene = new Scene(borderPane)


  }

}
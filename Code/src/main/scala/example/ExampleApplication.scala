package example

import java.io.IOException

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
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
    val borderPane = new BorderPane()
    borderPane.setCenter(root)
    scene = new Scene(borderPane)


  }

}
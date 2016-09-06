package example

import java.io.IOException

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLView, NoDependencyResolver}

object ExampleApplication extends JFXApp {
  val resource = getClass.getResource("/BasicApplication_css.fxml")
  if (resource == null) {
    //TODO: Externalize String
    throw new IOException("Cannot load resource: BasicApplication_css.fxml")
  }

  val root = FXMLView(resource, NoDependencyResolver)

  stage = new PrimaryStage() {
    title = "Visual Mergesort"
    scene = new Scene(root)


  }

}
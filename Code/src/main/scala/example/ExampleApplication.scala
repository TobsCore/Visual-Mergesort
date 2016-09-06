package example

import java.io.IOException

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafx.Includes._
import scalafxml.core.{FXMLView, NoDependencyResolver}

object ExampleApplication extends JFXApp {
  private val layoutFile: String = "/VisualMergesort.fxml"
  val resource = getClass.getResource(layoutFile)
  if (resource == null) {
    //TODO: Externalize String
    throw new IOException(s"Cannot load resource: $layoutFile")
  }

  val root = FXMLView(resource, NoDependencyResolver)

  stage = new PrimaryStage() {
    title = "Visual Mergesort"
    scene = new Scene(root)


  }

}
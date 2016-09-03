package example

import java.io.IOException

import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.{Node, Parent, Scene}
import scalafx.scene.layout.{BorderPane, Pane}
import scalafxml.core.{FXMLView, NoDependencyResolver}

object ExampleApplication extends JFXApp {
  val resource = getClass.getResource("/BasicApplication_css.fxml")
  if (resource == null) {
    //TODO: Externalize String
    throw new IOException("Cannot load resource: BasicApplication_css.fxml")
  }

  val root = FXMLView(resource, NoDependencyResolver)

  stage = new PrimaryStage() {
    title = "Tobys und Pattys neue beste und faszinierende Application"
    val borderPane = new BorderPane()
    var canvas = new Pane();
    canvas.setPrefSize(400,300)
    borderPane.setTop(root)
    borderPane.setCenter(canvas)
    scene = new Scene(borderPane)


  }

}
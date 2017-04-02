package nephtys.ffxutils
import javafx.application.Application
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.layout.StackPane
import javafx.stage.Stage

/**
  * Created by Christopher on 02.04.2017.
  */
class FFXExample extends javafx.application.Application{
  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Hello World!")
    val btn = new Button
    btn.setText("Say 'Hello World'")
    btn.setOnAction(new EventHandler[ActionEvent] {
      override def handle(e: ActionEvent) {
        println("Hello World!")
      }
    })

    val root = new StackPane
    root.getChildren.add(btn)
    primaryStage.setScene(new Scene(root, 800, 600))
    primaryStage.show()
  }
}

object FFXExample {
  def main(args: Array[String]) {
    Application.launch(classOf[FFXExample], args: _*)
  }
}
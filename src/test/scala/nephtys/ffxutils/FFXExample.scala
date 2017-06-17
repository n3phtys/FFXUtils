package nephtys.ffxutils

import javafx.application.Platform
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.stage.Stage

import scala.concurrent.Future

/**
  * Created by Christopher on 02.04.2017.
  */
class FFXExample extends javafx.application.Application{
  val POPUPMENU_ID = "DEFAULT_POPUPMENU"


  override def start(primaryStage: Stage): Unit = {
    primaryStage.setTitle("Hello World!")
    val btn = new Button
    btn.setText("Say 'Hello World'")

    val popuppane = new PopupMenuPane(POPUPMENU_ID, 175, 250, 70, 70, 45, btn)
    popuppane.setupActiveHandler((a : Object, b : Object) => println(s"$a pressed $b"))
    popuppane.setupPassiveHandlers(Array(("First button", obj => println(s"Pressed on first button with object $obj")), ("Second button", obj => println(s"Pressed on second button with object $obj"))))


    btn.setOnAction(new EventHandler[ActionEvent] {
      override def handle(e: ActionEvent) {
        println("Hello World!")
        import scala.concurrent.ExecutionContext.Implicits.global
        val f : Future[Array[(String, Object)]] = Future{Array(
          ("Button #1", "first active object"),
          ("Button #2", "second active object"),
          ("Button #3", "third active object")
        )}
        PopupMenuPane.showPopupThreadsafe(POPUPMENU_ID, btn.localToScene(btn.getBoundsInLocal).getMinX, btn.localToScene(btn.getBoundsInLocal).getMinY, f, "Some title", "this is an object" )
        //popuppane.showPopupThreadsafe(btn.localToScene(btn.getBoundsInLocal).getMinX, btn.localToScene(btn.getBoundsInLocal).getMinY, f, "Some title", "this is an object" )
      }
    })

    val scene = new Scene(popuppane, 800, 600)
    primaryStage.setScene(scene)


    val css: String = classOf[Nothing].getResource("/popupmenu.css").toExternalForm
    scene.getStylesheets.clear()
    scene.getStylesheets.add(css)

    primaryStage.show()

    Platform.runLater(new Runnable {
      override def run(): Unit = btn.fire()
    })
  }
}

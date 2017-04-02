package nephtys.ffxutils

import java.lang.ref.WeakReference
import java.util
import java.util.concurrent.atomic.AtomicBoolean
import javafx.application.Platform
import javafx.event.{ActionEvent, EventHandler}
import javafx.scene.Node
import javafx.scene.control.{Button, Label}
import javafx.scene.layout.{BorderPane, Pane, VBox}

import scala.concurrent.Future


/**
  * Created by Christopher on 02.04.2017.
  */
class PopupMenuPane(background : Node, id : String, popupWidth : Double, popupHeight : Double, columns : Int = 2, maxActiveButtonNumber : Int = 4) extends Pane {

  //contains invisible menu that can be shown and rerendered via methods and another background

  //TODO: add frontpane with absolute positioning for borderpane
  //TODO: make PopupMenuPane a Stackpane and stretch background Node to full parent in background


  private val _buttons : util.ArrayList[Button] = {
    var list = new util.ArrayList[Button]()
    (1 to maxActiveButtonNumber).foreach(i => {
      val btn = new Button()
      btn.setText("N/A")
      btn.setVisible(false)
      btn.setOnAction(new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {

        }
      })
      list.add(btn)
    })
    list
  }


  private val _activeObjects : Array[Object] = new Array[Object](maxActiveButtonNumber)

  protected val popup = new BorderPane()
  protected val activeButtons = new VBox()
  activeButtons.getChildren.setAll(_buttons)
  protected val passiveButtons = new VBox()
  protected val titleLabel = new Label {}
  titleLabel.getStyleClass.addAll("base-padding", "quickmenu-colors")
  titleLabel.setStyle("-fx-alignment: center")

  //second type of Objects
  def showPopupThreadsafe(positionX : Double, positionY : Double, buttons : Future[Array[(String, Object)]], title : String): Unit = {
    ???
  }

  def isShown : Boolean = this.popup.isVisible

  def closeThreadsafe() : Unit = {
    ???
  }

  def setupActiveHandler(handler : (Object, Object) => Unit ) : Unit = {
    activeHandler = handler
  }

  def setupPassiveHandlers(handlers : Array[(String, Object => Unit)] ) : Unit = {
    //passiveHandlers = handlers
    Platform.runLater(new Runnable {
      override def run(): Unit = {
        handlers.foreach(a => {

        })
      }
    })

  }

  protected def setTitle(str : String) : Unit = this.titleLabel.setText(str)


  private var activeHandler : (Object, Object) => Unit = {
    (a: Object,  b: Object) => println("ActiveHandler in PopupMenuPane not set")
  }
  //not needed: private var passiveHandlers : Array[(String, Object => Unit)] = Array.empty

}
//all drinks, give out, statistics

object PopupMenuPane{
  private val _panes : util.HashMap[String, WeakReference[PopupMenuPane]] = new util.HashMap[String, WeakReference[PopupMenuPane]]()

  def addWeakReference(popupId : String, pane : PopupMenuPane): Unit = {
      ???
  }

  def showPopup(popupId : String, positionX : Double, positionY : Double, buttons : Future[Array[(String, Object)]], title : String ) : Unit = {
    val p = _panes.get(popupId)
    if (p != null) {
      val popuppane = p.get()
      if (popuppane != null) {
        popuppane.showPopupThreadsafe(positionX, positionY, buttons, title)
      }
    }
  }

  def setActiveHandler(popupId: String, handler : (Object, Object) => Unit) : Unit = {
    val p = _panes.get(popupId)
    if (p != null) {
      val popuppane = p.get()
      if (popuppane != null) {
        popuppane.setupActiveHandler(handler)
      }
    }
  }

  //first object type
  def setPassiveHandlers(popupId : String, handlers : Array[(String, Object => Unit)]) : Unit = {
    val p = _panes.get(popupId)
    if (p != null) {
      val popuppane = p.get()
      if (popuppane != null) {
        popuppane.setupPassiveHandlers(handlers)
      }
    }
  }

}
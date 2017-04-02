package nephtys.ffxutils

import java.util.concurrent.atomic.AtomicBoolean
import javafx.scene.Node
import javafx.scene.control.Label
import javafx.scene.layout.{BorderPane, Pane, VBox}

import scala.concurrent.Future


/**
  * Created by Christopher on 02.04.2017.
  */
class PopupMenuPane(background : Node, id : String, popupWidth : Double, popupHeight : Double) extends Pane {

  //contains invisible menu that can be shown and rerendered via methods and another background

  protected val popup = new BorderPane()
  protected val passiveButtons = new VBox()
  protected val titleLabel = new Label {}
  titleLabel.getStyleClass.addAll("base-padding", "quickmenu-colors")
  titleLabel.setStyle("-fx-alignment: center")

  //second type of Objects
  def showPopupThreadsafe(positionX : Double, positionY : Double, buttons : Future[Array[(String, Object)]], title : String): Unit = {

  }

  protected def rerenderVisibility() : Unit = {
    ???
  }


  def isShown : Boolean = {
    ???
  }

  def closeThreadsafe() : Unit = {

  }

  var activeHandler : (Object, Object) => Unit = null
  var passiveHandlers : Array[(String, Object => Unit)] = Array.empty
}
//all drinks, give out, statistics

object PopupMenuPane{
  def addWeakReference(popupId : String, pane : PopupMenuPane): Unit = {

  }

  def showPopup(popupId : String ) : Unit = {

  }


  def setActiveHandlers(popupId: String, handler : (Object, Object) => Unit) : Unit = ???

  //first object type
  def setPassiveHandlers(popupId : String, handlers : Array[(String, Object => Unit)]) : Unit = ???

}
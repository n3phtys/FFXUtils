package nephtys.ffxutils

import java.lang.ref.WeakReference
import java.util
import java.util.concurrent.Callable
import java.util.concurrent.atomic.{AtomicBoolean, AtomicReference}
import java.util.function.Consumer
import javafx.application.Platform
import javafx.event.{ActionEvent, EventHandler}
import javafx.geometry.{HPos, Pos, VPos}
import javafx.scene.Node
import javafx.scene.control.{Button, Label}
import javafx.scene.input.MouseEvent
import javafx.scene.layout._

import scala.concurrent.Future


/**
  * Created by Christopher on 02.04.2017.
  */
class PopupMenuPane(val popupid: String, val popupWidth: Double, val popupHeight: Double, background: Node,val columns: Int = 2, val maxActiveButtonNumber: Int = 4) extends StackPane {

  //TODO: Test in BiRe product, with a working minimal wrapper


  //this.setStyle("-fx-background-color: #AA00BB;")

  //contains invisible menu that can be shown and rerendered via methods and another background

  private val _buttons: util.ArrayList[Button] = {
    var list = new util.ArrayList[Button]()
    (0 until maxActiveButtonNumber).foreach(i => {
      val btn = new Button()
      btn.setText("N/A")
      btn.setVisible(false)
      styleActiveButton(btn)
      btn.setOnAction(new EventHandler[ActionEvent] {
        override def handle(event: ActionEvent): Unit = {
          val first_object = _firstObject.get()
          val x = activeObjects.get()
          if (first_object != null && x.length > i) {
            activeHandler.apply(first_object, x.apply(i))
          } else {
            println("Error in active button eventhandler")
          }
        }
      })
      list.add(btn)
    })
    list
  }


  protected def clipAndSetCoordinates(x: Double, y: Double): Unit = {
    //get total width height
    val w = foreground.getWidth
    val h = foreground.getHeight

    //clip against each of the 4 sides once by moving to the center
    val left: Double = Math.min(Math.max(0, x), w - popupWidth)
    val above: Double = Math.min(Math.max(0, y), h - popupHeight)

    //set constraints to match
    gridconstraint_left.setPrefWidth(left)
    gridconstraint_above.setPrefHeight(above)
  }


  protected val popup = new BorderPane()
  //popup.setStyle("-fx-background-color: #000000;")
  protected val activeButtons = new GridPane()
  activeButtons.getChildren.setAll(_buttons)

  protected val passiveButtons = new VBox()
  passiveButtons.setFillWidth(true)
  passiveButtons.setAlignment(Pos.CENTER)

  protected val titleLabel = new Label()
  popup.setTop(titleLabel)
  popup.setCenter(activeButtons)
  popup.setBottom(passiveButtons)



  private val activeObjects: AtomicReference[Array[(String, Object)]] = new AtomicReference(Array.empty)


  //Gridpane in center, for active buttons
  val rows : Int = Math.ceil(maxActiveButtonNumber.toFloat / columns.toFloat).toInt

  {
    //add equal column constraints
    (0 until columns).foreach( i => {
      val cc = new ColumnConstraints()
      cc.setPercentWidth(100.0f.toDouble / columns.toDouble)
      activeButtons.getColumnConstraints.add(cc)
    })

    //add equal row constraints
    (0 until rows).foreach( i => {
      val rc = new RowConstraints()
      rc.setPercentHeight(100.0f.toDouble / rows.toDouble)
      activeButtons.getRowConstraints.add(rc)
    })

    //set column and row for each button
    (0 until maxActiveButtonNumber).foreach(i => {
      val c = i % columns
      val r = i / rows
      GridPane.setConstraints(_buttons.get(i), c, r)
      GridPane.setHalignment(_buttons.get(i), HPos.CENTER)
      GridPane.setValignment(_buttons.get(i), VPos.CENTER)
    })
  }



  //add frontpane with absolute positioning for borderpane
  protected val foreground: GridPane = new GridPane() //used for pseudo-absolute positioning, using a 9 cell grid whose constraints are moved from time to time
  foreground.setVisible(false)
  private val gridconstraint_left: ColumnConstraints = new ColumnConstraints(0)
  private val gridconstraint_above: RowConstraints = new RowConstraints(0)
  private val gridconstraint_central_column: ColumnConstraints = new ColumnConstraints(popupWidth)
  private val gridconstraint_central_row: RowConstraints = new RowConstraints(popupHeight)
  foreground.getColumnConstraints.add(gridconstraint_left)
  foreground.getColumnConstraints.add(gridconstraint_central_column)
  foreground.getRowConstraints.add(gridconstraint_above)
  foreground.getRowConstraints.add(gridconstraint_central_row)
  GridPane.setConstraints(popup, 1, 1)
  foreground.getChildren.add(popup)
  this.getChildren.addAll(background, foreground)
  //foreground.setStyle("-fx-background-color: #00FF00;")

  //add foreground action handlers to close popup on any click that is not on the popup
  foreground.setOnMouseClicked(new EventHandler[MouseEvent] {
    override def handle(event: MouseEvent): Unit = {
      closeInThread()
    }
  })


  //second type of Objects
  def showPopupThreadsafe(positionX: Double, positionY: Double, buttons: Future[Array[(String, Object)]], title: String, firstObjectType: Object): Unit = {
    //println(s"Showing popup at position $positionX and $positionY")
    _firstObject.set(firstObjectType) //set user
    import scala.concurrent.ExecutionContext.Implicits.global
    Platform.runLater(new Runnable {
      //first: make every active button invisible and set title
      override def run(): Unit = {

        titleLabel.setText(title)

        _buttons.forEach(new Consumer[Button] {
          override def accept(t: Button): Unit = t.setVisible(false)
        })
      }
    })
    buttons.foreach(a => {
      activeObjects.set(a)
      Platform.runLater(new Runnable {
        override def run(): Unit = {
          //afterwards, make every active button visible that is needed
          require(a.length <= _buttons.size())
          a.indices.foreach(i => {
            //activate button
            _buttons.get(i).setText(a.apply(i)._1)
            _buttons.get(i).setVisible(true)
          })
          foreground.setVisible(true)
        }
      })
    })
    //clipping
    clipAndSetCoordinates(positionX, positionY)
    //showing
    foreground.setVisible(true)

  }

  private val _firstObject: AtomicReference[Object] = new AtomicReference[Object](null)

  def isShown: Boolean = this.foreground.isVisible

  def closeInThread(): Unit = {
    foreground.setVisible(false)
    _firstObject.set(null)
  }

  def closeThreadsafe(): Unit = {
    Platform.runLater(new Runnable {
      override def run(): Unit = {
        closeInThread()
      }
    })
  }

  def setupActiveHandler(handler: (Object, Object) => Unit): Unit = {
    activeHandler = handler
  }

  def setupPassiveHandlers(handlers: Array[(String, Object => Unit)]): Unit = {
    //passiveHandlers = handlers
    Platform.runLater(new Runnable {
      override def run(): Unit = {
        val _buttons = new util.ArrayList[Button]()
        handlers.foreach(a => {
          val b = new Button() //passive buttons are declared here
          stylePassiveButton(b)
          b.setText(a._1)
          b.setOnAction(new EventHandler[ActionEvent] {
            override def handle(event: ActionEvent): Unit = if (_firstObject.get() != null) {
              a._2.apply(_firstObject.get())
            }
          })
          _buttons.add(b)
        })
        Platform.runLater(new Runnable {
          override def run(): Unit = passiveButtons.getChildren.setAll(_buttons)
        })
      }
    })

  }

  private final def stylePassiveButton(btn : Button): Unit = {
    btn.getStyleClass.addAll(
      "user-button",
      "text-light-background-blueish"
    )

    btn.setPrefHeight(45)
    btn.setPrefWidth(2000)
    btn.setMaxHeight(Double.MaxValue)
    btn.setMaxWidth(Double.MaxValue)
  }

  private final def styleActiveButton(btn : Button) : Unit = {
      btn.getStyleClass.addAll(
        "top4-button",
        "text-light-background-blueish"
      )
    btn.setWrapText(true)

    val measurement : Double = 75

    btn.setMinHeight(measurement)
    btn.setPrefHeight(measurement)
    btn.setMaxHeight(measurement)
    btn.setMinWidth(measurement)
    btn.setPrefWidth(measurement)
    btn.setMaxWidth(measurement)
    //btn.setMaxWidth(Double.MaxValue)
  }

  private final def styleTopLabel(lbl : Label) : Unit = {
    titleLabel.getStyleClass.addAll("base-padding", "quickmenu-colors")
    titleLabel.setStyle("-fx-alignment: center")
    titleLabel.setWrapText(true)
    titleLabel.setMaxWidth(Double.MaxValue)
    titleLabel.setStyle("-fx-font-weight:bold;-fx-alignment:center")
  }

  private final def stylePopupBackground() : Unit = {
    this.popup.getStyleClass.addAll("base-no-padding")
    this.setAlignment(Pos.CENTER)
    activeButtons.getStyleClass.addAll("base-padding", "quickmenu-colors", "base-vh-gaps")
    passiveButtons.getStyleClass.addAll("base-padding", "base-spacing", "quickmenu-colors")
  }

  styleTopLabel(titleLabel)
  stylePopupBackground()

  protected def setTitle(str: String): Unit = this.titleLabel.setText(str)


  private var activeHandler: (Object, Object) => Unit = {
    (a: Object, b: Object) => println("ActiveHandler in PopupMenuPane not set")
  }
  //not needed: private var passiveHandlers : Array[(String, Object => Unit)] = Array.empty

  PopupMenuPane.addWeakReference(popupid, this)
}

//all drinks, give out, statistics

object PopupMenuPane {
  private val _panes: util.HashMap[String, WeakReference[PopupMenuPane]] = new util.HashMap[String, WeakReference[PopupMenuPane]]()

  def addWeakReference(popupId: String, pane: PopupMenuPane): Unit = {
    _panes.put(popupId, new WeakReference(pane))
  }

  def showPopupThreadsafe(popupId: String, positionX: Double, positionY: Double, buttons: Future[Array[(String, Object)]], title: String, firstObjectType: Object): Unit = {
    val p = _panes.get(popupId)
    if (p != null) {
      val popuppane = p.get()
      if (popuppane != null) {
        popuppane.showPopupThreadsafe(positionX, positionY, buttons, title, firstObjectType)
      }
    }
  }

  def showPopupThreadsafe(popupId : String, fromNode : Node, buttons : Future[Array[(String, Object)]], title : String, firstObjectType : Object) : Unit = {
    showPopupThreadsafe(popupId, fromNode.localToScene(fromNode.getBoundsInLocal).getMinX, fromNode.localToScene(fromNode.getBoundsInLocal).getMinY, buttons, title, firstObjectType)
  }

  def closePopupThreadsafe(popupId: String): Unit = {
    val p = _panes.get(popupId)
    if (p != null) {
      val popuppane = p.get()
      if (popuppane != null) {
        popuppane.closeThreadsafe()
      }
    }
  }

  def setActiveHandler(popupId: String, handler: (Object, Object) => Unit): Unit = {
    val p = _panes.get(popupId)
    if (p != null) {
      val popuppane = p.get()
      if (popuppane != null) {
        popuppane.setupActiveHandler(handler)
      }
    }
  }

  //first object type
  def setPassiveHandlers(popupId: String, handlers: Array[(String, Object => Unit)]): Unit = {
    val p = _panes.get(popupId)
    if (p != null) {
      val popuppane = p.get()
      if (popuppane != null) {
        popuppane.setupPassiveHandlers(handlers)
      }
    }
  }

}
package nephtys.ffxutils

/**
  * Created by Christopher on 02.04.2017.
  */
class ModalNotificationPane {

}


object ModalNotificationPane {
  sealed trait LifeCycleEvent
  case object Aborted extends LifeCycleEvent
  case object Acknowledged extends LifeCycleEvent
  case object Shown extends LifeCycleEvent
  case object Enqueued extends LifeCycleEvent
  case object Timeout extends LifeCycleEvent
  case object Hidden extends LifeCycleEvent

  def enqueueNotification(id : Long, modal : Boolean, title : String,
                          description : String, canAbort : Boolean,
                          canAcknowledge : Boolean) : Unit = {

  }

  final case class NotificationContainer(id : Long, modal : Boolean, title : String, description : String, canAbort : Boolean, canAcknowledge : Boolean)

  def setNotificationHandler(handler : (Long, LifeCycleEvent) => Unit): Unit = {

  }

  def getNotification(id : Long) : Option[NotificationContainer] = {
    None // TODO
  }
}
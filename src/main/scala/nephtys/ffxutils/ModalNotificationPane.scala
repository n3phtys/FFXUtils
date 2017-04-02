package nephtys.ffxutils

/**
  * Created by Christopher on 02.04.2017.
  */
class ModalNotificationPane {
  //TODO: make stackpane, where we can input a background node to

  //TODO: notifications can be enqueued and will be shown in the moment they are enqueued (and there is place) or after the previous one in the queue is finalized

  //TODO: notifications have lifecycle managemend, spawning LifeCycleEvent events that are dealt with by the given notification handler

  //TODO: do not reuse objects (but arrays should be constant size)

}


object ModalNotificationPane {
  sealed trait LifeCycleEvent
  case object Aborted extends LifeCycleEvent
  case object Acknowledged extends LifeCycleEvent
  case object Shown extends LifeCycleEvent
  case object Enqueued extends LifeCycleEvent
  case object Timeout extends LifeCycleEvent
  case object Hidden extends LifeCycleEvent

  def enqueueNotification(notificatorId : String, id : Long, modal : Boolean, title : String,
                          description : String, canAbort : Boolean,
                          canAcknowledge : Boolean) : Unit = {

  }

  final case class NotificationContainer(id : Long, modal : Boolean, title : String, description : String, canAbort : Boolean, canAcknowledge : Boolean)

  def setNotificationHandler(notificatorId : String, handler : (Long, LifeCycleEvent) => Unit): Unit = {

  }

  def getNotification(notificatorId : String, id : Long) : Option[NotificationContainer] = {
    None // TODO
  }
}
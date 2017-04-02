package nephtys.ffxutils

import java.util.{Timer, TimerTask}
import java.{lang, util}
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Consumer

import nephtys.ffxutils.Timeoutter.Timeouttable


final class Timeoutter(val timerIntervalMs : Long = 500) {

  private val _timer = new Timer()

  private def tick() : Unit = {
    val ms = System.currentTimeMillis()
    _events.forEach(new Consumer[Timeouttable] {
      override def accept(t: Timeouttable): Unit = {
        if (t.endpointMs >= ms) {
          _events.remove(t)
          t.endFunc.apply(t)
        } else {
          t.tickFunc.apply(t)
        }
      }
    })
    endTimerIfNotNeeded()
  }

  private val _events: ConcurrentHashMap.KeySetView[Timeouttable, lang.Boolean] = ConcurrentHashMap.newKeySet[Timeouttable]()

  private val _timerTask = new AtomicReference[Option[TimerTask]](null)

  def setTimeout(event: Timeouttable): Unit = {

    startTimerIfNotAlreadyRunning()
  }

  private def endTimerIfNotNeeded() : Unit = {
    if (_events.isEmpty) {
      _timerTask.synchronized{
        _timerTask.get().foreach(_.cancel())
        _timerTask.set(None)
      }
    }
  }

  private def startTimerIfNotAlreadyRunning() : Unit = {
    _timerTask.synchronized{
      if ( _timerTask.get().isEmpty) {
          _timerTask.set(Some(new java.util.TimerTask {
            override def run(): Unit = tick()
          }))
          _timerTask.get().foreach(t => _timer.schedule(t, 0, 500))
      }
    }
  }
}

object Timeoutter {
  trait Timeouttable {
    def endpointMs : Long
    def tickFunc : Timeoutter.Timeouttable => Unit
    def endFunc : Timeoutter.Timeouttable => Unit
  }
  final case class BasicTimeouttedEvent(endpointMs : Long, tickFunc : Timeoutter.Timeouttable => Unit, endFunc : Timeoutter.Timeouttable => Unit) extends Timeouttable
}
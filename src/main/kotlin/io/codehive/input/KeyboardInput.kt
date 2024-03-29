package io.codehive.input

import com.jogamp.newt.event.KeyEvent
import com.jogamp.newt.event.KeyListener
import io.codehive.bloc4j.game.Config

object KeyboardInput : KeyListener {

  var movingForward = false
  var movingBackwards = false
  var movingRight = false
  var movingLeft = false
  var movingUp = false
  var movingDown = false

  override fun keyPressed(e: KeyEvent) {
    when (e.keyCode) {
      Config.MOVE_FORWARD_KEY -> movingForward = true
      Config.MOVE_BACKWARD_KEY -> movingBackwards = true
      Config.MOVE_RIGHT_KEY -> movingRight = true
      Config.MOVE_LEFT_KEY -> movingLeft = true
      Config.MOVE_UP_KEY -> movingUp = true
      Config.MOVE_DOWN_KEY -> movingDown = true
    }
  }

  override fun keyReleased(e: KeyEvent) {
    when (e.keyCode) {
      Config.MOVE_FORWARD_KEY -> movingForward = false
      Config.MOVE_BACKWARD_KEY -> movingBackwards = false
      Config.MOVE_RIGHT_KEY -> movingRight = false
      Config.MOVE_LEFT_KEY -> movingLeft = false
      Config.MOVE_UP_KEY -> movingUp = false
      Config.MOVE_DOWN_KEY -> movingDown = false
    }
  }

}

package io.codehive.bloc4j.input

import com.jogamp.newt.event.MouseEvent
import com.jogamp.newt.event.MouseListener
import io.codehive.bloc4j.game.Bloc4J
import io.codehive.bloc4j.graphics.lib.Window
import java.awt.Robot

object MouseInput : MouseListener {
  var dx: Int = 400
  var dy: Int = 300

  private val robot = Robot()

  override fun mouseClicked(e: MouseEvent?) {

  }

  override fun mouseEntered(e: MouseEvent) {

  }

  override fun mouseExited(e: MouseEvent) {

  }

  override fun mousePressed(e: MouseEvent) {

  }

  override fun mouseReleased(e: MouseEvent) {

  }

  override fun mouseMoved(e: MouseEvent) {
    val centerX = Window.window.width / 2
    val centerY = Window.window.height / 2

    if (e.x == centerX && e.y == centerY) {
      return
    }

    dx = e.x - centerX
    dy = e.y - centerY

    Bloc4J.cameraEntity.updateFront()

    Bloc4J.cameraEntity.target.rotation.yaw += dx * 0.1f
    Bloc4J.cameraEntity.target.rotation.pitch -= dy * 0.1f

    robot.mouseMove(
      Window.window.x + centerX,
      Window.window.y + centerY,
    )
  }

  override fun mouseDragged(e: MouseEvent) {

  }

  override fun mouseWheelMoved(e: MouseEvent) {
  }
}
